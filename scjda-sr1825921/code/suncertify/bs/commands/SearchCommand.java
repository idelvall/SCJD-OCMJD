/*
 * SearchCommand.java 11/10/2010
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


package suncertify.bs.commands;

import java.util.List;

import suncertify.bs.Business;
import suncertify.bs.HotelRoom;
import suncertify.bs.Business.Condition;


/**
 * {@link BusinessCommand} that encapsulates calls to
 * {@link Business#search(String, String, Condition)}.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 11/10/2010
 */
public class SearchCommand extends BusinessCommand<List<HotelRoom>> {

	private final String name;
	private final String location;
	private final Condition condition;

	/**
	 * Creates a new instance from the {@link Business#search(String, String, Condition)}
	 * specified parameters.
	 * 
	 * @param name the hotel name.
	 * @param location the hotel location.
	 * @param condition
	 *            determines if the condition is "and" 
	 *            ({@link Condition#MATCH_BOTH}) or "or" 
	 *            ({@link Condition#MATCH_ANY})
	 */
	public SearchCommand(String name, String location,
			Condition condition) {

		this.name = name;
		this.location = location;
		this.condition = condition;
	}

	/**
	 * Returns the name command parameter.
	 * 
	 * @return the name specified at creation time.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the location command parameter.
	 * 
	 * @return the location specified at creation time.
	 */
	public String getLocation() {
		return this.location;
	}
	
	/**
	 * Returns the condition command parameter.
	 * 
	 * @return the condition specified at creation time.
	 */
	public Condition getCondition() {
		return this.condition;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<HotelRoom> doExecute() throws Exception {
		return this.business.search(this.name, this.location, this.condition);
	}
	
}
