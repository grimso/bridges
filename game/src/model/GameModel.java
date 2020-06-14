package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import common.BuildOrRemoveBridgeRequest;
import common.CardinalPoints;
import common.GameFileFormatException;
import common.GameObserver;

/**
 * This class holds the current state of a Bridges game and the logic to change
 * it according to the rules of the game.
 * 
 * <p> Implements the {@link GameModelnterface}
 * 
 * @author grimm
 *
 */
public class GameModel implements GameModelnterface {
	// game board dimensions
	private int nrOfRows;
	private int nrOfColumns;
	private int nrOfIslands;
	// list of bridges and islands
	private List<Island> islands = new ArrayList<Island>();
	private List<Bridge> bridges = new ArrayList<Bridge>();
	private List<GameObserver> observer = new ArrayList<GameObserver>();
	

	public int getNrOfRows() {
		return nrOfRows;
	}

	public int getNrOfColumns() {
		return nrOfColumns;
	}

	public List<Island> getIslands() {
		return islands;
	}

	public List<Bridge> getBridges() {
		return bridges;
	}

	private void updateGameOservers() {
		for (GameObserver o : this.observer) {
			o.updateGame();
		}
	}

	public void registerObserver(GameObserver o) {
		this.observer.add(o);
	}

	public void removeObserver(GameObserver o) {
		this.observer.remove(o);

	}

	int getNrOfIslands() {
		return nrOfIslands;
	}

	void setNrOfIslands(int nrOfIslands) {
		this.nrOfIslands = nrOfIslands;
	}

	void setNrOfRows(int nrOfRows) {
		this.nrOfRows = nrOfRows;
	}

	void setNrOfColumns(int nrOfColumns) {
		this.nrOfColumns = nrOfColumns;
	}

	void setIslands(List<Island> islands) {
		this.islands = islands;
	}

	void setBridges(List<Bridge> bridges) {
		this.bridges = bridges;
	}

	@Override
	public void loadGame(List<String> fileLines) throws GameFileFormatException {
		GameModel newGameModel = GameModelIO.loadGame(fileLines);
		this.islands = newGameModel.islands;
		this.bridges = newGameModel.bridges;
		this.nrOfColumns = newGameModel.nrOfColumns;
		this.nrOfIslands = newGameModel.nrOfIslands;
		this.nrOfRows = newGameModel.nrOfRows;
		this.updateGameOservers();

	}

	@Override
	public String toFileFormat() {
		return GameModelIO.toFileFormat(this);

	}

	@Override
	public boolean buildOrRemoveBridge(BuildOrRemoveBridgeRequest r) {
		return this.buildOrRemoveBridge(r, true);
	}

	private boolean buildOrRemoveBridge(BuildOrRemoveBridgeRequest r, boolean changeGameState) {
		// try to get a island form the game board at the given coordinates
		Optional<Island> possibleIsland = GameModelUtils.getIsland(r.getRow(), r.getCol(), this.islands);
		if (possibleIsland.isEmpty()) {
			// island is not present
			return false;
		}
		// island is present in the game at the requested coordinates
		Island island = possibleIsland.get();
		// get direct neighbours of this island
		Map<CardinalPoints, Optional<Island>> neighboursOfIsland = GameModelUtils.getNeighbours(island,
				this.islands);
		Optional<Island> wantedTragetIsland = neighboursOfIsland.get(r.getDirection());
		if (wantedTragetIsland.isEmpty()) {
			// no neighbor for defined island and direction
			// nothing can be changed
			return false;
		}
		Island targetIsland = wantedTragetIsland.get();

		if (r.isRemove()) {
			// attempt to remove bridge
			return this.removeBridgeBetweenIslands(island, targetIsland, changeGameState);
		} else {
			// attempt to build bridge
			return this.buildBridgeBetweenIslands(island, targetIsland, changeGameState, true);

		}
	}

	private boolean removeBridgeBetweenIslands(Island island, Island targetIsland, boolean changeGameState) {
		// get bridge between island (if present)
		Optional<Bridge> possibleBridge = GameModelUtils.getBridgeBetweenIslands(island, targetIsland, this.bridges);
		if (possibleBridge.isEmpty()) {
			// no bridge between islands, so nothing to remove...
			return false;
		} else {
			// there is a bridge that can be removed
			if (changeGameState) {
				// update which bridge was modified latest
				// if removing we set all to false
				for (Bridge bridge : this.bridges) {
					bridge.setModifiedLatest(false);
				}
				// remove (double bridge -> single bridge; single bridge -> no bridge)
				GameModelUtils.removeBridge(possibleBridge.get(), this.bridges);
				// game state changed notify observers
				this.updateGameOservers();
			}
			return true;
		}
	}

