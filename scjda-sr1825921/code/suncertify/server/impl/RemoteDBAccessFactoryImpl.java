/*
 * RemoteDataFactory.java 30/09/2010
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

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import suncertify.commons.LoggingObject;
import suncertify.db.DBAccess;
import suncertify.server.spec.RemoteDBAccess;
import suncertify.server.spec.RemoteDBAccessFactory;
import suncertify.server.spec.UnrefencedListener;

/**
 * Remote RMI Factory implementation: Instances of this class are bound to the RMI registry
 * and used remotely to obtain references to other remote objects created on demand.<br>
 * This class is used to create instances of {@link RemoteDBAccess}.<br>
 * <p>
 * The main purpose of using a RMI factory, creating <code>RemoteDBAccess</code> instances
 * on demand (usually one per client) instead of publishing a single instance of 
 * <code>RemoteDBAccess</code> in the registry and share it among all clients is to know when 
 * a client session terminates and so being able to release resources allocated by the client.   
 * </p>
 * A factory object keeps track of all active instances created by it, those instances are referred
 * later in the documentation as <i>managed instances</i>.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 30/09/2010
 * @see <a href="http://download.oracle.com/javase/6/docs/technotes/guides/rmi/Factory.html">
 * Applying the Factory Pattern to JavaTM RMI</a>
 * 
 */
public class RemoteDBAccessFactoryImpl extends LoggingObject implements
		RemoteDBAccessFactory, UnrefencedListener<RemoteData> {

	private static final String RJT_MSG = "Factory is marked to reject incoming calls";
	
	private final DBAccess dbAccess;
	private final int port;
	
	// Reject calls. Activated in shutdown
	private boolean rejectCalls = false;
	


	/*
	 * List of managed instances, created on factory method call and discarded
	 * on unreference notification
	 */
	private List<RemoteData> managedInstances = new Vector<RemoteData>();

	/**
	 * Creates a new factory from the specified arguments.
	 * 
	 * @param dbAccess
	 *            Database access instance that will be used by all the newly
	 *            created managed instances.
	 * @param port
	 *            Port where the newly created RemoteDBAccess instances will be
	 *            exported (making them available to receive remote calls).
	 */
	public RemoteDBAccessFactoryImpl(DBAccess dbAccess, int port) {
		this.dbAccess = dbAccess;
		this.port = port;
	}
	/**
	 * Creates and returns a new instance of {@link RemoteData}, exporting it to the RMI runtime in the
	 * port specified at constructor.
	 * 
	 * @return the newly created instance.
	 * @throws RemoteException if an communication-related error occurs.
	 */
	@Override
	public synchronized RemoteDBAccess createRemoteDBAccess() throws RemoteException {
		if (!this.rejectCalls) {
			this.logger.log(Level.INFO, "New client request. "
					+ "Creating new RemoteDBAccess instance");
			RemoteData data = new RemoteData(this.dbAccess);
			data.addUnreferencedListener(this);
			UnicastRemoteObject.exportObject(data, this.port);
			this.managedInstances.add(data);
			this.logNumberOfClients();
			return data;
		} else {
			throw new IllegalStateException(RJT_MSG);
		}
	}

	/**
	 * Sets factory to reject incoming calls. All managed instances that has
	 * been created by the factory are set to make the rejection.
	 */
	public synchronized void rejectIncomingCalls() {

		this.rejectCalls = true;
		this.logger.log(Level.INFO, "Setting all managed instances to "
				+ " reject incoming calls");
		for (RemoteData remoteData : this.managedInstances) {
			remoteData.setRejectCalls(true);
		}
	}

	/**
	 * Makes all managed instances to interrupt all their threads currently
	 * executing.
	 */
	public synchronized void interruptPendingThreads() {
		this.logger.log(Level.INFO,
				"Interrupting pending threads of all managed " + "instances");
		for (RemoteData remoteData : this.managedInstances) {
			for (Thread thread : remoteData.getPendingThreads()) {
				thread.interrupt();
			}
		}
	}

	/**
	 * Managed instances call this method to communicate its unreferenced state.
	 * When a managed instance is unreferenced (has lost its client
	 * reference), it is no longer needed and the factory discards it.
	 * 
	 * @see UnrefencedListener#notifyUnreference(java.rmi.server.Unreferenced)
	 */
	public synchronized void notifyUnreference(RemoteData remoteData) {
		this.logger.log(Level.INFO,
				"Unreference notified. Updating reference list");
		/*
		 * Discard managed instance
		 */
		this.managedInstances.remove(remoteData);
		this.logNumberOfClients();

		/*
		 * This is a good moment to request garbage collection
		 */
		System.gc();
	}

	/*
	 * Helper method to register in log the number of active references
	 */
	private void logNumberOfClients() {
		this.logger.log(Level.INFO, "Number of active clients: "
				+ this.managedInstances.size());
	}
}
