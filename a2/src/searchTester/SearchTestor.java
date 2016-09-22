package searchTester;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import problem.ArmConfig;

public class SearchTestor {
	
	public static void main(String[] args){
		//build graph
		List<Double> joint = new ArrayList<Double>();
		joint.add(0.1);
		joint.add(0.2);
		joint.add(0.2);
		Graph dagraph = new Graph();
		
		dagraph.addLoc(new Vertex(new ArmConfig(new Point2D.Double(0.1,0.1), joint)));
		joint.clear();
		joint.add(0.4);
		joint.add(0.3);
		joint.add(0.5);
		dagraph.addLoc(new Vertex(new ArmConfig(new Point2D.Double(0.5,0.2), joint)));
		joint.clear();
		joint.add(0.4);
		joint.add(1.0);
		joint.add(0.5);
		dagraph.addLoc(new Vertex(new ArmConfig(new Point2D.Double(0.2,0.1), joint)));
		joint.clear();
		joint.add(0.4);
		joint.add(1.0);
		joint.add(0.5);
		dagraph.addLoc(new Vertex(new ArmConfig(new Point2D.Double(0.2,0.2), joint)));
		joint.clear();
		joint.add(0.4);
		joint.add(0.3);
		joint.add(0.5);
		dagraph.addLoc(new Vertex(new ArmConfig(new Point2D.Double(0,0), joint)));

		int count = 0;
		
		//build edges
		for (Vertex e: dagraph.getLocations()) {
			
			for(int i=0;i<dagraph.getLocations().size();i++) {
				if (i == count) {
					continue;
				} else {
					e.getEdges().add(new Edge(e,dagraph.getLocations().get(i)));
				}
			}		
			count++;
		}
		
		dagraph.getLocations().get(0).getEdges().remove(0);
		
		/*
		//Print for tests
		for (Vertex e: dagraph.getLocations()) {
			System.out.println(e.getC().getBaseCenter() + " Edges: ");
			for (Edge a: e.getEdges()) {
				System.out.println("To: " + a.getV2().getC().getBaseCenter());
			}
		}
		
		*/
		
		//search on graph
		
		Search searcher = new Search(dagraph);
		
		List<ArmConfig> results = searcher.searcher();
		
		for (ArmConfig a: results) {
			System.out.println(a.getBaseCenter());
		}
		
	}
}
