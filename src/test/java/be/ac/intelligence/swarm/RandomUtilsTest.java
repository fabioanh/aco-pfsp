package be.ac.intelligence.swarm;

import static org.junit.Assert.assertTrue;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
	
	@Test
	public void getRandomFromList(){
		List<Map.Entry<Integer, Double>> list = new ArrayList<>();
		list.add(new AbstractMap.SimpleEntry<Integer, Double>(0, 0.25));
		list.add(new AbstractMap.SimpleEntry<Integer, Double>(1, 0.25));
		list.add(new AbstractMap.SimpleEntry<Integer, Double>(2, 0.15));
		list.add(new AbstractMap.SimpleEntry<Integer, Double>(3, 0.35));
		LOGGER.debug("Random Numbers From List:");
		LOGGER.debug(RandomUtils.getInstance(0).getRandomFromList(list));
		LOGGER.debug(RandomUtils.getInstance(0).getRandomFromList(list));
		LOGGER.debug(RandomUtils.getInstance(0).getRandomFromList(list));
	}
}
