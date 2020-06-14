package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import common.GameFileFormatException;

/**
 * This class provides static methods to convert a bridge game from a bgs file
 * to a {@link GameModel} and vice versa.
 * <p>
 * The bgs file format has following conventions:<br>
 * <li>Comments start with '<b>#</b>' and are ignored <br>
 * <li>Empty lines are ignored<br>
 * The structure of the file is defined as:<br>
 * <ul>
 * <b>FIELD</b><br>
 * <i># Width x Height | Number of islands<br>
 * # For example:</i><br>
 * 5 x 6 | 2 <br>
 * <b>ISLANDS </b><br>
 * <i># { ( Column, Row | Number of bridges ) } </i><br>
 * <i># Columns and rows are 0-indexed<br>
 * # For example: </i><br>
 * ( 0, 1 | 2 ) <br>
 * ( 0, 5 | 3 ) <br>
 * 
 * <b>BRIDGES </b><br>
 * #<i> { ( Start Index, End Index | Double Bridge ) }<br>
 * # Allowed to be empty<br>
 * # For example: </i><br>
 * ( 0, 1 | true )
 * </ul>
 * 
 * @author grimm
 *
 */
class GameModelIO {
	/**
	 * Converts the file lines to a {@link GameModel}, if syntax is incorrect a
	 * {@link GameFileFormatException} is thrown. Comments and empty lines are
	 * ignored.
	 * 
	 * @param fileLines A List of {@code String} where each element is a line in the
	 *                  bgs file
	 * @return {@link GameModel} of a bridge game
	 * @throws GameFileFormatException if the syntax of the {@code fileLines} is
	 *                                 incorrect
	 */

	static GameModel loadGame(List<String> fileLines) throws GameFileFormatException {
		GameModel newGameModel = new GameModel();
		ArrayList<String> filteredLines = new ArrayList<String>();
		for (String line : fileLines) {
			if (!(line.isEmpty() || line.startsWith("#"))) {
				filteredLines.add(line);
			}
		}
		// check fields are present
		int index = filteredLines.indexOf("FIELD");
		if (index != 0) {
			throw new GameFileFormatException();
		}
		index = filteredLines.indexOf("ISLANDS");
		if (index != 2) {
			throw new GameFileFormatException();
		}
		int islandDefinitionEnd = filteredLines.size();
		int indexBridges = filteredLines.indexOf("BRIDGES");
		if (indexBridges == -1) {
			// no bridges defined

		} else if (indexBridges > 3) {
			// everything okay
			islandDefinitionEnd = indexBridges;
		} else {
			throw new GameFileFormatException();
		}
		parseFieldDefinition(filteredLines.get(1), newGameModel);
		for (int i = 3; i < islandDefinitionEnd; i++) {
			parseIslandDefinition(filteredLines.get(i), newGameModel.getIslands());
		}
		if (indexBridges != -1) {
			for (int i = indexBridges + 1; i < filteredLines.size(); i++) {
				parseBridgeDefinition(filteredLines.get(i), newGameModel.getIslands(), newGameModel.getBridges());
			}
		}
		return newGameModel;

	}

	private static void parseFieldDefinition(String fieldString, GameModel gameModel) throws GameFileFormatException {
		try {

			String[] spl1 = fieldString.split("\\|");
			int nrIslands = Integer.parseInt(spl1[1].trim());

			String[] spl2 = spl1[0].split("x");
			int nrRows = Integer.parseInt(spl2[0].trim());
			int nrColumns = Integer.parseInt(spl2[1].trim());

			gameModel.setNrOfIslands(nrIslands);
			gameModel.setNrOfRows(nrRows);
			gameModel.setNrOfColumns(nrColumns);

		} catch (IndexOutOfBoundsException | NumberFormatException e) {
			throw new GameFileFormatException();
		}
	}

	private static void parseIslandDefinition(String fieldString, List<Island> islands) throws GameFileFormatException {
		try {
			if (!(fieldString.startsWith("(") && fieldString.endsWith(")"))) {
				throw new GameFileFormatException();
			}
			fieldString = fieldString.substring(1, fieldString.length() - 1);

			String[] spl1 = fieldString.split("\\|");
			int nrBridges = Integer.parseInt(spl1[1].trim());

			String[] spl2 = spl1[0].split(",");
			int row = Integer.parseInt(spl2[0].trim());
			int column = Integer.parseInt(spl2[1].trim());

			islands.add(new Island(row, column, nrBridges));

		} catch (IndexOutOfBoundsException | NumberFormatException e) {
			throw new GameFileFormatException();
		}
	}

	private static void parseBridgeDefinition(String fieldString, List<Island> islands, List<Bridge> bridges)
			throws GameFileFormatException {
		try {
			if (!(fieldString.startsWith("(") && fieldString.endsWith(")"))) {

			}
			fieldString = fieldString.substring(1, fieldString.length() - 1);

			String[] spl1 = fieldString.split("\\|");
			boolean isDoubleBridge = Boolean.parseBoolean(spl1[1].trim());

			String[] spl2 = spl1[0].split(",");
			int row = Integer.parseInt(spl2[0].trim());
			int column = Integer.parseInt(spl2[1].trim());

			bridges.add(new Bridge(islands.get(row), islands.get(column), isDoubleBridge, true));

		} catch (IndexOutOfBoundsException | NumberFormatException e) {
			throw new GameFileFormatException();
		}
	}

	/**
	 * Transfers a {@link GameModel} to a String which represents the grammar of the
	 * bgs file format
	 * 
	 * @param model a {@link GameModel}
	 * @return String in bgs file format
	 */

	static String toFileFormat(GameModel model) {
		StringBuffer sbf = new StringBuffer();
		sbf.append("FIELD\n");
		sbf.append("# Height x Width | Number of islands\n");
		sbf.append(
				String.format("%d x %d | %d\n\n", model.getNrOfRows(), model.getNrOfColumns(), model.getNrOfIslands()));

		sbf.append("ISLANDS\n");

		List<Island> sortedIslands = model.getIslands().stream()
				.sorted(Comparator.comparing(Island::getColumn).thenComparing(Island::getRow))
				.collect(Collectors.toList());

		for (Island island : sortedIslands) {
			sbf.append(island.toFileFormat() + "\n");
		}
		List<Bridge> sortedBridges = model.getBridges().stream()
				.sorted(Comparator.comparing(bridge -> getSmallerBridgeIndex(bridge, sortedIslands)))
				.collect(Collectors.toList());

		sbf.append("\n");
		sbf.append("BRIDGES");
		for (Bridge br : sortedBridges) {
			int smallerIndex = getSmallerBridgeIndex(br, sortedIslands);
			int biggerIndex = getBiggerBridgeIndex(br, sortedIslands);
			sbf.append(
					String.format("( %d , %d | %s)", smallerIndex, biggerIndex, String.valueOf(br.isDoubleBridge())));
		}

		return sbf.toString();

	}

	private static int getSmallerBridgeIndex(Bridge bridge, List<Island> sortedIsland) {
		int index1 = sortedIsland.indexOf(bridge.getIsland1());
		int index2 = sortedIsland.indexOf(bridge.getIsland2());
		return index1 < index2 ? index1 : index2;
	}

	private static int getBiggerBridgeIndex(Bridge bridge, List<Island> sortedIsland) {
		int index1 = sortedIsland.indexOf(bridge.getIsland1());
		int index2 = sortedIsland.indexOf(bridge.getIsland2());
		return index1 > index2 ? index1 : index2;
	}

}
