/*
 * DefaultLockManagerImpl.java 27/09/2010
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


package suncertify.db.impl;


/**
 * Concrete subclass of {@link BaseLockManagerImpl} that uses random Long cookies.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 28/09/2010 
 * @param <K> key class
 */
public class DefaultLockManagerImpl<K> extends BaseLockManagerImpl<K,Long>{
	
	@SuppressWarnings("boxing") 
	@Override
	 /**
	  * {@inheritDoc} 
	  */
	protected Long createCookie() {
		return Math.round((Math.random() * Long.MAX_VALUE));
	}
}
