package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import common.CardinalPoints;

/**
 * Provides functions to get information on the game state based which are
 * subject to the game's constraint.This helps implementing the game logic in
 * {@link GameModel}
 * 
 * @author grimm
 *
 */
class GameModelUtils {
	/**
	 * Given all {@code islands} of a bridge game this method finds the neighbour
	 * (closest island) for each cardinal point of a specific {@code island}. For
	 * each direction there is either one or no neighbour. Returns a
	 * {@link Map}<{@link CardinalPoints},{@link Optional}<{@link Island}>> where
	 * the {@code Optional} container is empty if no neighbour exists for a cardinal
	 * point.<br>
	 * <i> Note: This neighbour does not to have be reachable given other game
	 * constraints imposed by bridges.</i>
	 * 
	 * @param island  A specific {@link Island} of a bridge game
	 * @param islands A {@link List}<{@link Island}> comprising all island of a
	 *                bridges game
	 * @return {@link Map}<{@link CardinalPoints},{@link Optional}<{@link Island}>>
	 */
	static Map<CardinalPoints, Optional<Island>> getNeighbours(Island island, List<Island> islands) {
		Optional<Island> east = islands.stream()
				.filter(i -> i.getColumn() > island.getColumn() && i.getRow() == island.getRow())
				.sorted(Comparator.comparing(Island::getColumn)).findFirst();
		Optional<Island> west = islands.stream()
				.filter(i -> i.getColumn() < island.getColumn() && i.getRow() == island.getRow())
				.sorted(Comparator.comparing(Island::getColumn).reversed()).findFirst();
		Optional<Island> south = islands.stream()
				.filter(i -> i.getRow() > island.getRow() && i.getColumn() == island.getColumn())
				.sorted(Comparator.comparing(Island::getRow)).findFirst();
		Optional<Island> north = islands.stream()
				.filter(i -> i.getRow() < island.getRow() && i.getColumn() == island.getColumn())
				.sorted(Comparator.comparing(Island::getRow).reversed()).findFirst();
		Map<CardinalPoints, Optional<Island>> neighbourMap = new HashMap<CardinalPoints, Optional<Island>>();
		neighbourMap.put(CardinalPoints.EAST, east);
		neighbourMap.put(CardinalPoints.WEST, west);
		neighbourMap.put(CardinalPoints.SOUTH, south);
		neighbourMap.put(CardinalPoints.NORTH, north);
		return neighbourMap;

	}

	/**
	 * Given all {@code islands} and {@code bridges} of a bridges game this method
	 * finds the neighbour(s) (closest island) for each cardinal point of a specific
	 * {@code island}. Additionally these neighbouring islands needs to be reachable
	 * given all constraints imposed by {@code} bridges. Returns a
	 * {@link List}<{@link Island}>.<br>
	 * <i>Note:
	 * <li>The {@code List} is empty if no neighbouring islands can be reached.
	 * <li>A {@code island} can have 4 neighbours in maximum
	 * 
	 * 
	 * @param island  A specific {@link Island} of a bridge game
	 * @param islands A {@link List}<{@link Island}> comprising all island of a
	 *                bridges game
	 * @param bridges A {@link List}<{@link Bridge}> comprising all bridges of a
	 *                bridges game
	 * @return {@link List}<{@link Island}> - the neighbour islands given the game
	 *         situation
	 */
	static List<Island> getReachibleNeighbours(Island island, List<Island> islands, List<Bridge> bridges) {
		List<Island> neighbours = getNeighbours(island, islands).values().stream().filter(Optional::isPresent)
				.map(Optional::get).collect(Collectors.toList());
		List<Island> reachibleNehibours = new ArrayList<Island>();
		for (Island n : neighbours) {
			boolean crossing = false;
			for (Bridge bridge : bridges) {
				if (Bridge.areCrossingBridges(bridge, new Bridge(island, n, false, false))) {
					crossing = true;
					break;
				}
			}
			if (!crossing) {
				reachibleNehibours.add(n);
			}
		}
		return reachibleNehibours;
	}

