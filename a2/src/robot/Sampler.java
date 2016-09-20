package robot;
import problem.Obstacle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.swing.text.DefaultEditorKit.BeepAction;

import com.sun.javafx.scene.traversal.WeightedClosestCorner;

public class Sampler {
	boolean isPathFound;
	static final float D = (float)0.1;
	
	Graph configSpace = new Graph();
	
	List<Obstacle>obstacles;
	
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
		//might need to move all this in a chooseStrat method
		Graph result = new Graph();
		int counter = 0;
		double n = Math.random();
		int k =3;
		int r;
		StratList strats = new StratList();
		//initialise W_strats(0) with 1
		strats.add(new weightedStrat(SamplingStrat.UAR, 1));
		strats.add(new weightedStrat(SamplingStrat.betweenOBS, 1));
		strats.add(new weightedStrat(SamplingStrat.nearOBS, 1));	
		for(weightedStrat s: strats){
			s.setP(((1-n)*(s.getWeight()/strats.getSumOfWeight()))+n/3);
		}
		int started = counter;
		while(true){
			if(!isPathFound){
				while(started> counter-10){
					SamplingStrat s = chooseStrat(strats);// set something different here to be defined when we know what we will do for search
					Vertex v;
					switch(s){
						case UAR: v = randomSampling(); v.setId(counter++); result.addLoc(v); 
						break;
						case betweenOBS:v = sampleInsidePassage(); v.setId(counter++); result.addLoc(v);
						break;
						case nearOBS: v = nearObstacleSampling(); v.setId(counter++); result.addLoc(v);
					}
				}
			}else{
			return result;
			}
		}
	}
	
	public SamplingStrat chooseStrat(StratList strats){
		double ran = Math.random();
		double p1 = strats.get(0).getP();
		double p2 = strats.get(1).getP();
		double p3 = strats.get(2).getP();
		if(ran<=p1){
			return strats.get(0).getStrat();
		}else{
			if(ran<=p1+p2){
				return strats.get(1).getStrat();
			}else{
				return strats.get(2).getStrat();
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
		while(true){ //loop until we find a config that works
			 q1valid = true;
			 q2valid = true;
			//sample q1 uniformely at random
			Vertex q1 = randomSampling();
			//Sample q2 uniformely at random from the set of all configs withing Distance D
			Vertex q2 = new Vertex(q1.getP().getX()+Math.random()*D,q1.getP().getY()+Math.random()*D); 
			for(Obstacle o : obstacles){
				if (o.getRect().contains(q1.getP())&&q1valid){
					q1valid = false;
				}
				if(o.getRect().contains(q2.getP())&&q2valid){
					q2valid = false;
				}
			}
			if(!q1valid&&q2valid){
				return q2;
			}else if(!q2valid&&q1valid){
				return q1;
			}
		}
	}
	/**
	 * 
	 * @return a config sampled between 2 obstacles
	 */
	public Vertex sampleInsidePassage(){
		boolean q1valid,q2valid;
		while(true){ //loop until we find a config that works
			Vertex q1 = randomSampling();
			Vertex q2 = new Vertex(q1.getP().getX()+Math.random()*D,q1.getP().getY()+Math.random()*D); 
			q1valid = true;
			 q2valid = true;
			for(Obstacle o : obstacles){
				if (o.getRect().contains(q1.getP())&&q1valid){
					q1valid = false;
				}
				if(o.getRect().contains(q2.getP())&&q2valid){
					q2valid = false;
				}
			}
			if(q1valid == false && q2valid == false){
				double x = (q1.getP().getX()+q2.getP().getX())/2;
				double y = (q1.getP().getY()+q2.getP().getY())/2;
				boolean qmvalid =true;
				Vertex qm = new Vertex(x,y);
				for(Obstacle o : obstacles){
					qmvalid = !o.getRect().contains(qm.getP());
				}
				if(qmvalid){
					return qm;
				}
			}
		}
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
		public double getP() {
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
