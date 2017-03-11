/*
 * Miscellaneous.java 30/09/2010
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

import java.io.File;
import java.io.IOException;

/**
 * Collection of static utility methods of general purpose.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 30/09/2010
 * 
 */
public final class Miscellaneous {

	/*
	 * This class is not instantiable
	 */
	private Miscellaneous() {
		return;
	}

	/**
	 * Replaces all "\" and "/" in the specified file path by
	 * <code>file.separator</code> system property.
	 * 
	 * @param filePath
	 *            the original file path.
	 * @return the formatted file path.
	 */
	public static String formatFilePath(String filePath) {
		if (filePath == null) {
			return null;
		}
		return filePath.replaceAll("/",
				"\\" + System.getProperty("file.separator")).replaceAll("\\\\",
				"\\" + System.getProperty("file.separator"));

	}

	/**
	 * Creates a file in the specified path. Creates also any necessary folder
	 * needed to achieve the file level of nesting.
	 * 
	 * @param filePath
	 *            the path of file to create.
	 * @return the new created file. <code>null</code> if the file can no be
	 *         created.
	 * @throws IOException
	 *             if an IO error occurs.
	 */
	public static File createFile(String filePath) throws IOException {

		String formattedFilePath = formatFilePath(filePath);

		File f = new File(formattedFilePath);

		if (!formattedFilePath.endsWith(System.getProperty("file.separator"))) {
			f.getParentFile().mkdirs();
			f.createNewFile();
		} else {
			f.mkdirs();
		}
		if(f.exists()){
			return f;
		} else {	
			return null;
		}
	}

	/**
	 * Returns a string representation of the specified long array.
	 * 
	 * @param array
	 *            Array to obtain its representation.
	 * @return the elements of array separated by commas.
	 */
	public static String longArraytoString(long[] array) {
		if (array == null || array.length == 0) {
			return null;
		}

		StringBuffer sbRow = new StringBuffer("" + array[0]);
		for (int i = 1; i < array.length; i++) {
			sbRow.append("," + array[i]);
		}
		return sbRow.toString();
	}

	/**
	 * Returns a string representation of the specified object array.
	 * 
	 * @param array
	 *            Array to obtain its representation.
	 * @return the elements of array separated by commas.
	 */
	public static String arrayToString(Object[] array) {
		if (array == null || array.length == 0) {
			return null;
		}

		StringBuffer sbRow = new StringBuffer("" + array[0]);
		for (int i = 1; i < array.length; i++) {
			sbRow.append("," + array[i]);
		}
		return sbRow.toString();
	}
}
