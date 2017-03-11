/*
 * RemoteDataAdapter.java 30/09/2010
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


package suncertify.client;

import java.rmi.RemoteException;

import suncertify.commons.LoggingObject;
import suncertify.db.DBAccess;
import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;
import suncertify.db.impl.RuntimeIOException;
import suncertify.server.spec.RemoteDBAccess;


/**
 * Adapts {@link RemoteDBAccess} instances to {@link DBAccess} interface.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 30/09/2010
 *
 */
public class RemoteDataAdapter extends LoggingObject implements DBAccess {

	private final RemoteDBAccess remoteDBAccess;
	
	/**
	 * Creates adapted instance from the specified remote version.
	 *  
	 * @param remoteDBAccess the remote instance
	 */
	public RemoteDataAdapter(RemoteDBAccess remoteDBAccess){
		this.remoteDBAccess = remoteDBAccess;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long createRecord(String[] data) throws DuplicateKeyException {

		try {
			return this.remoteDBAccess.createRecord(data);
		} catch (RemoteException e) {
			throw new RuntimeIOException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteRecord(long recNo, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		
		try {
			this.remoteDBAccess.deleteRecord(recNo, lockCookie);
		} catch (RemoteException e) {
			throw new RuntimeIOException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long[] findByCriteria(String[] criteria) {
		
		try {
			return this.remoteDBAccess.findByCriteria(criteria);
		} catch (RemoteException e) {
			throw new RuntimeIOException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long lockRecord(long recNo) throws RecordNotFoundException {
		
		try {
			return this.remoteDBAccess.lockRecord(recNo);
		} catch (RemoteException e) {
			throw new RuntimeIOException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] readRecord(long recNo) throws RecordNotFoundException {
		
		try {
			return this.remoteDBAccess.readRecord(recNo);
		} catch (RemoteException e) {
			throw new RuntimeIOException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unlock(long recNo, long cookie) throws SecurityException {
		
		try {
			this.remoteDBAccess.unlock(recNo, cookie);
		} catch (RemoteException e) {
			throw new RuntimeIOException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateRecord(long recNo, String[] data, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		
		try {
			this.remoteDBAccess.updateRecord(recNo, data, lockCookie);
		} catch (RemoteException e) {
			throw new RuntimeIOException(e);
		}

	}

}
