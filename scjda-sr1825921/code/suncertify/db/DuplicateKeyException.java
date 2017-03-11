/*
 * DuplicateKeyException.java 27/09/2010
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
 * Thrown when creating a new data record with a duplicate primary key.
 * @author Ignacio del Valle Alles
 * @version 1.0 28/09/2010
 *
 */
public class DuplicateKeyException extends Exception {
	
	static final long serialVersionUID = -3932983401004932941L;
	
	/**
	 * Constructs a new <code>DuplicateKeyException</code>.
	 */
	public DuplicateKeyException(){
		super();
	}
	
	/**
	 * Constructs a new <code>DuplicateKeyException</code> with the specified message.
	 * 
	 * @param message the message String.
	 */
	public DuplicateKeyException(String message){
		super(message);
	}
	
	/**
	 * Constructs a new <code>DuplicateKeyException</code> from the specified 
	 * <code>Throwable</code> instance.
	 * 
	 * @param thr the throwable instance.
	 */
	public DuplicateKeyException(Throwable thr){
		super(thr);
	}
}
