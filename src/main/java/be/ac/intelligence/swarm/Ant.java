package be.ac.intelligence.swarm;

import java.util.ArrayList;

import org.apache.commons.lang3.SerializationUtils;

public class Ant {
	private PermutationFlowShopProblem problem;
	private Double[][] pheromone;
	private Double[][] initialPheromone;
	private Double[][] heuristicInformation;
	private Double beta;
	private Double pheromoneDecayCoeficient;

	public Ant(final PermutationFlowShopProblem problem, final Double[][] pheromone,
			final Double[][] heuristicInformation, final Double beta, final Double pheromoneDecayCoeficient) {
		this.problem = problem;
		this.pheromone = PfspUtils.deepCopy(pheromone);
		this.initialPheromone = PfspUtils.deepCopy(pheromone);
		this.heuristicInformation = PfspUtils.deepCopy(heuristicInformation);
		this.beta = beta;
		this.pheromoneDecayCoeficient = pheromoneDecayCoeficient;
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
	}

	/**
	 * Random proportional rule probability computation
	 * 
	 * @param set
	 * @param sets
	 * @return
	 */
	private Double randomProportionalProbability(int i, int j, ArrayList<Integer> candidateList) {
		Double numerator = pheromone[i][j] * Math.pow(heuristicInformation[i][j], beta);
		Double denominator = 0.0;
		for (Integer l : candidateList) {
			denominator += pheromone[i][l] * Math.pow(heuristicInformation[i][l], beta);
		}
		return numerator / denominator;
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

}
