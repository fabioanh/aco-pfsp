package be.ac.intelligence.swarm;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import be.ac.intelligence.swarm.AntSolver.AntSolverBuilder;

/**
 * Main class of the application
 */
public class App {
	private final static Logger LOGGER = Logger.getLogger(App.class);
	private static final String INSTANCE = "instance";
	private static final String SEED = "seed";
	private static final String BETA = "beta";
	private static final String EPSILON = "epsilon";
	private static final String RHO = "rho";
	private static final String NUMBER_OF_ANTS = "ants";
	private static final String NUM_ITERATIONS = "loops";
	private static final String DECAY_COEFICIENT = "decay";
	private static final String Q0 = "q0";

	public static void main(String[] args) {
		AntSolver solver = readArguments(args);
		solver.execute();
	}

	private static AntSolver readArguments(String[] args) {
		AntSolverBuilder builder = getDefaultParameters();

		Options options = new Options();

		options.addOption(INSTANCE, true, "Path for the instance file of the SCP");
		options.addOption(SEED, true, "Seed to be used in the random numbers obtention");
		options.addOption(BETA, true, "Beta parameter used in the probabilities of the Ant Colony Solver");
		options.addOption(EPSILON, true, "Beta parameter used in the pheromone thresholds of the Ant Colony Solver");
		options.addOption(RHO, true, "Rho parameter used in the probabilities of the Ant Colony Solver");
		options.addOption(NUMBER_OF_ANTS, true, "Number of ants for the Ant Colony Solver");
		options.addOption(NUM_ITERATIONS, true, "Maximum number of loops to execute");
		options.addOption(DECAY_COEFICIENT, true, "Pheromone decay coeficient");
		options.addOption(Q0, true, "q0 Parameter used in the pseudo-random proportional rule");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
			if (cmd.getOptionValue(SEED) != null) {
				builder = builder.seed(Integer.valueOf(cmd.getOptionValue(SEED)));
			}
			if (cmd.getOptionValue(NUMBER_OF_ANTS) != null) {
				builder = builder.numAnts(Integer.valueOf(cmd.getOptionValue(NUMBER_OF_ANTS)));
			}
			if (cmd.getOptionValue(NUM_ITERATIONS) != null) {
				builder = builder.numIterations(Integer.valueOf(cmd.getOptionValue(NUM_ITERATIONS)));
			}
			if (cmd.getOptionValue(BETA) != null) {
				builder = builder.beta(Double.valueOf(cmd.getOptionValue(BETA)));
			}
			if (cmd.getOptionValue(DECAY_COEFICIENT) != null) {
				builder = builder.pheromoneDecayCoeficient(Double.valueOf(cmd.getOptionValue(DECAY_COEFICIENT)));
			}
			if (cmd.getOptionValue(RHO) != null) {
				builder = builder.rho(Double.valueOf(cmd.getOptionValue(RHO)));
			}
			if (cmd.getOptionValue(Q0) != null) {
				builder = builder.q0(Double.valueOf(cmd.getOptionValue(Q0)));
			}
			if (cmd.getOptionValue(INSTANCE) != null) {
				builder = builder.instance(cmd.getOptionValue(INSTANCE));
			}

			return builder.build();
		} catch (ParseException e) {
			LOGGER.error(e);
		}
		return null;
	}

	private static AntSolverBuilder getDefaultParameters() {
		return new AntSolverBuilder().numIterations(100).numAnts(20).seed(0).pheromoneDecayCoeficient(0.05).beta(2.0)
				.q0(0.9).rho(0.1);
	}
}
