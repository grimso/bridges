package model;

/**
 * {@code Island} is a simple object defining the position (row,column) on the
 * game board and how many bridges can be added to this island.<br>
 * Can also be used to hold the state of how many bridges are currently
 * connected to a {@code Island}.
 * 
 * @author grimm
 *
 */
public class Island {
	// position on the game board
	private int row;
	private int column;

	private int bridgeCapacity;// number of bridges a island can have
	private int currentNrOfBridges;// current number of bridges which are connected to the island

	/**
	 * Initialize an {@code Island}
	 * 
	 * @param row            the row position
	 * @param column         the column position
	 * @param bridgeCapacity the number of bridges that can be added to the
	 *                       {@code Island}
	 */
	Island(int row, int column, int bridgeCapacity) {
		this.row = row;
		this.column = column;
		this.bridgeCapacity = bridgeCapacity;
	}

	/**
	 * Returns the number of connected bridges
	 * 
	 * @return the number of connected bridges to the {@code Island}
	 */
	public int getCurrentNrOfBridges() {
		return currentNrOfBridges;
	}

	/**
	 * Returns the capacity of how many bridges can be added to the island.<br>
	 * This is based on the total capacity an island can have minus the bridges
	 * which are currently connected to the island.
	 * 
	 * @return the number of bridges that can be added
	 */
	public int getCurrentBridgeCapacity() {
		return bridgeCapacity - currentNrOfBridges;
	}
	/**
	 * Returns the row position of the {@code Island}
	 * @return the row position as number 
	 */
	public int getRow() {
		return row;
	}
	/**
	 * Returns the column position of the {@code Island}
	 * @return the column position as number 
	 */
	public int getColumn() {
		return column;
	}
	/**
	 * Set the number of bridges, which are connected to the {@code Island}
	 * @param currentNrOfBridges number of connected bridges
	 */

	void setCurrentNrOfBridges(int currentNrOfBridges) {
		this.currentNrOfBridges = currentNrOfBridges;
	}
	/**
	 * Returns the capacity of bridges an {@code Island} can have
	 * @return number of bridge capacity
	 */

	public int getBridgeCapacity() {
		return bridgeCapacity;
	}
	/**
	 *  Set the number of bridges a {@code island} can have. 
	 * @param bridgeCapacity number of bridges the island can have
	 */

	void setBridgeCapacity(int bridgeCapacity) {
		this.bridgeCapacity = bridgeCapacity;
	}
	/**
	 * Returns the string representation of a {@code Island}, which satisfies the grammar of the file format  
	 * @return String 
	 */
	String toFileFormat() {
		return String.format("( %d, %d | %d )", this.row, this.column, this.bridgeCapacity);

	}

	@Override
	public String toString() {
		return "Island [row=" + row + ", column=" + column + ", bridgeCapacity=" + bridgeCapacity
				+ ", currentNrOfBridges=" + currentNrOfBridges + "]";
	}

}