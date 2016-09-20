package robot;
import problem.Obstacle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class Sampler {
	static final float D = (float)0.1;
	
	Graph ConfigSpace = new Graph();
	
	List<Obstacle>obstacles;
	
	public Sampler(List<Obstacle>obstacles){
		this.obstacles = obstacles;
	}
	
	public Graph generateConfigSpace(){
		return new Graph();
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
}
