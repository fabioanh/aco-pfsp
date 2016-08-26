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
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
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

	private Integer makespan;

	private Integer[][] jobs;
	private Integer[][] machines;

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

			jobs = new Integer[numJobs][numMachines];
			machines = new Integer[numMachines][numJobs];

			LOGGER.trace("Number of Jobs for the instance: " + numJobs);
			LOGGER.trace("Number of Machines for the instance: " + numMachines);

			loadProcessingTimess(reader);

			LOGGER.trace(Arrays.toString(jobs[0]));

			LOGGER.debug("Instance loaded successfully");

			LOGGER.trace(computeMakespan(2, 0));
			LOGGER.trace(computeMakespan(Arrays.asList(3, 2, 0, 1)));

		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * Method in charge of loading the processing times into the main matrix of
	 * the class
	 * 
	 * @param numJobss
	 * @param numMachiness
	 * @param reader
	 * @throws IOException
	 */
	private void loadProcessingTimess(BufferedReader reader) throws IOException {
		for (int i = 0; i < numMachines; i++) {
			machines[i] = ArrayUtils.toObject(Stream.of(reader.readLine().trim().replace("  ", " ").split(" "))
					.mapToInt(Integer::parseInt).toArray());
		}
		jobs = PfspUtils.transposeMatrix(machines);
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
	 * Computes the makespan of any given sequence of jobs
	 * 
	 * @param jobsSequence
	 * @return
	 */
	public Integer computeMakespan(List<Integer> jobsSequence) {

		Integer tmp = 0;
		Integer[] endTimes = new Integer[jobsSequence.size()];

		for (int i = 0; i < numMachines; i++) {
			for (int j = 0; j < jobsSequence.size(); j++) {
				if (i == 0) {
					endTimes[j] = tmp + machines[i][jobsSequence.get(j)];
					tmp = endTimes[j];
				} else {
					if (j > 0) {
						endTimes[j] = endTimes[j] > endTimes[j - 1] ? endTimes[j] + machines[i][jobsSequence.get(j)]
								: endTimes[j - 1] + machines[i][jobsSequence.get(j)];
					} else {
						endTimes[j] = endTimes[j] + machines[i][jobsSequence.get(j)];
					}
				}
			}
		}
		return endTimes[jobsSequence.size() - 1];
	}

	public Integer computeMakespan(Integer job1Id, Integer job2Id) {
		return computeMakespan(jobs[job1Id], jobs[job2Id]);
	}

	public Integer computeMakespan(Integer[] job1Times, Integer[] job2Times) {
		Integer xTmp;
		Integer x = job1Times[0];
		Integer y = 0;

		for (int i = 0; i < job1Times.length; i++) {
			if (x < y) {
				x = y;
			}
			xTmp = x;
			if (i < job1Times.length - 1) {
				x = xTmp + job1Times[i + 1];
			}
			y = xTmp + job2Times[i];
		}
		return y;
	}

	public Integer getNumJobs() {
		return numJobs;
	}

	public Integer getMakespan() {
		return makespan;
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
