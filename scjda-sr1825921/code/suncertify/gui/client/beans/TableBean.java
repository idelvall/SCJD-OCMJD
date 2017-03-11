/*
 * TableBean.java 06/10/2010
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

import static suncertify.gui.GUIUtils.*;
import suncertify.bs.HotelRoom;
import suncertify.bs.commands.BusinessCommand;
import suncertify.bs.commands.SearchCommand;
import suncertify.db.RecordNotFoundException;
import suncertify.gui.client.GUIBean;
import suncertify.gui.client.Mediator;

/**
 * A <code>GUIBean</code> to show a list of {@link HotelRoom}s in a table
 * format. These lists are received from the {@link Mediator} as
 * {@link SearchCommand} results.<br>
 * <br>
 * <code>TableBean</code> instances can generate the following events:
 * <ul>
 * <li>Selection events: fired when a new room is selected</li>
 * <li>Action events: fired when the {@link KeyEvent#VK_ENTER} key is pressed
 * over a selected room</li>
 * </ul>
 * Components interested in be notified of these events can register to the
 * instance as listeners.
 * 
 * @author Ignacio del Valle Alles
 * @version 1.0 06/10/2010
 * 
 */
public class TableBean extends GUIBean {

	static final long serialVersionUID = -3386461817205284020L;

	static final String[] COLUMN_NAMES = { "#", "Hotel name", "City",
			"Room size", "Smoking", "Prize ($)", "Date available", "Customer" };
	
	static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

	/**
	 * Table font.
	 */
	static final java.awt.Font TABLE_FONT = new java.awt.Font("Default", 0, 11);

	/**
	 * Color for selected and unbooked rows
	 */
	static final Color ROW_SEL_UNBKD_COLOR = new Color(150, 200, 100);

	/**
	 * Color for unselected even rows
	 */
	static final Color ROW_EVEN_COLOR = new Color(236, 233, 216);

	/**
	 * Color for unselected odd rows
	 */
	static final Color ROW_ODD_COLOR = new Color(255, 241, 200);

	/**
	 * Color for selected and booked rows
	 */
	static final Color ROW_SEL_BKD_COLOR = new Color(191, 127, 63);
	static final Color HEADER_BACKGROUND = new Color(100, 100, 100);
	static final Color HEADER_FOREGROUND = Color.WHITE;
	static final Color COMPONENT_BACKGROUND = Color.WHITE;

	private static final int TABLE_SPACING = 4;

	private static final int ROW_HEIGHT = 20;

	private final List<TableBeanListener> listeners = new ArrayList<TableBeanListener>();

	/*
	 * Enhanced JTable
	 */
	HotelRoomTable jTable;

	/**
	 * Creates a new instance from the specified mediator.
	 * 
	 * @param mediator the Mediator instance.
	 */
	public TableBean(Mediator mediator) {
		// All GUIBean instances need a Mediator to be created.
		super(mediator);
		initComponents();
	}

