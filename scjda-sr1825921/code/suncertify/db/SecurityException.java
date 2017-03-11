/*
 * SecurityException.java 27/09/2010
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
 * The Exception thrown by those methods that need a cookie, when  
 * the supplied value is incorrect.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 28/09/2010
 *
 */
public class SecurityException extends Exception {
	
	static final long serialVersionUID = -3932983401004932941L;
	
	/**
	 * Constructs a new <code>SecurityException</code>.
	 */
	public SecurityException(){
		super();
	}
	
	/**
	 * Constructs a new <code>SecurityException</code> with the specified message.
	 * 
	 * @param message the message String.
	 */
	public SecurityException(String message){
		super(message);
	}
	
	/**
	 * Constructs a new <code>SecurityException</code> from the specified 
	 * <code>Throwable</code> instance.
	 * 
	 * @param thr the throwable instance.
	 */
	public SecurityException(Throwable thr){
		super(thr);
	}
}
