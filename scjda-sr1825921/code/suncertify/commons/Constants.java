/*
 * Constants.java 29/09/2010
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

import java.rmi.registry.Registry;



/**
 * General constant definitions.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 29/09/2010
 */
public interface Constants {
	
	/**
	 * Remote factory binding alias to the RMI registry.
	 */
	public static final String MY_CANDIDATE_ID = "SR1825921";
	
	
	/**
	 * Remote factory binding alias to the RMI registry.
	 */
	public static final String REMOTE_OBJECT_ALIAS = "URLyBird121RemoteDBAccessFactory";
	
	/**
	 * Default server port.
	 * 
	 * @see Registry#REGISTRY_PORT
	 */
	public static final int DEFAULT_SERVER_PORT = Registry.REGISTRY_PORT;
	
}
