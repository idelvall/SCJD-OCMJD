/*
 * UnrefencedListener.java 30/09/2010
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
package suncertify.server.spec;

import java.rmi.server.Unreferenced;

/**
 * The listener interface for receiving notification of when a remote object is
 * no longer referenced.<br>
 * The class that is interested in being notified must implement this interface.<br>
 * The listener object created from that class is then registered with a
 * component using the component's <code>addUnreferencedListener()</code>.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 03/10/2010
 * 
 * @param <T> Template class that must extend {@link Unreferenced}
 */
public interface UnrefencedListener<T extends Unreferenced> {

	/**
	 * Method invoked by the notifier to communicate that is not longer
	 * referenced by remote objects.
	 * 
	 * @param unreferencedObject the unreferenced object.
	 */
	public void notifyUnreference(T unreferencedObject);
}
