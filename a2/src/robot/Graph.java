package robot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.awt.geom.AffineTransform;
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
		if(!locations.contains(loc)){
			locations.add(loc);
			numberOfLocation++;
		}
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

	public List<Edge> generateEdge(Vertex v, List<Obstacle>obs,Tester tester){
		//initialize result
		ArrayList<Edge> result = new ArrayList<Edge>();
		Vertex prev = v;
		//Flag to check that the Steps are valid
		boolean isValid = true;
		//For each Vertex in the graph
		vertexLoop: for(Vertex v1: this.getLocations()){
			//Create the appropriate Armconfigs to get from v to v1)
			if(!v.equals(v1)){
				List<ArmConfig> p = splitDirectPath(v.getC(),v1.getC(),tester);
				//System.out.println("Printing P: "+p);
				 for(int i=0;i< p.size()-1;i++ ){
					 isValid = isValid&&checkLineValid(p.get(i),p.get(i+1),obs,tester);
					 if(!isValid){
						 continue vertexLoop;
					 }else{
						 Vertex vTmp =new Vertex(p.get(i+1));
						 this.addLoc(vTmp);
						 Edge e = new Edge(prev, vTmp);
						 this.addE(e);
						 prev = vTmp;
					 }
				 }
				//If we reach this code the edge (v,v1) is valid and so are all the steps form v to v1
			 
			}
			 
		}
		System.out.println("graph: "+this);
			//For each link in the chair check that the config is valid
			//this needs to be moved into the sampler
			/*for(Line2D l: v.getC().getLinks()){
				Rectangle2D r = l.getBounds2D();
				for(Obstacle o: obs){
					//if the rectangles intersect
					if(r.intersects(o.getRect())){
						// do simple collision check
						isValid = !o.getRect().intersectsLine(l);
					}
				}
			}*/
		
		return result;
	}

	private boolean checkLineValid(ArmConfig c1, ArmConfig c2,List<Obstacle>obs,Tester test) {
		if(test.hasCollision(c1, obs)||test.hasCollision(c2, obs))
			return false;
		if(!test.isValidStep(c1, c2)){
			return false;
		}
		return true;
		/*//Compute distance to closest obs for p1 && p2
		double distClosestObsP1 = getDistanceToClosestObs(c1, obs);
		double distClosestObsP2 = getDistanceToClosestObs(c2, obs);
		/*
		 * create circle C1(P1,distClosestObsP1) and cricle C2(P2,distclosestObstP2)
		 * Same as the ellipse in the framing rectangle with top left corner = (X+dist,Y+dist)
		 * and  with = heigh = dist
		 */
		/*Ellipse2D.Double circle1 = new Ellipse2D.Double(c1.getBaseCenter().getX()+distClosestObsP1,c1.getBaseCenter().getY()+distClosestObsP1,distClosestObsP1,distClosestObsP1);
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
		/*Point2D temp = c3.getChair().get(0).getP1();
		Rectangle2D chair = new Rectangle2D.Double(temp.getX(), temp.getY(), ArmConfig.CHAIR_WIDTH,  ArmConfig.CHAIR_WIDTH);
		if(circle1.contains(chair)&&circle2.contains(chair)){
			return true;
		}else{
			return checkLineValid(c1,c3,obs,test)&&checkLineValid(c3,c2,obs,test); STACKOVERFLOW 
		}*/
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
	
	/**
	 * Splits a directPath between 2 ArmConfig into the required steps
	 * hen returns the appropriate steps as a list of ArmConfigs
	 */
	public List<ArmConfig> splitDirectPath(ArmConfig init, ArmConfig goal,Tester tester){
		//System.out.println(path);
		ArrayList<ArmConfig>result = new ArrayList<ArmConfig>();
		result.add(init);
		ArmConfig step = init;
		if(!tester.isValidStep(init, goal)){
			AffineTransform af = new AffineTransform();
			double distX = goal.getBaseCenter().getX()- init.getBaseCenter().getX();
			double distY = goal.getBaseCenter().getY()- init.getBaseCenter().getY();
			int signX = (int) (distX/Math.abs(distX));
			int signY = (int)(distY/Math.abs(distY));
			List<Double> angleToCover = new ArrayList<Double>();
			for(int i =0; i<goal.getJointCount();i++){
				angleToCover.add(goal.getJointAngles().get(i)-init.getJointAngles().get(i));
			}
			while(!tester.isValidStep(step, goal)){
				if(distY>=Sampler.CHAIR_STEP&& distX>=Sampler.CHAIR_STEP){
					af.setToTranslation(Sampler.CHAIR_STEP*signX, Sampler.CHAIR_STEP*signY);
					distX=distX-Sampler.CHAIR_STEP;
					distY= distY-Sampler.CHAIR_STEP;
				}else{
					if(distY>=Sampler.CHAIR_STEP&& distX<Sampler.CHAIR_STEP){
						af.setToTranslation(distX*signX, Sampler.CHAIR_STEP*signY);
						distX=0;
						distY=distY-Sampler.CHAIR_STEP;
					}
				
					if(distY<Sampler.CHAIR_STEP&& distX>=Sampler.CHAIR_STEP){
						af.setToTranslation(Sampler.CHAIR_STEP*signX, distY*signY);
						distX= distX-Sampler.CHAIR_STEP;
						distY=0;
					}
					
					if(distY<Sampler.CHAIR_STEP&& distX<Sampler.CHAIR_STEP){
						af.setToTranslation(distX*signX,distY*signY);
						distX=0;
						distY=0;
					}
				}
				 Point2D base = new Point2D.Double();
				 af.transform(step.getBaseCenter(), base);
				 List<Double>rotate = new ArrayList<Double>();
				 for(int i =0;i<angleToCover.size();i++){
					 double remaining = angleToCover.get(i);
					 if(remaining<Sampler.ANGLE_STEP){
					 	rotate.add(step.getJointAngles().get(i)+remaining);
					}else{
						rotate.add(step.getJointAngles().get(i)+Sampler.ANGLE_STEP);
					}
				}
				 ArmConfig nextStep = new ArmConfig(base,rotate);
				 result.add(nextStep);
				 step = nextStep;
			}
		}
		result.add(goal);
		return result;
			 
	}
	
	public Vertex getRandom(){
		int index =Double.valueOf(Math.random()*this.getNumberOfLocation()).intValue(); // this will always round down
		return this.getVertexById(index);	
	}
}