	/**
	 * Subscribes a listener in order to be notified of the events occurred.
	 * 
	 * @param listener
	 *            a <code>TableBeanListener</code> listener. Ignored if already
	 *            is subscribed.
	 */
	public void addListener(TableBeanListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	/**
	 * Removes a listener.
	 * 
	 * @param listener
	 *            the listener to be removed. Ignored if it not subscribed.
	 */
	public void removeSelectionListener(TableBeanListener listener) {
		if (this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}

	/**
	 * Returns the bean tooltip.
	 * 
	 * @return the tooltip
	 */
	@Override
	public String getToolTip() {
		return "Search results";
	}

	/**
	 * Returns the bean title.
	 * 
	 * @return the title
	 */
	@Override
	public String getTitle() {
		return "Search results";
	}

	/**
	 * In case of <code>cmd</code> being an instance of {@link SearchCommand}
	 * class, the <code>TableBean</code> instance updates the table with the new
	 * results.<br>
	 * <br>
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void commandEnded(BusinessCommand<?> cmd) {
		if (cmd instanceof SearchCommand) {
			SearchCommand sCmd = (SearchCommand) cmd;
			HotelRoomTableModel model = new HotelRoomTableModel(sCmd
					.getResult());
			this.jTable.setModel(model);
			this.setColumnsWidth();
			getParent().validate();
		}
	}

	/**
	 * This method does nothing.<br>
	 * <br>
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void commandExceptionEnded(BusinessCommand<?> cmd) {
		return;
	}

	/**
	 * This method does nothing.<br>
	 * <br>
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void commandStarted(BusinessCommand<?> cmd) {
		return;
	}

	/*
	 * Fire event methods
	 */
	void fireSelectedRoom(HotelRoom room) {
		for (TableBeanListener listener : this.listeners) {
			listener.selectedRoom(room);
		}
	}

	void fireActionForRoom(HotelRoom room) {
		for (TableBeanListener listener : this.listeners) {
			listener.actionPerformed(room);
		}
	}

	/*
	 * Returns the jTable member. Used by anonymous classes.
	 */
	HotelRoomTable getTable() {
		return this.jTable;
	}

	/*
	 * Returns the row number at the specified screen point
	 */
	int getRowAt(Point point) {
		return this.jTable.rowAtPoint(point);
	}

	/*
	 * Tells is the specified row belongs to a booked record.
	 */
	boolean isBooked(int row) {
		HotelRoom selectedRoom = this.jTable.getHotelRoomTableModel()
				.getRoomAt(row);
		return selectedRoom.isBooked();
	}

	/*
	 * Returns the first room available in the table view.
	 */
	int getFirstAvailable() throws RecordNotFoundException {

		int ret = -1;
		for (int i = 0; i < this.jTable.getRowCount(); i++) {
			if (!isBooked(i)) {
				ret = i;
				break;
			}
		}

		if (ret == -1) {
			throw new RecordNotFoundException(
					"There are not rooms available in current search!");
		}
		return ret;
	}

	/*
	 * Returns the next room available in the table view.
	 */
	int getNextAvailable(int iniRow) {

		int ret = -1;
		for (int i = iniRow + 1; i < this.jTable.getRowCount(); i++) {
			if (!isBooked(i)) {
				ret = i;
				break;
			}
		}
		return ret;
	}

	/*
	 * Returns the previous room available in the table view.
	 */
	int getPreviousAvailable(int iniRow) {

		int ret = -1;
		for (int i = iniRow - 1; i >= 0; i--) {
			if (!isBooked(i)) {
				ret = i;
				break;
			}
		}
		return ret;
	}

	void setColumnsWidth() {
		setColumnMaxWidth(0, 50);
		setColumnMaxWidth(3, 100);
		setColumnMaxWidth(4, 100);
		setColumnMaxWidth(5, 100);
		setColumnMaxWidth(6, 100);
		setColumnMaxWidth(7, 100);

	}

	void setColumnMaxWidth(int column, int width) {
		TableColumn col = this.jTable.getColumnModel().getColumn(column);
		col.setMaxWidth(width);
		col.setPreferredWidth(width);
	}

	/*
	 * Inner components initialization.
	 */
	void initComponents() {

		this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		/*
		 * Table initialization:
		 */
		this.jTable = new HotelRoomTable();
		this.jTable.setDragEnabled(false);
		this.jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.jTable.setColumnSelectionAllowed(false);
		this.jTable.setRowSelectionAllowed(true);
		this.jTable.setIntercellSpacing(new Dimension(TABLE_SPACING,
				TABLE_SPACING));
		this.jTable.setRowHeight(ROW_HEIGHT);
		this.jTable.setOpaque(false);
		this.jTable.setShowGrid(false);
		UIManager.getDefaults().put("TableHeader.cellBorder",
				BorderFactory.createEmptyBorder(1, 1, 1, 1));
		this.jTable.getTableHeader().setDefaultRenderer(
				new TableHeaderCellRender());

		/*
		 * Key listener:
		 */
		this.jTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent ke) {
				int[] selectedRows = getTable().getSelectedRows();
				int currentRow = 0;
				if (selectedRows.length > 0) {
					currentRow = selectedRows[0];
				}
				if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {

					int availableRow = getNextAvailable(currentRow);
					if (availableRow != -1) {
						getTable().setRowSelectionInterval(availableRow,
								availableRow);
					}

				} else if (ke.getKeyCode() == KeyEvent.VK_LEFT) {

					int availableRow = getPreviousAvailable(currentRow);
					if (availableRow != -1) {
						getTable().setRowSelectionInterval(availableRow,
								availableRow);
					}

				} else if (ke.getKeyCode() == KeyEvent.VK_ENTER) {

					if (currentRow != -1) {
						HotelRoom selectedRoom = getTable()
								.getHotelRoomTableModel().getRoomAt(currentRow);
						fireActionForRoom(selectedRoom);
					}
					ke.consume();
				}

			}
		});
		this.jTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (e.getValueIsAdjusting() == false) {
							int row = getTable().getSelectionModel()
									.getMinSelectionIndex();
							if (row >= 0) {
								HotelRoom selectedRoom = getTable()
										.getHotelRoomTableModel()
										.getRoomAt(row);
								fireSelectedRoom(selectedRoom);
							}
						}
					}
				});

		/*
		 * Table header initialization:
		 */
		this.jTable.getTableHeader().setVisible(true);

		/*
		 * Table cell renderers:
		 */
		HotelRoomTableBeanCellRender cellRender = new HotelRoomTableBeanCellRender();
		this.jTable.setDefaultRenderer(Object.class, cellRender);

		this.getViewport().setBackground(COMPONENT_BACKGROUND);
		this.setViewportView(this.jTable);
	}

	/**
	 * Non editable table. Accessor method to its {@link HotelRoomTableModel}
	 * model.
	 * 
	 * @author Ignacio del Valle Alles
	 * @version 1.0 14/10/2010
	 * 
	 */
	static class HotelRoomTable extends JTable {
		static final long serialVersionUID = 3203376260084848222L;

		
		@Override
		public boolean isCellEditable(int i, int j) {
			return false;
		}

		/**
		 * Accessor method for the model.
		 * 
		 * @return the <code>HotelRoomTableModel</code> instance.
		 *         <code>null</code> if the model is not "instance of"
		 *         HotelRoomTableModel class.
		 */
		public HotelRoomTableModel getHotelRoomTableModel() {
			if (this.getModel() instanceof HotelRoomTableModel) {
				return (HotelRoomTableModel) this.getModel();
			} else {
				return null;
			}
		}
	}

	/**
	 * Custom <code>TableModel</code> for a HotelRoomTable.<br>
	 * Provides an specific constructor and an accessor method to return the
	 * Hotel room in the specified row.
	 * 
	 * @author Ignacio del Valle Alles
	 * @version 1.0 14/10/2010
	 * 
	 */
	static class HotelRoomTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 4359414566735952304L;

		List<HotelRoom> rooms;

		public HotelRoomTableModel(List<HotelRoom> rooms) {
			if (rooms == null) {
				throw new IllegalArgumentException("rooms must be not null");
			}
			this.rooms = rooms;

		}

		/**
		 * Returns the hotel room in the specified row.
		 * 
		 * @param row
		 *            the row number, starting in 0.
		 * @return the hotel room business object.
		 */

		public HotelRoom getRoomAt(int row) {
			return this.rooms.get(row);
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public int getRowCount() {
			return this.rooms.size();
		}

		@SuppressWarnings("boxing")
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {

			HotelRoom room = this.rooms.get(rowIndex);

			switch (columnIndex) {
			case 0:
				return room.getId();
			case 1:
				return room.getHotelName();
			case 2:
				return room.getCity();
			case 3:
				return room.getSize();
			case 4:
				return room.isSmokingAllowed();
			case 5:
				return room.getPrice();
			case 6:
				return room.getDateAvailable();
			case 7:
				return room.getCustomer();
			}
			return null;
		}

		@Override
		public String getColumnName(int column) {
			return COLUMN_NAMES[column];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

	}

	/**
	 * Overrides the default table render changing colors, alignment and border.
	 * Used for the table header.
	 * 
	 * @author Ignacio del Valle Alles
	 * @version 1.0 14/10/2010
	 * 
	 */
	static class TableHeaderCellRender extends DefaultTableCellRenderer {
	
		private static final long serialVersionUID = -4700579507439335378L;
	
		@Override
		public java.awt.Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
	
			/*
			 * Call super method implementation
			 */
			super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);
	
			setBackground(HEADER_BACKGROUND);
			setForeground(HEADER_FOREGROUND);
			this.setHorizontalAlignment(CENTER);
			this.setBorder(BorderFactory.createLineBorder(COMPONENT_BACKGROUND,
					TABLE_SPACING / 2));
			return this;
		}
	}

	/**
	 * Overrides the default table render changing colors, alignment and border,
	 * highlights for row selection, and icons for certain columns. Used for the
	 * table body rows.
	 * 
	 * @author Ignacio del Valle Alles
	 * @version 1.0 14/10/2010
	 * 
	 */
	class HotelRoomTableBeanCellRender extends DefaultTableCellRenderer {
	
		private static final long serialVersionUID = 27386226927871838L;
	
		/**
		 * 
		 * Returns the default table cell renderer.
		 * 
		 * @param table
		 *            the <code>JTable</code>
		 * @param value
		 *            the value to assign to the cell at
		 *            <code>[row, column]</code>
		 * @param isSelected
		 *            true if cell is selected
		 * @param hasFocus
		 *            true if cell has focus
		 * @param row
		 *            the row of the cell to render
		 * @param column
		 *            the column of the cell to render
		 * @return the default table cell renderer
		 */
		@Override
		public java.awt.Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
	
			setValue(value);
	
			/*
			 * Change render font
			 */
			setFont(TableBean.TABLE_FONT);
	
			/*
			 * Apply type specific rendering
			 */
			applyColumnRendering(value);
	
			/*
			 * Treat cell highlighting
			 */
			highLightCell(this, isSelected, isBooked(row), row);
			return this;
		}
	
		/*
		 * Applies type specific rendering
		 */
		@SuppressWarnings("boxing")
		protected void applyColumnRendering(Object value) {
	
			if(value instanceof Integer
					||value instanceof Long
						||value instanceof Float
							||value instanceof Double
								||value instanceof BigDecimal) {
				this.setHorizontalAlignment(RIGHT);
				
			} else if (value instanceof Boolean
					||value instanceof Date) {
				this.setHorizontalAlignment(CENTER);
			} else {
				this.setHorizontalAlignment(LEFT);
			}
			
			if (value instanceof Date) {
				Date dateValue = (Date)value;
				this.setValue(DATE_FORMAT.format(dateValue));
			}
	
			if (value instanceof Boolean) {
				boolean boolValue = (Boolean)value;
				if (boolValue) {
					this.setIcon(getImageIcon("suncertify/gui/images/booleanTrue.gif"));
				} else {
					this.setIcon(getImageIcon("suncertify/gui/images/booleanFalse.gif"));
				}
				this.setValue(null);
			} else {
				this.setIcon(null);
			}
		}
	
		/*
		 * HighLights cell. Selected,booked, and available states.
		 */
		protected void highLightCell(Component comp, boolean isSelected,
				boolean isBooked, int row) {
			if (isSelected) {
				if (isBooked) {
					comp.setBackground(TableBean.ROW_SEL_BKD_COLOR);
				} else {
					comp.setBackground(TableBean.ROW_SEL_UNBKD_COLOR);
				}
			} else {
				if (row % 2 == 0) {
					comp.setBackground(TableBean.ROW_EVEN_COLOR);
				} else {
					comp.setBackground(TableBean.ROW_ODD_COLOR);
				}
			}
		}
	}
}
