/*
 * Business.java 06/10/2010
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


package suncertify.bs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import suncertify.commons.LoggingObject;
import suncertify.commons.Miscellaneous;
import suncertify.db.DBAccess;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;


/**
 * Implements the business operations performed by the user in the client GUI.
 * Business instances wrap a {@link DBAccess} object used to interact with the
 * database.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 06/10/2010
 * 
 */
public class Business extends LoggingObject {

	/**
	 * Search conditions.
	 * 
	 * @author Ignacio del Valle Alles
	 * @version 1.0 10/10/2010
	 * 
	 */
	public enum Condition {
		/**
		 * Search rooms matching any of the specified criteria.
		 */
		MATCH_ANY,

		/**
		 * Search rooms matching all the specified criteria.
		 */
		MATCH_BOTH;
	}

	// Delegate
	private final DBAccess dbAccess;

	/**
	 * Creates a new instance.
	 * 
	 * @param dbAccess
	 *            the wrapped object to interact with the database.
	 */
	public Business(DBAccess dbAccess) {
		this.dbAccess = dbAccess;
	}

	/**
	 * Search for hotel rooms where the name and/or location fields exactly match
	 * values specified by the user.<br>
	 * <code>null</code> values match any record.
	 * 
	 * @param name the hotel name.
	 * @param location the hotel location.
	 * @param condition
	 *            determines if the condition is "and" 
	 *            ({@link Condition#MATCH_BOTH}) or "or" 
	 *            ({@link Condition#MATCH_ANY})
	 * @return a list of matching rooms.
	 * @throws RoomNotFoundException if there is no matchings.
	 */
	public List<HotelRoom> search(String name, String location,
			Condition condition) throws RoomNotFoundException {

		List<HotelRoom> ret;
		if (condition.equals(Condition.MATCH_BOTH)) {
			ret = searchBothCriteria(name, location);
		} else {
			ret = searchAnyCriteria(name, location);
		}
		if (ret == null || ret.size() == 0) {
			throw new RoomNotFoundException();
		}
		return ret;

	}

	/**
	 * Delegates to {@link DBAccess#lockRecord(long)}.
	 * 
	 * @param roomNo the room id.
	 * @return a cookie value. A long that demonstrate the lock ownership.
	 * @throws RoomNotFoundException if the room does not exist.
	 */
	public long lock(long roomNo) throws RoomNotFoundException {
		try {

			this.logger.log(Level.INFO, "Locking room number " + roomNo);
			long cookie = this.dbAccess.lockRecord(roomNo);
			this.logger.log(Level.INFO, "Lock adquired for room number "
					+ roomNo + ", cookie=" + cookie);
			return cookie;
		} catch (RecordNotFoundException e) {
			throw new RoomNotFoundException(e);
		}

	}

	/**
	 * Delegates to {@link DBAccess#unlock(long, long)}.
	 * 
	 * @param roomNo the room number.
	 * @param cookie the result of <code>lockRecord(roomNo)</code> execution.
	 * @throws SecurityException if the cookie is not valid.
	 */
	public void unlock(long roomNo, long cookie)
			throws SecurityException {

		this.logger.log(Level.INFO, "Unlocking room number " + roomNo);
		this.dbAccess.unlock(roomNo, cookie);
		this.logger.log(Level.INFO, "Lock released for room number " + roomNo);

	}

	/**
	 * Books a room to a customer. The room must have been previously locked.
	 * 
	 * @param roomNo the room number.
	 * @param customerID customer identifying number.
	 * @param cookie returned by <code>lock()</code> call.
	 * @throws RoomNotFoundException if the room does not exist.
	 * @throws SecurityException if the cookie is not valid.
	 * @throws AlreadyBookedException if the room is already booked.
	 */
	public void book(long roomNo, String customerID, long cookie)
			throws RoomNotFoundException, SecurityException,
			AlreadyBookedException {

		validateCustomerId(customerID);

		try {
			// Log the operation
			this.logger.log(Level.INFO, "Booking room number " + roomNo
					+ " to customer " + customerID);
			// Read all room fields.
			String[] record = this.dbAccess.readRecord(roomNo);

			// Creates a new business object
			HotelRoom room = new HotelRoom(roomNo, record);

			// Check its assigned customer.
			if (room.getCustomer().length() > 0) {
				throw new AlreadyBookedException("Room " + roomNo
						+ " is already booked!");
			}
			room.setCustomer(customerID);
			
			// Update the room in database
			this.dbAccess.updateRecord(roomNo, room.getRecord(), cookie);

			this.logger.log(Level.INFO, "Room number " + roomNo
					+ " successfully booked to customer " + customerID);
		} catch (RecordNotFoundException e) {
			throw new RoomNotFoundException(e);
		}

	}

