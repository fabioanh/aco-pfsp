package be.ac.intelligence.swarm;

import java.util.ArrayList;
import java.util.HashMap;

public class AntSolver {

	private PermutationFlowShopProblem problem;
	private HashMap<Integer, Double> pheromone;
	private HashMap<Integer, Double> heuristicInformation;
	private ArrayList<Ant> ants;

	private AntSolver(String instance) {
		problem = new PermutationFlowShopProblem(instance);
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

}
