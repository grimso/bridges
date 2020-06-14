package model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestIsland {
	
	@Test
	public void testToFileFormat() {
		String expectedFileString="( 5, 10 | 4 )";
		Island island = new Island(10, 5, 4);
		String actualString = island.toFileFormat();
		assertTrue(String.format("Expected: %s, But acutal is: %s",expectedFileString,actualString),actualString.equals(expectedFileString));
	}

}
