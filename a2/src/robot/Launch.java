package robot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import problem.ProblemSpec;
import tester.Tester;

public class Launch {

	public static void main(String[] args) throws IOException {
		ProblemSpec specs = new ProblemSpec();
		specs.loadProblem(args[0]);
		Sampler sampler = new Sampler(specs);
		sampler.sampleConfigSpace();
		specs.saveSolution("solution");
		Tester tester = new Tester();
		tester.setPs(specs);
		int i =1;
		boolean verbose = true;
		tester.testInitialFirst(i++, verbose);
		tester.testGoalLast(i++, verbose);
		tester.testValidSteps(i++, verbose);
		tester.testCollisions(i++, verbose);
		tester.testJointAngles(i++, verbose);
		tester.testSelfCollision(i++, verbose);
		tester.testBounds(i++, verbose);
		tester.testGripperLengths(i++, verbose);
		System.out.println("DONE !");
	}

}
