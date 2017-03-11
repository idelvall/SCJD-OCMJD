/*
 * TableBeanSelectionListener.java 06/10/2010
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


package suncertify.gui.client.beans;

import suncertify.bs.HotelRoom;


/**
 * The listener interface for receiving notification from the {@link TableBean}
 * events.
 * 
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 06/10/2010
 */
public interface TableBeanListener {

	/**
	 * Fired when an action is performed by the user on a room.
	 * 
	 * @param room the room
	 */
	public void actionPerformed(HotelRoom room);

	/**
	 * Fired when the selected room in the {@link TableBean} changes.
	 * 
	 * @param room the new selected room
	 */
	public void selectedRoom(HotelRoom room);
}
