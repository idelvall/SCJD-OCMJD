/*
 * LoggingObject.java 27/09/2010
 * 
 * Candidate: Ignacio del Valle Alles
 * Candidate ID: SR1825921
 * 
 * Sun Certified Developer for Java 2 Platform, Standard Edition Programming
 * Assignment (CX-310-252A)
 * 
 * This class is part of the Programming Assignment of the Sun Certified
 * Developer for Java 2 Platform, Standard Edition certification program, must
 * not be used out of this context and must be used exclusively by Oracle 
 * Corporation.
 */


package suncertify.commons;

import static suncertify.commons.Miscellaneous.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.*;


/**
 * Adds logging capabilities to extending classes.<br>
 * Extending classes inherit a protected {@link Logger logger} ready to use.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 27/09/2010
 * 
 */
public class LoggingObject {

	protected final Logger logger;
	
	/**
	 * Path of the logging files.
	 */
	public static File loggingFolder;

	/*
	 * Configuration is done once per application. Since all assignment
	 * applications (client, server, server shutdown) uses a single classloader,
	 * a static flag can be used.
	 */
	private static boolean logAlreadyConfigurated = false;

	/**
	 * Configures the logging for namespace "suncertify". If called twice per
	 * application, an IllegalStateException is thrown.
	 * 
	 * @param relativeFolderPath
	 *            folder path relative to user.home where the logging files will
	 *            be located.
	 * @param filePrefix
	 *            prefix of logging files.
	 * @throws IOException
	 *             if an IO error occurs.
	 * @throws IllegalStateException,
	 *             if the method is called twice per application.
	 */
	public synchronized static void configureLogging(String relativeFolderPath,
			String filePrefix) throws IOException {

		if (!logAlreadyConfigurated) {

			Logger logger = Logger.getLogger("suncertify");
			logger.setUseParentHandlers(false);
			logger.setLevel(Level.ALL);
			String loggingFolderPath = formatFilePath(System.getProperty("user.home") + "/" + relativeFolderPath + "/");
			loggingFolder = createFile(loggingFolderPath);
			if(loggingFolder == null){
				throw new IOException("Can not create base logging folder: " + loggingFolderPath);
			}
			FileHandler fileHandler = new FileHandler(loggingFolderPath + filePrefix + "_%g.txt",
					500000, 10, true);
			fileHandler.setLevel(Level.ALL);
			fileHandler.setFormatter(new SimpleFormatter() {

				/*
				 * Added threadId to default message formatting
				 */
				@Override
				public synchronized String formatMessage(LogRecord record) {
					String ret = super.formatMessage(record);
					return "(thread " + record.getThreadID() + ") " + ret;
				}
			});
			logger.addHandler(fileHandler);
			/*
			 * Write in the console the logging path
			 */
			System.out.println("Logging to: " + loggingFolder.getAbsolutePath());
			logAlreadyConfigurated = true;
		} else {
			throw new IllegalStateException(
					"Logging has already been configurated");
		}
	}

	/*
	 * Constructor. Initializes the logger.
	 */
	protected LoggingObject() {
		this.logger = Logger.getLogger(getClass().getName());
	}

	/*
	 * Creates a title for log section.
	 */
	protected static String createLoggingTitle(String str) {
		StringBuffer sbRow = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			sbRow.append("-");
		}
		String row = sbRow.toString();
		return "\n" + row + "\n" + str + "\n" + row;
	}

	/*
	 * Logger getter
	 */
	protected final Logger getLogger() {
		return this.logger;
	}

}
