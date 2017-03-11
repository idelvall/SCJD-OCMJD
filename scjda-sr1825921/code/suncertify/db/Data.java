/*
 * Data.java 27/09/2010
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


package suncertify.db;

import static suncertify.commons.Miscellaneous.*;
import java.util.logging.Level;
import suncertify.commons.LoggingObject;
import suncertify.db.spec.DataAccessObject;
import suncertify.db.spec.FinderObject;
import suncertify.db.spec.LockManager;


/**
 * Sun's mandatory {@link DBAccess} implementation.<br><br>
 * {@link DBAccess} has three main independent concerns: data access, locking, and finder algorithm.<br>
 * Instances of this class orchestrate these concern implementations specified at creation time. 
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 27/09/2010 
 * 
 */
public final class Data extends LoggingObject implements DBAccess {

	protected final DataAccessObject<Long, String[]> dao;
	protected final FinderObject<Long, String[]> finder;
	protected final LockManager<Long, Long> lockManager;

	/**
	 * Creates a new instance from the specified parameters.
	 * 
	 * @param dao data access object to interact with the database.
	 * @param finder finder object to delegate {@link #findByCriteria(String[])} calls .
	 * @param lockManager to delegate locking methods.
	 * @throws IllegalArgumentException if dao or finder are null.
	 */
	public Data(DataAccessObject<Long, String[]> dao,
			FinderObject<Long, String[]> finder,
			LockManager<Long, Long> lockManager) {

		if(dao == null || finder==null){
			throw new IllegalArgumentException("dao and finder parameters must be not null");
		}
		
		this.dao = dao;
		this.finder = finder;
		this.lockManager = lockManager;
	}
	
	/**
	 * Creates a new instance from the specified parameters. Locking is bypassed.
	 * 
	 * @param dao data access object to interact with the database.
	 * @param finder finder object to delegate {@link #findByCriteria(String[])} calls. 
	 * @throws IllegalArgumentException if dao or finder are <code>null</code>.
	 */
	public Data(DataAccessObject<Long, String[]> dao,
			FinderObject<Long, String[]> finder) {

		this(dao,finder,null);
	}

	/**
	 * Reads a record.
	 * 
	 * @param recNo record number, returned by {@link #findByCriteria(String[])}.
	 * @return an array where each element is a field value.
	 * @throws RecordNotFoundException if the record does not exist.
	 * @see DBAccess#readRecord(long)
	 */
	@SuppressWarnings("boxing")
	public String[] readRecord(long recNo) throws RecordNotFoundException {
		this.logger.log(Level.CONFIG, "Called readRecord("+recNo+")");
		String[] ret = this.dao.findByPrimaryKey(recNo);
		this.logger.log(Level.CONFIG, "Returned {" + arrayToString(ret)+"}");
		return ret;
	}

	/**
	 * Modifies the fields of a record. The new value for field n appears in
	 * data[n].<br>
	 * First validates the cookie and then delegates the update to referenced
	 * DAO.
	 * 
	 * @param recNo
	 *            the record number.
	 * @param data
	 *            the record values.
	 * @param lockCookie
	 *            the cookie locking value.
	 * @throws RecordNotFoundException if the record does not exist.
	 * @throws SecurityException
	 *             if lockCookie is not the record locking cookie.
	 * @see DBAccess#updateRecord(long, String[], long)
	 * @see #lockRecord(long)
	 */
	@SuppressWarnings("boxing")
	public void updateRecord(long recNo, String[] data, long lockCookie)
			throws RecordNotFoundException, SecurityException {

		this.logger.log(Level.CONFIG, "Called updateRecord("+recNo+",{" + arrayToString(data)+"},"+lockCookie+")");
		/*
		 * Verify locking. -> SecurityException
		 */
		this.validateCookie(recNo, lockCookie);

		/*
		 * Perform the modification
		 */
		this.dao.update(recNo, data);
		this.logger.log(Level.CONFIG, "updateRecord() ended sucesfully");
	}

	/**
	 * Deletes a record, making the record number and associated disk storage
	 * available for reuse.
	 * 
	 * @param recNo
	 *            the record number.
	 * @param lockCookie
	 *            the cookie locking value.
	 * @throws RecordNotFoundException if the record does not exist.
	 * @throws SecurityException
	 *             if lockCookie is not the record locking cookie.
	 * @see DBAccess#deleteRecord(long, long)
	 * @see #lockRecord(long)
	 */
	@SuppressWarnings("boxing")
	public void deleteRecord(long recNo, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		
		this.logger.log(Level.CONFIG, "Called deleteRecord("+recNo+","+lockCookie+")");
		/*
		 * Verify record exists
		 */
		readRecord(recNo);

		/*
		 * Verify locking. -> SecurityException
		 */
		validateCookie(recNo, lockCookie);

		/*
		 * Perform the modification
		 */
		this.dao.delete(recNo);
		
		this.logger.log(Level.CONFIG, "deleteRecord() ended sucesfully");
	}

