/*
 * BaseGUI.java 04/10/2010
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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import suncertify.commons.LoggingObject;
import suncertify.commons.PropertiesManager;

/**
 * Base class for creating GUI frames. Implements common behaviour to client and
 * server GUIs.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 04/10/2010
 * 
 */
public abstract class BaseGUI extends JFrame {

	private static final long serialVersionUID = 1479707322327482792L;

	/**
	 * Last help window width. This value is overwritten by
	 * {@link PropertiesManager} .
	 */
	public static int lastHelpWindowWidth = 500;

	/**
	 * Last help window heights. This value is overwritten by
	 * {@link PropertiesManager} .
	 */
	public static int lastHelpWindowHeight = 500;

	/*
	 * Tell the PropertiesManager to overwrite public static not final fields,
	 * from previous executions stored values.
	 */
	static {
		PropertiesManager.getInstance().overrideFieldValues();
	}

	/*
	 * Logger.
	 */
	protected final Logger logger = Logger.getLogger(getClass().getName());

	/*
	 * GUI components
	 */

	/*
	 * Menu bar
	 */
	private JMenuBar menuBar;

	/*
	 * Menus
	 */
	private JMenu menuFile;
	private JMenu menuHelp;

	/*
	 * Menu items
	 */

	private JMenuItem menuItemExit;

	private JMenuItem menuItemDoc;
	private JMenuItem menuItemAbout;
	private JMenuItem menuItemTroubleshotting;

	/*
	 * About dialog
	 */
	private AboutDialog aboutDialog;

	/*
	 * Help dialog
	 */
	private DocDialog docDialog;

