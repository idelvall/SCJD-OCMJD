/*
 * PropertiesManager.java 30/09/2010
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

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

/**
 * Class <code>PropertiesManager</code> provides an easy and handy way for
 * storing and loading configuration properties to/from a properties file called
 * "suncertify.properties" located in the in the current working directory.<br>
 * <p>
 * This is the procedure:
 * </p>
 * <p>
 * <blockquote>
 * <ul>
 * <li>A client class must define needed properties as <i>overridable</i> fields
 * (<code>public static</code> fields) with their default values.<br>
 * Supported <i>overridable</i> fields types are: <code>String</code>,
 * <code>Class</code>, <code>int</code>, <code>long</code> and
 * <code>boolean</code>.
 * <p>
 * <blockquote>
 * 
 * <pre>
 * <code>
 * public AClass{
 *     public static String anOverridableFiled = &quot;DefaultValue&quot;;
 *     ...
 * }
 * </code>
 * </pre>
 * 
 * </blockquote>
 * </p>
 * </li>
 * <li>For updating these default values by stored ones use the 
 * {@link #overrideFieldValues()} method.
 * <p>
 * <blockquote>
 * 
 * <pre>
 * <code>
 * public AClass{
 *     public static String anOverridableFiled = &quot;DefaultValue&quot;;
 *     ...
 *   
 *     static{
 *         PropertiesManager.getInstance().overrideFieldValues();
 *     }
 * }
 * </code>
 * </pre>
 * 
 * </blockquote>
 * </p>
 * </li>
 * <li>Storing current class <i>overridable</i> fields to the file is performed
 * automatically at application shutdown.</li>
 * </ul>
 * </blockquote>
 * </p>
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 30/09/2010
 */
public final class PropertiesManager extends LoggingObject {

	/**
	 * Properties file name. Used to persist data.
	 */
	public static final String CONFIG_FILE_NAME = "suncertify.properties";

	private static final PropertiesManager instance = new PropertiesManager();

	private final File propertiesFile;
	private final Properties props;
	private final List<Class<?>> registeredClasses = new Vector<Class<?>>();

	/**
	 * Returns the <code>PropertiesManager</code> instance.
	 * 
	 * @return the instance.
	 */
	public synchronized static PropertiesManager getInstance() {

		return instance;
	}

