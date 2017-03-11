/*
 * RemoteDataFactory.java 30/09/2010
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

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for factories of {@link RemoteDBAccess} objects.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 30/09/2010
 * 
 */
public interface RemoteDBAccessFactory extends Remote {

	/**
	 * Creates a new RemoteDBAccess instance.
	 * 
	 * @return the newly created instance.
	 * @throws RemoteException if an communication-related error occurs.
	 */
	public RemoteDBAccess createRemoteDBAccess() throws RemoteException;
}
