/*
 * ServerStartException.java 27/09/2010
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


package suncertify.server;


/**
 * The Exception thrown when a {@link Server} instance can not be started.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 28/09/2010
 *
 */
public class ServerStartException extends Exception{
	
	static final long serialVersionUID = -7422579023953826974L;	
	
	/**
	 * Constructs a new <code>ServerStartException</code>.
	 */
	public ServerStartException(){
		super();
	}
	
	/**
	 * Constructs a new <code>ServerStartException</code> with the specified message.
	 * 
	 * @param message the message String.
	 */
	public ServerStartException(String message){
		super(message);
	}
	
	/**
	 * Constructs a new <code>ServerStartException</code> from the specified 
	 * <code>Throwable</code> instance.
	 * 
	 * @param thr the throwable instance.
	 */
	public ServerStartException(Throwable thr){
		super(thr);
	}

}