	/*
	 * Creates the single instance
	 */
	private PropertiesManager() {
		this.propertiesFile = new File(CONFIG_FILE_NAME);
		this.logger.log(Level.INFO, "Creating PropertiesManager instance...");
		this.logger.log(Level.INFO, "Configuration properties file: "
				+ this.propertiesFile.getAbsolutePath());
		this.props = new Properties();

		try {
			/*
			 * If the file exists, load properties from it.
			 */
			if (this.propertiesFile.exists()) {
				InputStream inputStream = new FileInputStream(
						this.propertiesFile);
				this.props.load(inputStream);
				inputStream.close();
			}
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					storeValues();
				}
			});

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the file where the properties are being stored.
	 * 
	 * @return the properties file.
	 */
	public File getPropertiesFile() {
		return this.propertiesFile;
	}

	/**
	 * Updates public static fields of the caller class from the properties
	 * values.<br>
	 * For a public static field <code>someField</code> of a class
	 * <code>AClass</code> being updated:<br>
	 * <p>
	 * <blockquote> 1) A property named <code>AClass.someField</code> must exist
	 * in the <code>PropertiesManager</code>.<br>
	 * 2) <code>someField</code> must be one of the following types:
	 * <code>String</code>, <code>Class</code>, <code>int</code>,
	 * <code>long</code> or <code>boolean</code>.
	 * </p>
	 * </blockquote>
	 */
	public void overrideFieldValues() {
		// Adds a class reference for future storeValues() call.
		StackTraceElement[] stackElements = Thread.currentThread()
				.getStackTrace();
		Class<?> callerClass;
		try {
			/*
			 * Element[0] is Thread.currentThread(); Element[1] is this method.
			 * Element[2] is caller
			 */
			callerClass = Class.forName(stackElements[2].getClassName());
		} catch (ClassNotFoundException e) {
			/*
			 * This can not happen
			 */
			assert (false);
			return;
		}
		registerClass(callerClass);
		StringBuffer logString = new StringBuffer("Overriding ");
		logString.append(callerClass.getName()).append(" parameters:");

		/*
		 * For each overridable field (public static) of the class:
		 */
		for (Field field : callerClass.getDeclaredFields()) {
			int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)
					&& !Modifier.isFinal(modifiers)) {

				String classParameterName = callerClass.getName() + "."
						+ field.getName();
				Class<?> fieldType = field.getType();

				/*
				 * If a property is found for that class field, and the field
				 * type is supported, update the field value
				 */
				try {
					if (this.props.containsKey(classParameterName)) {
						// String property
						if (fieldType.equals(String.class)) {
							field.set(null, readProperty(classParameterName));
							// Class property
						} else if (fieldType.equals(Class.class)) {
							field.set(null, readClass(classParameterName));
							// int property
						} else if (fieldType.equals(int.class)) {
							field.setInt(null, readInt(classParameterName));
							// long property
						} else if (fieldType.equals(long.class)) {
							field.setLong(null, readLong(classParameterName));
							// boolean property
						} else if (fieldType.equals(boolean.class)) {
							field.setBoolean(null,
									readBoolean(classParameterName));
						}
					}
					logString.append("\n\t").append(field.getName()).append(
							" = ").append(field.get(null));

				} catch (Exception e) {
					this.logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
		this.logger.log(Level.CONFIG, logString.toString());
	}

	/**
	 * Stores all public static field current values of the registered classes
	 * (classes that has invoked {@link #overrideFieldValues()}).
	 */
	void storeValues() {
		for (int i = 0; i < this.registeredClasses.size(); i++) {
			storeValues(this.registeredClasses.get(i));
		}
		try {
			this.writeToFile();
		} catch (IOException e) {
			this.logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Returns the String value of the specified property. The method returns
	 * <code>null</code> if the property is not found.
	 * 
	 * @param propName
	 * @return the property value
	 */
	private String readProperty(String propName) {
		String ret = this.props.getProperty(propName);
		return ret;
	}

	/**
	 * Parses the specified property value to a long value.
	 * 
	 * @param propName
	 *            property name.
	 * @return the <code>long</code> represented by the property value.
	 * @exception NumberFormatException
	 *                if the string does not contain a parsable
	 *                <code>long</code> or the property is not found.
	 */
	private long readLong(String propName) {

		try {
			long ret = Long.parseLong(readProperty(propName));
			return ret;

		} catch (NumberFormatException e) {
			throw new NumberFormatException("Error initializing "
					+ this.propertiesFile + " property: " + propName);
		}
	}

	/**
	 * Parses the specified property value to a int value.
	 * 
	 * @param propName
	 *            propertie name.
	 * @return the integer value represented by the property value.
	 * @exception NumberFormatException
	 *                if the string does not represent a valid integer.
	 */
	private int readInt(String propName) {
		try {
			int ret = Integer.parseInt(readProperty(propName));
			return ret;
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Error " + this.propertiesFile
					+ " initializing property: " + propName);
		}
	}

	/**
	 * Parses the specified property value to a boolean value.
	 * 
	 * @param propName
	 *            the property name to be parsed.
	 * @return the boolean represented by the property value.
	 */
	private boolean readBoolean(String propName) {

		boolean ret = Boolean.parseBoolean(readProperty(propName));
		return ret;
	}

	/**
	 * Parses the specified property value to a <code>Class</code> value.
	 * 
	 * @param propName
	 *            the property containing the <code>Class</code> name
	 * @return the <code>Class</code> object
	 * @exception ClassNotFoundException
	 *                if the class cannot be located
	 */
	private Class<?> readClass(String propName) throws ClassNotFoundException {

		System.out.println(propName);
		Class<?> ret = Class.forName(readProperty(propName));
		return ret;
	}

	/*
	 * Store properties to the file
	 */
	private void writeToFile() throws IOException {

		OutputStream outputStream = new FileOutputStream(this.propertiesFile);
		this.props.store(outputStream, "URLyBird configuration parameters");
		outputStream.close();
		this.logger.log(Level.INFO, "Properties stored to file: "
				+ this.propertiesFile.getName());

	}

	/*
	 * Adds the specified class to the collection.
	 */
	private synchronized void registerClass(Class<?> aClass) {
		if (!this.registeredClasses.contains(aClass)) {
			this.registeredClasses.add(aClass);
		}
	}

	/*
	 * Updates the property values from the values of the public static fields
	 * of the specified class.
	 */
	private void storeValues(Class<?> aClass) {
		String logString = "Saving " + aClass.getName() + " parameters:";
		/*
		 * For each updatable field (public static) of the class:
		 */
		for (Field field : aClass.getDeclaredFields()) {
			int modifiers = field.getModifiers();

			if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)
					&& !Modifier.isFinal(modifiers)) {

				String classParameterName = aClass.getName() + "."
						+ field.getName();
				Class<?> fieldType = field.getType();

				/*
				 * If the field type is supported, update the property value
				 */
				try {

					// String property
					if (fieldType.equals(String.class)) {
						this.props.setProperty(classParameterName, field.get(
								null).toString());
						// Class property
					} else if (fieldType.equals(Class.class)) {
						this.props.setProperty(classParameterName,
								((Class<?>) field.get(null)).getName());
						// int property
					} else if (fieldType.equals(int.class)) {
						this.props.setProperty(classParameterName, field
								.getInt(null)
								+ "");
						// long property
					} else if (fieldType.equals(long.class)) {
						this.props.setProperty(classParameterName, field
								.getLong(null)
								+ "");
						// boolean property
					} else if (fieldType.equals(boolean.class)) {
						this.props.setProperty(classParameterName, field
								.getBoolean(null)
								+ "");
					}

					// Append to log string field values
					logString += "\n\t" + field.getName() + " = "
							+ field.get(null);

				} catch (Exception e) {
					this.logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
		this.logger.log(Level.CONFIG, logString);
	}

}
