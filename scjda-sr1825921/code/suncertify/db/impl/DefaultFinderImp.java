/*
 * DefaultFinderImp.java 27/09/2010
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

import java.util.Vector;
import suncertify.commons.LoggingObject;
import suncertify.db.RecordNotFoundException;
import suncertify.db.spec.DataAccessObject;
import suncertify.db.spec.FinderObject;


/**
 * Default {@link FinderObject} implementation. 
 *  
 * @author Ignacio del Valle Alles
 */
public final class DefaultFinderImp extends LoggingObject implements FinderObject<Long, String[]> {
	
	private final DataAccessObject<Long, String[]> dao;
	
	
	/**
	 * Creates a new instance from the specified dao.
	 * 
	 * @param dao the data access object used to interact with the database.
	 * @throws IllegalArgumentException if dao is <code>null</code>
	 */
	public DefaultFinderImp(DataAccessObject<Long, String[]> dao){
		if(dao==null){
			throw new IllegalArgumentException("dao must be not null");
		}
		this.dao = dao;
	}

	/**
	 * Returns an array of record numbers that match the specified criteria. Field n in the 
	 * database file is described by criteria[n]. A null value in criteria[n] matches any field
	 * value. A non-null  value in criteria[n] matches any field value that begins with criteria[n]. 
	 * (For example, "Fred" matches "Fred" or "Freddy".)<br>
     * A simple iterative algorithm is used.
     *  
	 * @param criteria filter values.
	 * @return the array of record numbers.
	 */
	public Long[] findByCriteria(String[] criteria) {
		
		if(criteria == null || criteria.length == 0){
			return null;
		}
		
		// Used to store matching record numbers
        Vector <Long>vals = new Vector<Long>();
		
        /*
         * For each record in the datasource:
         */
        Long[] pks = this.dao.findAll();
		loopRecords: 			
		for (int i = 0; i < pks.length; i++) {
			Long pk = pks[i];
			try {
				String[] datos = this.dao.findByPrimaryKey(pk);
				
				/*
				 * For each field in the specified criteria array:
				 */
				for (int j = 0; (j < criteria.length && j < datos.length); j++) {
					/*
				     * If criteria is not null and does not match, escape to the next record 
					 */
				    if(criteria[j] != null && !this.matches(criteria[j],datos[j])){
				        continue loopRecords;                
				    }
				}
				/*
				 * If executions is here then this is a matching record, so 
				 * we store the record pk in the collection:
				 */
				vals.add(pk);
			/*
			 * Record could be deleted concurrently.
			 */
			} catch (RecordNotFoundException e) {
				this.logger.info("Record deleted between findAll() and findByPrimaryKey(): " + e.getMessage());
			}
		}
        /*
         * Construct the output array from the collection
         */
        
        Long[] ret = new Long[vals.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = vals.get(i);
		}

		return ret;
	}

	/*
     * A non-null value in criteria matches any objectString that begins with criteria value
     */
    private boolean matches(String criteria, String str){
        return str.regionMatches(false,0,criteria,0,criteria.length());
    }
}
