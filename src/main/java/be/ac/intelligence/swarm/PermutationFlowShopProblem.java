package be.ac.intelligence.swarm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

public class PermutationFlowShopProblem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -71058095287208590L;

	private final static Logger LOGGER = Logger.getLogger(Ant.class);

	private Integer numMachines;
	private Integer numJobs;
	/**
	 * Contains the list of processing times required for each job in the
	 * machine referenced by the key of the map
	 */
	private HashMap<Integer, List<Integer>> machineProcessingTime;
	/**
	 * Contains the list of processing times that a job, referenced by the key
	 * value, requires in the set of machines
	 */
	private HashMap<Integer, Job> jobProcessingTime;
	/**
	 * Contains the solution sequence of the way the jobs have to be approached
	 */
	private List<Integer> solution;

	public PermutationFlowShopProblem(String instanceFile) {
		machineProcessingTime = new HashMap<>();
		jobProcessingTime = new HashMap<>();
		readInstance(instanceFile);
	}

	private void readInstance(String instanceFile) {
		BufferedReader reader;
		try {
			reader = Files.newBufferedReader(Paths.get(instanceFile), Charset.defaultCharset());

			// Using values of the first line
			String[] splitLine = reader.readLine().trim().replace("  ", " ").split(" ");
			numJobs = Integer.valueOf(splitLine[0]);
			numMachines = Integer.valueOf(splitLine[1]);

			LOGGER.trace("Number of Jobs for the instance: " + numJobs);
			LOGGER.trace("Number of Machines for the instance: " + numMachines);

			loadProcessingTimes(reader);

			LOGGER.trace(Arrays.toString(machineProcessingTime.get(0).toArray()));
			LOGGER.trace(Arrays.toString(this.jobProcessingTime.get(0).getTimes().toArray()));

			LOGGER.debug("Instance loaded successfully");

			LOGGER.trace(Arrays.toString(getListOfTimesForJobs().toArray()));

			LOGGER.trace(Arrays.toString(getOrderedListOfJobsByTimes().toArray()));
			
			LOGGER.trace(computeMakespan(2, 0));

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
			machineProcessingTime.put(i, Arrays.asList(reader.readLine().trim().replace("  ", " ").split(" ")).stream()
					.map(elem -> Integer.valueOf(elem)).collect(Collectors.toList()));
		}
		// Initialize Job times array
		for (int i = 0; i < numJobs; i++) {
			jobProcessingTime.put(i, new Job());
		}
		// Put the values of the time required per machine in the jobs structure
		for (List<Integer> processingTimes : machineProcessingTime.values()) {
			for (int i = 0; i < numJobs; i++) {
				jobProcessingTime.get(i).getTimes().add(processingTimes.get(i));
			}
		}
		// Compute total processing time for each job.
		for (Job j : jobProcessingTime.values()) {
			j.computeTotalTime();
		}
	}

	/**
	 * Getter for the array containing the solution sequence
	 * 
	 * @return
	 */
	public List<Integer> getSolution() {
		return this.solution;
	}

	/**
	 * Get the list of times for the whole set of jobs
	 * 
	 * @return
	 */
	private List<Integer> getListOfTimesForJobs() {
		return jobProcessingTime.values().stream().map(j -> j.getTimes().stream().mapToInt(Integer::intValue).sum())
				.collect(Collectors.toList());
	}

	/**
	 * Returns the list of jobs ordered in a descendant way based on the total
	 * times for each job and all the machines
	 * 
	 * @return
	 */
	private List<Integer> getOrderedListOfJobsByTimes() {
		List<Integer> timesForJobs = getListOfTimesForJobs();
		Map<Integer, Integer> m = new HashMap<>();
		for (int i = 0; i < timesForJobs.size(); i++) {
			m.put(i, timesForJobs.get(i));
		}
		return m.entrySet().stream().sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
				.map(Map.Entry::getKey).collect(Collectors.toList());
	}

	public Integer computeMakespan(Integer job1Id, Integer job2Id) {
		return computeMakespan(jobProcessingTime.get(job1Id).getTimes(), jobProcessingTime.get(job2Id).getTimes());
	}

	public Integer computeMakespan(List<Integer> job1Times, List<Integer> job2Times) {
		Integer xTmp;
		Integer x = job1Times.get(0);
		Integer y = 0;

		for (int i = 0; i < job1Times.size(); i++) {
			if (x < y) {
				x = y;
			}
			xTmp = x;
			if (i < job1Times.size() - 1) {
				x = xTmp + job1Times.get(i + 1);
			}
			y = xTmp + job2Times.get(i);
		}
		return y;
	}

	public static class Job {
		private List<Integer> times;
		private Integer total;

		public Job(List<Integer> times) {
			this.times = times;
			computeTotalTime();
		}

		public Job() {
			this.times = new ArrayList<>();
		}

		public void computeTotalTime() {
			total = times.stream().mapToInt(a -> a).sum();
		}

		public List<Integer> getTimes() {
			return times;
		}

		public void setTimes(List<Integer> times) {
			this.times = times;
		}

		public Integer getTotal() {
			return total;
		}

		public void setTotal(Integer total) {
			this.total = total;
		}

	}
}
