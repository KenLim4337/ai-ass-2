package robot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import problem.ArmConfig;
import problem.Obstacle;
import problem.ProblemSpec;
import tester.Tester;

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
	//Rectangular bound collision check
	public List<Edge> generateEdge(Vertex v, List<Obstacle>obs,Tester tester){
		ArrayList<Edge> result = new ArrayList<Edge>();
		boolean isValid = true;
		for(Vertex v1: this.getLocations()){
			
			 isValid = checkLineValid(v.getC(),v1.getC(),obs,tester);
			 if(isValid){
					Edge e = new Edge(v, v1, v.getC().getBaseCenter().distance(v1.getC().getBaseCenter()));
					result.add(e);
					this.addE(e);
			}
		}
		
		return result;
	}

	private boolean checkLineValid(ArmConfig c1, ArmConfig c2,List<Obstacle>obs,Tester test) {
		if(test.hasCollision(c1, obs)||test.hasCollision(c2, obs))
			return false;
		//Compute distance to closest obs for p1 && p2
		double distClosestObsP1 = getDistanceToClosestObs(c1, obs);
		double distClosestObsP2 = getDistanceToClosestObs(c2, obs);
		/*
		 * create circle C1(P1,distClosestObsP1) and cricle C2(P2,distclosestObstP2)
		 * Same as the ellipse in the framing rectangle with top left corner = (X+dist,Y+dist)
		 * and  with = heigh = dist
		 */
		Ellipse2D.Double circle1 = new Ellipse2D.Double(c1.getBaseCenter().getX()+distClosestObsP1,c1.getBaseCenter().getY()+distClosestObsP1,distClosestObsP1,distClosestObsP1);
		Ellipse2D.Double circle2 = new Ellipse2D.Double(c2.getBaseCenter().getX()+distClosestObsP2,c2.getBaseCenter().getY()+distClosestObsP2,distClosestObsP2,distClosestObsP2);
		//generate c3 = c1+c2/2
		ArmConfig c3 = new ArmConfig(
				new Point2D.Double(
						(c1.getBaseCenter().getX()+c2.getBaseCenter().getX())/2,
						(c1.getBaseCenter().getY()+c2.getBaseCenter().getY())/2),
						c1.getJointAngles());
		//if p3 belongs to c1 and c2 then the segment is collision free, else
		/*
		 * the chair is generated as follow
		 * 1	2
		 * 4	3
		 * So point 1 is the one we need for initialising the rectangle
		 */
		Point2D temp = c3.getChair().get(0).getP1();
		Rectangle2D chair = new Rectangle2D.Double(temp.getX(), temp.getY(), ArmConfig.CHAIR_WIDTH/2,  ArmConfig.CHAIR_WIDTH/2);
		if(circle1.contains(chair)&&circle2.contains(chair)){
			return true;
		}else{
			return checkLineValid(c1,c3,obs,test)&&checkLineValid(c3,c2,obs,test);
		}
	}
	
	private double getDistanceToClosestObs(ArmConfig c, List<Obstacle>obs){
		double d = Double.MAX_VALUE;
		double[] coords = new double[6];
		//For each obstacle
		for(Obstacle o : obs){
			//Iterate over path
			for (PathIterator it = o.getRect().getPathIterator(null); !it.isDone(); it.next()){
				//retrieve coords of segment
				it.currentSegment(coords);
				Point2D a = new Point2D.Double(coords[0],coords[1]);
				it.next();
				it.currentSegment(coords);
				Point2D b = new Point2D.Double(coords[0],coords[1]);
				//define the line l from retrieved coords
				Line2D l = new Line2D.Double(a, b);
				//for each link in the chair
				for(Line2D l1: c.getLinks()){
					//retrieve end and start point
					//add griper case here to later on
					//in all cases the smallest distance between 2lines will be one of the end point of a line and a point on the other line
					Point2D p1 = l1.getP1();
					Point2D p2 = l1.getP2();
					double dist = Math.min(Math.min(l1.ptLineDist(l.getP1()), l1.ptLineDist(l.getP2())), Math.min(l.ptLineDist(p1),l.ptLineDist(p2)));
					d = (d< dist)? dist: d;
				}
			}
			
		}
		return d;
	}
}
