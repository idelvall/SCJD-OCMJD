/*
 * Command.java 11/10/2010
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


package suncertify.commons;

import java.util.logging.Level;


/**
 * Instances of this class represent receiver class (V parameter) methods calls.
 * 
 * Encapsulating method calls in <code>Command</code> objects makes possible to
 * establish an immutable framework that does not need to be recoded every time
 * a new operation is needed, ie improves the extensibility of the system.<br>
 * <p>
 * Subclasses must implement the protected method: <blockquote>
 * <ul>
 * <li><code>protected abstract S doExecute() throws Exception;</code></li>
 * </ul>
 * </blockquote> Exception handling is provided by this class so subclasses must
 * leave possible exceptions unhandled.
 * </p>
 * <p>
 * Subclasses can overwrite the following protected methods: <blockquote>
 * <ul>
 * <li><code>protected void doPreExecution() throws Exception</code></li>
 * <li><code>protected void doPostExecution() throws Exception</code></li>
 * </ul>
 * </blockquote> These life-cycle methods are called just before and after
 * receiver's method delegation, so usually are overwritten to perform
 * validations and resource allocation/deallocation.
 * </p>
 * <p>
 * A reference to the receiver object is given under the protected instance
 * field "receiver". This field is updated prior to any of the previous methods
 * invocation.
 * </p>
 * <p>
 * Instances of this class are not thread safe, and are not mean to be shared
 * among different threads.
 * </p>
 * 
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 11/10/2010
 * @see <a href="http://en.wikipedia.org/wiki/Command_pattern">Command
 *      Pattern</a>
 * 
 * @param <V>
 *            Receiver class, class of the target object.
 * @param <S>
 *            Class of the target method return type.
 * 
 */
public abstract class Command<V, S> extends LoggingObject {

	// Receiver object (see Command pattern terminology)
	protected V receiver;

	// Used to store the result returned by the target method.
	protected S result;

	// Used to store the exception produced in target method invocation.
	protected Exception exception;

	/**
	 * Default constructor.
	 */
	public Command() {
		return;
	}
	/**
	 * Called by the invoker of the command. 
	 * 
	 * @param receiver
	 *            the target object that will resolve the request.
	 * @throws Exception
	 *             if an error occurs.
	 */
	public final void execute(V receiver) throws Exception {

		this.logger.log(Level.INFO, "Executing command "
				+ getClass().getSimpleName());

		this.receiver = receiver;

		try {
			this.doPreExecution();
			this.result = this.doExecute();
			this.doPostExecution();
		} catch (RuntimeException e) {
			this.logger.log(Level.SEVERE, e.getMessage(), e);
			this.exception = e;
			throw (e);
		} catch (Exception e) {
			this.logger.log(Level.WARNING, e.getMessage(), e);
			this.exception = e;
			throw (e);
		}

		this.logger.log(Level.INFO, "Command " + getClass().getSimpleName()
				+ " executed successfully");
	}

	/**
	 * This method returns the result of command execution. Is used in
	 * asynchronous command executions.
	 * 
	 * @return the object returned by the target method of the receiver.
	 */
	public S getResult() {
		return this.result;
	}

	/**
	 * This method returns the exception thrown in command execution. Is used in
	 * asynchronous command executions.
	 * 
	 * @return the exception thrown by the target method of the receiver.
	 */
	public Exception getException() {
		return this.exception;
	}

	protected abstract S doExecute() throws Exception;

	/*
	 * Life cycle methods
	 */
	protected void doPreExecution() throws Exception {
		return;
	}

	protected void doPostExecution() throws Exception {
		return;
	}

}
