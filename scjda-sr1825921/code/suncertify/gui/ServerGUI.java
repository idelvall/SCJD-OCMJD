/*
 * ServerGUI.java 04/10/2010
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

import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.*;
import javax.swing.*;

import suncertify.commons.Constants;
import suncertify.commons.LoggingObject;
import suncertify.commons.PropertiesManager;
import suncertify.db.Data;
import suncertify.db.NotSupportedDataBaseException;
import suncertify.db.impl.DefaultFinderImp;
import suncertify.db.impl.DefaultLockManagerImpl;
import suncertify.db.impl.URLyBirdFile_1_2_1;
import suncertify.server.Server;
import suncertify.server.ServerStartException;



/**
 * Class <code>ServerGUI</code> provides a GUI for starting and stopping the
 * server.
 * 
 * @author Ignacio del Valle Alles
 */
public final class ServerGUI extends BaseGUI {

	static final long serialVersionUID = -1485678305143455841L;

	/**
	 * Last database file selected. This value is overwritten by
	 * {@link PropertiesManager}.
	 */
	public static String lastFileOpened = "";

	/**
	 * Last server port selected. This value is overwritten by
	 * {@link PropertiesManager}.
	 */
	public static int lastServerPort = Constants.DEFAULT_SERVER_PORT;

	/*
	 * Tell the PropertiesManager to overwrite public static not final fields, 
	 * from previous executions stored values.
	 */
	static {
		PropertiesManager.getInstance().overrideFieldValues();
	}

	/*
	 * Managed server
	 */
	private Server server;
	
	/*
	 * Database file chooser
	 */
	private DataBaseFileChooser dbFileChooser = new DataBaseFileChooser(this);

	/*
	 * Graphical components.
	 */
	private JTextField frameFoot; // Border layout south content
	private javax.swing.JPanel jPanel = null; // Border layout center content
	private javax.swing.JLabel dbLabel = null; // Label for file selector
	private javax.swing.JTextField dbField = null; // Text input for filename
	private javax.swing.JButton fileChooserButton = null; // File selector
															// button
	private javax.swing.JLabel portLabel = null; // Label for port input text
	private javax.swing.JTextField portField = null; // Port input text
	private javax.swing.JButton startButton = null; // Start server button
	private javax.swing.JButton exitButton = null; // Exit button

