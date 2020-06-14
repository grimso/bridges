package view;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

/**
 * Renders a circle (representing an island) around the center specified by the
 * coordinate position ({@code row},{@code column}) in a bridge game. The
 * position is calculated by supplying the current gridthLength of a
 * {@link BridgesBoard}. <br>
 * Allows adjustment of the cirecle's color. Additionally a centered label can
 * be displayed.<br>
 * 
 * @author grimm
 *
 */
class Island2D {
	private int column;
	private int row;
	private String label = "";
	private Color color = Color.LIGHT_GRAY;

	/**
	 * Initialize a {@link Island2D} by setting the {@code row} and {@code column}
	 * in a bridge game. The effective position is calculated based on that values.
	 * 
	 * @param row    position in x direction in a bridges game
	 * @param column position in y direction in a bridges game
	 */
	Island2D(int row, int column) {
		this.row = row;
		this.column = column;
	}

	/**
	 * Returns the {@code} column position of the island
	 * 
	 * @return column position of the island
	 */
	int getColumn() {
		return column;
	}

	/**
	 * Returns the {@code} row position of the island
	 * 
	 * @return row position of the island
	 */
	int getRow() {
		return row;
	}

	/**
	 * Set the {@code label} which is displayed
	 * 
	 * @param label A {@link String} which is displayed in the center of circle
	 */
	void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Set the {@code color} the island should have.
	 * 
	 * @param label {@link Color}
	 */
	void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Paints a circle on {@code g2}. The Appearance is scalable by passing in the
	 * current {@code gridLength} of the board.
	 * 
	 * @param g2         {@link Graphics2D} on which the island is rendered
	 * @param gridLength the current grid length (pixels) on the
	 *                   {@link BridgesBoard}
	 */
	void paint(Graphics2D g2, int gridLength) {
		FontMetrics fontMetrics = g2.getFontMetrics();
		g2.setColor(this.color);

		g2.fillOval((int) (gridLength * this.column + gridLength * ((1 - BridgesBoard.islandMargin) * 0.5)),
				(int) (gridLength * this.row + gridLength * ((1 - BridgesBoard.islandMargin) * 0.5)),
				(int) (gridLength * BridgesBoard.islandMargin), (int) (gridLength * BridgesBoard.islandMargin));
		g2.setColor(Color.BLACK);

		int xString = (int) (gridLength * this.column + gridLength * 0.5 - fontMetrics.stringWidth(this.label) * 0.5);
		int yString = (int) (gridLength * this.row + gridLength * 0.5 - fontMetrics.getHeight() * 0.5
				+ fontMetrics.getAscent());

		g2.drawString(this.label, xString, yString);
	}

	/**
	 * Returns the thought boarder of the rectangle in which the circle circle is
	 * drawn.
	 * 
	 * @param gridLength the current grid length (pixels) on the
	 *                   {@link BridgesBoard}
	 * @return {@link Rectangle} the gird square boarder of the
	 */
	Rectangle getBounds(int gridLength) {
		Ellipse2D bounds = new Ellipse2D.Double(gridLength * this.column, gridLength * this.row, gridLength,
				gridLength);
		return bounds.getBounds();
	}

}
