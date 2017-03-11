/*
 * CommandListener.java 11/10/2010
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

import suncertify.commons.Command;


/**
 * The listener interface for receiving notification from the invoker when
 * command is executed.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 11/10/2010
 *
 * @param <C> Command type.
 */
public interface CommandListener<C extends Command<?,?>> {

	/**
	 * Fired when a command is about to be executed.
	 * 
	 * @param cmd the command to be executed.
	 */
	public void commandStarted(C cmd);

	/**
	 * Fired after a command execution. Target method result can be retrieved from
	 * {@link Command#getResult()}.
	 * 
	 * @param cmd the executed command.
	 */
	public void commandEnded(C cmd);

	/**
	 * Fired if an exception is thrown during command target method execution. 
	 * The exception can be retrieved from {@link Command#getException()}.
	 * 
	 * @param cmd the executed command.
	 */
	public void commandExceptionEnded(C cmd);
}
