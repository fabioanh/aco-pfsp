package be.ac.intelligence.swarm;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;

public class Ant {
	private PermutationFlowShopProblem problem;
	private Double[][] pheromone;
	private Double[][] initialPheromone;
	private Double[][] heuristicInformation;
	private Double[][] insertionProbability;
	private Double beta;
	private Double alpha;
	private Double pheromoneDecayCoeficient;
	private Double q0;
	private Double initialPheromoneGlobal;

	public Ant(final PermutationFlowShopProblem problem, final Double[][] pheromone,
			final Double[][] heuristicInformation, final Double beta, final Double pheromoneDecayCoeficient,
			final double q0, final Double alpha, final Double initialPheromoneGlobal) {
		this.problem = SerializationUtils.clone(problem);
		// this.problem = new PermutationFlowShopProblem(problem);
		this.pheromone = PfspUtils.deepCopy(pheromone);
		this.initialPheromone = PfspUtils.deepCopy(pheromone);
		this.heuristicInformation = PfspUtils.deepCopy(heuristicInformation);
		this.beta = beta;
		this.alpha = alpha;
		this.pheromoneDecayCoeficient = pheromoneDecayCoeficient;
		this.q0 = q0;
		this.initialPheromoneGlobal = initialPheromoneGlobal;
		insertionProbability = new Double[problem.getNumJobs()][problem.getNumJobs()];
	}

	public Ant() {
	}

	/**
	 * Copy Constructor
	 * 
	 * @param ant
	 */
	public Ant(final Ant ant) {
		problem = SerializationUtils.clone(ant.getProblem());
		this.pheromone = PfspUtils.deepCopy(ant.getPheromone());
		this.heuristicInformation = PfspUtils.deepCopy(ant.getHeuristicInformation());
		this.beta = ant.getBeta();
		this.alpha = ant.getAlpha();
		this.pheromone = PfspUtils.deepCopy(ant.getPheromone());
		this.initialPheromone = PfspUtils.deepCopy(ant.getPheromone());
		this.pheromoneDecayCoeficient = ant.getPheromoneDecayCoeficient();
		this.q0 = ant.getQ0();
		this.initialPheromoneGlobal = ant.getInitialPheromoneGlobal();
		this.insertionProbability = PfspUtils.deepCopy(ant.getInsertionProbability());
	}

	/**
	 * Pseudo-random proportional rule probability computation
	 * 
	 * @param set
	 * @param sets
	 * @return
	 */
	private Double pseudorandomProportionalProbability(Integer i, Integer j, List<Integer> candidateList) {
		Double numerator = pheromone[i][j] * Math.pow(heuristicInformation[i][j], beta);
		Double denominator = 0.0;
		for (Integer l : candidateList) {
			denominator += pheromone[i][l] * Math.pow(heuristicInformation[i][l], beta);
		}
		return numerator / denominator;
	}

	private List<ArgMaxValue> getArgMaxList(List<Integer> candidateList, Integer lastScheduledJob) {
		List<ArgMaxValue> argMaxList = new ArrayList<>();
		if (lastScheduledJob != null) {
			for (Integer l : candidateList) {
				argMaxList.add(new ArgMaxValue(
						pheromone[lastScheduledJob][l] * Math.pow(heuristicInformation[lastScheduledJob][l], beta),
						lastScheduledJob, l));
			}
		}
		return argMaxList;
	}

	/**
	 * Returns the index for the max value of the Ant Colony System computation
	 * 
	 * @param candidateList
	 * @param lastScheduledJob
	 * @return
	 */
	private Integer getArgMaxValueIndex(List<Integer> candidateList, Integer lastScheduledJob) {
		final Comparator<ArgMaxValue> comp = (p1, p2) -> Double.compare(p1.getValue(), p2.getValue());
		ArgMaxValue max = getArgMaxList(candidateList, lastScheduledJob).stream().max(comp).get();
		return max.getJ();
	}

	/**
	 * Return the index of the next job to be chosen based on the candidate list
	 * using the random proportional rule
	 * 
	 * @return
	 */
	private Integer getNextJob() {
		if (RandomUtils.getInstance(null).getRandomDouble() <= q0) {
			return getArgMaxValueIndex(problem.getCandidateListValues(),
					problem.getSolution().get(problem.getSolution().size() - 1));
		}
		return getPseudoRandomJob(problem.getCandidateListValues(),
				problem.getSolution().get(problem.getSolution().size() - 1));
	}

	private Integer getPseudoRandomJob(List<Integer> candidateList, Integer lastInsertedJob) {
		List<Map.Entry<Integer, Double>> candidateProbabilities = new ArrayList<>();
		for (Integer c : candidateList) {
			candidateProbabilities.add(new AbstractMap.SimpleEntry<Integer, Double>(c,
					pseudorandomProportionalProbability(lastInsertedJob, c, candidateList)));
		}
		return RandomUtils.getInstance(null).getRandomFromList(candidateProbabilities);
//		return candidateProbabilities.get(0).getKey();
	}