	/**
	 * Given all {@code bridges} of a bridges game this method finds the cardinal
	 * points where a bridge exists for a specific {@code island}. Returns a
	 * {@link Set}<{@link CardinalPoints}>.<br>
	 * <i>Note:
	 * <li>The {@code Set} is empty if no bridges are build in any director for this
	 * island.
	 * <li>A {@code island} can have a bridge in every cardinal direction (max 4):
	 * 
	 * 
	 * @param island  A specific {@link Island} of a bridge game
	 * @param bridges A {@link List}<{@link Bridge}> comprising all bridges of a
	 *                bridges game
	 * @return {@link Set}<{@link CardinalPoints}>
	 */
	static Set<CardinalPoints> getBridgeDirectionsOfIsland(Island island, List<Bridge> bridges) {
		List<Bridge> connectedbridges = getBridgesOfIsland(island, bridges);
		Set<CardinalPoints> directions = new HashSet<CardinalPoints>();
		for (Bridge b : connectedbridges) {
			Island nIsland = b.getIsland1() == island ? b.getIsland2() : b.getIsland1();
			if (b.isHorizontal()) {

				if (nIsland.getColumn() > island.getColumn()) {
					directions.add(CardinalPoints.EAST);
				} else {
					directions.add(CardinalPoints.WEST);
				}
			} else {
				if (nIsland.getRow() > island.getRow()) {
					directions.add(CardinalPoints.SOUTH);
				} else {
					directions.add(CardinalPoints.NORTH);
				}
			}
		}
		return directions;

	}

	/**
	 * Given all {@code bridges} of a bridges game this method finds the bridges of
	 * a specific {@code island}. Returns a {@link List}<{@link Bridge}>.<br>
	 * <i>Note:
	 * <li>The {@code Set} is empty if no bridges are build in any director for this
	 * island.
	 * <li>A {@code island} can have a bridge in every cardinal direction (max 4):
	 * 
	 * 
	 * @param island  A specific {@link Island} of a bridge game
	 * @param bridges A {@link List}<{@link Bridge}> comprising all bridges of a
	 *                bridges game
	 * @return {@link List}<{@link Bridge}>
	 */
	private static List<Bridge> getBridgesOfIsland(Island island, List<Bridge> bridges) {
		List<Bridge> islandBridges = new ArrayList<Bridge>();
		for (Bridge bridge : bridges) {
			if (bridge.contains(island)) {
				islandBridges.add(bridge);
			}
		}
		return islandBridges;

	}

	/**
	 * Given all {@code bridges} of a bridges game this method finds the islands
	 * which are connected to a specific {@code island} by a bridge. Returns a
	 * {@link List}<{@link Island}>.<br>
	 * <i>Note:
	 * <li>The {@code List} is empty if the {@code island} is not connected to any
	 * neighbouring island.
	 * <li>A {@code island} can be connected to 4 island at maximum
	 * 
	 * 
	 * @param island  A specific {@link Island} of a bridge game
	 * @param bridges A {@link List}<{@link Bridge}> comprising all bridges of a
	 *                bridges game
	 * @return {@link List}<{@link Bridge}>
	 */
	private static List<Island> getConnectedNeighboursOfIsland(Island island, List<Bridge> bridges) {
		List<Island> connectedNeighbours = new ArrayList<Island>();
		for (Bridge bridge : bridges) {
			if (bridge.contains(island)) {
				Island connectedNeighbour = bridge.getIsland1() == island ? bridge.getIsland2() : bridge.getIsland1();
				connectedNeighbours.add(connectedNeighbour);
			}
		}
		return connectedNeighbours;

	}

	/**
	 * Given all {@code bridges} of a bridges game this method searches if two
	 * islands {@code island1} and {@code island2} are connected by a
	 * {@link Bridge}.. Returns a {@link Optional}<{@link Bridge}>.<br>
	 * <i>Note:
	 * <li>{@link Optional}<{@link Bridge}> is empty if no bridge exists between
	 * {@code island1} and {@code island2}
	 * 
	 * 
	 * @param island1 A specific {@link Island} of a bridge game
	 * @param island2 Another specific {@link Island} of a bridge game
	 * @param bridges A {@link List}<{@link Bridge}> comprising all bridges of a
	 *                bridges game
	 * @return {@link Optional}<{@link Bridge}>
	 */

	static Optional<Bridge> getBridgeBetweenIslands(Island island1, Island island2, List<Bridge> bridges) {
		Bridge b = null;
		for (Bridge bridge : bridges) {
			if (bridge.contains(island1, island2)) {
				b = bridge;
				break;
			}
		}

		return Optional.ofNullable(b);

	}

	/**
	 * Given all {@code bridges} of a bridges game this method evaluates if a bridge
	 * between two islands {@code island1} and {@code island2} could be build, such
	 * that this new bridge would not cross any existing bridge. Returns{@code true}
	 * if a bridge is buildable between islands with respect to constraints imposed
	 * by existing bridges
	 * 
	 * 
	 * @param island1 A specific {@link Island} of a bridge game
	 * @param island2 Another specific {@link Island} of a bridge game
	 * @param bridges A {@link List}<{@link Bridge}> comprising all bridges of a
	 *                bridges game
	 * @return {@code true} if a bridge is buildable between islands with respect to
	 *         constraints imposed by existing bridges
	 */

	static boolean isBridgeBuildable(Island island1, Island island2, List<Bridge> bridges) {
		for (Bridge bridge : bridges) {
			if (Bridge.areCrossingBridges(bridge, new Bridge(island1, island2, false, false))) {
				return false;
			}
		}
		return true;

	}

