/*
 * LockManager.java 27/09/2010
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


package suncertify.db.spec;

import suncertify.db.SecurityException;


/**
 * The <code>LockManager</code> provides unified management for a keyed
 * collection of locks.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 27/09/2010
 * 
 * @param <K>
 *            Key type
 * @param <C>
 *            Cookie type
 */
public interface LockManager<K, C> {

	/**
	 * Acquires the lock corresponding to the specified key and returns a cookie
	 * that must be used in order to release the lock. If the resolving lock is
	 * already owned, the current thread gives up the CPU and consumes no CPU
	 * cycles until the lock is released.
	 * 
	 * @param lockKey
	 *            the locking key identifying the lock.
	 * @return the cookie needed to perform the unlocking.
	 * @see #unlock(Object, Object) unlock(K lockKey, C cookie)
	 */
	public C lock(K lockKey);

	/**
	 * Releases the corresponding lock. <br>
	 * 
	 * @param lockKey
	 *            the locking key identifying the lock.
	 * @param cookie
	 *            the value returned in lock acquisition.
	 * @throws SecurityException
	 *             if the cookie supplied does not match the value returned at
	 *             locking time.
	 * @see #lock(Object) lock(K lockKey)
	 */
	public void unlock(K lockKey, C cookie) throws SecurityException;

	/**
	 * Tests if the cookie is valid for that key.
	 * 
	 * @param lockKey
	 *            the locking key identifying the lock.
	 * @param cookie
	 *            the cookie value to validate.
	 * @return <code>true</code> if the entity was locked and returned that
	 *         cookie. Otherwise returns <code>false</code>.
	 */
	public boolean isValidCookie(K lockKey, C cookie);

}
