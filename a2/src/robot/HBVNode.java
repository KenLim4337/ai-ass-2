 package robot;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

 public class HBVNode {
	    private List<HBVNode> children = new ArrayList<HBVNode>();
	    private HBVNode parent = null;
	    private Ellipse2D volume = null;

	    public HBVNode(Object primitive) {
	        /*
	         * Two Cases : either the primitive is a Line or it is another volume
	         */
	    	if(primitive instanceof Line2D){
	    		Line2D.Double l= (Line2D.Double)primitive;
	    		Rectangle2D bounds = l.getBounds2D();
	    		this.volume = new Ellipse2D.Double(bounds.getX(),bounds.getY(),bounds.getHeight(),bounds.getWidth());
	    	}else{
	    		if(primitive instanceof Ellipse2D){
	    			this.volume =(Ellipse2D.Double)primitive;
	    		}
	    	}
	    	 
	    }

	    public HBVNode(Object primitive, HBVNode parent) {
	    	
	    	if(primitive instanceof Line2D){
	    		Line2D.Double l= (Line2D.Double)primitive;
	    		Rectangle2D bounds = l.getBounds2D();
	    		this.volume = new Ellipse2D.Double(bounds.getX(),bounds.getY(),bounds.getHeight(),bounds.getWidth());
	    	}else{
	    		if(primitive instanceof Ellipse2D){
	    			this.volume =(Ellipse2D.Double)primitive;
	    		}
	    	}
	        this.parent = parent;
	    }

	    public List<HBVNode> getChildren() {
	        return children;
	    }

	    public void setParent(HBVNode parent) {
	        parent.addChild(this);
	        this.parent = parent;
	    }

	    public void addChild(Object primitive) {
	        HBVNode child = new HBVNode(primitive);
	        child.setParent(this);
	        this.children.add(child);
	    }

	    public void addChild(HBVNode child) {
	        child.setParent(this);
	        this.children.add(child);
	    }

	    public Ellipse2D getVolume() {
	        return this.volume;
	    }

	    /*public void setVolume(T data) {
	        this.data = data;
	    }*/

	    public boolean isRoot() {
	        return (this.parent == null);
	    }

	    public boolean isLeaf() {
	        if(this.children.size() == 0) 
	            return true;
	        else 
	            return false;
	    }

	    public void removeParent() {
	        this.parent = null;
	    }
	}