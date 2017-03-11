/*
 * UnlockCommand.java 11/10/2010
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

package suncertify.bs.commands;

import suncertify.bs.Business;
import suncertify.bs.Business.Condition;


/**
 * {@link BusinessCommand} that encapsulates calls to
 * {@link Business#unlock(long, long)}.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 11/10/2010
 */
public class UnlockCommand extends BusinessCommand<Void> {
	
	private final long roomNo;
	private final long cookie;
	
	/**
	 * Creates a new instance from the {@link Business#search(String, String, Condition)}
	 * specified parameters.
	 * 
	 * @param roomNo the room number.
	 * @param cookie the result of <code>lockRecord(roomNo)</code> execution.
	 */
	public UnlockCommand(long roomNo, long cookie){
		this.roomNo = roomNo;
		this.cookie = cookie;
	}

	/**
	 * Returns the roomNo command parameter.
	 * 
	 * @return the roomNo specified at creation time.
	 */
	public long getRoomNo() {
		return this.roomNo;
	}
	
	/**
	 * Returns the cookie command parameter.
	 * 
	 * @return the cookie specified at creation time.
	 */
	public long getCookie() {
		return this.cookie;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Void doExecute() throws Exception {
		this.business.unlock(this.roomNo,this.cookie);
		return null;
	}	
}
