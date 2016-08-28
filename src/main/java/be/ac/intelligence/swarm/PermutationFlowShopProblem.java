package be.ac.intelligence.swarm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

public class PermutationFlowShopProblem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -71058095287208590L;

	private final static Logger LOGGER = Logger.getLogger(Ant.class);

	/**
	 * Number of Machines
	 */
	private Integer numMachines;

	/**
	 * Number of Jobs
	 */
	private Integer numJobs;
	/**
	 * Contains the solution sequence of the way the jobs have to be approached
	 */
	private List<Integer> solution;

	private Integer makespan;

	/**
	 * Contains the end-times for each of the job in each of the machines.
	 * Matrix used to allow recalculating the makespan from an intermediate
	 * point without going through all the steps from the first machine
	 */
	private Integer[][] makespanMatrix;

	/**
	 * Main matrix of the problem containing the times for each job in the rows
	 * of the data structure
	 */
	private Integer[][] jobs;
	/**
	 * Same information of the jobs matrix just transposed. Data structure
	 * created just for easier understanding of some computations according to
	 * what was thought on the paper while designing the solution. The solution
	 * can be written only using the jobs matrix.
	 */
	private Integer[][] machines;

	/**
	 * List of unscheduled jobs whose total processing times are no less than
	 * the average value of all scheduled jobs.
	 */
	private ArrayList<Integer> candidateList;

	/**
	 * List of scheduled jobs
	 */
	private Set<Integer> scheduledJobs;

	/**
	 * List of unscheduled jobs
	 */
	private Set<Integer> unscheduledJobs;

	/**
	 * Total time for each job
	 */
	private List<Integer> timeForJobs;

	public PermutationFlowShopProblem(String instanceFile) {
		// Initialize variables of the instance
		readInstance(instanceFile);
		makespanMatrix = new Integer[numMachines][];
		timeForJobs = getListOfTimesForJobs();
		scheduledJobs = new HashSet<>();
		unscheduledJobs = IntStream.range(0, numJobs).boxed().collect(Collectors.toSet());
		solution = new ArrayList<>();

	}

	@SuppressWarnings("unchecked")
	public PermutationFlowShopProblem(PermutationFlowShopProblem copy) {
		super();
		this.numMachines = copy.getNumMachines();
		this.numJobs = copy.getNumJobs();
		this.solution = copy.getSolution() != null ? (List<Integer>) ((ArrayList<Integer>) copy.getSolution()).clone()
				: null;
		this.makespan = copy.getMakespan();
		this.makespanMatrix = PfspUtils.deepCopy(copy.getMakespanMatrix());
		this.jobs = PfspUtils.deepCopy(copy.getJobs());
		this.machines = PfspUtils.deepCopy(copy.getMachines());
		this.candidateList = copy.getCandidateList() != null
				? (ArrayList<Integer>) ((ArrayList<Integer>) copy.getCandidateList()).clone() : null;
		this.scheduledJobs = copy.getScheduledJobs() != null
				? (Set<Integer>) ((HashSet<Integer>) copy.getScheduledJobs()).clone() : null;
		this.unscheduledJobs = copy.getUnscheduledJobs() != null
				? (Set<Integer>) ((HashSet<Integer>) copy.getUnscheduledJobs()).clone() : null;
		this.timeForJobs = copy.getTimeForJobs() != null
				? (List<Integer>) ((ArrayList<Integer>) copy.getTimeForJobs()).clone() : null;
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

			LOGGER.debug("Instance loaded successfully");

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
	 * Get the list of times for the whole set of jobs
	 * 
	 * @return
	 */
	private List<Integer> getListOfTimesForJobs() {
		ArrayList<Integer> timesForJobs = new ArrayList<>();
		for (int i = 0; i < numJobs; i++) {
			timesForJobs.add(Stream.of(jobs[i]).mapToInt(Integer::intValue).sum());
		}
		return timesForJobs;
	}

	/**
	 * Computes the makespan of any given sequence of jobs
	 * 
	 * @param jobsSequence
	 * @param offset
	 *            Indicates the starting position in the jobs to compute the
	 *            makespan values. Used to allow recalculation of makespan
	 *            without going through the entire process taking advantage of
	 *            the previously stored information.
	 * @return
	 */
	public Integer computeMakespan(List<Integer> jobsSequence, Integer offset) {

		Integer tmp = 0;
		// Stores the end times of each task
		Integer[] endTimes = new Integer[jobsSequence.size()];
		if (offset > 0) {
			System.arraycopy(makespanMatrix[0], 0, endTimes, 0, makespanMatrix[0].length);
		}

		for (int i = 0; i < numMachines; i++) {
			for (int j = offset; j < jobsSequence.size(); j++) {
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
			makespanMatrix[i] = endTimes.clone();
		}
		makespan = endTimes[jobsSequence.size() - 1];
		return makespan;
	}

	/**
	 * Makes the computation to assign a value to the Candidate List used in the
	 * solution of the problem. Candidate list defined in the literature as the
	 * x unscheduled jobs whose total processing times are no less than the
	 * average value of all scheduled jobs.
	 */
	public List<Integer> getCandidateListValues() {
		List<Integer> candidateListt = new ArrayList<>();

		OptionalDouble avgTimeScheduledJobs = scheduledJobs.stream().map(i -> timeForJobs.get(i))
				.mapToDouble(a -> a.doubleValue()).average();
		if (avgTimeScheduledJobs.isPresent()) {
			for (Integer uj : unscheduledJobs) {
				if (timeForJobs.get(uj) > avgTimeScheduledJobs.getAsDouble()) {
					candidateListt.add(uj);
				}
			}
		}
		if (candidateListt.isEmpty()) {
			candidateListt.addAll(unscheduledJobs);
		}
		return candidateListt;
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

	/**
	 * modifies the list of scheduled and unscheduled jobs and the solution list
	 */
	public void scheduleJob(int jobId) {
		if (unscheduledJobs.remove(jobId)) {
			solution.add(jobId);
			scheduledJobs.add(jobId);
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

	public Integer getNumJobs() {
		return numJobs;
	}

	public Integer getMakespan() {
		return makespan;
	}

	public List<Integer> getCandidateList() {
		return candidateList;
	}

	public Set<Integer> getUnscheduledJobs() {
		return unscheduledJobs;
	}

	private List<Integer> getTimeForJobs() {
		// TODO Auto-generated method stub
		return null;
	}

	private Set<Integer> getScheduledJobs() {
		return scheduledJobs;
	}

	private Integer[][] getMachines() {
		return machines;
	}

	private Integer[][] getJobs() {
		return jobs;
	}

	private Integer[][] getMakespanMatrix() {
		return makespanMatrix;
	}

	private Integer getNumMachines() {
		return numMachines;
	}

}
