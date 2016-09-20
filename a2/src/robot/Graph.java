package robot;

import java.util.ArrayList;
import java.util.HashSet;

public class Graph implements Cloneable {
	ArrayList<Vertex>locations;
	HashSet<Edge> edges;
	int numberOfLocation;
	
	
	
	public Graph(){
		locations = new ArrayList<Vertex>();
		edges= new HashSet<Edge>();
		numberOfLocation =0;
	}
	
	public Graph(ArrayList<Vertex> vertices,HashSet<Edge> edges, int num) {
		this.locations = vertices;
		this.edges = edges;
		this.numberOfLocation = num;
	}
	
	public void addLoc(Vertex loc){
		if(!locations.contains(loc))
			locations.add(loc);
	}
	
	public void addE(Edge e){
		if(!edges.contains(e)){
			this.edges.add(e);
		}
	}
	
	public ArrayList<Vertex> getLocations() {
		return locations;
	}
	
	public HashSet<Edge> getEdges() {
		return edges;
	}

	public int getNumberOfLocation() {
		return numberOfLocation;
	}

	public void setLocations(ArrayList<Vertex> locations) {
		this.locations = locations;
	}

	public void setEdges(HashSet<Edge> edges) {
		this.edges = edges;
	}

	public void setNumberOfLocation(int numberOfLocation) {
		this.numberOfLocation = numberOfLocation;
	}
	
	public Vertex getVertexById(int id){
		for(Vertex v: locations){
			if(v.getId()==id){
				return v;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "Graph [locations=" + locations + ", edges=" + edges
				+ ", numberOfLocation=" + numberOfLocation + "]";
	}
	
}
