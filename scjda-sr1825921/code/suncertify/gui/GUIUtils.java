/*
 * GUIUtils.java 04/10/2010
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

package suncertify.gui;

import java.util.Hashtable;
import javax.swing.ImageIcon;

/**
 * Collection of static utility methods related to GUI.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 04/10/2010
 * 
 */
public class GUIUtils {
	/*
	 * Icons cache.
	 */
	private static Hashtable<String, ImageIcon> icons = new Hashtable<String, ImageIcon>();

	/*
	 * This class is not instantiable
	 */
	private GUIUtils() {
		return;
	}

	/**
	 * Returns an <code>ImageIcon</code> from the image file specified, reusing
	 * previous instances. This method locates the resource through the system
	 * class loader.
	 * 
	 * @param relativePath
	 *            the relative path of the resource to the class loading root
	 *            folder.
	 * @return the <code>ImageIcon</code> object if the specified path belogs to
	 *         a valid image file, or <tt>null</tt> if the resource could not be
	 *         found.
	 */
	public static ImageIcon getImageIcon(String relativePath) {
		if (icons.containsKey(relativePath)) {
			return icons.get(relativePath);
		}
		java.net.URL imgURL = ClassLoader.getSystemResource(relativePath);
		if (imgURL != null) {
			ImageIcon newIcon = new ImageIcon(imgURL);
			icons.put(relativePath, newIcon);
			return newIcon;
		}
		return null;
	}

	/**
	 * Resizes the specified <code>Component</code> and centers it in the
	 * screen.
	 * 
	 * @param comp
	 *            the component
	 * @param w
	 *            desired component width
	 * @param h
	 *            desired component height
	 */
	public static void center(java.awt.Component comp, int w, int h) {

		java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		int x0 = ge.getMaximumWindowBounds().x;
		int y0 = ge.getMaximumWindowBounds().y;

		int maxWidth = ge.getMaximumWindowBounds().width;
		int maxHeight = ge.getMaximumWindowBounds().height;

		int width;
		int height;

		if (w == 0) {
			width = maxWidth;
		} else {
			width = w;
		}
		if (h == 0) {
			height = maxHeight;
		} else {
			height = h;
		}
		comp.setSize(width, height);
		comp.setLocation(x0 + (maxWidth - width) / 2, y0 + (maxHeight - height)
				/ 2);
	}
}
