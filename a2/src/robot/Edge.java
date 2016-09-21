package robot;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import problem.ArmConfig;

public class Edge {
	Vertex v1;
	Vertex v2;
	double weight;

	public Edge() {
		v1 = new Vertex();
		v2 = new Vertex();
		weight = 0;
	}

	public Edge(Vertex v1, Vertex v2) {
		this.v1 = v1;
		this.v2 = v2;
		this.weight = weightFinder();
	}
	
	public boolean contains(Vertex v){
		return (v==v1)||(v==v2);
	}
	
	public Vertex getOther(Vertex v){
		if(v == v1){
			return v2;
		}
		if(v == v2){
			return v1;
		}
		return new Vertex();
	}
	
	public Vertex getV1() {
		return this.v1;
	}

	public Vertex getV2() {
		return this.v2;
	}
	
	//Returns net movement from initial vertex to end vertex as weight
	public double weightFinder() {
		double totalWeight = 0;
		List<Point2D> v1Pts = new ArrayList<Point2D>();	
		List<Point2D> v2Pts = new ArrayList<Point2D>();	
		
		ArmConfig vee1 = this.v1.getC();
		ArmConfig vee2 = this.v2.getC();
		
		v1Pts.add(vee1.getBaseCenter());
		
		for(Line2D e: vee1.getLinks()) {
			v1Pts.add(e.getP2());
		}
		
		v2Pts.add(vee2.getBaseCenter());
		
		for(Line2D e: vee2.getLinks()) {
			v2Pts.add(e.getP2());
		}
		
		for (int i=0; i < v1Pts.size(); i++) {
			Point2D tempv1 = v1Pts.get(i);
			Point2D tempv2 = v2Pts.get(i);
			if(tempv1 == tempv2) {
				totalWeight += 0;
			}else if (tempv1.getX() == tempv2.getX()) {
				totalWeight += Math.abs(tempv1.getY() - tempv2.getY());
			}else if (tempv1.getY() == tempv2.getY()) {
				totalWeight += Math.abs(tempv1.getX() - tempv2.getX());
			}else {
				totalWeight += Math.sqrt((Math.abs(tempv1.getY() - tempv2.getY()) + (Math.abs(tempv1.getX() - tempv2.getX()))));
			}
		}
		return totalWeight;
	}
	
	public double getWeight() {
		return weight;
	}

	@Override
	public boolean equals(Object obj){
		boolean result = false;
		if(obj instanceof Edge){
			Edge e = (Edge) obj;
			result = (e.getV1()==this.getV1() && e.getV2() == this.getV2())
					||(e.getV1()==this.getV2() && e.getV2() == this.getV1());
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 7;
		int result = 1;
		result = prime * result + (v1.hashCode()+v2.hashCode());
		return result;
	}
	
	@Override
	public String toString(){
		return v1.getId()+"-"+v2.getId();
	}

	
}