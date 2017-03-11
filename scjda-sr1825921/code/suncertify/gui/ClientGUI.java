/*
 * ClientGUI.java 04/10/2010
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

import static suncertify.gui.GUIUtils.center;
import static suncertify.gui.GUIUtils.getImageIcon;
import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.ConnectException;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

import suncertify.bs.AlreadyBookedException;
import suncertify.bs.Business;
import suncertify.bs.HotelRoom;
import suncertify.bs.RoomNotFoundException;
import suncertify.bs.Business.Condition;
import suncertify.bs.commands.BookCommand;
import suncertify.bs.commands.LockCommand;
import suncertify.bs.commands.SearchCommand;
import suncertify.bs.commands.UnlockCommand;
import suncertify.client.Client;
import suncertify.commons.Constants;
import suncertify.commons.PropertiesManager;
import suncertify.db.DBAccess;
import suncertify.db.Data;
import suncertify.db.NotSupportedDataBaseException;
import suncertify.db.SecurityException;
import suncertify.db.impl.DefaultFinderImp;
import suncertify.db.impl.DefaultLockManagerImpl;
import suncertify.db.impl.URLyBirdFile_1_2_1;
import suncertify.gui.client.Mediator;
import suncertify.gui.client.beans.TableBean;
import suncertify.gui.client.beans.TableBeanListener;

/**
 * URLyBird client GUI.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 04/10/2010
 */
public final class ClientGUI extends BaseGUI implements TableBeanListener {

	static final long serialVersionUID = 3944035492725313618L;

	/**
	 * Last window width. This value is overwritten by {@link PropertiesManager}
	 * .
	 */
	public static int lastWindowWidth = 800;

	/**
	 * Last window height. This value is overwritten by
	 * {@link PropertiesManager}.
	 */
	public static int lastWindowHeight = 600;

	/**
	 * Last window state. This value is overwritten by {@link PropertiesManager}
	 * .
	 */
	public static int lastWindowExtendedState = Frame.NORMAL;

	/**
	 * Last local database file name. This value is overwritten by
	 * {@link PropertiesManager}.
	 */
	public static String lastLocalDBFileName = "";

	/**
	 * Last server host. This value is overwritten by {@link PropertiesManager}.
	 */
	public static String lastServerHost = "";

	/**
	 * Last server port. This value is overwritten by {@link PropertiesManager}.
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
	 * Instance fields.
	 */

	/*
	 * Mediates communication between GUI and business service, notifying all
	 * subscribed listeners
	 */
	private Mediator mediator;
	/*
	 * Keep track of last successful search to perform view "refresh"
	 */
	private SearchCommand lastSearch;

	/*
	 * GUI components
	 */

	/*
	 * Menus
	 */
	private JMenu menuSearch;

	/*
	 * Menu items
	 */
	private JMenuItem menuItemOpen;
	private JMenuItem menuItemClose;
	private JMenuItem menuItemConnect;
	private JMenuItem menuItemDisconnect;
	private JMenuItem menuItemRefresh;
	private JMenuItem menuItemSearch;
	private JMenuItem menuItemSearchAll;

	/*
	 * Table foot buttons
	 */
	private JButton refreshBt;
	private JButton bookBt;

	/*
	 * Open remote database dialog components
	 */
	private Object[] remoteDialogMessages;
	private JTextField addressField;
	private JTextField portField;

	/*
	 * Search dialog components
	 */
	private Object[] searchDialogMessages;
	private JTextField searchNameField;
	private JTextField searchLocationField;
	private JComboBox searchConditionCombo;

	private JTextField frameFoot; // Shows database name

	/*
	 * Its content is cleaned on database closing, and initialized on database
	 * opening.
	 */
	private JPanel centerPanel;

	private JPanel footButtonsPanel;
	private JPanel footLeftButtonsPanel;
	private JPanel footRightButtonsPanel;

