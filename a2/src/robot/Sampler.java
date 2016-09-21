package robot;
import problem.Obstacle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.awt.geom.Rectangle2D;



/**
 * Class for sampling the workspace into a Configspace searcheable graph
 * @author marco
 *
 */
public class Sampler {
	//Bool defining if we have found a solution
	boolean isPathFound;
	//Distance D we are working with
	static final float D = (float)0.1;
	//result
	Graph configSpace = new Graph();
	//List of obstacle defining the workspace
	List<Obstacle>obstacles;
	//BVTree ObstacleBVT = new BVTree(obstacles);
	
	public Sampler(List<Obstacle>obstacles){
		this.obstacles = obstacles;
	}
	/**
	 * Exp3 Sampling stategy implementation
	 * P(strat) = (1-n)*(W_strat(t)/SUM(W_strat(t)))+n/k
	 * w_strat(t+1)= W_strat(t)+ e^((nr/P(strat))/K)
	 * Where:
	 * P(strat) = probability of using SamplingStrat strat
	 * w(strat)(t) = weight of strat at time t
	 * n= fixed UAR
	 * K= number of strats = 3
	 * r = if Num of components in the roadmap increases/decreases
	 *  
	 * 
	 *@return a Config space graph
	 */
	public Graph generateConfigSpace(){
		//Initialise result
		Graph result = new Graph();
		//Keep track of the number of vertices in result
		int counter = 0;
		//Randomly generate n
		double n = Math.random();
		//k = number of strats = 3 
		int k =3;
		//r is te rewards
		int r;
		//Initialise the list for the weighted starts
		StratList strats = new StratList();
		//initialise W_strats(0) with 1
		strats.add(new weightedStrat(SamplingStrat.UAR, 1));
		strats.add(new weightedStrat(SamplingStrat.betweenOBS, 1));
		strats.add(new weightedStrat(SamplingStrat.nearOBS, 1));	
		for(weightedStrat s: strats){
			//P(strat) = (1-n)*(W_strat(t)/SUM(W_strat(t)))+n/k
			s.setP(((1-n)*(s.getWeight()/strats.getSumOfWeight()))+n/k);
		}
		int started = counter;
		while(true){
			if(!isPathFound){
				//Add 10 samples to the graph
				while(started> counter-10){
					weightedStrat s = chooseStrat(strats);// set something different here to be defined when we know what we will do for search
					Vertex v;
					switch(s.getStrat()){
						case UAR: v = randomSampling(); 
						break;
						case betweenOBS:v = sampleInsidePassage();
						break;
						case nearOBS: v = nearObstacleSampling();
						default:
							v =null;
					}
					r = 0;
					if(!s.equals(null)){
						v.setId(counter++); 
						result.addLoc(v); 
						int i = result.generateEdge(v,obstacles).size();
						if(i>0)
							r =1;
					}
					/*Update the weight of strat with the formula 
					 * w(t+1) = w(t)*exp(((n*r)/P(strat))/k)
					 */
					//look for new edges if num of connected components increases/decreases update the weight of strat
					strats.get(strats.indexOf(s)).setWeight( s.getWeight()*Math.exp(((n*r)/s.getProb())/k ));
					
				}
				//search graph here
			}else{
			return result;
			}
		}
	}
	/**
	 * Chooses a strategy to use depending on their Probabilities
	 * @param strats
	 * @return
	 */
	public weightedStrat chooseStrat(StratList strats){
		double ran = Math.random();
		double p1 = strats.get(0).getProb();
		double p2 = strats.get(1).getProb();
		double p3 = strats.get(2).getProb();
		if(ran<=p1){
			return strats.get(0);
		}else{
			if(ran<=p1+p2){
				return strats.get(1);
			}else{
				return strats.get(2);
			}
		}
		
	}
	/**
	 * 
	 * @return sample a config q Uniformely at random
	 */
	public Vertex randomSampling(){
		return new Vertex((float) Math.random(),(float)Math.random());
	}
	
	
	/**
	 * 
	 * @return a config sampled near an obstacle
	 */
	public Vertex nearObstacleSampling(){
		boolean q1valid,q2valid;
		 //loop until we find a config that works, might need to take this off to account for weighting.
		 q1valid = true;
		 q2valid = true;
		//sample q1 uniformely at random
		Vertex q1 = randomSampling();
		//Sample q2 uniformely at random from the set of all configs withing Distance D
		Vertex q2 = new Vertex(q1.getC().getBaseCenter().getX()+Math.random()*D,q1.getC().getBaseCenter().getY()+Math.random()*D); 
		//Check that the configs are valid
		for(Obstacle o : obstacles){
			if (q1valid&& o.getRect().contains(q1.getC().getBaseCenter())){
				q1valid = false;
			}
			if(q2valid&&o.getRect().contains(q2.getC().getBaseCenter())){
				q2valid = false;
			}
		}
		//if one of the 2 is valid and the other isnt then we have a sampling near an obstacle
		if(!q1valid&&q2valid){
			return q2;
		}else if(!q2valid&&q1valid){
			return q1;
		}
		//We didnt find a valid config
		return null;
	}
	/**
	 * 
	 * @return a config sampled between 2 obstacles
	 */
	public Vertex sampleInsidePassage(){
		boolean q1valid,q2valid;
		Vertex q1 = randomSampling();
		Vertex q2 = new Vertex(q1.getC().getBaseCenter().getX()+Math.random()*D,q1.getC().getBaseCenter().getY()+Math.random()*D); 
		q1valid = true;
		q2valid = true;
		//Check if q1 & q2 are invalid
		for(Obstacle o : obstacles){
			if (o.getRect().contains(q1.getC().getBaseCenter())&&q1valid){
				q1valid = false;
			}
			if(o.getRect().contains(q2.getC().getBaseCenter())&&q2valid){
				q2valid = false;
			}
		}
		if(q1valid == false && q2valid == false){
			double x = (q1.getC().getBaseCenter().getX()+q2.getC().getBaseCenter().getX())/2;
			double y = (q1.getC().getBaseCenter().getY()+q2.getC().getBaseCenter().getY())/2;
			boolean qmvalid =true;
			Vertex qm = new Vertex(x,y);
			for(Obstacle o : obstacles){
				qmvalid = !o.getRect().contains(qm.getC().getBaseCenter());
			}
			if(qmvalid){
				return qm;
			}
		}
		//We didnt find a valid config
		return null;
	}
	private enum SamplingStrat{
		UAR,nearOBS,betweenOBS;
	}
	private class weightedStrat{
		SamplingStrat strat;
		double p=0;
		double weight;
		private weightedStrat(SamplingStrat strat, double w){
			this.strat = strat;
			this.weight = w;
		}
		public SamplingStrat getStrat() {
			return strat;
		}
		public double getWeight() {
			return weight;
		}
		public void setStrat(SamplingStrat strat) {
			this.strat = strat;
		}
		public void setWeight(double weight) {
			this.weight = weight;
		}
		public double getProb() {
			return p;
		}
		public void setP(double p) {
			this.p = p;
		}
		
	}
	private class StratList extends ArrayList<weightedStrat>{
		private StratList(){
			super();
		}
		
		private int getSumOfWeight(){
			int retval=0;
			for(weightedStrat s : this){
				retval+=s.getWeight();
			}
			return retval;
		}
	}
}
