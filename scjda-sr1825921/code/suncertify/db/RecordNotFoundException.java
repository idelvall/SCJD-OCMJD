/*
 * RecordNotFoundException.java 27/09/2010
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
 * The Exception thrown when specified record cannot be found.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 28/09/2010
 *
 */
public class RecordNotFoundException extends Exception{
	
	static final long serialVersionUID = -7422579023953826974L;	
	
	/**
	 * Constructs a new <code>RecordNotFoundException</code>.
	 */
	public RecordNotFoundException(){
		super();
	}
	
	/**
	 * Constructs a new <code>RecordNotFoundException</code> with the specified message.
	 * 
	 * @param message the message String.
	 */
	public RecordNotFoundException(String message){
		super(message);
	}
	
	/**
	 * Constructs a new <code>RecordNotFoundException</code> from the specified 
	 * <code>Throwable</code> instance.
	 * 
	 * @param thr the throwable instance.
	 */
	public RecordNotFoundException(Throwable thr){
		super(thr);
	}

}
