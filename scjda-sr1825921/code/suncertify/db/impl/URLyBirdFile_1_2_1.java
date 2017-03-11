/*
 * URLyBirdFile_1_2_1.java 27/09/2010
 * 
 * Candidate: Ignacio del Valle Alles
 * Candidate ID: SR1825921
 * 
 * Sun Certified Developer for Java 2 Platform, Standard Edition Programming
 * Assignment (CX-310-252A)
 * 
 * This class is part of the Programming Assignment of the Sun Certified
 * Developer for Java 2 Platform, Standard Edition certification program, must
 * not be used out of this context and must be used exclusively by Oracle 
 * Corporation.
 */


package suncertify.db.impl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import suncertify.db.*;
import suncertify.db.spec.DataAccessObject;
import suncertify.commons.LoggingObject;


/**
 * Provides a simple final implementation of
 * {@link suncertify.db.spec.DataAccessObject} interface for accessing URLBird
 * 1.2.1 binary database files. <br>
 * Instances of this class are thread-safe, and so can be used by several
 * threads concurrently.
 * <p>
 * Only database files with magic cookie equal to
 * {@link #SUPPORTED_MAGIC_COOKIE} are supported, having the following format:<br>
 * <blockquote>
 * 
 * <pre>
 * &lt;i&gt;
 * &lt;b&gt;Header section:&lt;/b&gt;
 * 4 byte numeric, magic cookie value. Identifies this as a data file 
 * 4 byte numeric, total overall length in bytes of each record 
 * 2 byte numeric, number of fields in each record 
 * 
 * &lt;b&gt;Schema section:&lt;/b&gt; 
 * Repeated for each field in a record: 
 * 2 byte numeric, length in bytes of field name 
 * n bytes (defined by previous entry), field name 
 * 2 byte numeric, field length in bytes 
 * end of repeating block 
 * 
 * &lt;b&gt;Data section:&lt;/b&gt; 
 * Repeat to end of file: 
 * 1 byte &quot;deleted&quot; flag. 0 implies valid record, 1 implies deleted 
 * record 
 * Record containing fields in order specified in schema section, no 
 * separators between fields, each field fixed length at maximum specified 
 * in schema information 
 * 
 * End of file 
 * 
 * All numeric values are stored in the header information use the formats 
 * of the DataInputStream and DataOutputStream classes. All text values, 
 * and all fields (which are text only), contain only 8 bit characters,
 * null terminated if less than the maximum length for the field. The 
 * character encoding is 8 bit US ASCII. 
 * &lt;/i&gt;
 * </pre>
 * 
 * </blockquote>
 * <p>
 * Record deletion is performed logically (tagging the record as deleted).<br>
 * Deleted records are reused in record creation.
 * 
 * 
 * @author Ignacio del Valle Alles.
 * @see suncertify.db.spec.DataAccessObject
 */