	/**
	 * Creates a new server GUI instance.
	 */
	public ServerGUI() {

		this.setResizable(false);
		this.setIconImage(GUIUtils.getImageIcon(
				"suncertify/gui/images/disconnected.gif").getImage());
		this.setTitle("URLyBird Server - Offline");
		this.getContentPane().add(getJPanel(), java.awt.BorderLayout.CENTER);
		this.getContentPane().add(getFrameFoot(), java.awt.BorderLayout.SOUTH);

		GUIUtils.center(this, 430, 220);
		setVisible(true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see suncertify.gui.BaseGUI#getHelpAnchorName()
	 */
	@Override
	protected String getHelpAnchorName() {
		return "server";
	}
	
	/*
	 * (non-Javadoc)
	 * @see suncertify.gui.BaseGUI#addMenuItemsToFileMenu(javax.swing.JMenu)
	 */
	@Override
	protected void addMenuItemsToFileMenu(JMenu fileMenu) {
		return;		
	}
	
	/*
	 * (non-Javadoc)
	 * @see suncertify.gui.BaseGUI#addMenusToMenuBar(javax.swing.JMenuBar)
	 */
	@Override
	protected void addMenusToMenuBar(JMenuBar menuBar) {
		return;				
	}
	
	/*
	 * (non-Javadoc)
	 * @see suncertify.gui.BaseGUI#doBeforeExit()
	 */
	@Override
	protected boolean doBeforeExit() {

		if (this.server != null && this.server.isStarted()) {
			if (!showConfirm(
					"Are you sure you want to stop the server \nand quit the application?",
					"Exit application")) {
				return false;
			}
			this.server.stop();
			return true;
		}
		return true;	
	}

	/*
	 * This method initializes and returns dbField.
	 */
	JTextField getDBField() {
		if (this.dbField == null) {
			this.dbField = new javax.swing.JTextField();
			this.dbField.setBounds(130, 20, 225, 25);
			this.dbField.setText(ServerGUI.lastFileOpened);
			this.dbField.setCaretPosition(this.dbField.getText().length());
		}
		return this.dbField;
	}

	/*
	 * Initializes and starts the server
	 */
	void startServer(int port, File dbFile) throws IOException,
			NotSupportedDataBaseException, ServerStartException {
		this.logger.log(Level.INFO, "Starting server...");
		URLyBirdFile_1_2_1 uf = new URLyBirdFile_1_2_1(dbFile);
		DefaultFinderImp finder = new DefaultFinderImp(uf);
		DefaultLockManagerImpl<Long> lockManagerImpl = new DefaultLockManagerImpl<Long>();
		Data data = new Data(uf, finder, lockManagerImpl);
		this.server = new Server(data);
		this.server.start(port);
	}

	/*
	 * Opens the dialog to choose the database file.
	 */
	File getFile() {
		return this.dbFileChooser.getFile(ServerGUI.lastFileOpened);
	}

	/*
	 * This method initializes and returns portField.
	 */
	javax.swing.JTextField getPortField() {
		if (this.portField == null) {
			this.portField = new javax.swing.JTextField();
			this.portField.setBounds(130, 60, 60, 25);
			this.portField.setText("" + ServerGUI.lastServerPort);
		}
		return this.portField;
	}

	/*
	 * From here, the getters with lazy-initialization for the GUI components.
	 */
	
	/*
	 * This method initializes and returns fileChooserButton.
	 */
	JButton getFileChooserButton() {
		if (this.fileChooserButton == null) {
			this.fileChooserButton = new javax.swing.JButton();
			this.fileChooserButton.setBounds(360, 20, 45, 25);
			this.fileChooserButton.setText("...");
			this.fileChooserButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					File file = getFile();
					if (file != null) {
						getDBField().setText(file.getAbsolutePath());
					}
				}
			});
		}
		return this.fileChooserButton;
	}

	/*
	 * Initializes 'start' button.
	 */
	javax.swing.JButton getStartButton() {
		if (this.startButton == null) {
			this.startButton = new javax.swing.JButton();
			this.startButton.setBounds(100, 100, 100, 25);
			this.startButton.setText("Start");
			this.startButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {

						/*
						 * Start the server with the user entered parameters.
						 */
						int port = Integer.parseInt(getPortField().getText());
						String fileName = getDBField().getText();

						startServer(port, new File(fileName));

						/*
						 * Change gui appearance. Disable some components and
						 * show a connected status.
						 */
						setIconImage(GUIUtils.getImageIcon(
								"suncertify/gui/images/connected.gif")
								.getImage());
						setTitle("URLyBird Server - Online");
						getFileChooserButton().setEnabled(false);
						getStartButton().setEnabled(false);
						getDBField().setEditable(false);
						getPortField().setEditable(false);

						/*
						 * Update the following field values, for future
						 * executions. See PropertiesManager.
						 */
						ServerGUI.lastFileOpened = fileName;
						ServerGUI.lastServerPort = port;

					} catch (ServerStartException e) {
						warn("Server can not be started.\nEnsure that the port is not already in use.");
						getLogger().log(Level.SEVERE, e.toString(), e);
					}  catch (FileNotFoundException e) {
						warn("Error setting up database.\nEnsure that the file " +
								"exists and can be written.");
						getLogger().log(Level.SEVERE, e.toString(), e);
					} catch (IOException e) {
						warn("Error setting up database.\n" +
								"Possibly corrupted database file.\n" +
								"Please see logs for more details");
						getLogger().log(Level.SEVERE, e.toString(), e);
					} catch (NotSupportedDataBaseException e) {
						warn("Database file format not supported.");
						getLogger().log(Level.SEVERE, e.toString(), e);
					} catch (NumberFormatException e) {
						warn("Port value must be integer");
					} catch (Exception e) {
						error("An error has occured. See logs for more details.");
						getLogger().log(Level.SEVERE, e.toString(), e);
					}
				}
			});
		}
		return this.startButton;
	}

	/*
	 * This method initializes and returns the exit button.
	 */
	private javax.swing.JButton getExitButton() {
		if (this.exitButton == null) {
			this.exitButton = new javax.swing.JButton();
			this.exitButton.setBounds(215, 100, 100, 25);
			this.exitButton.setText("Exit");
			this.exitButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					quit();
				}
			});
		}
		return this.exitButton;
	}

	/*
	 * This method initializes and returns portLabel.
	 */
	private javax.swing.JLabel getPortLabel() {
		if (this.portLabel == null) {
			this.portLabel = new javax.swing.JLabel();
			this.portLabel.setBounds(10, 60, 115, 25);
			this.portLabel.setText("Server port:");
			this.portLabel.setName("this.dbLabel");
			this.portLabel
					.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			this.portLabel.setBorder(null);
		}
		return this.portLabel;
	}

	/*
	 * This method initializes and returns the frame foot.
	 */
	private JTextField getFrameFoot() {
		if (this.frameFoot == null) {
			this.frameFoot = new JTextField();
			this.frameFoot.setEditable(false);
			this.frameFoot.setText("Logging to: " + LoggingObject.loggingFolder);
		}
		return this.frameFoot;
	}

	/*
	 * This method initializes and returns jPanel.
	 */
	private javax.swing.JPanel getJPanel() {
		if (this.jPanel == null) {
			this.jPanel = new javax.swing.JPanel();
			this.jPanel.setLayout(null);
			this.jPanel.add(getDBField(), null);
			this.jPanel.add(getDBLabel(), null);
			this.jPanel.add(getFileChooserButton(), null);
			this.jPanel.add(getPortLabel(), null);
			this.jPanel.add(getPortField(), null);
			this.jPanel.add(getStartButton(), null);
			this.jPanel.add(getExitButton(), null);
			this.jPanel.setSize(415, 200);
		}
		return this.jPanel;
	}

	/*
	 * This method initializes and returns dbLabel.
	 */
	private javax.swing.JLabel getDBLabel() {
		if (this.dbLabel == null) {
			this.dbLabel = new javax.swing.JLabel();
			this.dbLabel.setBounds(10, 20, 115, 25);
			this.dbLabel.setText("Database location:");
			this.dbLabel.setName("this.dbLabel");
			this.dbLabel
					.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			this.dbLabel.setBorder(null);
		}
		return this.dbLabel;
	}
}
