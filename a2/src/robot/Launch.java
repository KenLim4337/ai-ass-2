package robot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tester.Tester;

public class Launch {

	public static void main(String[] args) {
		double maxError = Tester.DEFAULT_MAX_ERROR;
		boolean verbose = true;
		String problemPath = null;
		String solutionPath = null;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i].trim();
			if (arg.equals("-e")) {
				i++;
				if (i < args.length) {
					maxError = Double.valueOf(args[i]);
				}
			} else if (arg.equals("-v")) {
				verbose = true;
			} else {
				if (problemPath == null) {
					problemPath = arg;
				} else {
					solutionPath = arg;
				}
			}
		}
		if (problemPath == null) {
			System.out.println("Usage: launch [-e maxError] [-v] "
					+ "problem-file [solution-file]");
			System.exit(1);
		}
		System.out.println("Test #0: Loading files");
		Tester tester = new Tester(maxError);
		try {
			tester.getPs().loadProblem(problemPath);
		} catch (IOException e1) {
			System.out.println("FAILED: Invalid problem file");
			System.out.println(e1.getMessage());
			System.exit(1);
		}

		if (solutionPath != null) {
			//If a solution file is used load The Solution
			try {
				tester.getPs().loadSolution(solutionPath);
			} catch (IOException e1) {
				System.out.println("FAILED: Invalid solution file");
				System.out.println(e1.getMessage());
				System.exit(1);
			}

		} else {
			//Else generate the solution
			
			Sampler sampler = new Sampler(tester);
			if(tester.getPs().getObstacles().isEmpty()){
				sampler.specs.assumeDirectSolution();
			}else{
			sampler.sampleConfigSpace();
			}
			solutionPath =sampler.specs.getPath().toString();
			//tester.getPs().assumeDirectSolution();
		}
		System.out.println("Passed.");

		List<String> testsToRun = new ArrayList<String>();
		if (solutionPath != null) {
			testsToRun.addAll(Arrays.asList(new String[] { "initial", "goal",
					"steps"}));
		}
		testsToRun.addAll(Arrays.asList(new String[] { "angles", "gripper",
				"self-collision", "bounds", "collisions" }));
		int testNo = 1;
		int numFailures = 0;
		for (String name : testsToRun) {
			if (!tester.testByName(name, testNo, verbose)) {
				numFailures++;
			}
			testNo++;
		}
		
		System.exit(numFailures);
		
	}

}
