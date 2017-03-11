/*
 * Server.java 29/09/2010
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

package suncertify.server;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;

import suncertify.client.Client;
import suncertify.commons.Constants;
import suncertify.commons.LoggingObject;
import suncertify.db.DBAccess;
import suncertify.server.impl.RemoteDBAccessFactoryImpl;

/**
 * This class makes a {@link DBAccess} instance available over the network.
 * <hr>
 * Server side code:<br>
 * <blockquote>
 * <p>
 * <code>
 * Server server = new Server(dBAccess);<br>
 * server.start(port);
 * </code>
 * </p>
 * </blockquote>
 * <hr>
 * Client side code:<br>
 * <blockquote>
 * <p>
 * <code>
 * DBAccess dBAccess = {@linkplain Client}.connect(host, port);
 * </code>
 * </p>
 * </blockquote>
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 29/09/2010
 * @see Client
 * 
 */
public class Server extends LoggingObject {

	// Time in milliseconds to stop the server cleanly
	private static final long GRACE_SHUTDOWN_PERIOD = 10000;

	private final DBAccess dbAccess;
	
	private Registry registry;
	private RemoteDBAccessFactoryImpl remoteFactory;
	private boolean started = false;

	/**
	 * Creates a new server from the specified dbAccess.
	 * 
	 * @param dbAccess
	 *            the database access instance.
	 */
	public Server(DBAccess dbAccess) {
		if (dbAccess == null) {
			throw new IllegalArgumentException(
					"Parameter dbAccess must be not null");
		}
		this.dbAccess = dbAccess;
		
		this.logger.log(Level.INFO, "Adding shutdown hook to stop server");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (getServer().isStarted()) {
					getServer().stop();
				}
			}
		});
	}

	/**
	 * Starts the server in the specified port.
	 * 
	 * @param port
	 *            Port where server listens to incoming connections.
	 * @exception ServerStartException
	 *                if an error occurs starting the server.
	 */
	public void start(int port) throws ServerStartException {
		
		this.logger.log(Level.INFO, createLoggingTitle("Starting Server instance"));
		
		if(this.started){
			throw new IllegalStateException("Server is already started");
		}

		try {
			/*
			 * First create a dedicated registry on specified port
			 */
			this.registry = LocateRegistry.createRegistry(port);
			/*
			 * Instantiate the factory. Port is specified in order to let the
			 * factory export its managed remote instances to the same port, in
			 * order to use only one port per server.
			 */
			this.remoteFactory = new RemoteDBAccessFactoryImpl(this.dbAccess,
					port);
			/*
			 * Export the factory itself to same port, making it available to
			 * receive incoming calls.
			 */
			Remote stub = UnicastRemoteObject.exportObject(this.remoteFactory,
					port);
			/*
			 * Bind the factory to the registry so it can be referenced
			 */
			this.registry.bind(Constants.REMOTE_OBJECT_ALIAS, stub);
			this.logger.log(Level.INFO,
					createLoggingTitle("Server started successfully on port "
							+ port));
			/*
			 * Now the server state is "started"
			 */
			this.started = true;

		} catch (Exception e) {
			this.logger.log(Level.WARNING,
					"Exception thrown while starting server", e);
			throw new ServerStartException(e);
		}
	}

	/**
	 * Stops the server.
	 */
	public void stop() {
		
		this.logger.log(Level.INFO, createLoggingTitle("Stopping Server instance"));
		
		if (!this.started) {
			throw new IllegalStateException("Server is not started");
		}
		try {		
			/*
			 * Unbind factory from registry to avoid new clients
			 */
			this.registry.unbind(Constants.REMOTE_OBJECT_ALIAS);
			/*
			 * Set factory to reject incoming calls. The factory forwards the
			 * setting to all the managed instances that has created.
			 */
			this.remoteFactory.rejectIncomingCalls();
			this.logger.log(Level.INFO, "Waiting grace shutdown " + "period ("
					+ GRACE_SHUTDOWN_PERIOD + " ms)");
			/*
			 * Wait the configured interval in ms, in order to give executing
			 * threads a chance to end by themselves.
			 */
			Thread.sleep(GRACE_SHUTDOWN_PERIOD);
			/*
			 * Tell the factory to interrupt all pending threads executing in
			 * any managed instance
			 */
			this.remoteFactory.interruptPendingThreads();
			/*
			 * Reset server state
			 */
			this.started = false;
			this.registry = null;
			this.remoteFactory = null;
			this.logger.log(Level.INFO,
					createLoggingTitle("Server stopped successfully"));
			/*
			 * Request the garbage collector to free resources.
			 */
			System.gc();
		} catch (Exception e) {
			this.logger.log(Level.WARNING,
					"Exception thrown while starting server", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Tests if this server is started.
	 * 
	 * @return <code>true</code> if this server is started; <code>false</code>
	 *         otherwise.
	 */
	public boolean isStarted() {
		return this.started;
	}
	
	/*
	 * Reference server instance from anonymous classes. 
	 */
	protected Server getServer(){
		return this;
	}
}
