package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import common.BuildOrRemoveBridgeRequest;
import common.CardinalPoints;
/**
 * 
 * @author grimm
 *
 */
class BridgesBoard extends JPanel {
	private ControllerInterface controller;
	private final int maxCharHeight = 30;
	private final int minCharHeight = 10;
	private final int preferredGridSize = 25;
	private int nrRows = 5;
	private int nrColumns = 5;
	protected final static double islandMargin = 0.7;
	private List<Island2D> islands = new ArrayList<Island2D>();
	private List<Bridge2D> bridges = new ArrayList<Bridge2D>();

	BridgesBoard(ControllerInterface controller) {
		this.setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(nrRows * preferredGridSize, nrColumns * preferredGridSize));
		this.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), new LineBorder(Color.BLACK)));
		this.controller = controller;
		addMouseListener(this.createCustomMouseAdapter());
	}
	
	private MouseAdapter createCustomMouseAdapter() {
		MouseAdapter customMouseAdapter = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Point clickedPoint = e.getPoint();
				int columnPosition = (int) clickedPoint.getX() / (int) getGridWidth();
				int rowPosition = (int) clickedPoint.getY() / (int) getGridHeight();
				int normalizedX = (int) (clickedPoint.getX() - columnPosition * getGridWidth());
				int normalizedY = (int) (clickedPoint.getY() - rowPosition * getGridHeight());
				Point normalizedPoint = new Point(normalizedX, normalizedY);
				int xMax = (int) getGridWidth();
				int yMax = (int) getGridHeight();

				Optional<CardinalPoints> res = getBridgeDirection(xMax, yMax, normalizedPoint);
				if (res.isPresent()) {
					boolean removeBridge = SwingUtilities.isRightMouseButton(e);
					BuildOrRemoveBridgeRequest request = new BuildOrRemoveBridgeRequest(columnPosition, rowPosition,
							res.get(), removeBridge);
					controller.buildBridge(request);

				}

			}

		};
		return customMouseAdapter;
		
	}

	void setIslands(List<Island2D> islands) {
		this.islands = islands;
	}

	void setBridges(List<Bridge2D> bridges) {
		this.bridges = bridges;
	}

	void setNrColumnsAndRows(int nrRows, int nrColumns) {
		this.nrRows = nrRows;
		this.nrColumns = nrColumns;
		this.setPreferredSize(new Dimension(nrRows * preferredGridSize, nrColumns * preferredGridSize));
		this.setMinimumSize(new Dimension(nrRows * preferredGridSize, nrColumns * preferredGridSize));
		this.revalidate();
		this.repaint();
	}

	private double getGridWidth() {
		return getSize().getWidth() / this.nrColumns;
	}

	private double getGridHeight() {
		return getSize().getHeight() / this.nrRows;
	}

	@Override
	public final Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		Dimension prefSize = null;
		Component c = getParent();
		if (c == null) {
			prefSize = new Dimension((int) d.getWidth(), (int) d.getHeight());
		} else if (c != null && c.getWidth() > d.getWidth() && c.getHeight() > d.getHeight()) {
			prefSize = c.getSize();
		} else {
			prefSize = d;
		}
		int w = (int) prefSize.getWidth();
		int h = (int) prefSize.getHeight();

		// aspect ratio
		double aspectRatio = this.nrRows == 0 ? 1.0 : (double) this.nrColumns / this.nrRows;

		// the smaller of the two sizes
		int s = (w > h ? h : w);

		if (aspectRatio >= 1) {
			return new Dimension(s, (int) ((1 / aspectRatio) * s));
		}
		return new Dimension((int) (s * aspectRatio), s);
	}

	private FontMetrics pickFont(Graphics2D g2, String longString, int xSpace, int ySpace) {
		boolean fontFits = false;
		Font font = g2.getFont();
		FontMetrics fontMetrics = g2.getFontMetrics();
		int size = font.getSize();
		String name = font.getName();
		int style = font.getStyle();
		while (!fontFits) {
			if ((fontMetrics.getHeight() <= ySpace) && (fontMetrics.stringWidth(longString) <= xSpace)) {
				fontFits = true;
			} else {
				if (size <= minCharHeight) {
					fontFits = true;
				} else {
					g2.setFont(font = new Font(name, style, --size));
					fontMetrics = g2.getFontMetrics();
				}
			}
		}

		return fontMetrics;
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		int gridWidth = (int) this.getGridWidth();
		int gridHeight = (int) this.getGridHeight();

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Font font = g2.getFont();
		String name = font.getName();
		int style = font.getStyle();
		g2.setFont(new Font(name, style, this.maxCharHeight));
		this.pickFont((Graphics2D) g, "99", (int) (gridWidth * islandMargin), (int) (gridHeight * islandMargin));

		for (Bridge2D bridge : this.bridges) {
			bridge.paint(g2, gridWidth);
		}

		for (Island2D island : this.islands) {
			island.paint(g2, gridWidth);
		}

	}

	private Optional<CardinalPoints> getBridgeDirection(int xMax, int yMax, Point p) {

		CardinalPoints direction = null;
		int xCenter = xMax / 2;
		int yCenter = yMax / 2;
		Polygon westTriangle = new Polygon();
		westTriangle.addPoint(0, 0);
		westTriangle.addPoint(0, yMax);
		westTriangle.addPoint(xCenter, yCenter);

		Polygon northTriangle = new Polygon();
		northTriangle.addPoint(0, 0);
		northTriangle.addPoint(xMax, 0);
		northTriangle.addPoint(xCenter, yCenter);

		Polygon eastTriangle = new Polygon();
		eastTriangle.addPoint(xMax, 0);
		eastTriangle.addPoint(xMax, yMax);
		eastTriangle.addPoint(xCenter, yCenter);

		Polygon southTriangle = new Polygon();
		southTriangle.addPoint(0, yMax);
		southTriangle.addPoint(xMax, yMax);
		southTriangle.addPoint(xCenter, yCenter);

		if (westTriangle.contains(p)) {
			direction = CardinalPoints.WEST;
		} else if (northTriangle.contains(p)) {
			direction = CardinalPoints.NORTH;
		} else if (eastTriangle.contains(p)) {
			direction = CardinalPoints.EAST;
		} else if (southTriangle.contains(p)) {
			direction = CardinalPoints.SOUTH;
		}

		return Optional.ofNullable(direction);

	};

}
