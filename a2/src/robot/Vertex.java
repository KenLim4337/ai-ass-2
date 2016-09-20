package robot;

import java.util.ArrayList;
import java.util.List;

public class Vertex {
	int id; /* The course name */
	List<Edge> edges;
	float pathCost = Integer.MAX_VALUE;//Max value by default
	float f = Integer.MAX_VALUE;
	float h = 0;
	float x;
	float y;

	/**
	 * Creates a new vertex
	 */
	public Vertex(){
		this.id =-1;
		this.edges = new ArrayList<Edge>();
	}
	
	public Vertex(int id,float x, float y) {
		this.id = id;
		this.x = x;
		this.y =y;
	}

	public Vertex(int id, ArrayList<Edge>edges,float x, float y){
		this.id =id;
		this.edges = edges;
		this.x = x;
		this.y =y;
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

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public String toString() {
		return ""+getId();
	}
}

