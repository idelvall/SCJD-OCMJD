/*
 * DBAccess.java 27/09/2010
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


/**
 * The Sun's provided interface. Declares data-level functionality.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 28/09/2010
 *
 */
public interface DBAccess {
	
	/**
	 * Reads a record.
	 * 
	 * @param recNo the record identifying number.
	 * @return an array where each element is a record value.
	 * @throws RecordNotFoundException if the record does not exist.
	 */	
	public String [] readRecord(long recNo)
			throws RecordNotFoundException;

	/**
	 * Modifies the fields of a record. The new value for field n appears in data[n].
	 * 
	 * @param recNo the record number.
	 * @param data the record values.
	 * @param lockCookie the cookie value.
	 * @throws RecordNotFoundException if the record does not exist.
	 * @throws SecurityException if the record is locked with a cookie other than 
	 *         lockCookie.
	 */
  	public void updateRecord(long recNo, String[] data, long lockCookie)
  			throws RecordNotFoundException, SecurityException;


	/**
	 * Deletes a record, making the record number and associated disk storage available
     * for reuse. 
     * 
	 * @param recNo the record number.
	 * @param lockCookie the cookie value.
	 * @throws RecordNotFoundException if the record does not exist.
	 * @throws SecurityException if the record is locked with a cookie other than 
     *         lockCookie.
	 */
  	public void deleteRecord(long recNo, long lockCookie)
  			throws RecordNotFoundException, SecurityException;

	/**
	 * Returns an array of record numbers that match the specified criteria. Field n in
     * the database file is described by criteria[n]. A null value in criteria[n] 
     * matches any field value. A non-null  value in criteria[n] matches any field value
     * that begins with criteria[n]. (For example, "Fred" matches "Fred" or "Freddy").
     * 
	 * @param criteria the filter criteria array.
	 * @return the array of record numbers.
	 */
  	public long[] findByCriteria(String[] criteria);

	/**
	 * Creates a new record in the database (possibly reusing a deleted entry).
	 * 
	 * @param data the given data.
	 * @return the record number of the new record.
	 * @throws DuplicateKeyException if already exists a record with the same key.
	 */
  	public long createRecord(String [] data)
  			throws DuplicateKeyException;

	/**
	 * Locks a record so that it can only be updated or deleted by this client.
	 * Returned value is a cookie that must be used when the record is unlocked,
	 * updated, or deleted. If the specified record is already locked by a different
	 * client, the current thread gives up the CPU and consumes no CPU cycles until the 
	 * record is unlocked.
	 * 
	 * @param recNo the record number.
	 * @return a cookie value. A long that demonstrate the lock ownership.
	 * @throws RecordNotFoundException if the record does not exist.
	 */
  	public long lockRecord(long recNo)
  			throws RecordNotFoundException;

	/**
	 * Releases the lock on a record. Cookie must be the cookie returned when the record 
	 * was locked; otherwise throws SecurityException.
	 * 
	 * @param recNo the record number.
	 * @param cookie the result of <code>lockRecord(recNo)</code> execution.
	 * @throws SecurityException if the cookie is not valid.
	 */
  	public void unlock(long recNo, long cookie)
  			throws SecurityException;
}
