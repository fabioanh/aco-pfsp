package be.ac.intelligence.swarm;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
//		assertTrue(true);
		System.out.println("97 81   3 30  7 ".trim().replaceAll("\\s+", " "));
		String[] strArray = "97 81   3 30  7 ".trim().replaceAll("\\s+", " ").split(" ");
		assertEquals(5, strArray.length);
	}
}