	private boolean buildBridgeBetweenIslands(Island island, Island targetIsland, boolean changeGameState,
			boolean allowInsertDoubleBridge) {
		// get bridge between island (if present)
		Optional<Bridge> possibleBridge = GameModelUtils.getBridgeBetweenIslands(island, targetIsland, this.bridges);
		// no bridge - attempt building a new single bridge
		if (possibleBridge.isEmpty()) {
			// a bridge can only build if both islands sill have capacity
			if (island.getCurrentBridgeCapacity() > 0 && targetIsland.getCurrentBridgeCapacity() > 0) {
				// both island fullfill prerequisite, a single build is build
				Bridge potentialNewBridge = new Bridge(island, targetIsland, false, false);
				// check if this bridge is crossing any other bridges
				// if so this violated the game constraints and bridge can not be added
				for (Bridge currentBridge : this.bridges) {
					if (Bridge.areCrossingBridges(potentialNewBridge, currentBridge)) {
						return false;
					}
				}
				// no it is sure that bridge does not violate any constraints, add to game if
				// changeGameStat is true
				if (changeGameState) {
					for (Bridge bridge : this.bridges) {
						bridge.setModifiedLatest(false);
					}
					// we can add the bridge (here the same bridge is build, but island states are
					// changed accordingly)
					Bridge newBridge = new Bridge(island, targetIsland, false,true);
					newBridge.setModifiedLatest(true);
					// add bridge to game
					this.bridges.add(newBridge);
					// notify observers game state changed
					this.updateGameOservers();
				}
				return true;
			} else {
				return false;
			}
		}
		// a bridge is present if possible we can "upgrade" it to a double bridge
		else {
			Bridge bridge = possibleBridge.get();
			if (bridge.isDoubleBridge()) {
				// no more bridge capacity
				return false;
			} else if (allowInsertDoubleBridge && island.getCurrentBridgeCapacity() > 0
					&& targetIsland.getCurrentBridgeCapacity() > 0) {
				if (changeGameState) {
					for (Bridge bri : this.bridges) {
						bri.setModifiedLatest(false);
					}
					bridge.setDoubleBridge(true);
					bridge.setModifiedLatest(true);
					bridge.getIsland1().setCurrentNrOfBridges(bridge.getIsland1().getCurrentNrOfBridges() + 1);
					bridge.getIsland2().setCurrentNrOfBridges(bridge.getIsland2().getCurrentNrOfBridges() + 1);
					this.updateGameOservers();
				}
				return true;
			} else {
				return false;
			}
		}

	}

	public void restartGame() {
		this.bridges = new ArrayList<Bridge>();
		for (Island island : this.islands) {
			island.setCurrentNrOfBridges(0);
		}
		this.updateGameOservers();
	}

	@Override
	public boolean isGameSolved() {
		// condition 1 no more bridge capacity for all island
		int nrIslandWithNoBridgeCapacity = this.islands.stream().filter(i -> i.getCurrentBridgeCapacity() == 0)
				.collect(Collectors.toList()).size();
		if (nrIslandWithNoBridgeCapacity != this.nrOfIslands) {

			return false;
		}
		// we only want to check if all islands are connected when we now that islands
		// have no more bridge capacity

		int nrOfReacbilbeIslands = GameModelUtils.depthFirstSearch(this.islands.get(0), this.bridges);
		if (nrOfReacbilbeIslands != this.nrOfIslands) {
			return false;
		}
		return true;
	}


