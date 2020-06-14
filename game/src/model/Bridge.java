package model;

/**
 * A {@code Bridge} is defined as a relation between a {@link Island} island1
 * and a {@link Island} island2, indicating that those islands are connected via
 * a bridge. One such relation can either be a single or a double bridge. The
 * relation has now direction, but the islands island1,island2 are ordered, so
 * that the {@code Island} with the smaller Manhattan distance to the point of
 * origin (0,0) is set as island1.
 * 
 * @author grimm
 *
 */
public class Bridge {
	private final Island island1;
	private final Island island2;
	private boolean isDoubleBridge;
	private boolean modifiedLatest;

	/**
	 * Create a {@code Bridge} (Relation) between island1 and island2. Order of
	 * islands is not impotent, as they are sorted according to the Manhattan
	 * distance.
	 * 
	 * @param island1           {@link Island}
	 * @param island2           {@link Island}
	 * @param isDoubleBridge    {@code true} if the relation between the islands is
	 *                          a double bridge
	 * @param modifyIslandState {@code true} if the state of the islands should be
	 *                          updated
	 */
	Bridge(Island island1, Island island2, boolean isDoubleBridge, boolean modifyIslandState) {
		if ((island1.getColumn() + island1.getRow()) <= (island2.getColumn() + island2.getRow())) {
			this.island1 = island1;
			this.island2 = island2;
		} else {
			this.island1 = island2;
			this.island2 = island1;
		}
		int increment = isDoubleBridge ? 2 : 1;
		if (modifyIslandState) {
			island1.setCurrentNrOfBridges(island1.getCurrentNrOfBridges() + increment);
			island2.setCurrentNrOfBridges(island2.getCurrentNrOfBridges() + increment);
		}
		this.isDoubleBridge = isDoubleBridge;
	}

	/**
	 * Returns true if the bridge between the islands is a double bridge
	 * 
	 * @return {@code true} if the bridge between the islands is a double bridge
	 */
	public boolean isDoubleBridge() {
		return isDoubleBridge;
	}

	/**
	 * Set {@code true} if this bridge (relation) is a double bridge, otherwise it
	 * is a single bridge
	 * 
	 * @param isDoubleBridge set {@code true} if this is a double bridge
	 */
	void setDoubleBridge(boolean isDoubleBridge) {

		this.isDoubleBridge = isDoubleBridge;
	}

	/**
	 * Returns the {@code Island} with the smaller Manhattan distance in the bridge
	 * relation
	 * 
	 * @return {@link Island}
	 */
	public Island getIsland1() {
		return island1;
	}

	/**
	 * Returns the {@code Island} with the bigger Manhattan distance in the bridge
	 * relation
	 * 
	 * @return {@link Island}
	 */

	public Island getIsland2() {
		return island2;
	}

	/**
	 * Returns {@code true} if bridge was modified latest
	 * 
	 * @return {@code true} if bridge was modified latest
	 */
	public boolean isModifiedLatest() {
		return modifiedLatest;
	}

	/**
	 * Set {@code true} if a new bridge (relation) is created between two islands or
	 * a single bridge is transformed to a double bridge.
	 * 
	 * @param modifiedLatest {@code true} if bridge was modified latest
	 */
	void setModifiedLatest(boolean modifiedLatest) {
		this.modifiedLatest = modifiedLatest;
	}

	/**
	 * Returns {@code true} if the bridge between island is horizontal otherwise
	 * false, meaning bridge is vertical
	 * 
	 * @return {@code true} if the bridge between island is horizontal
	 */
	boolean isHorizontal() {
		return this.island1.getRow() == this.island2.getRow();
	}
	
	/**
	 * Returns true if two bridges {@code bridge1} and {@code bridge2} do cross each other.<br>
	 * order is not important.
	 * @param bridge1 {@link Bridge}
	 * @param bridge2 {@link Bridge}
	 * @return {@code true} if two bridges {@code bridge1} and {@code bridge2} do cross each other
	 */

	static boolean areCrossingBridges(Bridge bridge1, Bridge bridge2) {
		if (bridge1.isHorizontal() == bridge2.isHorizontal()) {
			return false;
		} else if (bridge1.isHorizontal()) {
			int y = bridge1.getIsland1().getRow();
			int x = bridge2.getIsland1().getColumn();
			if ((bridge1.getIsland1().getColumn() < x && x < bridge1.getIsland2().getColumn())
					&& ((bridge2.getIsland1().getRow() < y && y < bridge2.getIsland2().getRow()))) {
				return true;
			} else {
				return false;
			}

		} else {
			int y = bridge2.getIsland1().getRow();
			int x = bridge1.getIsland1().getColumn();
			if ((bridge2.getIsland1().getColumn() < x && x < bridge2.getIsland2().getColumn())
					&& ((bridge1.getIsland1().getRow() < y && y < bridge1.getIsland2().getRow()))) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Returns {@code true} if a island is in the bridge relation
	 * 
	 * @param island {@link Island}
	 * @return {@code true} if island is in the bridge relation
	 */

	boolean contains(Island island) {
		return this.island1 == island || this.island2 == island;
	}

	/**
	 * Returns {@code true} if two island build the bridge relation - order is not
	 * important
	 * 
	 * @param island1 {@link Island}
	 * @param island2 {@link Island}
	 * @return {@code true} if both island are in the bridge relation
	 */

	boolean contains(Island island1, Island island2) {
		return this.contains(island1) && this.contains(island2);
	}

}
