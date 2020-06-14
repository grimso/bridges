package view;

import java.nio.file.Path;

import common.BuildOrRemoveBridgeRequest;

public interface ControllerInterface {
	public void loadGame(Path pathToFile);

	public void saveGame();

	public void saveGameAt();

	boolean  createNewGameRandomly();
	
	boolean createNewGameHightWidth(int height,int width );
	
	boolean createNewGameHightWidthIslands(int height,int width,int islands);

	public void buildBridge(BuildOrRemoveBridgeRequest req);

	public void restartGame();

	public void solveNextBridge();

	public void startAndStopAutoSolving();
}
