package robot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import problem.ArmConfig;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class search {

	public void searcher(Graph x, Vertex end) {
		
		List<Point2D> fin = new ArrayList<Point2D>();	
		
		fin.add(end.getC().getBaseCenter());
		
		for(Line2D e: end.getC().getLinks()) {
			fin.add(e.getP2());
		}
		
		
		//Priority queue init
		PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>(10, new Comparator<Vertex>() {
		public int compare(Vertex o1, Vertex o2) {
			if (o1.getH() < o2.getH()) return -1;
	        if (o1.getH() > o2.getH()) return 1;
	        return 0;
		}});
		

		Graph environment = x;
		
		//Result
		List<Vertex> result = new ArrayList<Vertex>();
		
		//Init list for explored nodes
		List<Vertex> explored = new ArrayList<Vertex>();
		
		//Set starting point initial cost to 0
		environment.getVertexById(0);
		
		//Add root to PQ
		queue.add(environment.getVertexById(0));
		
		//Main PQ Loop
		while (!queue.isEmpty()){
			
		//Takes top node in PQ
	    Vertex current = queue.poll();
    
	    //Runs if solution found
	    if(current.getId() == end.getId()){
	    	/*
	    	//Print check
	    	System.out.print("Cost: " + current.getPath() + " |");
	    	System.out.print(" Depth: " + (result.size() - 1) + " |");
	    	System.out.print(" Explored: " + (explored.size()) + " Nodes |");
	    	//return result;
	    	 * 
	    	 */
	    }
    
	    //Marks node as explored
	    explored.add(current);
	    
	    //Iterates through each edge on node
	    for(Edge e: current.getEdges()){
	    	
	    	//Checks if destination already explored or if there is a shorter path to destination
	    	if (explored.contains(e.getV2()) || queue.contains(e.getV2())) {                	
	    		continue;
	    	}
	    	
	    	//Calculates total heuristics of next node
	    	
	    	double heuristic = calculateHeuristic(e.getV2().getC(), fin);
	
	    	e.getV2().setH(heuristic);
	    	
	    	/*
	    	//Sets node parent to current if if destination has no parent or destination is not current's parent
	    	if ((graph[e.getDestIndex()].getParent() == null) 
	    			|| !(graph[current.getIndex()].getParent().equals(graph[e.getDestIndex()]))) {
	    		graph[e.getDestIndex()].setParent(graph[current.getIndex()]);
	    	}	
	    	*/
	    	
	    	/*
	    	//Removes queue entry if it already exists
	    	if(queue.contains(graph[e.getDestIndex()])) {
	    		queue.remove(graph[e.getDestIndex()]);
	    	}
	    	*/
	    	
	    	//Adds destination to queue
			queue.add(e.getV2());
	    }
	
	}
	
	//Prints error message if solution does not exist
		System.out.println("Solution does not exist.");
		result = null;
		return;
	}
	
	public double calculateHeuristic(ArmConfig a, List<Point2D> goal) {
		//Get list of point 2Ds for comparison
		List<Point2D> comp = new ArrayList<Point2D>();	
		double configH = 0;
		
		comp.add(a.getBaseCenter());
		
		for(Line2D e: a.getLinks()) {
			comp.add(e.getP2());
		}
		
		for (int i=0; i < comp.size(); i++) {
			Point2D tempcomp = comp.get(i);
			Point2D tempgoal = goal.get(i);
			if(tempcomp == tempgoal) {
				configH += 0;
			}else if (tempcomp.getX() == tempgoal.getX()) {
				configH += Math.abs(tempcomp.getY() - tempgoal.getY());
			}else if (tempcomp.getY() == tempgoal.getY()) {
				configH += Math.abs(tempcomp.getX() - tempgoal.getX());
			}else {
				configH += Math.sqrt((Math.abs(tempcomp.getY() - tempgoal.getY()) + (Math.abs(tempcomp.getX() - tempgoal.getX()))));
			}
		}
		return configH;
	}
}
