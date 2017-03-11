/*
 * LockCommand.java 11/10/2010
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


/**
 * {@link BusinessCommand} that encapsulates calls to
 * {@link Business#lock(long)}.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 11/10/2010
 */
public class LockCommand extends BusinessCommand<Long> {
	
	private final long roomNo;
	
	/**
	 * Creates a new instance from the {@link Business#lock(long)}
	 * specified parameters.
	 * 
	 * @param roomNo the room id.
	 */
	public LockCommand(long roomNo){
		this.roomNo = roomNo;
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
	 * {@inheritDoc}
	 */
	@SuppressWarnings("boxing")
	@Override
	protected Long doExecute() throws Exception {
		return this.business.lock(this.roomNo);
	}
}
