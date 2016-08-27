package be.ac.intelligence.swarm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AntSolver {

	private PermutationFlowShopProblem problem;

	/**
	 * Pheromone and heuristic information matrices
	 */
	private Double[][] pheromone;
	private Double[][] heuristicInformation;

	private ArrayList<Ant> ants;
	private Ant bestAnt;

	/**
	 * Pheromone evaporation rate
	 */
	private Double rho;

	/**
	 * Number of ants to use in the solution
	 */
	private Integer numAnts;

	/**
	 * Number of iterations
	 */
	private Integer numIterations;

	private AntSolver(String instance, Double rho, Double beta, Integer numAnts, Integer numIterations) {
		problem = new PermutationFlowShopProblem(instance);
		initHeuristicInformation();
		initPheromone();
		this.pheromone = new Double[problem.getNumJobs()][problem.getNumJobs()];
		this.numAnts = numAnts;
		this.numIterations = numIterations;
		this.rho = rho;
	}

	public void execute() {

	}

	/**
	 * Updates pheromone in a global way for all node values.
	 * 
	 * @param minimumMakespan
	 */
	public void updatePhereomone(Integer minimumMakespan) {
		// TODO: Optionally add evaporate pheromone using concepts of min-max
		for (int i = 0; i < this.problem.getNumJobs(); i++) {
			for (int j = 0; j < this.problem.getNumJobs(); j++) {
				updatePheromone(i, j, minimumMakespan);
			}
		}
	}

	/**
	 * Updates the pheromone nodes contained in the solution sequence as
	 * suggested by the literature for ACS
	 * 
	 * @param solution
	 * @param minimumMakespan
	 */
	public void updatePheromone(ArrayList<Integer> solution, Integer minimumMakespan) {
		for (int i = 0; i < solution.size() - 1; i++) {
			updatePheromone(solution.get(i), solution.get(i + 1), minimumMakespan);
		}
	}

	/**
	 * Extracted method to allow pheromone updates only to specific nodes
	 * depending on conditions given by the algorithm
	 * 
	 * @param i
	 * @param j
	 * @param minimumMakespan
	 */
	public void updatePheromone(Integer i, Integer j, Integer minimumMakespan) {
		this.pheromone[i][j] = (1 - rho) * this.pheromone[i][j] + (rho / minimumMakespan);
	}

	/**
	 * Computes the distance between each of the jobs using the makespan
	 * computation between two jobs (i, j) as distance value stored in a two
	 * dimensional array for easy access
	 */
	private void initHeuristicInformation() {
		this.heuristicInformation = new Double[problem.getNumJobs()][problem.getNumJobs()];
		for (int i = 0; i < problem.getNumJobs(); i++) {
			for (int j = 0; j < problem.getNumJobs(); j++) {
				this.heuristicInformation[i][j] = 1.0 / this.problem.computeMakespan(i, j);
			}
		}
	}

	/**
	 * Initializes the pheromone values using a random tour as reference
	 */
	private void initPheromone() {
		ArrayList<Integer> seq = new ArrayList<>(problem.getUnscheduledJobs());
		Collections.shuffle(seq);
		Integer refMakespan = problem.computeMakespan(seq, 0);
		Double initVal = this.numAnts.doubleValue() / refMakespan;
		Arrays.fill(pheromone, initVal);
	}

	private void performLocalSearch() {

	}

	public static class AntSolverBuilder {
		private String instance;
		private Integer numAnts;
		private Integer numIterations;
		private Double rho;
		private Double beta;

		public AntSolverBuilder instance(String instance) {
			this.instance = instance;
			return this;
		}

		public AntSolverBuilder numAnts(Integer val) {
			this.numAnts = val;
			return this;
		}

		public AntSolverBuilder numIterations(Integer val) {
			this.numIterations = val;
			return this;
		}

		public AntSolverBuilder rho(Double val) {
			this.rho = val;
			return this;
		}

		public AntSolverBuilder beta(Double val) {
			this.beta = val;
			return this;
		}

		public AntSolver build() {
			return new AntSolver(instance, rho, beta, numAnts, numIterations);
		}
	}
}
