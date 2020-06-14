package model;

import org.junit.Test;

public class TestGameModel {
	@Test
	public void testIsInBridgeIntervall() {
		Island island1 = new Island(1,1,8);
		Island island2 = new Island(4,1,8);
		
		Bridge bridge = new Bridge(island1,island2,false,true);
		
		for(int i=0;i<6;i++) {
			//System.out.println(GameModel.isIslandInBridgeInervall(new Island(i,1,8), bridge));
		}
	}
	@Test
	public void testIsInBridgeIntervall1() {
		Island island1 = new Island(1,1,8);
		Island island2 = new Island(1,4,8);
		
		Bridge bridge = new Bridge(island1,island2,false,true);
		
		for(int i=0;i<6;i++) {
			//System.out.println(GameModel.isIslandInBridgeInervall(new Island(2,i,8), bridge));
		}
	}

}