	/**
	 * Remove a {@code bridge} from {@code bridges} of a bridges. This takes into
	 * account that a @{link Bridge} relation can either represent a single or
	 * double bridge. If {@code bridge} is a double bridge it is transformed to a
	 * single bridge. If {@code bridge} is a single bridge it is removed from {@
	 * code bridges}. In both cases the state the involved islands is updated
	 * accordingly.
	 * 
	 * 
	 * @param bridge  {@link bridge} which should be removed
	 * @param bridges A {@link List}<{@link Bridge}> comprising all bridges of a
	 *                bridges game
	 */

	static void removeBridge(Bridge bridge, List<Bridge> bridges) {
		if (bridge.isDoubleBridge()) {
			bridge.setModifiedLatest(true);
			bridge.setDoubleBridge(false);
			bridge.getIsland1().setCurrentNrOfBridges(bridge.getIsland1().getCurrentNrOfBridges() - 1);
			bridge.getIsland2().setCurrentNrOfBridges(bridge.getIsland2().getCurrentNrOfBridges() - 1);
		} else {
			bridge.getIsland1().setCurrentNrOfBridges(bridge.getIsland1().getCurrentNrOfBridges() - 1);
			bridge.getIsland2().setCurrentNrOfBridges(bridge.getIsland2().getCurrentNrOfBridges() - 1);
			bridges.remove(bridge);

		}
	}

	/**
	 * Given all {@code islands} this method finds the {@link Island} at position
	 * {@code row},{@code column}.Returns a {@link Optional}<{@link Island}>, which
	 * is empty of no island is at this position.
	 * 
	 * 
	 * @param row     position of a potential island
	 * @param column  position of a potential island
	 * @param islands A {@link List}<{@link Island}> comprising all island of a
	 *                bridges game
	 * 
	 * @return {@link Optional}<{@link Island}> - island at specified coordinates
	 */
	static Optional<Island> getIsland(int row, int column, List<Island> islands) {
		return islands.stream().filter(i -> i.getRow() == row && i.getColumn() == column).findFirst();

	}

	/**
	 * Evaluates if a {@code island} is positioned on a {@code bridge} Returns
	 * {@code true} if {@code island} is within a interval spanned by a
	 * {@code bridge}
	 * 
	 * 
	 * @param island A specific {@link Island} of a bridge game
	 * @param bridge A specific {@link bridge} of a bridge game
	 * 
	 * @return Returns {@code true} if {@code island} is within a interval spanned
	 *         by a {@code bridge}
	 */
	static boolean isIslandInBridgeInervall(Island island, Bridge bridge) {
		if (bridge.isHorizontal()) {
			if (island.getRow() == bridge.getIsland1().getRow()) {
				if ((island.getColumn() >= bridge.getIsland1().getColumn()
						&& island.getColumn() <= bridge.getIsland2().getColumn())
						|| (island.getColumn() <= bridge.getIsland1().getColumn()
								&& island.getColumn() >= bridge.getIsland2().getColumn()))
					return true;
			}
		} else {
			if (island.getColumn() == bridge.getIsland1().getColumn()) {
				if ((island.getRow() >= bridge.getIsland1().getRow() && island.getRow() <= bridge.getIsland2().getRow())
						|| (island.getRow() <= bridge.getIsland1().getRow()
								&& island.getRow() >= bridge.getIsland2().getRow()))
					return true;
			}

		}
		return false;
	}

	/**
	 * Given all {@code bridges} of a bridges game this method finds the number of
	 * unique islands which are connected to a specific {@code island}. The game can
	 * be seen as a graph (islands are the vertices, bridges are the edges- here a
	 * unordered pair comprising of two islands). Hence, we try to traverse all
	 * islands, which is formulates as depth first search problem. Returns the
	 * number of islands which can be reached starting from {@code island}.
	 * 
	 * 
	 * @param island  A specific {@link Island} of a bridge game
	 * @param bridges A {@link List}<{@link Bridge}> comprising all bridges of a
	 *                bridges game
	 * @return number of reachable islands
	 */

	static int depthFirstSearch(Island island, List<Bridge> bridges) {
		Stack<Island> frontier = new Stack<Island>();
		frontier.push(island);
		Set<Island> explored = new HashSet<Island>();
		while (!frontier.isEmpty()) {

			Island state = frontier.pop();
			explored.add(state);

			for (Island neighbour : GameModelUtils.getConnectedNeighboursOfIsland(state, bridges)) {
				if (!explored.contains(neighbour) && !frontier.contains(neighbour)) {
					frontier.push(neighbour);
				}
			}
		}
		return explored.size();
	}

}
