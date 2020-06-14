package view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import common.BuildOrRemoveBridgeRequest;
import common.GameFileFormatException;
import model.GameModelnterface;

public class GameController implements ControllerInterface {
	GameModelnterface model;
	GameGUI view;
	private Optional<Path> savedFilePath = Optional.ofNullable(null);
	private boolean autoSolve = false;
	private final Executor executor = Executors.newFixedThreadPool(1, new ThreadFactory() {
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		}
	});

	public GameController(GameModelnterface model) {
		this.model = model;
		this.view = new GameGUI(model, this);
		this.view.disableRestartGameMenuItem();
		this.view.disableSaveGameMenuItems();
		this.view.disableTools();
	}

	@Override
	public void loadGame(Path path) {
		try {

			List<String> inputStrings = new ArrayList<String>();
			try (BufferedReader br = Files.newBufferedReader(path)) {

				String line = br.readLine();
				while (line != null) {
					line = line.trim();
					// skip empty lines or comments (indicated by leading #)
					if (!(line.isEmpty() || line.startsWith("#"))) {
						inputStrings.add(line);
					}
					line = br.readLine();
				}

			} catch (IOException e) {
				this.view.displayErrorDialog(e.getMessage());

			}
			this.view.setDisplayGameStatus(true);
			this.model.loadGame(inputStrings);
			this.view.enableRestartGameMenuItem();
			this.view.enableSaveGameMenuItems();
			this.view.enableTools();
			this.savedFilePath = Optional.ofNullable(null);
			this.view.packAndRepaint();
		} catch (GameFileFormatException e) {
			this.view.displayErrorDialog(e.getMessage());
		}
	}

	public void restartGame() {
		this.model.restartGame();
	}

	@Override
	public void buildBridge(BuildOrRemoveBridgeRequest req) {
		boolean isValid = this.model.buildOrRemoveBridge(req);

	}

	@Override
	public void saveGame() {
		if (savedFilePath.isEmpty()) {
			this.saveGameAt();
		} else {
			this.saveGame(this.savedFilePath.get());
		}
	}

	private void saveGame(Path pathToFile) {
		String currentGameState = this.model.toFileFormat();

		try (BufferedWriter fbw = Files.newBufferedWriter(pathToFile)) {
			fbw.write(currentGameState);
		} catch (IOException e) {
			this.view.displayErrorDialog(e.getMessage());
		}

	}

	@Override
	public void saveGameAt() {
		int returnVal = this.view.getBGSFileChooser().showSaveDialog(null);
		if (returnVal == 0) {
			File file = this.view.getBGSFileChooser().getSelectedFile();
			this.view.getBGSFileChooser().setCurrentDirectory(file);
			if (file.getName().endsWith("." + BGSFileFilter.FILE_ENDING)) {
				Path path = Paths.get(file.getAbsolutePath());
				this.savedFilePath = Optional.ofNullable(path);
				this.saveGame(path);

			} else {
				Path path = Paths.get(file.getAbsolutePath() + "." + BGSFileFilter.FILE_ENDING);
				this.savedFilePath = Optional.ofNullable(path);
				this.saveGame(path);
			}

		}

	}

	@Override
	public void solveNextBridge() {
		this.model.solveNextBridge();

	}

	@Override
	public void startAndStopAutoSolving() {
		this.autoSolve = !this.autoSolve;
		if (autoSolve) {
			CompletableFuture.supplyAsync(() -> startAndStopAutoSolving1(), executor);
		}

	}

	private boolean startAndStopAutoSolving1() {
		boolean nextStepSolved = true;

		while (nextStepSolved && autoSolve) {
			nextStepSolved = this.model.solveNextBridge();
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.autoSolve = false;

		return true;
	}

	public void createNewGame() {
		CompletableFuture.supplyAsync(() -> performNewGame(), executor);
		this.view.enableTools();
		this.view.enableSaveGameMenuItems();
		this.view.enableRestartGameMenuItem();

	}

	private boolean performNewGame() {
		this.model.createNewGame(4, 4, 4);
		return true;
	}

	@Override
	public boolean createNewGameRandomly() {
		// next int upper range limit is exclusive, therefore we add +1
		int randomNrRows = ThreadLocalRandom.current().nextInt(4, 25 + 1);
		int randomNrColumns = ThreadLocalRandom.current().nextInt(4, 25 + 1);
		int randomNrIslands = ThreadLocalRandom.current().nextInt(2, (int) (randomNrRows * randomNrColumns * 0.2) + 1);
		boolean successfull=this.model.createNewGame(randomNrRows, randomNrColumns, randomNrIslands);
		if(successfull) {
			this.view.packAndRepaint();
			this.view.enableRestartGameMenuItem();
			this.view.enableSaveGameMenuItems();
			this.view.enableTools();
		}
		return successfull;
	}

	@Override
	public boolean createNewGameHightWidth(int height, int width) {

		validateNewGameHightWidth(height, width);
		int randomNrIslands = ThreadLocalRandom.current().nextInt(2, (int) (height * width * 0.2) + 1);
		boolean successfull= this.model.createNewGame(height, width, randomNrIslands);
		if(successfull) {
			this.view.packAndRepaint();
			this.view.enableRestartGameMenuItem();
			this.view.enableSaveGameMenuItems();
			this.view.enableTools();
		}
		return successfull;

	}

	@Override
	public boolean createNewGameHightWidthIslands(int height, int width, int islands) {

		validateNewGameHightWidthIslands(height, width, islands);
		boolean successfull=this.model.createNewGame(height, width, islands);
		if(successfull) {
			this.view.packAndRepaint();
			this.view.enableRestartGameMenuItem();
			this.view.enableSaveGameMenuItems();
			this.view.enableTools();
		}
		return successfull;
	}

	public static void validateNewGameHightWidth(int height, int width) {
		String errorMessage = "";
		if ((width < 4 || width > 25)) {
			errorMessage += "Der Wert für 'Breite' is nicht im erlaubten Bereich: 4 => x => 25\n";
		}
		if ((height < 4 || height > 25)) {
			errorMessage += "Der Wert für 'Höhe' is nicht im erlaubten Bereich: 4 => x => 25\n";
		}
		if (!errorMessage.isEmpty()) {
			throw new IllegalArgumentException(errorMessage);
		}

	}

	public static void validateNewGameHightWidthIslands(int height, int width, int islands) {
		validateNewGameHightWidth(height, width);
		if (!(islands >= 2 && islands <= (int) (0.2 * width * height))) {
			throw new IllegalArgumentException(String.format("Inselanzahl ist nicht im erlaubten Bereich: 2=> x => %d",
					(int) (0.2 * width * height)));
		}
	}

}