	/*
	 * Validates that customerId is an 8 digit number.
	 */
	private void validateCustomerId(String customerID) {
		boolean correct = true;
		if (customerID == null || customerID.trim().length() != 8) {
			correct = false;
		}
		try {
			Integer.parseInt(customerID);
		} catch (NumberFormatException e) {
			correct = false;
		}
		if (!correct) {
			throw new IllegalArgumentException(
					"Customer ID must be an 8 digit number");
		}
	}
	
	/*
	 * Returns all the rooms that matches both criteria, name and location.
	 * <code>null</code> values match any record.
	 */
	private List<HotelRoom> searchBothCriteria(String name, String location) {

		String[] searchFields = new String[] { name, location };
		// Construct logging message
		String cad = "{" + Miscellaneous.arrayToString(searchFields) + "}";
		// Log operation
		this.logger.log(Level.INFO, "Searching for " + cad);
		/*
		 * First obtain record ids. This method does not return exact matching needed,
		 * (see findByCriteria comments) so more logic is needed.
		 */
	
		long[] recordsID = this.dbAccess.findByCriteria(searchFields);
		// Build the result object to create the FindEvent

		this.logger.log(Level.INFO, recordsID.length + " coincidences found");

		if (recordsID.length == 0) {
			return null;
		}

		List<HotelRoom> matchingRecords = new ArrayList<HotelRoom>();

		for (int i = 0; i < recordsID.length; i++) {
			try {
				// Read each of the matching records
				String[] fieldValues = this.dbAccess.readRecord(recordsID[i]);

				if ((name == null || fieldValues[0].equals(name))
						&& (location == null || fieldValues[1].equals(location))) {

					matchingRecords
							.add(new HotelRoom(recordsID[i], fieldValues));
				}
			} catch (RecordNotFoundException e) {
				this.logger.log(Level.WARNING, e.getMessage(), e);
			}
		}

		return matchingRecords;

	}

	/*
	 * Search rooms matching name or location.
	 */
	private List<HotelRoom> searchAnyCriteria(String name, String location) {
		
		/*
		 * Perform two searches, one for {name,null} and other for 
		 * {null,location}
		 */
		List<HotelRoom> nameMatchingRecords = null;
		List<HotelRoom> locationMatchingRecords = null;
		if (name != null) {
			nameMatchingRecords = searchBothCriteria(name, null);
		}
		if (location != null) {
			locationMatchingRecords = searchBothCriteria(null, location);
		}
		
		/*
		 * A set will be used to discard duplicates. Key here is overwritten
		 * equals() and hash() methods in HotelRoom class.
		 */
		Set<HotelRoom> nonRepeatedRecords;
		if (nameMatchingRecords == null) {
			if (locationMatchingRecords == null) {
				nonRepeatedRecords = null;
			} else {
				nonRepeatedRecords = new HashSet<HotelRoom>(
						locationMatchingRecords);
			}
		} else if (locationMatchingRecords == null) {
			nonRepeatedRecords = new HashSet<HotelRoom>(nameMatchingRecords);

		// Both resultsets are not null
		} else {
			nonRepeatedRecords = new LinkedHashSet<HotelRoom>(
					nameMatchingRecords);
			for (HotelRoom locationMatchingRecord : locationMatchingRecords) {
				
				// Set discards duplicates
				nonRepeatedRecords.add(locationMatchingRecord);
			}
		}
		if (nonRepeatedRecords == null) {
			return null;
		}
		// Return an orderer list. See HotelRoom.compareTo() method.
		List<HotelRoom> list = new ArrayList<HotelRoom>(nonRepeatedRecords);
		Collections.sort(list);
		return list;

	}
}
