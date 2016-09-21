package robot;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Vertex {
	int id; /* The course name */
	List<Edge> edges;
	float pathCost = Integer.MAX_VALUE;//Max value by default
	float f = Integer.MAX_VALUE;
	float h = 0;
	Point2D p;

	/**
	 * Creates a new vertex
	 */
	public Vertex(){
		this.id =-1;
		this.edges = new ArrayList<Edge>();
	}
	

	public Vertex(double x, double y) {
		this.p.setLocation(x, y);
	}
	
	public Vertex(int id,double x, double y) {
		this.id = id;
		this.p.setLocation(x, y);
	}

	public Vertex(int id, ArrayList<Edge>edges,double x, double y){
		this.id =id;
		this.edges = edges;
		this.p.setLocation(x, y);
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
	
	public void setPathCost(float cost){
		this.pathCost = cost;
	}
	
	public float getPathCost(){
		return this.pathCost;
	}
	
	public float getF() {
		return f;
	}
	
	public void setF(float f) {
		this.f = f;
	}
	
	public float getH() {
		return h;
	}
	
	public void setH(float h) {
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


	public Point2D getP() {
		return p;
	}


	public void setP(Point2D p) {
		this.p = p;
	}

	public void setP(double x, double y) {
		this.p.setLocation(x, y);
	}
	
	public String toString() {
		return ""+getId();
	}
}
