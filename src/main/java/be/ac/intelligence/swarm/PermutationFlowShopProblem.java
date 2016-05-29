package be.ac.intelligence.swarm;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

public class PermutationFlowShopProblem {

	private final static Logger LOGGER = Logger.getLogger(Ant.class);

	private Integer numMachines;
	private Integer numJobs;
	/**
	 * Contains the list of processing times required for each job in the
	 * machine referenced by the key of the map
	 */
	private HashMap<Integer, List<Integer>> machineProcessingTime;
	/**
	 * Contains the list of processing times that a job referenced by the key
	 * value requires in the set of machines
	 */
	private HashMap<Integer, List<Integer>> jobProcessingTime;

	private PermutationFlowShopProblem(String instanceFile) {
		readInstance(instanceFile);
	}

	private void readInstance(String instanceFile) {
		BufferedReader reader;
		try {
			reader = Files.newBufferedReader(Paths.get(instanceFile), Charset.defaultCharset());
			String line = null;

			// Using values of the first line
			String[] splitLine = reader.readLine().trim().replace("  ", " ").split(" ");
			numJobs = Integer.valueOf(splitLine[0]);
			numMachines = Integer.valueOf(splitLine[1]);

			LOGGER.trace("Number of Jobs for the instance: " + numJobs);
			LOGGER.trace("Number of Machines for the instance: " + numMachines);

			loadProcessingTimes(numJobs, numMachines, reader);

			LOGGER.debug("Instance loaded successfully");
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * Method in charge of loading the processing times into the HashMaps of the
	 * class
	 * 
	 * @param numJobss
	 * @param numMachiness
	 * @param reader
	 * @throws IOException
	 */
	private void loadProcessingTimes(BufferedReader reader) throws IOException {
		for (int i = 0; i < numMachines; i++) {
			machineProcessingTime.put(i,
					Arrays.asList(reader.readLine().trim().replace("  ", " ").split(" ")).stream()
							.map(elem -> Integer.valueOf(elem)).collect(Collectors.toList()));

		}

		for (int i = 0; i < numJobs; i++) {
			jobProcessingTime.put(i, new ArrayList<>());
		}

		for (List<Integer> processingTimes : machineProcessingTime.values()) {
			for (int i = 0; i < numJobs; i++) {
				jobProcessingTime.get(i).add(processingTimes.get(i));
			}
		}
	}
}
