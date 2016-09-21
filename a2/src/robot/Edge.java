package robot;


public class Edge {
	Vertex v1;
	Vertex v2;
	double weight;

	public Edge() {
		v1 = new Vertex();
		v2 = new Vertex();
		weight = 0;
	}

	public Edge(Vertex v1, Vertex v2, double weight) {
		this.v1 = v1;
		this.v2 = v2;
		this.weight = weight;
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