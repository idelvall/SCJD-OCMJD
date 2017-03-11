/*
 * DataAccessObject.java 27/09/2010
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

import suncertify.db.RecordNotFoundException;


/**
 * Provides a unified interface to a persistent mechanism, isolating the client
 * from the storage nature.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 27/09/2010
 * @see <a href="http://en.wikipedia.org/wiki/Data_access_object">Wikipedia DAO
 *      article</a>
 * 
 * @param <P>
 * 	 		Primary key type.
 * @param <R>
 *      	Record type.
 */
public interface DataAccessObject<P, R> {

	/**
	 * Updates the record identified by this pk.
	 * 
	 * @param pk
	 *            the record primary key.
	 * @param record
	 *            holds the values to update.
	 * @throws RecordNotFoundException if the record can no be found.
	 */
	public void update(P pk, R record) throws RecordNotFoundException;

	/**
	 * Deletes the record identified by this pk.
	 * 
	 * @param pk
	 *            the record primary key.
	 * @throws RecordNotFoundException
	 *             if the record does not exists.
	 */
	public void delete(P pk) throws RecordNotFoundException;

	/**
	 * Creates a new persistent record from the specified values
	 * 
	 * @param record
	 *            The record create.
	 * @return the primary key of the new record.
	 */
	public P insert(R record);

	/**
	 * Returns the record identified by the pk
	 * 
	 * @param pk
	 *            the record primary key.
	 * @return the record.
	 * @throws RecordNotFoundException if the record does not exists.
	 */
	public R findByPrimaryKey(P pk) throws RecordNotFoundException;

	/**
	 * Returns all the records.
	 * 
	 * @return an array of all the primary keys.
	 */
	public P[] findAll();

}
