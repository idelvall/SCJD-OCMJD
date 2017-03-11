/*
 * HotelRoom.java 06/10/2010
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

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import suncertify.commons.LoggingObject;

/**
 * UrlyBird Business Object. Business representation of a database record.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 06/10/2010
 * 
 */
public class HotelRoom extends LoggingObject implements Comparable<HotelRoom> {

	private final long id;
	private final String[] record;

	private final String hotelName;
	private final String city;
	private final int size;
	private final boolean smokingAllowed;
	private final BigDecimal price;
	private final Date dateAvailable;
	private String customer;

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy/MM/dd");

	/**
	 * Creates a new instance.
	 * 
	 * @param id
	 *            also referred as roomNo, the number identifying number.
	 * @param record
	 *            record raw data obtained from data access layer.
	 */
	public HotelRoom(long id, String[] record) {

		this.id = id;
		this.record = record;

		this.hotelName = record[0];
		this.city = record[1];
		this.size = Integer.parseInt(record[2]);
		this.smokingAllowed = record[3].equals("Y");
		this.price = new BigDecimal(record[4].replaceAll("\\$", ""));
		Date date = null;
		try {
			date = DATE_FORMAT.parse(record[5]);
		} catch (ParseException e) {
			this.logger.log(Level.SEVERE, e.getMessage(), e);
		}
		this.dateAvailable = date;
		this.customer = record[6];
	}

	/**
	 * Id getter.
	 * 
	 * @return the id of the room.
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Name getter.
	 * 
	 * @return the name of the hotel.
	 */
	public String getHotelName() {
		return this.hotelName;
	}

	/**
	 * Return the hotel location.
	 * 
	 * @return the city where the hotel is.
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * Return the room's size.
	 * 
	 * @return the maximum occupancy of the room.
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * 
	 * @return <code>true</code> if smoking is allowed in the room.
	 *         <code>false</code> otherwise.
	 */
	public boolean isSmokingAllowed() {
		return this.smokingAllowed;
	}

	/**
	 * Return the price per night.
	 * 
	 * @return the price
	 */
	public BigDecimal getPrice() {
		return this.price;
	}

	/**
	 * Returns the date where the room starts to be available.
	 * 
	 * @return the date.
	 */
	public Date getDateAvailable() {
		return this.dateAvailable;
	}

	/**
	 * Customer who has booked the room.
	 * 
	 * @return the customer id. An empty String if the room is available.
	 */
	public String getCustomer() {
		return this.customer;
	}

	/**
	 * Updates to customer who holds the booking.
	 * 
	 * @param customer
	 *            customer id to assign the room to.
	 */
	public void setCustomer(String customer) {
		this.customer = customer;
		this.record[6] = customer;
	}

	/**
	 * Informs if the room is booked or available.
	 * 
	 * @return <code>true</code> if the room is booked, <code>false</code> if
	 *         not.
	 */
	public boolean isBooked() {
		return this.customer != null && this.customer.length() > 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof HotelRoom)) {
			return false;
		}
		HotelRoom other = (HotelRoom) obj;

		return this.getId() == other.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return (int) this.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(HotelRoom other) {
		return (int) (this.getId() - other.getId());
	}

	/**
	 * Return the data access raw data. Usually used to interact from BLL to
	 * DAL. This method is visible only in the same package level, in order to
	 * decouple clients from data layer since business layer must not depend
	 * upon wrapped raw data.
	 * 
	 * @return an array with the field values of the record.
	 */
	String[] getRecord() {
		return this.record;
	}

}