	/**
	 * Add next job into the solution modifying the reference lists and
	 * re-computing the makespan value for a job chosen using the random
	 * proportional rule
	 */
	public void addNextJob() {
		addNextJob(getNextJob());
	}

	/**
	 * Add next job into the solution modifying the reference lists and
	 * re-computing the makespan value
	 */
	public void addNextJob(Integer nextJob) {
		problem.scheduleJob(nextJob);
		problem.computeMakespan(problem.getSolution(), problem.getSolution().size() - 1);
	}

	/**
	 * Adds a list of jobs into the solution
	 * 
	 * @param nextJobs
	 */
	public void addNextJobs(List<Integer> nextJobs) {
		for (Integer nj : nextJobs) {
			problem.scheduleJob(nj);
		}
		problem.computeMakespan(problem.getSolution(), problem.getSolution().size() - nextJobs.size());
	}

	/**
	 * Method to locally update the pheromone values
	 */
	private void updatePheromone() {
		int solSize = problem.getSolution().size();
		if (solSize > 1) {
			this.updatePheromoneSingle(problem.getSolution().get(solSize - 2), problem.getSolution().get(solSize - 1));
		}
	}

	/**
	 * Method to update a single value of the pheromone based on its indices for
	 * the pheromone matrix
	 */
	public void updatePheromoneSingle(int i, int j) {
		this.pheromone[i][j] = (1.0 - pheromoneDecayCoeficient) * pheromone[i][j]
				+ (pheromoneDecayCoeficient * initialPheromone[i][j]);
	}

	/**
	 * Method to randomly select two jobs and check which permutation a-b or b-a
	 * has a better makespan adding it as initial solution.
	 */
	public void preProcess() {
		Integer a, b;
		a = RandomUtils.getInstance(null).getRandomFromCollection(problem.getUnscheduledJobs());
		do {
			b = RandomUtils.getInstance(null).getRandomFromCollection(problem.getUnscheduledJobs());
		} while (a == b);
		if (problem.computeMakespan(a, b) < problem.computeMakespan(b, a)) {
			addNextJobs(Arrays.asList(a, b));
		} else {
			addNextJobs(Arrays.asList(b, a));
		}
	}

	/**
	 * Solution following the ACS approach
	 */
	public void solveACS() {
		while (!problem.getUnscheduledJobs().isEmpty()) {
			addNextJob();
			
			updatePheromoneSingle(problem.getSolution().get(problem.getSolution().size() - 2),
					problem.getSolution().get(problem.getSolution().size() - 1));
		}
		problem.computeMakespan(problem.getSolution(), 0);
	}

	/**
	 * Proxy method to return the solution of the problem
	 * 
	 * @return
	 */
	public List<Integer> getSolution() {
		return this.problem.getSolution();
	}

	/**
	 * Proxy method to return the makespan value of the problem
	 * 
	 * @return
	 */
	public Integer getMakespan() {
		return this.problem.getMakespan();
	}

	public PermutationFlowShopProblem getProblem() {
		return problem;
	}

	public void setProblem(PermutationFlowShopProblem problem) {
		this.problem = problem;
	}

	public Double[][] getPheromone() {
		return pheromone;
	}

	public void setPheromone(Double[][] pheromone) {
		this.pheromone = pheromone;
	}

	public Double[][] getHeuristicInformation() {
		return heuristicInformation;
	}

	public void setHeuristicInformation(Double[][] heuristicInformation) {
		this.heuristicInformation = heuristicInformation;
	}

	public Double getBeta() {
		return beta;
	}
	
	public Double getAlpha() {
		return alpha;
	}

	public void setBeta(Double beta) {
		this.beta = beta;
	}

	public Double getPheromoneDecayCoeficient() {
		return pheromoneDecayCoeficient;
	}

	public void setPheromoneDecayCoeficient(Double pheromoneDecayCoeficient) {
		this.pheromoneDecayCoeficient = pheromoneDecayCoeficient;
	}

	public Double getQ0() {
		return q0;
	}
	
	public Double getInitialPheromoneGlobal() {
		return initialPheromoneGlobal;
	}

	public void setQ0(Double q0) {
		this.q0 = q0;
	}

	public Double[][] getInsertionProbability() {
		return insertionProbability;
	}

	private static class ArgMaxValue {
		private final Double value;
		private final Integer i;
		private final Integer j;

		public ArgMaxValue(Double value, Integer i, Integer j) {
			this.value = value;
			this.i = i;
			this.j = j;
		}

		public Double getValue() {
			return value;
		}

		public Integer getI() {
			return i;
		}

		public Integer getJ() {
			return j;
		}

	}

}
