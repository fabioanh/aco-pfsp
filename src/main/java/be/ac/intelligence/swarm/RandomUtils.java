package be.ac.intelligence.swarm;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

	/**
	 * Generates a randomized list of integers in the range specified by the
	 * parameters start (inclusive) and end (exclusive)
	 * 
	 * @param start
	 *            Start value for the range. Included in the values for the list
	 * @param end
	 *            Upper limit for the sequence of number. It's not included in
	 *            the result list
	 * @param exclude
	 *            List of numbers to exclude from the generated list of numbers
	 * @return
	 */
	public Set<Integer> getRandomRange(int start, int end, Integer... exclude) {
		List<Integer> resultList = IntStream.range(start, end).boxed().collect(Collectors.toList());
		List<Integer> exclusionList = Arrays.asList(exclude);
		if (!exclusionList.isEmpty()) {
			resultList = resultList.stream().filter(p -> !exclusionList.contains(p)).collect(Collectors.toList());
		}
		Collections.shuffle(resultList);
		return new HashSet<Integer>(resultList);

	}

	/**
	 * Returns a random element from a list given the probability items in an
	 * {@link Map.Entry<Integer, Double>} format, where the key of the entry is
	 * the element and the value is its probability.
	 * 
	 * Asumes that the list of elements total 1.0 for the sum of their
	 * probabilities.
	 * 
	 * @param list
	 * @return
	 */
	public Integer getRandomForList(List<Map.Entry<Integer, Double>> list) {
		Double prob = getRandomDouble();
		Double sum = 0.0;
		for (Map.Entry<Integer, Double> elem : list) {
			sum += elem.getValue();
			if (prob <= sum) {
				return elem.getKey();
			}
		}
		throw new IllegalStateException("The probabilities of the given elements don't sum 1.0");
	}

}
