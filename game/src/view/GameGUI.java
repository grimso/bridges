package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import common.GameObserver;
import model.Bridge;
import model.GameModelnterface;
import model.Island;

public class GameGUI implements GameObserver {

	GameModelnterface model;
	ControllerInterface controller;
	private BridgesBoard gameBord;
	private final static String GAME_SOLVED_MESSAGE = "Game is solved";
	private final static String GAME_UNSOLVED_MESSAGE = "Game is not solved yet";

	private boolean displayMissingBridges = true;
	private boolean displayGameStatus;

	protected JFrame frame;
	private final JPanel gui = new JPanel(new BorderLayout(3, 3));
	protected JPanel boardConstrain = new JPanel(new GridBagLayout());
	private final JFileChooser fileChooser = new JFileChooser();

	private final JToolBar tools = new JToolBar();;
	private JButton nextBridge;
	private JButton solveGame;
	private JCheckBox displayOption;

	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu menu = new JMenu("File");
	private JMenuItem createNewGame;
	private JMenuItem restartGame;
	private JMenuItem loadGame;
	private JMenuItem saveGame;
	private JMenuItem saveGameAt;
	private JMenuItem exitGame;

	private final JLabel statusLabel = new JLabel("Load or create a game");
	
	private NewGameDialog dilaog;

	public GameGUI(GameModelnterface model, ControllerInterface controller) {
		this.model = model;
		this.controller = controller;
		this.model.registerObserver((GameObserver) this);
		this.gameBord = new BridgesBoard(this.controller);
		this.inizalizeMenu();
		this.inizaliteTools();
		this.createAndShowGUI();
		this.dilaog=new NewGameDialog(this.frame,controller);
	}

	public void enableRestartGameMenuItem() {
		this.restartGame.setEnabled(true);
	}

	public void disableRestartGameMenuItem() {
		this.restartGame.setEnabled(false);
	}

	public void enableSaveGameMenuItems() {
		this.saveGame.setEnabled(true);
		this.saveGameAt.setEnabled(true);
	}

	public void disableSaveGameMenuItems() {
		this.saveGame.setEnabled(false);
		this.saveGameAt.setEnabled(false);
	}

	public void disableTools() {
		this.displayOption.setEnabled(false);
		this.solveGame.setEnabled(false);
		this.nextBridge.setEnabled(false);
	}

	public void enableTools() {
		this.displayOption.setEnabled(true);
		this.solveGame.setEnabled(true);
		this.nextBridge.setEnabled(true);
	}

	private final void inizalizeMenu() {

		this.fileChooser.setFileFilter(new BGSFileFilter());
		this.createNewGame = new JMenuItem("New puzzle");
		this.createNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dilaog.setLocationRelativeTo(frame);
				dilaog.setVisible(true);
				//controller.createNewGame();

			}
		});

		menu.add(this.createNewGame);

		this.restartGame = new JMenuItem("Restart puzzle");

		this.restartGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.restartGame();

			}
		});
		menu.add(this.restartGame);

		this.loadGame = new JMenuItem("Load puzzle");

		this.loadGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = fileChooser.showOpenDialog(null);
				if (returnVal == 0) {

					File file = fileChooser.getSelectedFile();
					fileChooser.setCurrentDirectory(file);
					controller.loadGame(Paths.get(file.getPath()));

				}

			}
		});
		menu.add(this.loadGame);

		this.saveGame = new JMenuItem("Save puzzle");

		this.saveGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				controller.saveGame();

			}
		});
		menu.add(this.saveGame);

		this.saveGameAt = new JMenuItem("Save puzzle under ...");

		this.saveGameAt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.saveGameAt();
			}
		});
		menu.add(this.saveGameAt);

		this.exitGame = new JMenuItem("Exit");

		this.exitGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		menu.add(this.exitGame);
	}

	private final void inizaliteTools() {
		tools.setFloatable(false);
		tools.setAlignmentX(Component.CENTER_ALIGNMENT);

		this.nextBridge = new JButton("Next Bridge");
		this.nextBridge.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.solveNextBridge();

			}

		});

		tools.add(this.nextBridge);

		this.solveGame = new JButton("Solve");
		this.solveGame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.startAndStopAutoSolving();

			}

		});
		tools.add(this.solveGame);
		tools.addSeparator();
		this.displayOption = new JCheckBox("display nr of missing bridges");
		this.displayOption.setSelected(true);
		this.displayOption.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED) {
					displayMissingBridges = true;
					updateGame();
				} else {
					displayMissingBridges = false;
					updateGame();
				}

			}
		});
		tools.add(this.displayOption);

	}

	private final void createAndShowGUI() {
		// Create and set up the window.
		this.frame = new JFrame("Bridges Game");
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.menuBar.add(this.menu);
		frame.setJMenuBar(this.menuBar);

		this.gui.setLayout(new BoxLayout(this.gui, BoxLayout.PAGE_AXIS));

		this.gui.add(this.tools);
		gui.add(Box.createHorizontalGlue());

		this.boardConstrain.add(this.gameBord);
		this.boardConstrain.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.gui.add(boardConstrain);

		this.statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.gui.add(this.statusLabel);

		frame.getContentPane().add(this.gui);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void updateGame() {
		this.gameBord.setNrColumnsAndRows(this.model.getNrOfRows(), this.model.getNrOfColumns());
		List<Island2D> lIsland = new ArrayList<Island2D>();
		for (Island island : this.model.getIslands()) {
			Color color = island.getCurrentBridgeCapacity() == 0 ? new Color(130, 200, 140) : Color.LIGHT_GRAY;
			Island2D iView = new Island2D(island.getRow(), island.getColumn());
			iView.setColor(color);
			iView.setLabel(this.displayMissingBridges ? String.valueOf(island.getCurrentBridgeCapacity())
					: String.valueOf(island.getBridgeCapacity()));

			lIsland.add(iView);
		}
		List<Bridge2D> lBridges = new ArrayList<Bridge2D>();
		for (Bridge bridge : this.model.getBridges()) {
			Island2D iv1 = lIsland.stream()
					.filter(f -> f.getColumn() == bridge.getIsland1().getColumn() && f.getRow() == bridge.getIsland1().getRow())
					.findFirst().get();
			Island2D iv2 = lIsland.stream()
					.filter(f -> f.getColumn() == bridge.getIsland2().getColumn() && f.getRow() == bridge.getIsland2().getRow())
					.findFirst().get();
			lBridges.add(new Bridge2D(iv1, iv2, bridge.isDoubleBridge(), bridge.isModifiedLatest()));
		}
		if (this.displayGameStatus) {
			if (this.model.isGameSolved()) {
				this.statusLabel.setText(GAME_SOLVED_MESSAGE);
			} else {
				this.statusLabel.setText(GAME_UNSOLVED_MESSAGE);
			}
		}

		this.gameBord.setIslands(lIsland);
		this.gameBord.setBridges(lBridges);
		this.gameBord.repaint();

		frame.revalidate();
		frame.repaint();

	}

	protected void packAndRepaint() {

		frame.revalidate();
		frame.pack();
		frame.repaint();
	}

	protected JFileChooser getBGSFileChooser() {
		return this.fileChooser;
	}

	protected void setDisplayGameStatus(boolean isDisplay) {
		this.displayGameStatus = isDisplay;

	}

	protected void displayErrorDialog(String message) {
		JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

}
