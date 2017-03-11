/*
 * RuntimeIOException.java 27/09/2010
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


/**
 * An unchecked IO exception. 
 * 
 * @author Ignacio del Valle Alles.
 */
public class RuntimeIOException extends RuntimeException {
	
    static final long serialVersionUID = -6915523005722621695L;
    
	/**
	 * Creates a new <code>RuntimeIOException</code>.
	 */
	public RuntimeIOException(){
		super();
	}
	
	/**
	 * Creates a new <code>RuntimeIOException</code> with the specified message.
	 * 
	 * @param message the message String
	 */
	public RuntimeIOException(String message){
		super(message);
	}
	
	/**
	 * Creates a new <code>RuntimeIOException</code> from the specified 
	 * <code>Throwable</code> instance.
	 * 
	 * @param thr the throwable instance.
	 */
	public RuntimeIOException(Throwable thr){
		super(thr);
	}
}
