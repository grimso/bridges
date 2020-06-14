package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Renders a black line (representing a bridge) connecting two islands
 * ({@link Island2D}) which are on a {@link BridgesBoard}. The appearance can by
 * modified by following setting:
 * <li>isDoubleBridge={@code true} two lines are drawn connecting the islands,
 * otherwise a single line
 * <li>modifiedLatest={@code true} highlight bridge by increasing stroke size
 * 
 * @author grimm
 *
 */
class Bridge2D {
	private final Island2D island1;
	private final Island2D island2;
	private boolean isDoubleBridge;
	private boolean modifiedLast;

	/**
	 * Initialize a bridge between a {@code island1} and {@code island2} which can
	 * then be painted on a {@link BridgesBoard}
	 * 
	 * @param island1          {@link Island2D}
	 * @param island2          {@link Island2D}
	 * @param isDoubleBridge   {@code true} if two lines should be drawn
	 * @param idModifiedLatest {@code true}
	 */
	Bridge2D(Island2D island1, Island2D island2, boolean isDoubleBridge, boolean idModifiedLatest) {
		this.island1 = island1;
		this.island2 = island2;
		this.isDoubleBridge = isDoubleBridge;
		this.modifiedLast = idModifiedLatest;
	}

	/**
	 * Paints the bridge on {@code g2}. The
	 * Appearance is scalable by passing in the current {@code gridWidth} of the
	 * board.
	 * 
	 * @param g2        {@link Graphics2D} on which the bridge is rendered
	 * @param gridWidth the current grid width (pixels) on the {@link BridgesBoard
	 *                  }}
	 */
	void paint(Graphics2D g2, int gridWidth) {
		g2.setColor(Color.BLACK);
		if (!isDoubleBridge) {
			this.paintBridgeWithOffset(g2, gridWidth, 0);
		} else {
			this.paintBridgeWithOffset(g2, gridWidth, (int) (gridWidth * 0.15));
			this.paintBridgeWithOffset(g2, gridWidth, (int) (gridWidth * -0.15));

		}
	}

	private void paintBridgeWithOffset(Graphics2D g2, int gridWidth, int offset) {

		Point2D p1 = new Point2D.Double(island1.getBounds(gridWidth).getCenterX() + offset,
				island1.getBounds(gridWidth).getCenterY() + offset);
		Point2D p2 = new Point2D.Double(island2.getBounds(gridWidth).getCenterX() + offset,
				island2.getBounds(gridWidth).getCenterY() + offset);
		if (this.modifiedLast) {
			g2.setStroke(new BasicStroke(3));
		} else {
			g2.setStroke(new BasicStroke(1));
		}
		g2.draw(new Line2D.Double(p1, p2));

	}

}
