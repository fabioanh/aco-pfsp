package be.ac.intelligence.swarm;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import be.ac.intelligence.swarm.AntSolver.AntSolverBuilder;

/**
 * Hello world!
 *
 */
public class App {
	private final static Logger LOGGER = Logger.getLogger(App.class);
	private static final String INSTANCE = "instance";
	private static final String SEED = "seed";
	private static final String BETA = "beta";
	private static final String EPSILON = "epsilon";
	private static final String RHO = "rho";
	private static final String NUMBER_OF_ANTS = "ants";
	private static final String MAX_LOOPS = "loops";
	private static final String DURATION = "duration";

	public static void main(String[] args) {
		AntSolver solver = readArguments(args);
		solver.execute();
	}

	private static AntSolver readArguments(String[] args) {
		AntSolverBuilder builder = getDefaultParameters();

		Options options = new Options();

		options.addOption(INSTANCE, true, "Path for the instance file of the SCP");
		options.addOption(SEED, true, "Seed to be used in the random numbers obtention");
		options.addOption(BETA, true,
				"Beta parameter used in the probabilities of the Ant Colony Solver");
		options.addOption(EPSILON, true,
				"Beta parameter used in the pheromone thresholds of the Ant Colony Solver");
		options.addOption(RHO, true,
				"Rho parameter used in the probabilities of the Ant Colony Solver");
		options.addOption(NUMBER_OF_ANTS, true, "Number of ants for the Ant Colony Solver");
		options.addOption(MAX_LOOPS, true, "Maximum number of loops to execute");
		options.addOption(DURATION, true, "Expected max duration for the execution");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
			// if (cmd.getOptionValue(SEED) != null) {
			// builder =
			// builder.seed(Integer.valueOf(cmd.getOptionValue(SEED)));
			// }
			// if (cmd.getOptionValue(NUMBER_OF_ANTS) != null) {
			// builder =
			// builder.numberOfAnts(Integer.valueOf(cmd.getOptionValue(NUMBER_OF_ANTS)));
			// }
			// if (cmd.getOptionValue(MAX_LOOPS) != null) {
			// builder =
			// builder.maxLoops(Integer.valueOf(cmd.getOptionValue(MAX_LOOPS)));
			// }
			// if (cmd.getOptionValue(BETA) != null) {
			// builder = builder.beta(Double.valueOf(cmd.getOptionValue(BETA)));
			// }
			// if (cmd.getOptionValue(EPSILON) != null) {
			// builder =
			// builder.epsilon(Double.valueOf(cmd.getOptionValue(EPSILON)));
			// }
			// if (cmd.getOptionValue(RHO) != null) {
			// builder = builder.rho(Double.valueOf(cmd.getOptionValue(RHO)));
			// }
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
		return new AntSolverBuilder();
	}
}
