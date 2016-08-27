package be.ac.intelligence.swarm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;

public class Ant {
	private PermutationFlowShopProblem problem;
	private Double[][] pheromone;
	private Double[][] initialPheromone;
	private Double[][] heuristicInformation;
	private Double beta;
	private Double pheromoneDecayCoeficient;
	private Double q0;

	public Ant(final PermutationFlowShopProblem problem, final Double[][] pheromone,
			final Double[][] heuristicInformation, final Double beta, final Double pheromoneDecayCoeficient,
			final double q0) {
		this.problem = problem;
		this.pheromone = PfspUtils.deepCopy(pheromone);
		this.initialPheromone = PfspUtils.deepCopy(pheromone);
		this.heuristicInformation = PfspUtils.deepCopy(heuristicInformation);
		this.beta = beta;
		this.pheromoneDecayCoeficient = pheromoneDecayCoeficient;
		this.q0 = q0;
	}

	/**
	 * Copy Constructor
	 * 
	 * @param ant
	 */
	public Ant(final Ant ant) {
		problem = SerializationUtils.clone(ant.getProblem());
		this.pheromone = ant.getPheromone();
		this.heuristicInformation = ant.getHeuristicInformation();
		this.beta = ant.getBeta();
		this.pheromone = ant.getPheromone();
		this.initialPheromone = ant.getPheromone();
		this.pheromoneDecayCoeficient = ant.getPheromoneDecayCoeficient();
		this.q0 = ant.getQ0();
	}

	/**
	 * Pseudo-random proportional rule probability computation
	 * 
	 * @param set
	 * @param sets
	 * @return
	 */
	private Double pseudorandomProportionalProbability(int i, int j, ArrayList<Integer> candidateList) {
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

	private Integer getNextJob() {
		if (RandomUtils.getInstance(null).getRandomDouble() <= q0) {
			return getArgMaxValueIndex(problem.getCandidateList(),
					problem.getSolution().get(problem.getSolution().size() - 1));
		}
		return RandomUtils.getInstance(null).getRandomFromCollection(problem.getUnscheduledJobs());
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
	private void updatePheromoneSingle(int i, int j) {
		this.pheromone[i][j] = (1.0 - pheromoneDecayCoeficient) * pheromone[i][j]
				+ (pheromoneDecayCoeficient * initialPheromone[i][j]);
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

	public void setQ0(Double q0) {
		this.q0 = q0;
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
