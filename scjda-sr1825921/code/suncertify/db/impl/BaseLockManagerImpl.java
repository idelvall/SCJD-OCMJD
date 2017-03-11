/*
 * BaseLockManagerImpl.java 27/09/2010
 * 
 * Candidate: Ignacio del Valle Alles
 * Candidate ID: SR1825921
 * 
 * Sun Certified Developer for Java 2 Platform, Standard Edition Programming
 * Assignment (CX-310-252A)
 * 
 * This class is part of the Programming Assignment of the Sun Certified
 * Developer for Java 2 Platform, Standard Edition certification program, must
 * not be used out of this context and must be used exclusively by Oracle 
 * Corporation.
 */

package suncertify.db.impl;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import suncertify.commons.LoggingObject;
import suncertify.db.SecurityException;
import suncertify.db.spec.LockManager;

/**
 * Abstract implementation of {@link LockManager}.<br>
 * This implementation makes use of the {@link java.util.concurrent.locks}
 * package instead of lower level language semantics.<br>
 * Instances of this class maintain an inner map of fair {@link ReentrantLock}
 * instances (this {@link Lock} implementation can be changed overriding
 * <code>protected Lock createNewLock()</code> method).
 * <p>
 * When a call to <code>lock(key)</code> is performed two things may happen:
 * <ol>
 * <li>If the map does not hold a Lock for that key, a new Lock is created, and
 * added to the map under the specified key, also a single
 * {@link java.util.concurrent.locks.Condition} per Lock is created.</li>
 * <li>If the map does hold a Lock for key, that is the lock to use.</li>
 * </ol>
 * Then in both cases the lock is entered and condition awaited. When the
 * condition is passed a cookie is generated, binded to lock and returned to the
 * client. This cookie must be used in order to perform the release of the lock
 * (see {@link #unlock(Object, Object) unlock(K lockKey, C cookie)}) <br>
 * A call to <code>unlock()</code> validates the key-cookie pair, finds the lock
 * by the specified key, enters the lock, and signals the condition, resuming
 * the next waiting thread.<br>
 * <br>
 * Concrete subclasses must implement the method <code>C createCookie()</code>
 * in order to specify how cookies are created.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 28/09/2010
 * 
 * @param <K>
 *            key class
 * @param <C>
 *            cookie class
 */