	protected BaseGUI() {

		/*
		 * On window closing call quit() method
		 */
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				quit();
			}
		});

		this.setJMenuBar(getMenu());
	}

	/**
	 * Shows an info message to the user.
	 * 
	 * @param msg
	 *            the message String
	 */
	protected final void alert(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}

	/**
	 * Shows a warning message to the user.
	 * 
	 * @param msg
	 *            the message String
	 */
	protected final void warn(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Warning",
				JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Shows an error message to the user.
	 * 
	 * @param msg
	 *            the message String
	 */
	protected final void error(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Shows an yes/no option dialog to the user and stops current executing
	 * thread until a selection is made.
	 * 
	 * @param msg
	 *            Question message.
	 * @param title
	 *            Title of the dialog.
	 * @return <code>true</code> if the user selected yes option,
	 *         <code>false</code> otherwise.
	 */
	protected final boolean showConfirm(String msg, String title) {
		int option = JOptionPane.showConfirmDialog(this, msg, title,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		return option == JOptionPane.YES_OPTION;
	}

	/**
	 * Utility method for show the online help scrolling to the specified HTML
	 * anchor defined in the "userguide.html" file.
	 * 
	 * @param anchorName
	 *            must match an anchor name in the document.
	 */
	protected final void showUserGuide(String anchorName) {
		try {
			this.getDocDialog().goToAnchor(anchorName);
			this.getDocDialog().setVisible(true);
		} catch (IOException e) {
			error("An error has occurred while opening the user guide.\n"
					+ "Please see the logs for more details");
		}
	}

	/**
	 * Shows the about dialog (author information)
	 */
	protected final void showAboutDialog() {
		this.getAboutDialog().setVisible(true);
	}

	/**
	 * Returns the logger object.
	 * 
	 * @return
	 */
	protected final Logger getLogger() {
		return this.logger;
	}

	/**
	 * Helper method to create a menu Item.
	 * 
	 * @param name
	 *            Name of the menu item
	 * @param icon
	 *            Icon the show, <code>null</null> if no needed.
	 * @param actionListener
	 *            item action listener.
	 * @param enabled
	 *            <code>true</null> if the item is enabled by default,
	 *            <code>false</null> if not.
	 * @return the created menu item.
	 */
	protected final JMenuItem createMenuItem(String name, Icon icon,
			ActionListener actionListener, boolean enabled) {

		JMenuItem ret = new javax.swing.JMenuItem(name);
		ret.addActionListener(actionListener);
		ret.setIconTextGap(8);
		ret.setEnabled(enabled);
		ret.setIcon(icon);

		return ret;

	}

	/**
	 * Subclasses must specify the HTML anchor name in the userguide.html file
	 * to point the help document to the specific section.
	 * 
	 * @return the anchor name to point the online help of the application.
	 */
	protected abstract String getHelpAnchorName();

	/*
	 * From here, the getters with lazy-initialization for the common GUI
	 * components.
	 */

	/**
	 * Subclasses implement this method to add menus to the bar. Added menus are
	 * positioned after the default "File" menu and before the default "Help"
	 * menu.
	 * 
	 * @param menuBar bar where the menus will be added to.
	 */
	protected abstract void addMenusToMenuBar(JMenuBar menuBar);

	/**
	 * Subclasses implement this method to add items to the default "File" menu. Added items are
	 * positioned before the default "Exit" item.
	 * 
	 * @param fileMenu menu where the items will be added to.
	 */
	protected abstract void addMenuItemsToFileMenu(JMenu fileMenu);

	/**
	 * This method is called just before exiting the application, and before
	 * {@link PropertiesManager#storeValues()} is called.
	 * 
	 * @return <code>true</code> if the exit must continue. <code>false</code>
	 *         to do not exit the application.
	 */
	protected abstract boolean doBeforeExit();

	/**
	 * Called on frame closing. Exits the application.
	 */
	protected void quit() {
		if (doBeforeExit()) {
			try {
				lastHelpWindowWidth = getDocDialog().getWidth();
				lastHelpWindowHeight = getDocDialog().getHeight();
			} catch (IOException e) {
				getLogger().log(Level.SEVERE, e.getMessage(), e);
			}
			dispose();
			System.exit(0);
		}
	}

	private DocDialog getDocDialog() throws IOException {
		if (this.docDialog == null) {
			this.docDialog = new DocDialog(this);
		}
		return this.docDialog;
	}

	private AboutDialog getAboutDialog() {
		if (this.aboutDialog == null) {
			this.aboutDialog = new AboutDialog(this);
		}
		return this.aboutDialog;
	}

	private JMenuBar getMenu() {
		if (this.menuBar == null) {
			this.menuBar = new JMenuBar();
			this.menuBar.add(getMenuFile());
			addMenusToMenuBar(this.menuBar);
			this.menuBar.add(getMenuHelp());
		}
		return this.menuBar;

	}

	private JMenu getMenuFile() {
		if (this.menuFile == null) {
			this.menuFile = new JMenu("File");
			addMenuItemsToFileMenu(this.menuFile);
			this.menuFile.add(this.getMenuItemExit());
			this.menuFile.setMnemonic('F');
		}
		return this.menuFile;
	}

	private JMenu getMenuHelp() {
		if (this.menuHelp == null) {
			this.menuHelp = new JMenu("Help");
			this.menuHelp.add(this.getMenuItemDoc());
			this.menuHelp.add(new JPopupMenu.Separator());
			this.menuHelp.add(this.getMenuItemTroubleshooting());
			this.menuHelp.add(new JPopupMenu.Separator());
			this.menuHelp.add(this.getMenuItemAbout());
			this.menuHelp.setMnemonic('H');
		}
		return this.menuHelp;
	}

	private JMenuItem getMenuItemExit() {
		if (this.menuItemExit == null) {
			this.menuItemExit = createMenuItem("Exit", null,
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							quit();
						}
					}, true);
			this.menuItemExit.setToolTipText("Exit the application");
		}
		return this.menuItemExit;
	}

	private JMenuItem getMenuItemDoc() {
		if (this.menuItemDoc == null) {
			this.menuItemDoc = createMenuItem("User guide",
					getImageIcon("suncertify/gui/images/icons_sun/Help16.gif"),
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							showUserGuide(getHelpAnchorName());
						}
					}, true);
			this.menuItemDoc.setToolTipText("View user guide");
			KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
			this.menuItemDoc.setAccelerator(keyStroke);
		}
		return this.menuItemDoc;
	}

	private JMenuItem getMenuItemAbout() {
		if (this.menuItemAbout == null) {
			this.menuItemAbout = createMenuItem(
					"About",
					getImageIcon("suncertify/gui/images/icons_sun/About16.gif"),
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							showAboutDialog();
						}
					}, true);
			this.menuItemAbout.setToolTipText("Author information");
		}
		return this.menuItemAbout;
	}

	private JMenuItem getMenuItemTroubleshooting() {
		if (this.menuItemTroubleshotting == null) {
			this.menuItemTroubleshotting = createMenuItem("Troubleshooting",
					null, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							alert("For detailed execution logs, see folder:\n "
									+ LoggingObject.loggingFolder
									+ "\n\n To restore default configuration, "
									+ "delete the file:\n "
									+ PropertiesManager.getInstance()
											.getPropertiesFile()
											.getAbsolutePath());
						}
					}, true);
			this.menuItemTroubleshotting
					.setToolTipText("Troubleshotting useful information");
		}
		return this.menuItemTroubleshotting;
	}

	/**
	 * A dialog class to show the online help.
	 *
	 * @author Ignacio del Valle Alles
	 * @version 1.0 15/10/2010
	 *
	 */
	private class DocDialog extends JDialog {

		static final long serialVersionUID = -1816291282449154701L;

		private JTextPane textPane;
		private JScrollPane scrollPane;
		private String baseUrl;

		/**
		 * Creates a new instance with the specified frame as parent.
		 * 
		 * @param owner
		 *            parent frame
		 */
		DocDialog(Frame owner) throws IOException {
			super(owner, true);
			this.textPane = new JTextPane();
			
			/*
			 * The following code updates the content page on hyperlink actions.
			 */
			this.textPane.addHyperlinkListener(new HyperlinkListener() {

				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {

					if (e.getEventType() == EventType.ACTIVATED) {
						JEditorPane pane = (JEditorPane) e.getSource();
						if (e instanceof HTMLFrameHyperlinkEvent) {
							HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
							HTMLDocument doc = (HTMLDocument) pane
									.getDocument();
							doc.processHTMLFrameHyperlinkEvent(evt);
						} else {
							try {
								pane.setPage(e.getURL());
							} catch (Throwable t) {
								getLogger()
										.log(Level.SEVERE, t.getMessage(), t);
							}
						}
					}
				}
			});
			this.scrollPane = new JScrollPane();
			this.setTitle("User Guide");
			this.baseUrl = ClassLoader.getSystemResource("suncertify/gui/userguide.html")
					.toString();
			this.textPane.setPage(this.baseUrl);
			this.textPane.setEditable(false);
			this.scrollPane.setViewportView(this.textPane);
			this.setIconImage(getImageIcon(
					"suncertify/gui/images/icons_sun/Help16.gif").getImage());
			this.add(this.scrollPane);
			center(this, BaseGUI.lastHelpWindowWidth,
					BaseGUI.lastHelpWindowHeight);
		}

		/**
		 * This method lets the dialog to be parametrized externally, to point to specified document anchor.
		 * Used in help dialog opening.
		 * 
		 * @param anchorName
		 */
		void goToAnchor(String anchorName) {
			try {
				this.textPane.setPage(this.baseUrl + "#" + anchorName);
			} catch (IOException e) {
				getLogger().log(Level.SEVERE, e.getMessage(), e);
			}
		}

	}

	/**
	 * Informative dialog. Show author's information.
	 *
	 * @author Ignacio del Valle Alles
	 * @version 1.0 15/10/2010
	 *
	 */
	private class AboutDialog extends JDialog {

		static final long serialVersionUID = -6655667836987949115L;

		/**
		 * Creates a new instance with the specified frame as parent.
		 * 
		 * @param owner
		 *            parent frame
		 */
		AboutDialog(Frame owner) {
			super(owner, true);
			ImageIcon icono = getImageIcon("suncertify/gui/images/portada.gif");
			this.add(new JLabel(icono));
			center(this, 516, 362);
		}
	}
}
