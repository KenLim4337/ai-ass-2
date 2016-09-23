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
	}

}
