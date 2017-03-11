/*
 * FinderObject.java 27/09/2010
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


/**
 * Declares advanced finder methods.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 27/09/2010
 * 
 * @param <P>
 *            Primary key type
 * @param <R>
 *            Record type
 */
public interface FinderObject<P, R> {

	/**
	 * Returns the primary keys of those records that match the specified
	 * criteria.
	 * 
	 * @param criteria
	 *            criteria record object.
	 * @return an array of primary keys.
	 */
	public P[] findByCriteria(R criteria);

}
