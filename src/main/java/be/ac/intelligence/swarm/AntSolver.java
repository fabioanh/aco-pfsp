package be.ac.intelligence.swarm;

import java.util.ArrayList;

public class AntSolver {

	private PermutationFlowShopProblem problem;
	private Double[][] pheromone;
	private Double[][] heuristicInformation;
	private ArrayList<Ant> ants;
	private Ant bestAnt;
	// Pheromone evaporation rate
	private Double rho;

	private AntSolver(String instance) {
		problem = new PermutationFlowShopProblem(instance);
		initHeuristicInformation();
		this.pheromone = new Double[problem.getNumJobs()][problem.getNumJobs()];
	}

	public static class AntSolverBuilder {
		private String instance;

		public AntSolverBuilder instance(String instance) {
			this.instance = instance;
			return this;
		}

		public AntSolver build() {
			return new AntSolver(instance);
		}
	}

	public void execute() {
		// TODO Auto-generated method stub
	}

	/**
	 * Updates pheromone in a global way for all node values
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
	 * Updates the pheromone nodes contained in the solution sequence
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

}
