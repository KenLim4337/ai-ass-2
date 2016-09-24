package robot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
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
	
	
	@Override
	public String toString() {
		return "Graph [locations=" + locations + "\n edges=" + edges
				+ "\n	 numberOfLocation=" + numberOfLocation + "]";
	}

	public List<Edge> generateEdge(Vertex v, HBVNode obs){
		//initialize result
		ArrayList<Edge> result = new ArrayList<Edge>();
		Vertex prev = v;
		//Flag to check that the Steps are valid
		boolean isValid = true;
		//For each Vertex in the graph
		vertexLoop: for(Vertex v1: this.getLocations()){
			//Create the appropriate Armconfigs to get from v to v1)
			if(!v.equals(v1)){
				//List<ArmConfig> p = splitDirectPath(v.getC(),v1.getC());
				//System.out.println("Printing P: "+p);
				//for(int i=0;i< p.size()-1;i++ ){
		
					 isValid = isValid&&checkLineValid(v.getC(),v1.getC(),obs);
					 if(!isValid){
						 continue vertexLoop;
					 }else{
						 //Vertex vTmp =new Vertex(p.get(i+1));
						 //this.addLoc(vTmp);
						 System.out.println("Line from "+v+" to "+ v1+ " is Valid ! ");
						 Edge e = new Edge(v, v1);
						 this.addE(e);
						 v.addE(e);
						 //prev = vTmp;
					 }
				 //}
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
	
	/**
	 * @requires c1 & c2 two vertex with max distance = CHAIR_STEP and max angledifferent = ANGLE_STEP
	 * @param c1 First configuration
	 * @param c2 Second configuration
	 * @param obs list of obstacles
	 * @return Wether the Line between c1 and c2 is valid
	 */
	private boolean checkLineValid(ArmConfig c1, ArmConfig c2,HBVNode obs) {
		if (obs.isEmpty()){
			return true;
		}else{
			if(testConfigCollision(c1, obs)||testConfigCollision(c2, obs))
				return false;
			
			//return true;
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
	
			Rectangle2D chair =c3.getChairAsRect();
			if(circle1.contains(chair)&&circle2.contains(chair)){
				return true;
			}else{
				boolean a = checkLineValid(c1,c3,obs);
				boolean b = checkLineValid(c3,c2,obs);
				return a&&b;
			}
		}
	}
	
	private double getDistanceToClosestObs(ArmConfig c, HBVNode obs){
		double d = Double.MAX_VALUE;
		Stack<HBVNode> s = new Stack<HBVNode>();
		s.push(obs);
		
		for( Line2D l: c.getLinks()){
			d = Math.min(d, DFSDistance(obs,s,d,l));
		}
		return d;
	}
	
	private double DFSDistance(HBVNode obs, Stack<HBVNode>s, double d, Line2D l){
		if(!s.empty()){
			HBVNode current = s.pop();
			if(l.ptSegDist(new Point2D.Double(current.getVolume().getCenterX(),current.getVolume().getCenterY()))<d){
				if(current.isLeaf()){
					Line2D primitive = (Line2D)current.getPrimitive();
					d = Math.min(d,SegSegDistance(l, primitive));
				}else{
					for(HBVNode n:current.getChildren()){
						s.push(n);
						return DFSDistance(obs,s,d,l);
					}
				}
			}
		}
		return d;
	}
	
	public List<ArmConfig> splitValidPath(List<ArmConfig> validPath){
		ArrayList<ArmConfig>result = new ArrayList<ArmConfig>();
		for(int i =0; i<validPath.size()-1;i++){
			result.addAll(splitDirectPath(validPath.get(i),validPath.get(i+1)));
			System.out.println("Done direct path");
			System.out.println("Path is : "+result);
		}
		return result;
	}
	/**
	 * Splits a directPath between 2 ArmConfig into the required steps
	 * hen returns the appropriate steps as a list of ArmConfigs
	 */
	public List<ArmConfig> splitDirectPath(ArmConfig init, ArmConfig goal){
		//System.out.println(path);
		ArrayList<ArmConfig>result = new ArrayList<ArmConfig>();
		result.add(init);
		ArmConfig step = init;
		if(!isValidStep(init, goal)){
			AffineTransform af = new AffineTransform();
			double distX = goal.getBaseCenter().getX()- init.getBaseCenter().getX();
			double distY = goal.getBaseCenter().getY()- init.getBaseCenter().getY();
			int signX = (int) (distX/Math.abs(distX));
			int signY = (int)(distY/Math.abs(distY));
			List<Double> angleToCover = new ArrayList<Double>();
			for(int i =0; i<goal.getJointCount();i++){
				angleToCover.add(goal.getJointAngles().get(i)-init.getJointAngles().get(i));
			}
			//need to change this so that only X or Y is changed
			while(!isValidStep(step, goal)){
				System.out.println(result);
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
				 if(af.getTranslateX()!=0 && af.getTranslateY()!=0){
					 if(Math.random()>0.5){
						 distY+=af.getTranslateY();
						 af.setToTranslation(af.getTranslateX(), 0);
					 }else{
						 distX+= af.getTranslateX();
						 af.setToTranslation(0, af.getTranslateY());
					 }
				 }
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
		return locations.get(index);	
	}
	
	
	public boolean testConfigCollision(ArmConfig c, HBVNode obs){
		boolean result = false;
		if(!obs.isEmpty()){
			for(Line2D link: c.getLinks()){
				result &= testCollision(link,obs);
			}
		}
		return result;
	}
	
	
	public boolean testCollision(Line2D link, HBVNode obs){
		if(!obs.getVolume().intersectsLine(link)){
			return false;
		}else{
			if(obs.isLeaf()){
				Line2D p = (Line2D)obs.getPrimitive();
				return simpleCollisionCheck(link,p);
			}else{
				return testCollision(link,obs.getChildren().get(0))||testCollision(link,obs.getChildren().get(0));
			}
		}
	}
	
	
	/**
	 * Test wether a config is in collision a primitive from an obstacle
	 * @param c The config to test
	 * @param primitive the primitive
	 * @return True if colliding with obstacle false otherwise
	 */
	public boolean simpleCollisionCheck(Line2D link, Line2D primitive){
		return primitive.intersectsLine(link);
	}
	
	
	public boolean isValidStep(ArmConfig cfg0, ArmConfig cfg1) {
		if (cfg0.getJointCount() != cfg1.getJointCount()) {
			return false;
		} else if (cfg0.maxAngleDiff(cfg1) > Sampler.ANGLE_STEP) {
			return false;
		} else if (cfg0.maxGripperDiff(cfg1) > Sampler.CHAIR_STEP ) {
			return false;
		} else{
			//distance-step and check if its greater then +- error
			if (cfg0.getBaseCenter().distance(cfg1.getBaseCenter())-0.00000000000000001 > Sampler.CHAIR_STEP ) {
				return false;
			}
		return true;
		}
	}
	
	private double SegSegDistance(Line2D l1, Line2D l2){
		return Math.min(Math.min(l1.ptSegDist(l2.getP1()), l1.ptSegDist(l2.getP2())), Math.min(l2.ptSegDist(l1.getP1()), l2.ptSegDist(l1.getP2())));
	}
}