	private TableBean tableBean;
	private HotelRoom selectedRoom;

	/*
	 * Common action listeners
	 */
	private ActionListener closeActionListener;

	/*
	 * Database file chooser
	 */
	private DataBaseFileChooser dbFileChooser = new DataBaseFileChooser(this);

	/**
	 * Default constructor.
	 */
	public ClientGUI() {

		this.logger.log(Level.INFO, "Running application ...");
		this.initComponents();
		this.logger.log(Level.INFO, "Client GUI frame opened ...");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectedRoom(HotelRoom room) {
		/*
		 * Table listener method. Called in row selection
		 */
		this.selectedRoom = room;
		this.getBookButton().setEnabled(!room.isBooked());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(HotelRoom room) {
		/*
		 * Table listener method. Called in row action.(Pressed enter over a row
		 * table)
		 */
		if (!room.isBooked()) {
			book();
		}
	}

	/**
	 * Shows a dialog to the user, for connect to a remote database.
	 */
	public void openRemoteDB() {

		Object[] messages = getRemoteDialogMessages();
		String[] options = { "Connect", "Cancel" };

		/*
		 * Shows a dialog to the user. Execution is waiting until the user
		 * selects an option.
		 */
		int result = JOptionPane.showOptionDialog(this, // the parent that the
				// dialog blocks
				messages, // the dialog message array
				"Connection parameters", // the title of the dialog window
				JOptionPane.DEFAULT_OPTION, // option type
				JOptionPane.QUESTION_MESSAGE, // message type
				null, // optional icon, use null to use the default icon
				options, // options string array, will be made into buttons
				options[0] // option that should be made into a default button
				);

		/*
		 * If the user presses the first ("Connect") button
		 */
		if (result == 0) {
			String messageHeader = "Connecting remote database.";

			int port;
			String host;
			try {
				this.logger.log(Level.INFO, messageHeader);
				/*
				 * Get entered port value. NumberFormatException can be thrown.
				 */
				port = Integer.parseInt(this.portField.getText());

				/*
				 * Entered host address
				 */
				host = this.addressField.getText();
				if (host.equals("")) {
					host = "localhost";
				}
			} catch (NumberFormatException e) {
				warn("Port must be a number");
				return;
			}
			try {
				/*
				 * Update Business. Common behaviour for local and remote
				 * databases
				 */
				DBAccess dbAccess = Client.connect(host, port);
				updateBusiness(new Business(dbAccess));

				this.getMenuItemDisconnect().setEnabled(true);

				/*
				 * Show server information in the bottom panel.
				 */
				this.frameFoot.setText("Connected to " + host + ":" + port);

				/*
				 * Log information
				 */
				messageHeader += " '" + this.frameFoot.getText() + "'";
				this.logger.log(Level.INFO, messageHeader + ". Connected");

				/*
				 * Update class fields according to PropertiesManager behaviour,
				 * for future application executions.
				 */
				lastServerHost = host;
				lastServerPort = port;

				this.setIconImage(getImageIcon(
						"suncertify/gui/images/remoteClient.gif").getImage());
				this.setTitle("URLyBird remote");

			} catch (java.net.ConnectException e) {
				this.logger.log(Level.SEVERE, messageHeader + ". "
						+ e.toString(), e);
				error("Can not connect to " + host + ":" + port
						+ ".\n Please ensure the server is running");
			} catch (Exception e) {
				this.logger.log(Level.SEVERE, messageHeader + ". "
						+ e.toString(), e);
				error("An error has occurred:\nSee log for more details.");
			}
		}
	}

	/**
	 * Shows a dialog to the user, for connecting to a local database.
	 */
	public void openLocalDB() {
		String messageHeader = "Opening local db.";
		this.logger.log(Level.INFO, messageHeader);

		/*
		 * Shows a dialog to the user. The thread is blocked until the user
		 * makes a selection.
		 */
		File file = this.dbFileChooser.getFile(lastLocalDBFileName);
		if (file != null) {
			try {
				String path = file.getAbsolutePath();

				URLyBirdFile_1_2_1 uf = new URLyBirdFile_1_2_1(file);
				DefaultFinderImp finder = new DefaultFinderImp(uf);
				DefaultLockManagerImpl<Long> lockManagerImpl = new DefaultLockManagerImpl<Long>();

				/*
				 * Update dBAccess. Common behaviour for local and remote
				 * databases
				 */
				DBAccess dbAccess = new Data(uf, finder, lockManagerImpl);
				updateBusiness(new Business(dbAccess));

				this.getMenuItemClose().setEnabled(true);

				this.logger.log(Level.INFO, "Local DB '" + path
						+ "' opened successfully.");

				/*
				 * Show database information in the bottom panel.
				 */
				this.frameFoot.setText(path);

				/*
				 * Update class parameters according to PropertiesManager
				 * behaviour.
				 */
				lastLocalDBFileName = path;

				this.setIconImage(getImageIcon(
						"suncertify/gui/images/localClient.gif").getImage());
				this.setTitle("URLyBird local");

			} catch (FileNotFoundException e) {
				closeDB();
				warn("Error setting up database.\nEnsure that the file "
						+ "exists and can be written.");
				this.logger.log(Level.SEVERE, messageHeader + ". "
						+ e.toString(), e);
			} catch (IOException e) {
				closeDB();
				warn("Error setting up database.\n"
						+ "Possibly corrupted database file.\n"
						+ "Please see logs for more details");
				this.logger.log(Level.SEVERE, messageHeader + ". "
						+ e.toString(), e);
			} catch (NotSupportedDataBaseException e) {
				closeDB();
				warn("Database file format not supported.");
				this.logger.log(Level.SEVERE, messageHeader + ". "
						+ e.toString(), e);
			} catch (Exception e) {
				closeDB();
				error("An error has occurred.\n See log for more details.");
				this.logger.log(Level.SEVERE, messageHeader + ". "
						+ e.toString(), e);
			}
		}
	}

	@Override
	protected String getHelpAnchorName() {
		return "client";
	}
	
	/*
	 * Performs a search returning all rows in database.
	 */
	void searchAll() {
		try {
			this.doSearch(null, null, Condition.MATCH_BOTH);
		} catch (Exception e) {
			handleServerException(e);
		}
	}

	/*
	 * Opens the search dialog to the user and performs a search.
	 */
	void search() {
		try {
			if (this.mediator != null) {
				Object[] messages = getSearchDialogMessages();
				String[] options = { "Search", "Cancel" };

				/*
				 * Shows a dialog to the user. Execution is waiting until the
				 * user selects an option.
				 */
				int result = JOptionPane
						.showOptionDialog(this, // the parent
								// that
								// the
								// dialog blocks
								messages, // the dialog message array
								"Search hotels where:", // the title of the
								// dialog
								// window
								JOptionPane.DEFAULT_OPTION, // option type
								JOptionPane.QUESTION_MESSAGE, // message type
								// optional icon, use null to use the default
								getImageIcon("suncertify/gui/images/icons_sun/Search24.gif"),
								// icon
								options, // options string array, will be
								// made into
								// buttons
								options[0] // option that should be made into a
						// default
						// button
						);

				// Pressed search option
				if (result == 0) {
					this
							.doSearch(
									getSearchNameField().getText(),
									getSearchLocationField().getText(),
									getSearchConditionCombo()
											.getSelectedIndex() == 1 ? Condition.MATCH_BOTH
											: Condition.MATCH_ANY);
				}
			}
		} catch (Exception e) {
			handleServerException(e);
		}
	}

	/*
	 * Repeats the last successful search.
	 */
	void refresh() {
		if (this.mediator != null && this.lastSearch != null) {
			try {
				this.mediator.executeCommandSync(this.lastSearch);
			} catch (RoomNotFoundException e) {
				warn("All the records returned in last search have been deleted.\n"
						+ "Perform a new search to update de result table");
			} catch (Exception e) {
				handleServerException(e);
			}
		}
	}

	/*
	 * For the selected table row: Locks the record, opens a dialog to the user,
	 * and finally unlocks the record.
	 */
	@SuppressWarnings("boxing")
	void book() {
		if (this.tableBean != null) {
			try {
				LockCommand lCmd = new LockCommand(this.selectedRoom.getId());
				long cookie = this.mediator.executeCommandSync(lCmd);
				try {
					String customerID = JOptionPane.showInputDialog(this,
							"Enter customer ID:", "Booking room with id: "
									+ this.selectedRoom.getId(),
							JOptionPane.QUESTION_MESSAGE);
					if (customerID == null) {
						return;
					}
					BookCommand bCmd = new BookCommand(this.selectedRoom
							.getId(), customerID, cookie);
					this.mediator.executeCommandSync(bCmd);
					refresh();
				} finally {
					try {
						UnlockCommand uCmd = new UnlockCommand(
								this.selectedRoom.getId(), cookie);
						this.mediator.executeCommandSync(uCmd);
					} catch (SecurityException e) {
						/*
						 * This should not be run
						 */
						assert (false);
					}
				}
			} catch (Exception e) {
				handleServerException(e);
			}
		}
	}

	/*
	 * Common exception handling.
	 */
	void handleServerException(Exception e) {

		Throwable th = e.getCause();
		if (th == null) {
			th = e;
		}
		Level level = null;
		String msg = null;
		if (th instanceof RoomNotFoundException) {
			level = Level.INFO;
			msg = "Room can not be found.\nPress ok to refresh data";
			alert(msg);
			refresh();
		} else if (th instanceof ConnectException) {
			level = Level.WARNING;
			msg = "Server connection lost.\nDatabase session will be closed";
			warn(msg);
			closeDB();
		} else if (th instanceof AlreadyBookedException) {
			level = Level.WARNING;
			msg = "Room has been booked externaly.\nView will be refreshed";
			warn(msg);
			refresh();
		} else if (th instanceof IllegalStateException) {
			level = Level.WARNING;
			warn("Server illegal state:\n" + th.getMessage());
			closeDB();
		} else if (th instanceof IllegalArgumentException) {
			level = Level.WARNING;
			warn(th.getMessage());
		} else if (th instanceof InterruptedException) {
			level = Level.WARNING;
			warn("Server request has been interrupted.\nPlease try again");
		} else {
			level = Level.SEVERE;
			if(th.getMessage()!=null){
				msg = "The following error has occurred:\n" + th.getMessage();
			} else {
				msg = "An unkown error has occurred.\n See logs for more details";
			}
			
			error(msg);
		}
		this.logger.log(level, msg, th);
	}

	/*
	 * Helper method. Closes current database session and updates the GUI.
	 */
	void closeDB() {

		this.logger.log(Level.INFO, "Database closed");
		this.mediator = null;
		this.getFrameFoot().setText("");
		this.getMenuItemOpen().setEnabled(true);
		this.getMenuItemConnect().setEnabled(true);
		this.getMenuItemClose().setEnabled(false);
		this.getMenuItemDisconnect().setEnabled(false);
		this.getMenuItemSearch().setEnabled(false);
		this.getMenuItemSearchAll().setEnabled(false);
		this.getMenuItemRefresh().setEnabled(false);

		this.setIconImage(getImageIcon("suncertify/gui/images/clientOff.gif")
				.getImage());
		this.setTitle("URLyBird");
		if (this.tableBean != null) {
			this.tableBean.removeSelectionListener(this);
			this.tableBean = null;
		}

		this.getCenterPanel().removeAll();
		this.repaint();

		System.gc();
	}
	
	/*
	 * (non-Javadoc)
	 * @see suncertify.gui.BaseGUI#addMenusToMenuBar(javax.swing.JMenuBar)
	 */
	@Override
	protected void addMenusToMenuBar(JMenuBar menuBar) {
		menuBar.add(getMenuSearch());
	}
	
	/*
	 * (non-Javadoc)
	 * @see suncertify.gui.BaseGUI#addMenuItemsToFileMenu(javax.swing.JMenu)
	 */
	@Override
	protected void addMenuItemsToFileMenu(JMenu fileMenu) {
		fileMenu.add(this.getMenuItemOpen());
		fileMenu.add(this.getMenuItemClose());
		fileMenu.add(new JPopupMenu.Separator());
		fileMenu.add(this.getMenuItemConnect());
		fileMenu.add(this.getMenuItemDisconnect());
		fileMenu.add(new JPopupMenu.Separator());
		fileMenu.add(this.getMenuItemRefresh());
		fileMenu.add(new JPopupMenu.Separator());
	}
	
	/*
	 * Called just before exit the application. Exit skipped if returns false.
	 */
	@Override
	protected boolean doBeforeExit() {
		lastWindowExtendedState = getExtendedState();
		return true;
	}

	/*
	 * Search helper method. Creates a seach command, and requests the mediator
	 * to execute it synchronously. If the search returns ok, the command is
	 * remembered to be used again in refresh()
	 */
	private void doSearch(String name, String location, Condition condition)
			throws Exception {
	
		SearchCommand cmd = new SearchCommand(name, location, condition);
		this.mediator.executeCommandSync(cmd);
		this.lastSearch = cmd;
	}

	/*
	 * Called on database connection
	 */
	private void updateBusiness(Business business) {

		/*
		 * Create a new mediator for the new business instance.
		 */
		this.mediator = new Mediator(business);

		/*
		 * Enable/disable controls
		 */
		this.getMenuItemOpen().setEnabled(false);
		this.getMenuItemConnect().setEnabled(false);
		this.getBookButton().setEnabled(false);
		this.getMenuItemSearch().setEnabled(true);
		this.getMenuItemSearchAll().setEnabled(true);
		this.getMenuItemRefresh().setEnabled(true);

		/*
		 * Initialize a new table bean and register the GUI to listen to its
		 * events.
		 */

		this.tableBean = new TableBean(this.mediator);
		this.tableBean.addListener(this);

		/*
		 * Add content to CenterPanel.
		 */
		this.getCenterPanel().add(this.tableBean, BorderLayout.CENTER);
		this.getCenterPanel().add(getFootButtonsPanel(), BorderLayout.SOUTH);

		/*
		 * Show all records
		 */
		searchAll();

		this.repaint();

	}

	/*
	 * Called once in a client GUI instance (from constructor).
	 */
	private void initComponents() {

		this.setIconImage(getImageIcon("suncertify/gui/images/clientOff.gif")
				.getImage());
		this.setTitle("URLyBird");

		/*
		 * On window resize save the window size
		 */
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (getExtendedState() == Frame.NORMAL) {
					ClientGUI.lastWindowWidth = getWidth();
					ClientGUI.lastWindowHeight = getHeight();
				}
			}
		});
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.getCenterPanel(), BorderLayout.CENTER);
		this.getContentPane().add(this.getFrameFoot(),
				java.awt.BorderLayout.SOUTH);
		
		this.setEnabled(true);
		pack();
		center(this, lastWindowWidth, lastWindowHeight);	
		this.setExtendedState(lastWindowExtendedState);
		this.setVisible(true);
	}

	/*
	 * Helper method to create a button instance
	 */
	private JButton createButtonFromMenuItem(JMenuItem menuItem) {
		JButton button = new JButton(menuItem.getIcon());
		button.setToolTipText(menuItem.getToolTipText());
		button.setEnabled(menuItem.isEnabled());
		for (ActionListener listener : menuItem.getActionListeners()) {
			button.addActionListener(listener);
		}
		return button;
	}

	private JTextField getAddressField() {
		if (this.addressField == null) {
			this.addressField = new JTextField(lastServerHost);
		}
		return this.addressField;
	}

	private JTextField getPortField() {
		if (this.portField == null) {
			this.portField = new JTextField("" + lastServerPort);
		}
		return this.portField;
	}

	/*
	 * Construct an object array, for entering remote DB properties in a option
	 * dialog.
	 */
	private Object[] getRemoteDialogMessages() {
		if (this.remoteDialogMessages == null) {
			this.remoteDialogMessages = new Object[6];
			this.remoteDialogMessages[0] = "Server address:";
			this.remoteDialogMessages[1] = this.getAddressField();
			this.remoteDialogMessages[2] = "Server port:";
			this.remoteDialogMessages[3] = this.getPortField();
		}

		return this.remoteDialogMessages;
	}

	private JTextField getSearchNameField() {
		if (this.searchNameField == null) {
			this.searchNameField = new JTextField();
		}
		return this.searchNameField;
	}

	private JTextField getSearchLocationField() {
		if (this.searchLocationField == null) {
			this.searchLocationField = new JTextField();
		}
		return this.searchLocationField;
	}

	private JComboBox getSearchConditionCombo() {
		if (this.searchConditionCombo == null) {
			this.searchConditionCombo = new JComboBox(new String[] { "or",
					"and" });
		}
		return this.searchConditionCombo;

	}

	private Object[] getSearchDialogMessages() {
		if (this.searchDialogMessages == null) {
			this.searchDialogMessages = new Object[6];
			this.searchDialogMessages[0] = "Hotel name equals to ";
			this.searchDialogMessages[1] = this.getSearchNameField();
			this.searchDialogMessages[3] = this.getSearchConditionCombo();
			this.searchDialogMessages[4] = "Location equals to";
			this.searchDialogMessages[5] = this.getSearchLocationField();
		}

		return this.searchDialogMessages;
	}

	private JButton getRefreshButton() {
		if (this.refreshBt == null) {
			this.refreshBt = createButtonFromMenuItem(this.getMenuItemRefresh());
			this.refreshBt.setText(this.getMenuItemRefresh().getText());
		}
		return this.refreshBt;
	}

	private JButton getBookButton() {
		if (this.bookBt == null) {
			this.bookBt = new JButton("Book room");
			this.bookBt.setToolTipText("Book room to a customer");
			this.bookBt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					book();
				}
			});
		}
		return this.bookBt;
	}


	private JMenu getMenuSearch() {
		if (this.menuSearch == null) {
			this.menuSearch = new JMenu("Search");
			this.menuSearch.add(this.getMenuItemSearchAll());
			this.menuSearch.add(this.getMenuItemSearch());
			this.menuSearch.setMnemonic('S');
		}
		return this.menuSearch;
	}

	
	private JMenuItem getMenuItemOpen() {
		if (this.menuItemOpen == null) {
			this.menuItemOpen = createMenuItem("Open",
					getImageIcon("suncertify/gui/images/icons_sun/Open16.gif"),
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							openLocalDB();
						}
					}, true);
			this.menuItemOpen.setToolTipText("Open a local database file");
		}
		return this.menuItemOpen;

	}

	private JMenuItem getMenuItemClose() {
		if (this.menuItemClose == null) {
			this.menuItemClose = createMenuItem("Close", null, this
					.getCloseActionListener(), false);
			this.menuItemClose.setToolTipText("Close current database file");
		}
		return this.menuItemClose;

	}

	private JMenuItem getMenuItemConnect() {
		if (this.menuItemConnect == null) {
			this.menuItemConnect = createMenuItem("Connect", null,
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							openRemoteDB();
						}
					}, true);
			this.menuItemConnect
					.setToolTipText("Connect to remote database server");
		}
		return this.menuItemConnect;

	}

	private JMenuItem getMenuItemDisconnect() {
		if (this.menuItemDisconnect == null) {
			this.menuItemDisconnect = createMenuItem("Disconnect", null, this
					.getCloseActionListener(), false);
			this.menuItemDisconnect
					.setToolTipText("Disconnect from remote database server");
		}
		return this.menuItemDisconnect;
	}

	private JMenuItem getMenuItemRefresh() {
		if (this.menuItemRefresh == null) {
			this.menuItemRefresh = createMenuItem(
					"Refresh",
					getImageIcon("suncertify/gui/images/icons_sun/Refresh16.gif"),
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							refresh();
						}
					}, false);
			this.menuItemRefresh.setToolTipText("Refresh results");
			KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);
			this.menuItemRefresh.setAccelerator(keyStroke);
		}
		return this.menuItemRefresh;
	}

	private JMenuItem getMenuItemSearch() {
		if (this.menuItemSearch == null) {
			this.menuItemSearch = createMenuItem(
					"Search",
					getImageIcon("suncertify/gui/images/icons_sun/Search16.gif"),
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							search();
						}
					}, false);
			this.menuItemSearch
					.setToolTipText("Search rooms by specified criteria");
			KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F,
					Event.CTRL_MASK);
			this.menuItemSearch.setAccelerator(keyStroke);
		}
		return this.menuItemSearch;

	}

	private JMenuItem getMenuItemSearchAll() {
		if (this.menuItemSearchAll == null) {
			this.menuItemSearchAll = createMenuItem("View all database", null,
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							searchAll();
						}
					}, false);
			this.menuItemSearchAll.setToolTipText("View all rooms in database");
			KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_D,
					Event.CTRL_MASK);
			this.menuItemSearchAll.setAccelerator(keyStroke);
		}
		return this.menuItemSearchAll;

	}

	private ActionListener getCloseActionListener() {
		if (this.closeActionListener == null) {
			this.closeActionListener = new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					closeDB();
				}
			};
		}
		return this.closeActionListener;
	}

	private JPanel getFootButtonsPanel() {
		if (this.footButtonsPanel == null) {
			this.footButtonsPanel = new JPanel();
			Border border = BorderFactory.createEmptyBorder(4, 0, 0, 0);
			this.footButtonsPanel.setBorder(border);
			this.footButtonsPanel.setLayout(new BorderLayout());
			this.footButtonsPanel.add(getFootLeftButtonsPanel(),
					BorderLayout.WEST);
			this.footButtonsPanel.add(getFootRightButtonsPanel(),
					BorderLayout.EAST);
		}
		return this.footButtonsPanel;
	}

	private JPanel getFootRightButtonsPanel() {
		if (this.footRightButtonsPanel == null) {
			this.footRightButtonsPanel = new JPanel();
			this.footRightButtonsPanel.setLayout(new BorderLayout());
			this.footRightButtonsPanel.add(getBookButton());
		}
		return this.footRightButtonsPanel;
	}

	private JPanel getFootLeftButtonsPanel() {
		if (this.footLeftButtonsPanel == null) {
			this.footLeftButtonsPanel = new JPanel();
			this.footLeftButtonsPanel.setLayout(new BorderLayout());
			this.footLeftButtonsPanel.add(getRefreshButton());
		}
		return this.footLeftButtonsPanel;
	}

	private JTextField getFrameFoot() {
		if (this.frameFoot == null) {
			this.frameFoot = new JTextField();
			this.frameFoot.setEditable(false);
		}
		return this.frameFoot;
	}

	private JPanel getCenterPanel() {
		if (this.centerPanel == null) {
			this.centerPanel = new JPanel();
			this.centerPanel.setLayout(new BorderLayout());
			Border border = BorderFactory.createEmptyBorder(8, 8, 8, 8);
			this.centerPanel.setBorder(border);
		}
		return this.centerPanel;
	}

}
