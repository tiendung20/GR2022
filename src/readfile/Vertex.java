package readfile;

import java.util.HashMap;

public class Vertex {

	private String name;
	private HashMap<String, String> edge_to = new HashMap<String, String>();
	private HashMap<String, Float> length = new HashMap<String, Float>();

	public Vertex() {
	}

	public Vertex(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, String> getEdge_to() {
		return edge_to;
	}

	public HashMap<String, Float> getLength() {
		return length;
	}

}
