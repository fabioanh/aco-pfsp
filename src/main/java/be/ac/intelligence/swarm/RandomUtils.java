package be.ac.intelligence.swarm;

import java.util.Collection;
import java.util.Random;

public class RandomUtils {

	private static RandomUtils instance = null;
	private final Random random;

	private RandomUtils(Integer seed) {
		random = new Random(seed);
	}

	public static RandomUtils getInstance(Integer seed) {
		if (instance == null && seed != null) {
			instance = new RandomUtils(seed);
		}
		return instance;
	}

	/**
	 * Returns an unbounded random integer
	 * 
	 * @return
	 */
	public Integer getRandomInt() {
		return random.nextInt();
	}

	/**
	 * Returns a bounded random integer
	 * 
	 * @param upperBound
	 * @return
	 */
	public Integer getRandomInt(Integer upperBound) {
		Integer aux = random.nextInt(upperBound);
		return aux;
	}

	/**
	 * Returns a random double between 0.0 (inclusive) and 1.0 (exclusive)
	 * 
	 * @return
	 */
	public Double getRandomDouble() {
		return random.nextDouble();
	}

	public Random getRandom() {
		return random;
	}

	public <T> T getRandomFromCollection(Collection<T> c) {
		int rndPos = random.nextInt(c.size());
		int i = 0;
		for (T obj : c) {
			if (i == rndPos) {
				return obj;
			}
			i++;
		}
		return null;
	}

}