public abstract class BaseLockManagerImpl<K, C> extends LoggingObject implements
		LockManager<K, C> {

	private final Map<K, LockingElement<C>> lockMap = new HashMap<K, LockingElement<C>>();

	@Override
	/**
	 * {@inheritDoc}
	 */
	public final boolean isValidCookie(K lockKey, C cookie) {
		LockingElement<C> lockingElement = this.lockMap.get(lockKey);
		if (lockingElement != null && lockingElement.getCookie().equals(cookie)) {
			return true;
		}
		return false;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public final C lock(K lockKey) {
		this.logger
				.fine("Entered lock method. (threadId="
						+ Thread.currentThread().getId() + ", lockKey="
						+ lockKey + ")");
		LockingElement<C> lockingElement = getLockingElement(lockKey);
		this.logger.finest("Incrementing reference counter. (thread"
				+ Thread.currentThread().getId() + ", lockKey=" + lockKey
				+ ", counter=" + lockingElement.getReferenceCounter() + ")");
		lockingElement.incrementReferenceCounter();
		Lock l = lockingElement.getLock();
		Condition condition = lockingElement.getCondition();
		l.lock();
		this.logger
				.finest("Entered lock synchronization block method. (threadId="
						+ Thread.currentThread().getId() + ", lockKey="
						+ lockKey + ", counter="
						+ lockingElement.getReferenceCounter() + ")");
		try {
			while (lockingElement.getCookie() != null) {
				this.logger
						.finest("Cookie is not null for this lock. Awaiting on condition. (thread"
								+ Thread.currentThread().getId()
								+ ", lockKey="
								+ lockKey
								+ ", counter="
								+ lockingElement.getReferenceCounter() + ")");
				try {
					condition.await();
				} catch (InterruptedException e) {
					this.logger.info("Awaiting thread interrupted. (threadId="
							+ Thread.currentThread().getId() + ", lockKey="
							+ lockKey + ", counter="
							+ lockingElement.getReferenceCounter() + ")");
					lockingElement.decrementReferenceCounter();
					this.logger.finest("Decremented reference counter. (thread"
							+ Thread.currentThread().getId() + ", lockKey="
							+ lockKey + ", counter="
							+ lockingElement.getReferenceCounter() + ")");
					throw new RuntimeException(e);
				}
			}
			this.logger
					.finest("Lock adquired. Setting cookie. (threadId="
							+ Thread.currentThread().getId() + ", lockKey="
							+ lockKey + ", counter="
							+ lockingElement.getReferenceCounter() + ")");
			lockingElement.setCookie(createCookie());
		} finally {
			this.logger
					.finest("Exiting lock synchronization block method. (threadId="
							+ Thread.currentThread().getId()
							+ ", lockKey="
							+ lockKey
							+ ", counter="
							+ lockingElement.getReferenceCounter() + ")");
			l.unlock();
		}
		C cookie = lockingElement.getCookie();
		this.logger.fine("Exiting lock method. Returning cookie=" + cookie
				+ ". (threadId=" + Thread.currentThread().getId()
				+ ", lockKey=" + lockKey + ", counter="
				+ lockingElement.getReferenceCounter() + ")");
		return cookie;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public final void unlock(K lockKey, C cookie) throws SecurityException {

		this.logger
				.fine("Entering unlock method. (threadId="
						+ Thread.currentThread().getId() + ", lockKey="
						+ lockKey + ")");
		if (!isValidCookie(lockKey, cookie)) {
			String msg = "Illegal unlock. There is no lock with key = "
					+ lockKey + " and cookie = " + cookie;
			this.logger.warning(msg);
			throw new SecurityException(msg);
		}

		LockingElement<C> lockingElement = getLockingElement(lockKey);
		Lock l = lockingElement.getLock();
		Condition condition = lockingElement.getCondition();

		l.lock();
		this.logger
				.finest("Entered lock synchronization block method. (threadId="
						+ Thread.currentThread().getId() + ", lockKey="
						+ lockKey + ", counter="
						+ lockingElement.getReferenceCounter() + ")");
		try {
			lockingElement.setCookie(null);
			this.logger
					.finest("Decrementing reference counter. (thread"
							+ Thread.currentThread().getId() + ", lockKey="
							+ lockKey + ", counter="
							+ lockingElement.getReferenceCounter() + ")");
			lockingElement.decrementReferenceCounter();
			tryLockingElementRemoval(lockKey);
			this.logger
					.finest("Notify other thread. (threadId="
							+ Thread.currentThread().getId() + ", lockKey="
							+ lockKey + ", counter="
							+ lockingElement.getReferenceCounter() + ")");
			condition.signal();
		} finally {
			this.logger
					.fine("Exiting unlock method. (threadId="
							+ Thread.currentThread().getId() + ", lockKey="
							+ lockKey + ", counter="
							+ lockingElement.getReferenceCounter() + ")");
			l.unlock();
		}

	}

	/*
	 * Getter with lazy initialization
	 */
	private synchronized LockingElement<C> getLockingElement(K lockKey) {
		LockingElement<C> lockingElement = this.lockMap.get(lockKey);
		if (lockingElement == null) {
			lockingElement = new LockingElement<C>();
			lockingElement.setLock(createNewLock());
			lockingElement
					.setCondition(lockingElement.getLock().newCondition());
			this.lockMap.put(lockKey, lockingElement);
		}
		return lockingElement;
	}

	/*
	 * Removes the LockingElement from the internal map if it is not longer
	 * needed
	 */
	private synchronized void tryLockingElementRemoval(K lockKey) {
		LockingElement<C> lockingElement = this.lockMap.get(lockKey);
		if (lockingElement != null && lockingElement.getReferenceCounter() == 0) {
			this.logger
					.fine("Removing unused locking element from cache. (thread"
							+ Thread.currentThread().getId() + ", lockKey="
							+ lockKey + ", counter="
							+ lockingElement.getReferenceCounter() + ")");
			this.lockMap.remove(lockKey);
			System.gc();
		}
	}

	/*
	 * Extension points
	 */
	protected abstract C createCookie();

	protected Lock createNewLock() {
		Lock ret = new ReentrantLock(true);
		return ret;
	}

	/**
	 * This class is used to bind together the cookie, the lock and the
	 * condition. Also provides an internal counter to be managed by its
	 * clients.
	 * 
	 * @author Ignacio del Valle Alles
	 * @version 1.0 16/10/2010
	 * 
	 * @param <T>
	 *            The cookie class
	 */
	final class LockingElement<T> {

		private T cookie;
		private Lock lock;
		private Condition condition;
		private long referenceCounter;

		T getCookie() {
			return this.cookie;
		}

		void setCookie(T cookie) {
			this.cookie = cookie;
		}

		Lock getLock() {
			return this.lock;
		}

		void setLock(Lock lock) {
			this.lock = lock;
		}

		Condition getCondition() {
			return this.condition;
		}

		void setCondition(Condition condition) {
			this.condition = condition;
		}

		synchronized void incrementReferenceCounter() {
			this.referenceCounter++;
		}

		synchronized void decrementReferenceCounter() {
			this.referenceCounter--;
		}

		long getReferenceCounter() {
			return this.referenceCounter;
		}
	}
}