	/**
	 * Returns an array of record numbers that match the specified criteria. The
	 * implementation is delegated to the specified in construction
	 * {@link suncertify.db.spec.FinderObject} implementation.
	 * 
	 * @param criteria the filter object.
	 * @return the array of record numbers.
	 * @see DBAccess#findByCriteria(String[])
	 */
	@SuppressWarnings("boxing")
	public long[] findByCriteria(String[] criteria) {
		
		this.logger.log(Level.CONFIG, "Called findByCriteria({" + arrayToString(criteria)+"})");
		
		Long[] pks = this.finder.findByCriteria(criteria);
		long[] ret = new long[pks.length];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = pks[i];
		}
		this.logger.log(Level.CONFIG, "Returned {" + longArraytoString(ret)+"}");
		return ret;
	}

	/**
	 * Creates a new record in the database (possibly reusing a deleted entry).
	 * 
	 * @param data
	 *            the given data.
	 * @return the record number of the new record.
	 * @see DBAccess#createRecord(String[])
	 */
	@SuppressWarnings("boxing")
	public long createRecord(String[] data) {
		this.logger.log(Level.CONFIG, "Called createRecord({" + arrayToString(data)+"})");
		long ret = this.dao.insert(data);
		this.logger.log(Level.CONFIG, "Returned " + ret);
		return ret;

	}

	/**
	 * Locks a record so that it can only be updated or deleted by this client.
	 * Returned value is a cookie that must be used when the record is unlocked,
	 * updated, or deleted. If the specified record is already locked by a
	 * different client, the current thread gives up the CPU and consumes no CPU
	 * cycles until the record is unlocked.
	 * 
	 * @param recNo
	 *            the record number.
	 * @return a cookie value.
	 * @throws RecordNotFoundException if the record does not exist.
	 * @see DBAccess#lockRecord(long)
	 */
	@SuppressWarnings("boxing")
	public long lockRecord(long recNo) throws RecordNotFoundException {
		this.logger.log(Level.CONFIG, "Called lockRecord(" + recNo+")");
		/*
		 * Record existence pre-verification
		 */
		readRecord(recNo);
		try {
			long ret = 0;
			if (this.lockManager != null) {
				ret = this.lockManager.lock(recNo);

				/*
				 * Record existence post-verification. (After lock acquired)
				 */
				try {
					readRecord(recNo);
				} catch (RecordNotFoundException e) {
					this.lockManager.unlock(recNo, ret);
					throw new RecordNotFoundException(
							"Error locking record number " + recNo
									+ ". This record has been deleted");
				}

			}
			this.logger.log(Level.CONFIG, "Returned " + ret);
			return ret;
			
			/*
			 * The following exceptions should never be thrown.
			 */
		} catch (SecurityException e) {
			this.logger.log(Level.SEVERE,
					"This exception should had never been thrown", e);
			throw new Error(e);
		}
	}

	/**
	 * Releases the lock on a record. Cookie must be the cookie returned when
	 * the record was locked; otherwise throws SecurityException.
	 * 
	 * @param recNo
	 *            the record number.
	 * @param cookie value returned by lock().
	 * @throws SecurityException if the cookie is not valid.
	 * @see #lockRecord(long)
	 * @see DBAccess#unlock(long, long)
	 */
	@SuppressWarnings("boxing")
	public void unlock(long recNo, long cookie) throws SecurityException {

		this.logger.log(Level.CONFIG, "Called unlock(" + recNo+","+cookie+")");
		
		if (this.lockManager != null) {
			this.lockManager.unlock(recNo, cookie);
		} else {
			this.logger.log(Level.WARNING, "Locking is disabled!");
		}
		
		this.logger.log(Level.CONFIG, "deleteRecord() ended sucesfully");
	}

	/*
	 * Delegates cookie validation to the this.lockManager, if this is not null
	 */
	@SuppressWarnings("boxing")
	private void validateCookie(long recNo, long lockCookie)
			throws SecurityException {
		if (this.lockManager != null
				&& !this.lockManager.isValidCookie(recNo, lockCookie)) {
			throw new SecurityException("Cookie verification failed");
		}
	}
}