	public boolean createNewGame(int nrOfRows, int nrOfColumns, int nrOfIslands) {
		// randomly create coordinated for the first island
		int initalIslandRow = ThreadLocalRandom.current().nextInt(0, nrOfRows);
		int initalIslandColumn = ThreadLocalRandom.current().nextInt(0, nrOfColumns);
		// when creating a new island we set the bridge capacity to the maximum (8)
		// -> whenever we add a bridge successfully where this island is part the
		// current bridge capacity is
		// decreased accordingly
		Island initalIsland = new Island(initalIslandRow, initalIslandColumn, 8);

		// In newGameIslands, we track which island(s) were added
		ArrayList<Island> newGameIslands = new ArrayList<Island>();
		newGameIslands.add(initalIsland);
		// In newGameBridges, we track which bridge(s) were added
		ArrayList<Bridge> newGameBridges = new ArrayList<Bridge>();

		boolean additionSucessfull = true;
		// add new islands until number of wanted islands is reached or abort if it was
		// not possible to add a new island
		while (newGameIslands.size() != nrOfIslands && additionSucessfull) {
			System.out.println(newGameIslands.size());
			// create a index of all islands, which are added to the game
			// shuffle index, so that we can traverse the islands randomly, while trying to
			// add a new island to it
			List<Integer> randomTraversal = IntStream.range(0, newGameIslands.size()).boxed()
					.collect(Collectors.toList());
			Collections.shuffle(randomTraversal);
			additionSucessfull = false;
			// traverse randomized index (hence islands are traversed randomly)
			for (int index : randomTraversal) {
				Island potentialNewIsland = newGameIslands.get(index);
				//
				Map<CardinalPoints, List<Island>> possibleDirections = this.getFeasibeDirectionIslandMap(
						potentialNewIsland, newGameIslands, newGameBridges, nrOfRows, nrOfColumns, true);
				if (possibleDirections.size() == 0) {
					// map is empty, not possible to build a bridge and create a new island in any
					// direction.
					// no new bridges can be build starting from this island
					// continue in island traversal
					continue;
				} else {
					// at least in one direction a bridge can be build
					// pick direction randomly(via position)
					int randomDirection = ThreadLocalRandom.current().nextInt(0, possibleDirections.size());
					// possible list of island in one direction, where it is possible to build a
					// bridge starting from the current position
					List<Island> islandsInDirection = new ArrayList<List<Island>>(possibleDirections.values())
							.get(randomDirection);
					// randomly pick island where the bridge will build to
					int islandNR = ThreadLocalRandom.current().nextInt(0, islandsInDirection.size());
					Island islandToAdd = islandsInDirection.get(islandNR);
					// add island to available islands
					newGameIslands.add(islandToAdd);
					// create a new bridge between the islands, and randomly choose if it is a
					// single or double bridge
					Bridge newBridge = new Bridge(potentialNewIsland, islandToAdd,
							ThreadLocalRandom.current().nextBoolean(),true);
					newBridge.setModifiedLatest(true);
					for (Bridge b : newGameBridges) {
						b.setModifiedLatest(false);
					}
					newGameBridges.add(newBridge);
					// addition was success full, break so that a new island can be added
					additionSucessfull = true;
					break;

				}
			}

		}
		if (newGameIslands.size() == nrOfIslands && additionSucessfull) {
			for (Island isl : newGameIslands) {
				isl.setBridgeCapacity(isl.getCurrentNrOfBridges());
				isl.setCurrentNrOfBridges(0);
			}
			this.nrOfColumns = nrOfColumns;
			this.nrOfIslands = nrOfIslands;
			this.nrOfRows = nrOfRows;
			this.bridges = new ArrayList<Bridge>();
			this.islands = newGameIslands;

			this.updateGameOservers();
			return true;
		} else {
			return false;
		}

	}

	private Map<CardinalPoints, List<Island>> getFeasibeDirectionIslandMap(Island islandBridgeStart,
			List<Island> islands, ArrayList<Bridge> newGameBridges, int nrOfRows, int nrOfColumns,
			boolean limitBridgeLength) {
		// get directions of island where a bridge exists already (no new bridge can be
		// build here)
		Set<CardinalPoints> directionsWithBridge = GameModelUtils.getBridgeDirectionsOfIsland(islandBridgeStart,
				newGameBridges);
		Map<CardinalPoints, List<Island>> result = new HashMap<CardinalPoints, List<Island>>();
		// iterate over all cardinal directions
		for (CardinalPoints direction : CardinalPoints.values())
			// if a in a direction a bridge is already build, no new bridge can be added
			if (!directionsWithBridge.contains(direction)) {
				// get all feasible islands (with respect to the current game constraints) where
				// a bridge could be built to
				List<Island> feasibeCandidatesInDirections = getPossibleIslandsInDirection(direction, islandBridgeStart,
						newGameBridges, islands, limitBridgeLength ? 1 : nrOfColumns, nrOfColumns, nrOfRows);
				if (!feasibeCandidatesInDirections.isEmpty()) {
					// add to result if there are feasible candidates
					result.put(direction, feasibeCandidatesInDirections);
				}
			}
		return result;

	}

