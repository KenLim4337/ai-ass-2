package robot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import problem.ArmConfig;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class search {

	
	
	public void searcher(List<Vertex> x, Vertex start, Vertex end) {
		//Priority queue init
		
		PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>(10, new Comparator<Vertex>() {
		public int compare(Vertex o1, Vertex o2) {
			if (o1.getH() < o2.getH()) return -1;
	        if (o1.getH() > o2.getH()) return 1;
	        return 0;
		}});
		

		List<Vertex> environment = x;
		
		//Result
		List<Vertex> result = new ArrayList<Vertex>();
		
		//Init list for explored nodes
		List<Vertex> explored = new ArrayList<Vertex>();
		
		//Set starting point initial cost to 0
		environment.get(0).setH(0);
		
		//Add root to PQ
		queue.add(environment.get(0));
		
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
	    	
	    	/*To do:
	    	 *  - Add check where branch terminates if over maximum length (no. joints * 0.05) 
	    	 *  - Add distance total to each vertex
	    	 *  
	    	 * 
	    	 */
	    	
	    	
	    	//Calculates total heuristics of next node
	    	double heuristic = graph[current.getIndex()].getPath() + e.getCost();
	
	    	//Checks if destination already explored or if there is a shorter path to destination
	    	if (explored.contains(e.getDestination()) || 
	    			(e.getDestPath() != 0 && currentpath >= e.getDestPath())) {                	
	    		continue;
	    	}
	    	
	    	//Sets node parent to current if if destination has no parent or destination is not current's parent
	    	if ((graph[e.getDestIndex()].getParent() == null) 
	    			|| !(graph[current.getIndex()].getParent().equals(graph[e.getDestIndex()]))) {
	    		graph[e.getDestIndex()].setParent(graph[current.getIndex()]);
	    	}	
	    	
	    	//Removes queue entry if it already exists
	    	if(queue.contains(graph[e.getDestIndex()])) {
	    		queue.remove(graph[e.getDestIndex()]);
	    	}
	    	
	    	//Sets current path total for destination
	    	graph[e.getDestIndex()].setPath(currentpath);
	    	
	    	//Adds destination to queue
			queue.add(graph[e.getDestIndex()]);
	    }
	
	}
	
	//Prints error message if solution does not exist
		System.out.println("Solution does not exist.");
		result = null;
		return result;
	}
	
	public double calculateHeuristic(ArmConfig a, ArmConfig goal) {
		//Get list of point 2Ds for comparison
		List<Point2D> comp = new ArrayList<Point2D>();	
		double configH = 0;
		
		comp.add(a.getBaseCenter());
		
		for(Line2D e: a.getLinks()) {
			comp.add(e.getP2());
		}
		
		List<Point2D> fin = new ArrayList<Point2D>();	
		
		fin.add(goal.getBaseCenter());
		
		for(Line2D e: goal.getLinks()) {
			fin.add(e.getP2());
		}
		
		for (int i=0; i < comp.size(); i++) {
			Point2D tempcomp = comp.get(i);
			Point2D tempgoal = fin.get(i);
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
