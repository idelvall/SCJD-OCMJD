/*
 * RemoteData.java 29/09/2010
 * 
 * Candidate: Ignacio del Valle Alles
 * Candidate ID: SR1825921
 * 
 * Sun Certified Developer for Java 2 Platform, Standard Edition Programming
 * Assignment (CX-310-252A)
 * 
 * This class is part of the Programming Assignment of the Sun Certified
 * Developer for Java 2 Platform, Standard Edition certification program, must
 * not be used out of this context and must be used exclusively by Oracle Corporation
 */

package suncertify.server.impl;

import java.rmi.server.Unreferenced;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;

import suncertify.commons.LoggingObject;
import suncertify.db.DBAccess;
import suncertify.db.Data;
import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;
import suncertify.server.spec.RemoteDBAccess;
import suncertify.server.spec.UnrefencedListener;

/**
 * Remote version of class {@link Data}. This class is not directly
 * instantiable. Instances of this class are meant to be created by a factory on
 * client demand.<br>
 * Instances of this class are not bound to a RMI registry so they are not
 * shared among several clients (also to avoid the registry reference and be
 * able to be unreference-notified).<br>
 * They can be used to represent client identity.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 29/09/2010
 * 
 */
public class RemoteData extends LoggingObject implements RemoteDBAccess,
		Unreferenced {

	private static final String RJT_MSG = "Current instance is marked to reject incoming calls";

	// Wrapped local data implementation to delegate calls
	private final DBAccess dbAccess;
	// Locks held by the client (recNo,cookie)
	private final Map<Long, Long> heldLocks = new Hashtable<Long, Long>();
	// Threads currently executing
	private final List<Thread> pendingThreads = new Vector<Thread>();
	// Listeners
	private final List<UnrefencedListener<RemoteData>> unrefListeners = new Vector<UnrefencedListener<RemoteData>>();
	// Reject calls. Activated in shutdown
	private boolean rejectCalls = false;

	/*
	 * Package visibility. Only factory implementations instantiate this class.
	 */
	RemoteData(DBAccess dbAccess) {
		if (dbAccess == null) {
			throw new IllegalArgumentException("dbAccess must be not null");
		}
		this.dbAccess = dbAccess;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public long createRecord(String[] data) throws DuplicateKeyException {
		if (!this.rejectCalls) {
			try {
				this.pendingThreads.add(Thread.currentThread());
				return this.dbAccess.createRecord(data);
			} finally {
				this.pendingThreads.remove(Thread.currentThread());
			}
		} else {
			throw new IllegalStateException(RJT_MSG);
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void deleteRecord(long recNo, long lockCookie)
			throws RecordNotFoundException, SecurityException {

		if (!this.rejectCalls) {
			try {
				this.pendingThreads.add(Thread.currentThread());
				this.dbAccess.deleteRecord(recNo, lockCookie);
			} finally {
				this.pendingThreads.remove(Thread.currentThread());
			}
		} else {
			throw new IllegalStateException(RJT_MSG);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public long[] findByCriteria(String[] criteria) {
		if (!this.rejectCalls) {
			try {
				this.pendingThreads.add(Thread.currentThread());
				return this.dbAccess.findByCriteria(criteria);
			} finally {
				this.pendingThreads.remove(Thread.currentThread());
			}
		} else {
			throw new IllegalStateException(RJT_MSG);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@SuppressWarnings("boxing")
	public long lockRecord(long recNo) throws RecordNotFoundException {
		if (!this.rejectCalls) {
			try {
				this.pendingThreads.add(Thread.currentThread());
				long cookie = this.dbAccess.lockRecord(recNo);
				this.heldLocks.put(recNo, cookie);
				return cookie;
			} finally {
				this.pendingThreads.remove(Thread.currentThread());
			}
		} else {
			throw new IllegalStateException(RJT_MSG);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public String[] readRecord(long recNo) throws RecordNotFoundException {
		if (!this.rejectCalls) {
			try {
				this.pendingThreads.add(Thread.currentThread());
				return this.dbAccess.readRecord(recNo);
			} finally {
				this.pendingThreads.remove(Thread.currentThread());
			}
		} else {
			throw new IllegalStateException(RJT_MSG);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@SuppressWarnings("boxing")
	public void unlock(long recNo, long cookie) throws SecurityException {
		if (!this.rejectCalls) {
			try {
				this.pendingThreads.add(Thread.currentThread());
				this.dbAccess.unlock(recNo, cookie);
				// Now this lock is not held by the client
				this.heldLocks.remove(recNo);
			} finally {
				this.pendingThreads.remove(Thread.currentThread());
			}
		} else {
			throw new IllegalStateException(RJT_MSG);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void updateRecord(long recNo, String[] data, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		if (!this.rejectCalls) {
			try {
				this.pendingThreads.add(Thread.currentThread());
				this.dbAccess.updateRecord(recNo, data, lockCookie);
			} finally {
				this.pendingThreads.remove(Thread.currentThread());
			}
		} else {
			throw new IllegalStateException(RJT_MSG);
		}
	}

	/**
	 * Returns reject calls state.
	 * 
	 * @return true if the instance is set to reject remote incoming calls. Used
	 *         to ensure a clean shutdown of the server.
	 */
	public boolean isRejectCalls() {
		return this.rejectCalls;
	}

	/**
	 * Sets the reject calls state. Set to true in server shutdown to avoid new
	 * executing threads.
	 * 
	 * @param rejectCalls
	 *            <code>true</code> to make the instance reject all remote
	 *            incoming calls.
	 */
	public void setRejectCalls(boolean rejectCalls) {
		this.rejectCalls = rejectCalls;
	}

	/**
	 * Returns all the threads that are currently executing on this instance.
	 * 
	 * @return the thread list.
	 */
	public List<Thread> getPendingThreads() {
		return this.pendingThreads;
	}

	/**
	 * Register a listener to notify when the instance is no longer referenced
	 * remotely.
	 * 
	 * @param listener
	 *            Listener object to forward unreferenced notification.
	 * @see Unreferenced
	 */
	public void addUnreferencedListener(UnrefencedListener<RemoteData> listener) {
		this.unrefListeners.add(listener);

	}

	/**
	 * This method is called by the RMI runtime when no remote clients hold a
	 * reference to this instance. Since instances of this class are not bound
	 * to the registry and served exclusively by the remote factory (see {$link
	 * RemoteDBAccessFactory}), one client can reference an instance, so this
	 * method acts as a listener for client lost of communication, and it is
	 * used to clean resources, like for example the locks held by the client.
	 */
	@SuppressWarnings("boxing")
	@Override
	public void unreferenced() {

		try {
			/*
			 * Possibly this method is called by the RMI runtime not before all
			 * pending threads have been returned, but the java.rmi.server.
			 * Unreferenced API is not clear about that, so the following code
			 * ensures all pending threads are interrupted.
			 */
			this.logger.log(Level.INFO,
					"Client reference ended. Releasing resources...");

			if (this.getPendingThreads().size() > 0) {
				this.logger.log(Level.INFO, "Client had "
						+ this.getPendingThreads().size() + " pendind"
						+ "threads. Interrupting...");

				for (Thread thread : this.getPendingThreads()) {
					thread.interrupt();
				}
			}

			if (this.heldLocks.size() > 0) {
				this.logger.log(Level.INFO, "Client owned "
						+ this.heldLocks.size() + " locks. " + "Releasing...");
			}
			Iterator<Long> recNoIt = this.heldLocks.keySet().iterator();
			while (recNoIt.hasNext()) {
				Long recNo = recNoIt.next();
				Long cookie = this.heldLocks.get(recNo);

				try {
					this.dbAccess.unlock(recNo, cookie);
				} catch (Exception e) {
					this.logger.log(Level.WARNING, "Error in automatic "
							+ "release of lock " + recNo, e);
				}
			}
		} finally {
			for (UnrefencedListener<RemoteData> listener : this.unrefListeners) {
				listener.notifyUnreference(this);
			}
		}

	}

}
