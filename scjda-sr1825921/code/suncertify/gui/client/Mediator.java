/*
 * Mediator.java 11/10/2010
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


package suncertify.gui.client;

import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;

import suncertify.bs.Business;
import suncertify.bs.commands.BusinessCommand;
import suncertify.bs.commands.CommandListener;
import suncertify.commons.LoggingObject;


/**
 * Executes {@link BusinessCommand}s and broadcast the
 * event to the subscribed listeners.<br>
 * Execution of the commands can be made synchronously or asynchronously. Listeners
 * notification thread is:
 * <ul>
 * <li>in case of synchronous execution listeners are notified in the same thread</li>
 * <li>in case of asynchronous execution listeners are notified in the AWT event dispatching thread.</li>
 * </ul>  
 *  
 * @author Ignacio del Valle Alles
 * @version 1.0 11/10/2010
 */
public final class Mediator extends LoggingObject {

	private List<CommandListener<BusinessCommand<?>>> listeners = new Vector<CommandListener<BusinessCommand<?>>>();
	private Business business;

	/**
	 * Creates a new instance from the specified {@link Business} object, that
	 * will be used as "receiver" object, in the Command Pattern context.
	 * 
	 * 
	 * @param business
	 *            the receiver that will be used to execute the commands to.
	 */
	public Mediator(Business business) {
		this.business = business;
	}

	/**
	 * Subscribes a listener in order to be notified of the events occurred.
	 * 
	 * @param listener
	 *            a command listener. Ignored if already is subscribed.
	 */
	public void addCommandListener(CommandListener<BusinessCommand<?>> listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	/**
	 * Removes a listener.
	 * 
	 * @param listener
	 *            the listener to be removed. Ignored if it not subscribed.
	 */
	public void removeCommandListener(CommandListener<BusinessCommand<?>> listener) {
		if (this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}

	/**
	 * Executes the command in the current thread. Result object and possible
	 * exceptions thrown are returned to the caller in the common way, but also
	 * are notified to all the registered listerners.
	 * 
	 * @param <R>
	 *            the class of the command target method result type.
	 * @param cmd
	 *            the command to be executed.
	 * @return the target method returned object.
	 * @throws Exception
	 *             the exception originally thrown by the target method.
	 */
	public <R> R executeCommandSync(final BusinessCommand<R> cmd)
			throws Exception {
		try {
			fireCommandStart(cmd);
			cmd.execute(this.business);
			fireCommandEnd(cmd);
		} catch (Exception e) {
			fireCommandExceptionEnd(cmd);
			throw (e);
		}
		return cmd.getResult();
	}

	/**
	 * Executes the command asynchronous (in a separate thread).
	 * Result and exception handling must be done via a {@link CommandListener}.
	 * 
	 * @param cmd
	 *            the command to be executed.
	 */
	public void executeCommandAsync(final BusinessCommand<?> cmd) {

		Thread thread = new Thread() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void run() {
				try {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							fireCommandStart(cmd);
						}
					});

					cmd.execute(getBusiness());

					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							fireCommandEnd(cmd);
						}
					});

				} catch (Exception e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							fireCommandExceptionEnd(cmd);
						}
					});
				}
			}
		};
		thread.start();
	}
	
	/*
	 *	Fire event methods
	 */

	void fireCommandStart(final BusinessCommand<?> cmd) {
		for (int i = 0; i < this.listeners.size(); i++) {
			CommandListener<BusinessCommand<?>> listener = this.listeners.get(i);
			listener.commandStarted(cmd);
		}
	}

	void fireCommandEnd(final BusinessCommand<?> cmd) {
		for (int i = 0; i < this.listeners.size(); i++) {
			CommandListener<BusinessCommand<?>> listener = this.listeners.get(i);
			listener.commandEnded(cmd);
		}
	}

	void fireCommandExceptionEnd(final BusinessCommand<?> cmd) {
		for (int i = 0; i < this.listeners.size(); i++) {
			CommandListener<BusinessCommand<?>> listener = this.listeners.get(i);
			listener.commandExceptionEnded(cmd);
		}
	}

	/*
	 * Accessor method to be used in anonymous classes
	 */
	Business getBusiness() {
		return this.business;
	}
}
