package be.ac.intelligence.swarm;

public class AntSolver {

	PermutationFlowShopProblem problem;

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