	private List<Island> getPossibleIslandsInDirection(CardinalPoints direction, Island islandBridgeStart,
			List<Bridge> currentBridges, List<Island> currentIslands, int limit, int nrOfColumns, int nrOfRows) {
		// coordinates of starting island
		int islandRow = islandBridgeStart.getRow();
		int islandColumn = islandBridgeStart.getColumn();
		// get neighbours of island in all directions (if present, no matter if there is
		// a bridge between or not)
		Map<CardinalPoints, Optional<Island>> closessNeighbour = GameModelUtils.getNeighbours(islandBridgeStart,
				currentIslands);
		// get island in a specific direction (empty if no neighbour in that direction)
		// any island further away can't be a neighbour..
		Optional<Island> closestN = closessNeighbour.get(direction);

		int[] range = null;
		if (direction == CardinalPoints.WEST) {
			int rangeRestrictionW = closestN.isPresent() ? closestN.get().getColumn() : 0;
			range = IntStream.iterate(islandColumn - 2, n -> n - 2).limit(limit).filter(n -> n >= rangeRestrictionW)
					.toArray();
		} else if (direction == CardinalPoints.EAST) {
			int rangeRestrictionE = closestN.isPresent() ? closestN.get().getColumn() : nrOfColumns;
			range = IntStream.iterate(islandColumn + 2, n -> n + 2).limit(limit).filter(n -> n < rangeRestrictionE)
					.toArray();
		} else if (direction == CardinalPoints.NORTH) {
			int rangeRestrictionN = closestN.isPresent() ? closestN.get().getRow() : 0;
			range = IntStream.iterate(islandRow - 2, n -> n - 2).limit(limit).filter(n -> n >= rangeRestrictionN)
					.toArray();
		} else if (direction == CardinalPoints.SOUTH) {
			int rangeRestrictionS = closestN.isPresent() ? closestN.get().getRow() : nrOfRows;
			range = IntStream.iterate(islandRow + 2, n -> n + 2).limit(limit).filter(n -> n < rangeRestrictionS)
					.toArray();
		} else {
			throw new IllegalArgumentException();

		}

		List<Island> islandsInDirection = new ArrayList<Island>();

		for (int i : range) {
			Island potentialIsland;
			if (direction == CardinalPoints.WEST || direction == CardinalPoints.EAST) {
				potentialIsland = new Island(islandRow, i, 8);
			} else {
				potentialIsland = new Island(i, islandColumn, 8);
			}
			boolean valid = true;
			for (Bridge bridge : currentBridges) {
				if (GameModelUtils.isIslandInBridgeInervall(potentialIsland, bridge)) {
					valid = false;
					break;
				}
			}
			if (valid && GameModelUtils.isBridgeBuildable(islandBridgeStart, potentialIsland, currentBridges)) {
				islandsInDirection.add(potentialIsland);
			}
		}
		return islandsInDirection;

	}


	@Override
	public boolean solveNextBridge() {
		for (Island island : this.islands) {
			if (this.solveNextBridge1(island)) {
				return true;
			}
		}
		return false;

	}

	private boolean solveNextBridge1(Island island) {
		int bridgeCapacity = island.getBridgeCapacity();
		List<Island> reachibleNeighbours = GameModelUtils.getReachibleNeighbours(island, this.islands, this.bridges);
		List<Island> allNeighbours = reachibleNeighbours;
		int numberOfPossibleNeighbours = allNeighbours.size();
		boolean bridgeAdded = false;
		if (allNeighbours.stream().filter(i -> i.getCurrentBridgeCapacity() != 0).collect(Collectors.toList())
				.size() == 1) {
			Island nIsland = allNeighbours.stream().filter(i -> i.getCurrentBridgeCapacity() != 0)
					.collect(Collectors.toList()).get(0);
			if (!(this.nrOfIslands > 2 && island.getBridgeCapacity() == 1 & nIsland.getBridgeCapacity() == 1)) {
				bridgeAdded = this.buildBridgeBetweenIslands(island, nIsland, true, true);

			}

		} else if (2 * numberOfPossibleNeighbours - 1 == bridgeCapacity) {
			for (Island neighbourIsland : allNeighbours) {
				if (!(this.nrOfIslands > 2
						&& island.getBridgeCapacity() == 1 & neighbourIsland.getBridgeCapacity() == 1)) {
					bridgeAdded = this.buildBridgeBetweenIslands(island, neighbourIsland, true, false);
					if (bridgeAdded) {
						break;
					}
				}

			}

		} else if (2 * numberOfPossibleNeighbours == bridgeCapacity) {
			for (Island neighbourIsland : allNeighbours) {
				if (!(this.nrOfIslands > 2
						&& island.getBridgeCapacity() == 2 & neighbourIsland.getBridgeCapacity() == 2)) {
					bridgeAdded = this.buildBridgeBetweenIslands(island, neighbourIsland, true, true);
					bridgeAdded = this.buildBridgeBetweenIslands(island, neighbourIsland, true, true);
					if (bridgeAdded) {
						break;
					}
				}

			}
		}
		return bridgeAdded;

	}

}
