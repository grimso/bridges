package model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import model.Bridge;
import model.Island;

public class TestBridge {

	@Test
	public void testIsHorizontal() {
		// Horizontal when row position of two islands is same
		// horizontal islands
		Island island1 = new Island(5, 10, 0);
		Island island2 = new Island(5, 5, 0);
		// hence bridge is horizontal
		Bridge horizonalBridge = new Bridge(island1, island2, false,true);
		assertTrue(horizonalBridge.isHorizontal());

		// vertical islands, columns are same
		island1 = new Island(10, 5, 0);
		island2 = new Island(5, 5, 0);
		// hence bridge is vertical
		Bridge verticalBridge = new Bridge(island1, island2, false,true);
		assertFalse(verticalBridge.isHorizontal());

	}
	
	@Test
	public void testBridgeCrossingBothHorizontalOrVertical() {
		//Bridges can not cross each other if they are both vertical or both horizontal
		//1. horizontal bridges
		Bridge bridge1 = new Bridge(new Island(2, 2, 0), new Island(2, 4, 0), false,true);
		Bridge bridge2 = new Bridge(new Island(1, 2, 0), new Island(1, 2, 0), false,true);
		assertFalse(Bridge.areCrossingBridges(bridge1, bridge2));
		//2. vertical bridges
		bridge1 = new Bridge(new Island(2, 2, 0), new Island(4, 2, 0), false,true);
		bridge2 = new Bridge(new Island(2, 1, 0), new Island(4, 3, 0), false,true);
		assertFalse(Bridge.areCrossingBridges(bridge1, bridge2));

	}
	

	@Test
	public void testBridgeCrossingTrue1() {
		//coordinates are defined, so that bridge1 is horizontal, bridge2 is horizontal and are crossing each other
		Bridge bridge1 = new Bridge(new Island(2, 2, 0), new Island(2, 4, 0), false,true);
		Bridge bridge2 = new Bridge(new Island(1, 3, 0), new Island(3, 3, 0), false,true);

		assertTrue(Bridge.areCrossingBridges(bridge1, bridge2));

	}
	@Test
	public void testBridgeCrossingTrue2() {
		//coordinates are defined, so that bridge1 is horizontal, bridge2 is horizontal,
		//but they are not crossing each other 
		Bridge bridge1 = new Bridge(new Island(2, 2, 0), new Island(4, 2, 0), false,true);
		Bridge bridge2 = new Bridge(new Island(3, 1, 0), new Island(3, 3, 0), false,true);

		assertTrue(Bridge.areCrossingBridges(bridge1, bridge2));

	}

	@Test
	public void testBridgeCrossingFalse1() {
		//coordinates are defined, so that bridge1 is horizontal, bridge2 is horizontal and are crossing each other
		Bridge bridge1 = new Bridge( new Island(2, 2, 0), new Island(2, 4, 0), false,true);
		Bridge bridge2 = new Bridge( new Island(1, 5, 0), new Island(3, 5, 0), false,true);

		assertFalse(Bridge.areCrossingBridges(bridge1, bridge2));

	}
	@Test
	public void testContains() {
		//tess if a island or two islands are part of the bridge
		// currently implementation comapres based if object reference is the same... 
		
		Island island1 = new Island(0,1,0);
		Island island2 = new Island(1,0,0);
		Bridge bridge = new Bridge(island1,island2,false,true);
		
		assertTrue(bridge.contains(island1));
		assertTrue(bridge.contains(island2));
		assertTrue(bridge.contains(island1,island2));
		//as contains currently checks equality based on reference, island3 is not part of brdige
		Island island3=new Island(0,1,0);
		
		assertFalse(bridge.contains(island3));
		assertFalse(bridge.contains(island1,island3));
		
	}
	
	@Test
	public void testConstructor() {
		//this.i1 is island with smaller manhatten distance to (0,0), 
		//if manhatten disatnce is same first island in constructor is referenced in i1
		
		Island island1 = new Island(0,1,0);
		Island island2 = new Island(0,2,0);
		Bridge bridge = new Bridge(island1,island2,false,true);
		assertTrue(bridge.getIsland1()==island1);
		bridge = new Bridge(island2,island1,false,true);
		assertTrue(bridge.getIsland1()==island1);
		
		//manhatten distance is same,first argument at i1
		bridge=new Bridge(island1,new Island(0,1,0),false,true);
		assertTrue(bridge.getIsland1()==island1);
		
		
		
		
	}


}
