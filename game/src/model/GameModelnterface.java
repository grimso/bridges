package model;

import java.util.List;

import common.BuildOrRemoveBridgeRequest;
import common.GameFileFormatException;
import common.GameObserver;

/**
 * The {@code GameModelInterface} specifies the methods a bridge game needs to provide.
 * 
 * 
 * @author grimm
 *
 */
public interface GameModelnterface {
	/**
	 * Create a new bridge game
	 * @return 
	 */
	public boolean createNewGame(int nrOfRows, int nrOfColumns, int nrOfIslands);


	/**
	 * Given the current state of the game, try to add a bridge and update the game
	 * state on success. <br>
	 * Solves the next step and add a bridge which is in compliance with the game
	 * constraints.
	 *
	 * <p>
	 * Returns {@code true} if a bridge was successfully added to the game.
	 *
	 * @return {@code true} if a bridge was successfully added to the game.
	 */
	public boolean solveNextBridge();
	/**
	 * Converts the string lines resulting from reading a bridge game file (*.bgs) to it's object representation.
	 * Requires a {@code List<String> }
	 * @param fileLines
	 * @throws GameFileFormatException
	 */
	public void loadGame(List<String> filelines) throws GameFileFormatException;

	/**
	 * Converts the current game state to a String with represents the grammar of bridge game file format (*bgs).
	 * @return {@Code String}  representing the bridge game in the bridge game file grammar
	 */
	public String toFileFormat();

	/**
	 * Restarts a bridge game. 
	 * <p>
	 * Removes all bridges build so far, hence only islands are present.
	 */
	public void restartGame();
	/**
	 * Try to add or remove a bridge as specified in {@link BuildOrRemoveBridgeRequest}.
	 * <p>
	 * Given island coordinates x,y and a direction try to build or remove a bridge according to the rules of the game.
	 * 
	 * @param gameMoveRequest
	 * @return {@code true} if the requests game move was successfully
	 */
	public boolean buildOrRemoveBridge(BuildOrRemoveBridgeRequest gameMoveRequest);

	/**
	 * Register a {@link GameObserver}, which can then be notified accordingly.
	 * @param gameObserver
	 */
	public void registerObserver(GameObserver gameObserver);

	/**
	 * Returns {@code true} if a bridge game is solved. More precice returns
	 * {@code true} if no more bridges can be added to the game and all islands are
	 * connected.
	 * 
	 * @return {@code true] if a bridge game is solved
	 */
	public boolean isGameSolved();

	/**
	 * Get the number of columns of a bridge game board
	 * 
	 * @return the number of columns of a game board
	 */
	public int getNrOfColumns();

	/**
	 * Get the number of rows of a bridge game board
	 * 
	 * @return the number of rows of a game board
	 */
	public int getNrOfRows();
	/**
	 * Returns a @Code{List} of {@link Island} of the game.
	 * <br> All islands of a game are stored in this unordered list
	 * @return a @Code{List} of {@link Island} of the game.
	 */
	public List<Island> getIslands();
	/**
	 * Returns a @Code{List} of {@link Bridge} of the game.
	 * <br> All bridges of a game are stored in this unordered list
	 * @return a @Code{List} of {@link Bridge} of the game.
	 */
	public List<Bridge> getBridges();

}
