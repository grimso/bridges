package common;

public class BuildOrRemoveBridgeRequest {
	public BuildOrRemoveBridgeRequest(int col, int row, CardinalPoints direction, boolean remove) {
		super();
		this.col = col;
		this.row = row;
		this.direction = direction;
		this.remove = remove;
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}

	public CardinalPoints getDirection() {
		return direction;
	}

	public boolean isRemove() {
		return remove;
	}

	private int col;
	private int row;
	private CardinalPoints direction;
	private boolean remove;

}
