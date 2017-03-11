/*
 * Client.java 03/10/2010
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


package suncertify.client;

import java.net.ConnectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import suncertify.commons.Constants;
import suncertify.db.DBAccess;
import suncertify.server.Server;
import suncertify.server.spec.RemoteDBAccess;
import suncertify.server.spec.RemoteDBAccessFactory;


/**
 * This class is used to connect to a {@link Server}.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 03/10/2010
 * @see Server
 * 
 */
public final class Client {

	/**
	 * Connects to a server.
	 * 
	 * @param host
	 *            Host where the server is running
	 * @param port
	 *            Server listening port
	 * @return a <code>DBAccess</code> proxy to interact with the remote
	 *         database.
	 * @throws ConnectException
	 *             If any error occurs.
	 */
	public static DBAccess connect(String host, int port)
			throws ConnectException {
		try {
			Registry registry = LocateRegistry.getRegistry(host, port);
			RemoteDBAccessFactory factory = (RemoteDBAccessFactory) registry
					.lookup(Constants.REMOTE_OBJECT_ALIAS);
			RemoteDBAccess remoteDBAccess = factory.createRemoteDBAccess();
			return new RemoteDataAdapter(remoteDBAccess);
		} catch (Exception e) {
			throw new ConnectException(e.getMessage());
		}
	}
		
	private Client(){
		// This class is not instantiable.
	}

}
