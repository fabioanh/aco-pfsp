package be.ac.intelligence.swarm;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;

public class RandomUtilsTest {

	private final static Logger LOGGER = Logger.getLogger(RandomUtilsTest.class);

	@Test
	public void getRandomRange() {
		int start = 0;
		int end = 10;
		Set<Integer> randomSetSequence = RandomUtils.getInstance(0).getRandomRange(start, end, 1, 4);
		LOGGER.debug(randomSetSequence);
		assertTrue("Exclusion of numbers is not being done properly", randomSetSequence.size() != start - end);
	}
}
