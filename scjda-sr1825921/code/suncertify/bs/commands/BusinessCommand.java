/*
 * BusinessCommand.java 11/10/2010
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
import suncertify.commons.Command;


/**
 * Base command for {@link Business} method invocations.<br>
 * Overrides <code>doPreExecution()</code> method to make the receiver parameter
 * mandatory not null to the invoker.<br><br>
 * Provides a protected field named "business" to its subclasses. This field is 
 * initialized in the <code>doPreExecution()</code> method. 
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 14/10/2010
 * 
 * @param <S>
 *            Class of the target method return type.
 */
public abstract class BusinessCommand<S> extends Command<Business, S> {

	protected Business business;

	@Override
	protected void doPreExecution() throws Exception {
		this.business = this.receiver;
		if (this.business == null) {
			throw new IllegalArgumentException(
					"Receiver object can not be null in business commands");
		}
	}
}
