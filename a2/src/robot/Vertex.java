package robot;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import problem.ArmConfig;

public class Vertex {
	int id; /* The course name */
	List<Edge> edges;
	double pathCost = Integer.MAX_VALUE;//Max value by default
	double h = -1;
	ArmConfig c;
	Vertex parent;

	/**
	 * Creates a new vertex
	 */
	public Vertex(){
		this.id =-1;
		this.edges = new ArrayList<Edge>();
	}
	

	public Vertex(double x, double y) {
		this.c.getBaseCenter().setLocation(x, y);
	}
	
	public Vertex(int id,double x, double y) {
		this.id = id;
		this.c.getBaseCenter().setLocation(x, y);
	}
	
	public Vertex(ArmConfig c){
		this.c = c;
	}

	public Vertex(int id, ArrayList<Edge>edges,double x, double y){
		this.id =id;
		this.edges = edges;
		this.c.getBaseCenter().setLocation(x, y);
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setEdges(ArrayList<Edge>edges){
		this.edges = edges;
	}
	
	public List<Edge> getEdges(){
		return this.edges;	
	}
	
	public void setPathCost(double cost){
		this.pathCost = cost;
	}
	
	public double getPathCost(){
		return this.pathCost;
	}
	
	public double getF() {
		return pathCost + h;
	}
	
	public double getH() {
		return h;
	}
	
	public void setH(double h) {
		this.h = h;
	}
	
	public boolean intersects(Vertex v1){
		for(Edge e: edges){
			if(e.contains(v1)){
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 3;
		int result = 1;
		result = prime * result + id;
		//result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = true;
		if (obj instanceof Vertex){
			Vertex other = (Vertex) obj;
			if (id != other.getId()) {
				result = false;
			} else{ if (!edges.equals(other.edges))
				result = false;
			}
		}
		return result;
	}

	

	public void setId(int id) {
		this.id = id;
	}


	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}


	public ArmConfig getC() {
		return c;
	}


	public void setC(ArmConfig c) {
		this.c = c;
	}

	public void setC(double x, double y) {
		this.c.getBaseCenter().setLocation(x, y);
	}
	
	public String toString() {
		return ""+getId();
	}
	
	public void setParent(Vertex x) {
		this.parent = x;
	}
	
	public Vertex getParent() {
		return parent;
	}
}

