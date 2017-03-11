/*
 * DataFileChooser.java 04/10/2010
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

import javax.swing.JFileChooser;
import java.awt.Component;
import java.io.File;


/**
 * Custom file chooser dialog, to select database files.
 *
 * @author Ignacio del Valle Alles
 * @version 1.0 04/10/2010
 *
 */
public final class DataBaseFileChooser {
	
	private final JFileChooser fileChooser  = new JFileChooser(".");
	private Component parent;
	
	/**
	 * Creates a new dialog from the specified parent component.
	 * 
	 * @param parent parent component to show the modal dialog in.
	 */
	public DataBaseFileChooser(Component parent){
		this.parent = parent;
		this.fileChooser.setFileFilter(new DBFileFilter());
	}
	
	/**
	 * Opens a dialog in the specified component and returns the file 
	 * selected by the user.
	 * 
	 * @param lastOpenedFile last file opened path. To select it by default.
	 * @return null if the selection is canceled. 
	 */
	public File getFile(String lastOpenedFile){
		if(!lastOpenedFile.equals("")){
			this.fileChooser.setSelectedFile(new File(lastOpenedFile));
		}
		
		int returnVal = this.fileChooser.showOpenDialog(this.parent);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			return this.fileChooser.getSelectedFile();
		} else {
			return null;
		}
	}
	
	/**
	 * FileFilter that only accepts ".db" files and folders.
	 *
	 * @author Ignacio del Valle Alles
	 * @version 1.0 16/10/2010
	 *
	 */
	class DBFileFilter extends javax.swing.filechooser.FileFilter {
		/**
		 * Accepts only directories and ".db" files.
		 * @param file file to test aceptation
		 * @return true if the file is accepted. False otherwise.
		 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
		 */
		@Override
		public boolean accept(File file) {
			return file.isDirectory() || file.getName().toLowerCase().endsWith(".db");
		}
		/**
		 * Returns accepted files description.
		 * @return the description
		 * @see javax.swing.filechooser.FileFilter#getDescription()
		 */  
		@Override
		public String getDescription() {
			return "Database files";
		}
	}
}


