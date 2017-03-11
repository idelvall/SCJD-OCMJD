/*
 * Launcher.java 04/10/2010
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


package suncertify;

import java.io.IOException;

import suncertify.commons.Constants;
import suncertify.commons.LoggingObject;
import suncertify.commons.Miscellaneous;
import suncertify.gui.ClientGUI;
import suncertify.gui.ServerGUI;


/**
 * Execution starter class, for both client and server applications.<br>
 * 
 * To sun the application, from the command line type: <blockquote>
 * 
 * <pre>
 * java -jar &lt;path_and_filename&gt; [&lt;mode&gt;]
 * </pre>
 * 
 * </blockquote> Being <code>&lt;path_and_filename&gt;</code> the path of the database
 * file and <code>&lt;mode&gt;</code> a flag with one of the following values:
 * <ul>
 * <li>"alone"</li>To run the client application for standalone (offline)
 * execution.
 * <li>"server"</li>To run the database server.
 * <li>""</li>To run the client application for remote (online) execution.
 * </ul>
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 04/10/2010
 */
public final class Launcher {

	/**
	 * Flag indicating standalone mode.
	 */
	public static final String ALONE_MODE = "alone";

	/**
	 * Mode flag indicating the server program must run.
	 */
	public static final String SERVER_MODE = "server";

	/**
	 * Mode flag indicating the network client and gui must run.
	 */
	public static final String CLIENT_MODE = "";

	/**
	 * Application main method. Entry point to server a client applications.
	 * 
	 * @param args
	 *            One of the three modes of execution.
	 */
	public static void main(final String[] args) {

		try {
			/*
			 * Flag discriminator. Logging path is configured
			 */
			if (args.length == 0) {
				LoggingObject.configureLogging("SCJD_"+Constants.MY_CANDIDATE_ID+"/logs/client", "remote");
				new ClientGUI().openRemoteDB();
			} else if (args[0].equals(ALONE_MODE)) {
				LoggingObject.configureLogging("SCJD_"+Constants.MY_CANDIDATE_ID+"/logs/client", "local");
				new ClientGUI().openLocalDB();
			} else if (args[0].equals(SERVER_MODE)) {
				LoggingObject.configureLogging("SCJD_"+Constants.MY_CANDIDATE_ID+"/logs/server", "server");
				new ServerGUI();
			} else {
				System.out.println("Incorrect parameter values: "
						+ Miscellaneous.arrayToString(args) + "\n"
						+ "Correct values are:\n" + "\tServer mode: '"
						+ SERVER_MODE + "'\n" + "\tClient mode: '"
						+ CLIENT_MODE + "'\n" + "\tAlone  mode: '" + ALONE_MODE
						+ "'\n");
			}
		} catch (IOException e) {
			System.out.println("An error has occurred setting up logging");
			e.printStackTrace();
		}

	}

	private Launcher() {
		// This class is not instantiable.
	}

}
