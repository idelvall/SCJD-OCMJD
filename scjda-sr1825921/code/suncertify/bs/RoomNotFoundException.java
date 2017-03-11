/*
 * RoomNotFoundException.java 06/10/2010
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
 * The exception thrown in the business logic layer to indicate that no rooms
 * cannot be found for the specified search criteria or the referenced room 
 * does not exist.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 06/10/2010
 * 
 */
public class RoomNotFoundException extends Exception {

	static final long serialVersionUID = -7422579023953826974L;

	/**
	 * Constructs a new <code>RoomNotFoundException</code>.
	 */
	public RoomNotFoundException() {
		super();
	}

	/**
	 * Constructs a new <code>RoomNotFoundException</code> with the specified
	 * message.
	 * 
	 * @param message
	 *            the message String.
	 */
	public RoomNotFoundException(String message) {
		super(message);
	}

	/**
	 * Constructs a new <code>RoomNotFoundException</code> from the specified
	 * <code>Throwable</code> instance.
	 * 
	 * @param thr
	 *            the throwable instance.
	 */
	public RoomNotFoundException(Throwable thr) {
		super(thr);
	}

}
