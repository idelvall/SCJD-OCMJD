/*
 * AlreadyBookedException.java 06/10/2010
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


package suncertify.bs;


/**
 * Thrown when trying to book a booked room.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 06/10/2010
 *
 */
public class AlreadyBookedException extends Exception {
	
	static final long serialVersionUID = -3932983401004932941L;
	
	/**
	 * Constructs a new <code>AlreadyBookedException</code>.
	 */
	public AlreadyBookedException(){
		super();
	}
	
	/**
	 * Constructs a new <code>AlreadyBookedException</code> with the specified message.
	 * 
	 * @param message the message String.
	 */
	public AlreadyBookedException(String message){
		super(message);
	}
	
	/**
	 * Constructs a new <code>AlreadyBookedException</code> from the specified 
	 * <code>Throwable</code> instance.
	 * 
	 * @param thr the throwable instance.
	 */
	public AlreadyBookedException(Throwable thr){
		super(thr);
	}
}
