package be.ac.intelligence.swarm;

import java.util.ArrayList;

import org.apache.commons.lang3.SerializationUtils;

public class Ant {
	private PermutationFlowShopProblem problem;
	private Integer[][] pheromone;
	private Integer[][] heuristicInformation;
	private Double beta;

	/**
	 * Copy Constructor
	 * 
	 * @param ant
	 */
	public Ant(Ant ant) {
		problem = SerializationUtils.clone(ant.getProblem());
		this.pheromone = ant.pheromone;
		this.heuristicInformation = ant.getHeuristicInformation();
		this.beta = ant.getBeta();
	}

	/**
	 * Random proportional rule computation
	 * 
	 * @param set
	 * @param sets
	 * @return
	 */
	private Double randomProportionalProbability(Integer set, ArrayList<Integer> sets) {
		Double numerator = pheromone.get(set) * Math.pow(heuristicInformation.get(set), beta);
		Double denominator = 0.0;
		for (Integer s : sets) {
			denominator += pheromone.get(s) * Math.pow(heuristicInformation.get(s), beta);
		}
		return numerator / denominator;
	}

	public PermutationFlowShopProblem getProblem() {
		return problem;
	}

	public void setProblem(PermutationFlowShopProblem problem) {
		this.problem = problem;
	}

	public Integer[][] getPheromone() {
		return pheromone;
	}

	public void setPheromone(Integer[][] pheromone) {
		this.pheromone = pheromone;
	}

	public Integer[][] getHeuristicInformation() {
		return heuristicInformation;
	}

	public void setHeuristicInformation(Integer[][] heuristicInformation) {
		this.heuristicInformation = heuristicInformation;
	}

	public Double getBeta() {
		return beta;
	}

	public void setBeta(Double beta) {
		this.beta = beta;
	}
}
