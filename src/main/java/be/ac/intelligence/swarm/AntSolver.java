package be.ac.intelligence.swarm;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

public class AntSolver {

	private final static Logger LOGGER = Logger.getLogger(AntSolver.class);

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

	private Double beta;

	private Double pheromoneDecayCoeficient;

	private Double q0;

	private Double alpha;

	private double initPheromoneValGlobal;

	private AntSolver(String instance, Double rho, Double beta, Integer numAnts, Integer numIterations,
			Double pheromoneDecayCoeficient, Double q0, Integer seed, Double alpha) {
		// Initialize random utilities with seed parameter
		RandomUtils.getInstance(seed);
		problem = new PermutationFlowShopProblem(instance);
		this.pheromone = new Double[problem.getNumJobs()][problem.getNumJobs()];
		this.numAnts = numAnts;
		this.numIterations = numIterations;
		this.rho = rho;
		this.beta = beta;
		this.alpha = alpha;
		this.pheromoneDecayCoeficient = pheromoneDecayCoeficient;
		this.q0 = q0;
		this.ants = new ArrayList<>();
		initHeuristicInformation();
		initPheromone();
	}

	public void execute() {
		Integer currentIteration = 0;
		while (currentIteration < numIterations) {
			// LOGGER.debug("iteration: " + currentIteration);
			initAnts();
			executeACSIteration();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(currentIteration + ": " + bestAnt.getMakespan() + ", " + bestAnt.getSolution());
			}
			currentIteration++;

		}
		LOGGER.info("Best makespan achieved: " + bestAnt.getMakespan() + " for the sequence: " + bestAnt.getSolution());
	}

	/**
	 * Executes a round on the whole colony for the ACS algorithm
	 */
	public void executeACSIteration() {
		bestAnt = ants.get(0);
		for (Ant ant : ants) {
			ant.preProcess();
			ant.solveACS();
			if (ant.getMakespan() < bestAnt.getMakespan()) {
				bestAnt = ant;
			}
		}
		performLocalSearch();
		updatePheromone((ArrayList<Integer>) bestAnt.getSolution(), bestAnt.getMakespan());
	}

	/**
	 * Initializes the ants
	 */
	private void initAnts() {
		this.ants = new ArrayList<>();
		for (int i = 0; i < numAnts; i++) {
			this.ants.add(new Ant(problem, pheromone, heuristicInformation, beta, pheromoneDecayCoeficient, q0, alpha,
					initPheromoneValGlobal));
			// this.ants.add(new Ant());
		}

	}

	/**
	 * Updates pheromone in a global way for all node values.
	 * 
	 * @param minimumMakespan
	 */
	public void updatePheromone(Integer minimumMakespan) {
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
			for (int j = i; j < problem.getNumJobs(); j++) {
				this.heuristicInformation[i][j] = 1.0 / this.problem.computeMakespan(i, j);
				if (j != i) {
					this.heuristicInformation[j][i] = 1.0 / this.problem.computeMakespan(j, i);
				}
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
		LOGGER.trace("Initializing pheromone");
		LOGGER.trace(refMakespan);
		LOGGER.trace(seq);
		initPheromoneValGlobal = 1 / (numAnts.doubleValue() * refMakespan);
		for (int i = 0; i < problem.getNumJobs(); i++) {
			for (int j = i; j < problem.getNumJobs(); j++) {
				pheromone[i][j] = initPheromoneValGlobal;
				if (i != j) {
					pheromone[j][i] = initPheromoneValGlobal;
				}
			}
		}
	}

	private void performLocalSearch() {

	}

	public static class AntSolverBuilder {
		private String instance;
		private Integer numAnts;
		private Integer numIterations;
		private Double rho;
		private Double beta;
		private Double alpha;
		private Double pheromoneDecayCoeficient;
		private Double q0;
		private Integer seed;

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

		public AntSolverBuilder alpha(Double val) {
			this.alpha = val;
			return this;
		}

		public AntSolverBuilder pheromoneDecayCoeficient(Double val) {
			this.pheromoneDecayCoeficient = val;
			return this;
		}

		public AntSolverBuilder q0(Double val) {
			this.q0 = val;
			return this;
		}

		public AntSolverBuilder seed(Integer val) {
			this.seed = val;
			return this;
		}

		public AntSolver build() {
			LOGGER.trace("Building solver with parameters: " + printableVersion());
			return new AntSolver(instance, rho, beta, numAnts, numIterations, pheromoneDecayCoeficient, q0, seed,
					alpha);
		}

		private String printableVersion() {
			return ReflectionToStringBuilder.toString(this);
		}
	}
}
