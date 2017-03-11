/*
 * GUIBean.java 11/10/2010
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


package suncertify.gui.client;

import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JScrollPane;

import suncertify.bs.Business;
import suncertify.bs.commands.BusinessCommand;
import suncertify.bs.commands.CommandListener;


/**
 * An abstract class to extend in order to develop GUI components, that integrate in the client
 * GUI, and perform and are notified of {@link Business} invocations.<br><br>
 * All business interactions are made via a {@link Mediator}. This basic architecture facilitates
 * beans interconnection.
 * 
 * @see suncertify.gui.client.Mediator
 * @author Ignacio del Valle Alles
 * @version 1.0 11/10/2010
 */
public abstract class GUIBean extends JScrollPane implements CommandListener<BusinessCommand<?>> {
	
	private static final long serialVersionUID = 4603878015537714653L;
	
	protected final Mediator mediator;
	protected final Logger logger;
	
	/**
	 * Creates a new instance from the specified mediator and automatically subscribes to
	 * it as a listener. 
	 * 
	 * @param mediator the Mediator instance.
	 */
	public GUIBean(Mediator mediator) {
		this.mediator = mediator;
		this.logger = Logger.getLogger(getClass().getName());
		mediator.addCommandListener(this);
	}
	
	/**
	 * Returns the bean icon.
	 * @return the icon
	 */
	public Icon getIcon() {
		return null;
	}
	
	/**
	 * Returns the bean tooltip.
	 * @return the tooltip
	 */
	public String getToolTip() {
		return null;
	}

	/**
	 * Returns the bean title.
	 * @return the title
	 */
	public String getTitle() {
		return null;
	}

	/*
	 * Logger getter
	 */
	protected final Logger getLogger() {
		return this.logger;
	}
}