public final class URLyBirdFile_1_2_1 extends LoggingObject implements
		DataAccessObject<Long, String[]> {

	/**
	 * Supported maginc cookie.
	 */
	public static final int SUPPORTED_MAGIC_COOKIE = 257;

	/*
	 * Private constants
	 */
	private static final int FLAG_FIELD_SIZE = 1;
	private static final byte DELETED_FLAG = 1;
	private static final byte VALID_FLAG = 0;
	private static final byte FLAG_LENGTH = 1;

	private static final String DB_CHARSET = "UTF-8";

	/*
	 * Instance fields section
	 */
	private long startOfSchemaSection; // file position, byte number
	private long startOfDataSection; // file position, byte number
	private final RandomAccessFile raf; // wrapped file
	private URLyBirdFileHeader header; // header section
	private DataSchema schema; // information about fields
	private long currentRecNo; // last recNo pointed.

	/**
	 * Creates a new instance associated with the specified database file.
	 * 
	 * @param file
	 *            the database file.
	 * @throws IOException if an error occurs.
	 * @throws NotSupportedDataBaseException if the file format is not supported.
	 */
	public URLyBirdFile_1_2_1(File file) throws IOException,
			NotSupportedDataBaseException {

		/*
		 * Checks if the specified file exists.
		 */
		boolean existsFile = file.exists();
		if (!existsFile) {
			throw new FileNotFoundException("Could not find file: '"
					+ file.getAbsolutePath() + "'");
		}

		/*
		 * Initialize class fields.
		 */
		this.raf = new RandomAccessFile(file, "rw");

		/*
		 * Read the following file sections.
		 */
		readHeaderSection();
		readSchemaSection();

		this.logger.log(Level.INFO,
				"URLyBirdFile instance created successfully");

	}

	/**
	 * Updates the recNo-th record of the data file.
	 * 
	 * @param recNo
	 *            the record id number.
	 * @param data
	 *            the record field values.
	 * @throws RecordNotFoundException
	 *             if the the specified recNo does not belong to any record.
	 * @throws RuntimeIOException
	 *             if an IO error occurs.
	 */
	@SuppressWarnings("boxing")
	public synchronized void update(Long recNo, String[] data)
			throws RecordNotFoundException {

		try {

			DataRecord record = readRecord(recNo);
			// Throw RecordNotFoundException if record is deleted
			verifyRecNotDeleted(record);

			// Write
			writeRecord(recNo, new DataRecord(false, data));
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}

	}

	/**
	 * Creates a new record in data file, possibly reusing a deleted entry.
	 * 
	 * @param data
	 *            record field values.
	 * @return the record id number.
	 * @throws RuntimeIOException
	 *             if an IO error occurs.
	 */
	@SuppressWarnings("boxing")
	public synchronized Long insert(String[] data) {

		try {
			/*
			 * Iterate over all records until find a deleted record
			 */
			for (long recNo = 0; recNo < getNumberOfRecords(); recNo++) {
				try {
					DataRecord record = readRecord(recNo);
					if (record.isDeleted()) {
						writeRecord(recNo, new DataRecord(false, data));
						return recNo;
					}

				} catch (RecordNotFoundException e) {
					/*
					 * This exception should not had been thrown
					 */
					assert false;
				}
			}
			/*
			 * If no deleted record is found, append a new record to the file
			 */
			return appendRecord(data);

		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}

	}

	/**
	 * Sets the recNo-th record to a <i>deleted</i> state. Makes it available
	 * for reuse.
	 * 
	 * @param recNo
	 *            the record id number.
	 * @throws RecordNotFoundException
	 *             if the the specified recNo is out of bounds.
	 * @throws RuntimeIOException
	 *             if an IO error occurs.
	 */
	@SuppressWarnings("boxing")
	public synchronized void delete(Long recNo) throws RecordNotFoundException {

		try {
			// Throw RecordNotFoundException if record is deleted
			DataRecord record = readRecord(recNo);
			verifyRecNotDeleted(record);

			// Update deleted flag
			writeRecord(recNo, new DataRecord(true, record.getFieldValues()));

		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	/**
	 * Returns the recNo-th record in the data file.
	 * 
	 * @param recNo
	 *            the record id number.
	 * @return the DataRecord object.
	 * @throws RecordNotFoundException
	 *             if the the specified recNo is out of bounds.
	 * @throws RuntimeIOException
	 *             if an IO error occurs.
	 */
	@SuppressWarnings("boxing")
	public synchronized String[] findByPrimaryKey(Long recNo)
			throws RecordNotFoundException {

		try {

			// Throw RecordNotFoundException if record is deleted
			DataRecord record = readRecord(recNo);
			verifyRecNotDeleted(record);

			return record.getFieldValues();
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	/**
	 * Returns all the records in the file
	 * 
	 * @return an array of positions
	 */
	@SuppressWarnings("boxing")
	public synchronized Long[] findAll() {
		List<Long> list = new ArrayList<Long>();

		for (long recNo = 0; recNo < getNumberOfRecords(); recNo++) {
			try {

				DataRecord record = readRecord(recNo);
				if (!record.isDeleted()) {
					list.add(recNo);
				}

			} catch (RecordNotFoundException e) {
				/*
				 * This exception should not had been thrown
				 */
				assert false;
			} catch (IOException e) {
				throw new RuntimeIOException(e);
			}
		}
		Long[] ret = new Long[list.size()];
		return list.toArray(ret);
	}

	@Override
	protected void finalize() throws Throwable {
		if (this.raf != null) {
			this.raf.close();
		}
	}

	/*
	 * Adds a new record at the end of the file.
	 */
	@SuppressWarnings("boxing")
	private Long appendRecord(String[] dataRecord) {

		try {
			long initialLength = this.raf.length();
			// Increase file length to add a new record
			this.raf.setLength(initialLength + FLAG_FIELD_SIZE
					+ this.schema.getRecordLength());

			/*
			 * Update new record (last record) content.
			 */
			try {
				update(getNumberOfRecords() - 1, dataRecord);
			} catch (RecordNotFoundException e) {
				/*
				 * This exception should not had been thrown
				 */
				assert false;
			} catch (RuntimeException e) {
				this.logger.log(Level.INFO, e.getMessage());
				this.raf.setLength(initialLength);
				throw (e);
			}
			return getNumberOfRecords() - 1;
		} catch (IOException e) {
			throw new RuntimeIOException(
					"IO Exception found when adding new record. " + e);
		}
	}

	/*
	 * Gets the number of records (deleted or not) in the data file.
	 */
	private long getNumberOfRecords() {
		try {
			/*
			 * number of recs = (records section length) / (record length)
			 */
			return (this.raf.length() - this.startOfDataSection)
					/ (FLAG_FIELD_SIZE + this.header.getRecordLength());
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	/*
	 * This method writes a database record to the file.
	 */
	private void writeRecord(long recNo, DataRecord datarecord)
			throws RecordNotFoundException, IOException {

		// Move the file pointer
		goToRecord(recNo);

		String[] data = datarecord.getFieldValues();
		boolean deleted = datarecord.isDeleted();

		// Validate the number of fields in the record
		if (data.length != this.schema.getNumberOfFields()) {
			throw new IllegalArgumentException(
					"Error writing data to file. Incorrect number of fields");
		}

		// Build the record String.
		StringBuffer cad = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			if (data[i].length() > this.schema.getField(i).getLength()) {
				throw new IllegalArgumentException(
						"Error writing data to file. Value specified for field "
								+ this.schema.getField(i).getName()
								+ " can not exceed "
								+ this.schema.getField(i).getLength()
								+ " characters length");
			}
			StringBuffer fieldValue = new StringBuffer(data[i].trim());
			int fieldLength = this.schema.getField(i).getLength();

			// Ensure fields length, truncating ...
			if (fieldValue.length() > fieldLength) {
				cad.append(fieldValue.substring(0, fieldLength));
				// Or filling with blanks
			} else if (fieldValue.length() < fieldLength) {
				while (fieldValue.length() != fieldLength) {
					fieldValue.append(" ");
				}
				cad.append(fieldValue);
			} else {
				cad.append(fieldValue);
			}
		}

		/*
		 * Finally writing of record data
		 */
		this.raf.writeByte(deleted ? DELETED_FLAG : VALID_FLAG);
		this.raf.write(cad.toString().getBytes(DB_CHARSET));
	}

	/*
	 * Returns the database record at the current file pointer position
	 */
	private DataRecord readRecord(long recNo) throws RecordNotFoundException,
			IOException {

		// Move the file pointer
		goToRecord(recNo);

		String[] recordFields = new String[this.header.getFieldsNo()];
		boolean deleted = false;

		byte deletedFlag = this.raf.readByte();
		deleted = (deletedFlag == DELETED_FLAG) ? true : false;

		for (int j = 0; j < recordFields.length; j++) {
			byte[] buffer = new byte[this.schema.getField(j).getLength()];
			this.raf.readFully(buffer);
			recordFields[j] = new String(buffer, DB_CHARSET).trim();
		}
		return new DataRecord(deleted, recordFields);
	}

	/*
	 * This method moves the file pointer to the position of the specified
	 * record
	 */
	private void goToRecord(long recNo) throws IOException,
			RecordNotFoundException {

		verifyRecNoInOfBounds(recNo);
		this.currentRecNo = recNo;
		this.raf.seek(this.startOfDataSection
				+ (FLAG_LENGTH + this.schema.getRecordLength()) * recNo);

	}

	/*
	 * This method validates the record number
	 */
	private void verifyRecNoInOfBounds(long recNo)
			throws RecordNotFoundException {

		if (recNo < 0 || recNo > getNumberOfRecords() - 1) {
			throw new RecordNotFoundException("Record number out of bounds");
		}
	}

	/*
	 * This method validates the record is not deleted
	 */
	private void verifyRecNotDeleted(DataRecord record)
			throws RecordNotFoundException {

		if (record.isDeleted()) {
			throw new RecordNotFoundException("Could not find record number "
					+ this.currentRecNo + ". This record has been deleted");
		}
	}

	/*
	 * Reading of the header section of the file
	 */
	private void readHeaderSection() throws IOException,
			NotSupportedDataBaseException {

		this.raf.seek(0);

		// Magic cookie
		int magic = this.raf.readInt();

		// Total overall length in bytes of each record
		int bpr = this.raf.readInt();

		// Number of fields in each record
		short fpr = this.raf.readShort();

		this.header = new URLyBirdFileHeader(magic, bpr, fpr);
		this.startOfSchemaSection = this.raf.getFilePointer();
	}

	/*
	 * Reading of the schema section of the file
	 */
	private void readSchemaSection() throws IOException {

		this.raf.seek(this.startOfSchemaSection);
		DBField[] fields = new DBField[this.header.getFieldsNo()];

		// for each field
		for (int i = 0; i < fields.length; i++) {

			// Length in bytes of field name
			short nameBytes = this.raf.readShort();

			// Stores field name
			byte[] nameBuffer = new byte[nameBytes];
			this.raf.readFully(nameBuffer);
			String fieldName = new String(nameBuffer, DB_CHARSET);

			// Field length in bytes
			short fieldBytes = this.raf.readShort();

			fields[i] = new DBField(fieldName, fieldBytes);
		}

		this.schema = new DataSchema(fields);
		this.startOfDataSection = this.raf.getFilePointer();

	}

	/**
	 * This class encapsulates the schema information in a URLyBird database
	 * file header section (see {@link URLyBirdFile_1_2_1}).
	 * 
	 * @author Ignacio del Valle Alles.
	 */
	private class URLyBirdFileHeader {

		private static final String COOKIE_EX = "URLyBird 1.2.1: Database file format not supported";
		private static final String INCOMPATIBLE_EX = "Incompatible file header";

		// bytes per record
		private int recordLength;

		// fields per record
		private short fieldsNo;

		/**
		 * Constructs a new <code>URLyBirdFileHeader</code> object.
		 * 
		 * @param magic
		 *            an integer that identifies the URLyBirdFile
		 * @param recordLength
		 *            the length of each record, in bytes
		 * @param fieldsNo
		 *            the number of fields of the database
		 * @throws DataSourceCreationException
		 */
		URLyBirdFileHeader(int magic, int recordLength, short fieldsNo)
				throws NotSupportedDataBaseException {

			this.recordLength = recordLength;
			this.fieldsNo = fieldsNo;

			if (magic != SUPPORTED_MAGIC_COOKIE) {
				throw new NotSupportedDataBaseException(COOKIE_EX);
			}
			if (recordLength < 1 || fieldsNo < 1) {
				throw new NotSupportedDataBaseException(INCOMPATIBLE_EX);
			}

		}

		/**
		 * Returns the record length of the file
		 * 
		 * @return an integer, the record length
		 */
		int getRecordLength() {
			return this.recordLength;
		}

		/**
		 * Returns the number of fields.
		 * 
		 * @return a short, the number of fields
		 */
		short getFieldsNo() {
			return this.fieldsNo;
		}

	}

	/**
	 * This class encapsulates the schema information of a URLyBird database
	 * file.
	 * 
	 * @author Ignacio del Valle Alles.
	 */
	private class DataSchema implements java.io.Serializable {

		static final long serialVersionUID = 3366704377499621356L;

		private final DBField[] fields;

		/**
		 * Creates a new instance from the specified field information
		 * 
		 * @param fields An array with the fields definition.
		 */
		public DataSchema(DBField[] fields) {
			this.fields = fields;
		}

		/**
		 * Returns the field at the specified position.
		 * 
		 * @param fieldNo
		 *            index of element to return.
		 * @return the DBField object at the specified position.
		 * @throws IndexOutOfBoundsException
		 *             if index is out of range <code>
		 * 		  <tt>(fieldNo &lt; 0 || index &gt;= getNumberOfFields())</tt></code>
		 */
		public DBField getField(int fieldNo) {
			return this.fields[fieldNo];
		}

		/**
		 * Returns the length in bytes of a database record.
		 * 
		 * @return an integer
		 */
		public int getRecordLength() {
			int ret = 0;
			for (int i = 0; i < this.fields.length; i++) {
				ret += this.fields[i].getLength();
			}
			return ret;
		}

		/**
		 * Returns the number of fields in the database.
		 * 
		 * @return an integer
		 */
		public int getNumberOfFields() {
			return this.fields.length;
		}
	}

	private class DBField {

		private final String name;
		private final short length; // field length in bytes

		/**
		 * Constructs a new <code>DBField</code> object with the specified
		 * name and length.
		 * 
		 * @param name
		 *            the name of the field
		 * @param length
		 *            the length in bytes of the field
		 */
		public DBField(String name, short length) {
			this.name = name;
			this.length = length;
		}

		/**
		 * Returns the name of the field.
		 * 
		 * @return a String representing the name of the field
		 */
		public String getName() {
			return this.name.substring(0, 1).toUpperCase()
					+ this.name.substring(1, this.name.length());

		}

		/**
		 * Returns the length in bytes of the field.
		 * 
		 * @return a short, the length in bytes
		 */
		public short getLength() {
			return this.length;
		}

	}

	/**
	 * Each record contains information for each database field and a <i>deleted
	 * state</i>.
	 * 
	 * @author Ignacio del Valle Alles.
	 */
	private class DataRecord {

		private final boolean deleted;
		private final String[] fieldValues;

		/**
		 * Constructs a new <code>DataRecord</code> with the field values and
		 * <i>deleted state</i> specified.
		 * 
		 * @param deleted
		 *            the <i>deleted state</i> of the record.
		 * @param fieldValues
		 *            an String array with the values of each field.
		 */
		public DataRecord(boolean deleted, String[] fieldValues) {
			this.deleted = deleted;
			this.fieldValues = new String[fieldValues.length];
			for (int i = 0; i < fieldValues.length; i++) {
				this.fieldValues[i] = fieldValues[i];
			}
		}


		/**
		 * Returns the <i>deleted state</i> of the record. <br>
		 * <br>
		 * 
		 * @return <code>true</code> if the record is available for reuse.
		 *         Otherwise <code>false</code>.
		 */
		public boolean isDeleted() {
			return this.deleted;
		}

		/**
		 * Returns the field values
		 * 
		 * @return field values
		 */
		public String[] getFieldValues() {
			return this.fieldValues;
		}

	}

}
