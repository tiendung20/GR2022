package readfile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReadFile {

	private static final String FILENAME = "vd001.net.xml";
	private static final String FILE_MAP = "map.txt";
	private static float length = 0.0F;

	private static void search(List<Vertex> listVertex, String nameVertex, String nextVertex) {
		for (Vertex vertex : listVertex) {
			if (vertex.getName().compareTo(nameVertex) == 0) {
				if (vertex.getEdge_to().size() > 2) {
					return;
				}
				Set<String> edge_toSet = vertex.getEdge_to().keySet();
				for (String e : edge_toSet) {
					if (vertex.getEdge_to().get(e).compareTo(nextVertex) != 0) {
						length += vertex.getLength().get(e);
						search(listVertex, vertex.getEdge_to().get(e), vertex.getName());
					}
				}
				return;
			}
		}
	}

	private static void handle(NodeList listlane, NodeList listjunction) {

		HashMap<String, Integer> junctionAndCount = new HashMap<String, Integer>();

		List<Vertex> listVertex = new ArrayList<Vertex>();
		Vertex vertex = null;

		for (int i = 0; i < listlane.getLength(); i++) {
			Element element = (Element) listlane.item(i);
			Element parent = (Element) element.getParentNode();

			if (parent.hasAttribute("to") && element.hasAttribute("disallow")) {
				String to = parent.getAttribute("to");
				String from = parent.getAttribute("from");
				String edge_to = parent.getAttribute("id");
				String length = element.getAttribute("length");

				if (junctionAndCount.containsKey(to)) {
					for (Vertex v : listVertex) {
						if (v.getName().compareTo(to) == 0) {
							vertex = v;
							break;
						}
					}
					vertex.getEdge_to().put(edge_to, from);
					vertex.getLength().put(edge_to, Float.parseFloat(length));
					junctionAndCount.put(to, junctionAndCount.get(to) + 1);
				} else {
					vertex = new Vertex(to);
					vertex.getEdge_to().put(edge_to, from);
					vertex.getLength().put(edge_to, Float.parseFloat(length));
					listVertex.add(vertex);
					junctionAndCount.put(to, 1);
				}
			}
		}

		try (FileWriter writer = new FileWriter(FILE_MAP)) {
			Set<String> junctionSet = junctionAndCount.keySet();
			int i = 0;
			for (String junction : junctionSet) {
				if (junctionAndCount.get(junction) >= 3) {
					i++;
				}
			}
			writer.write(i + "\n");
			for (i = 0; i < listjunction.getLength(); i++) {
				Element element_junction = (Element) listjunction.item(i);
				String[] array = element_junction.getAttribute("shape").split(" ");
				if (array.length >= 8) {
					String[] a, b;
					float x1, x2, y1, y2;
					a = array[0].split(",");
					b = array[1].split(",");
					x1 = Float.parseFloat(a[0]);
					y1 = Float.parseFloat(a[1]);
					x2 = Float.parseFloat(b[0]);
					y2 = Float.parseFloat(b[1]);
					writer.write(Double.toString(Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2))) + "\n");
					break;
				}
			}
			for (String junction : junctionSet) {
				if (junctionAndCount.get(junction) >= 3) {
					for (Vertex v : listVertex) {
						if (v.getName().compareTo(junction) == 0) {
							Set<String> edge_toSet = v.getEdge_to().keySet();
							writer.write(v.getName() + " " + v.getEdge_to().size());
							for (String e : edge_toSet) {
								length += v.getLength().get(e);
								search(listVertex, v.getEdge_to().get(e), v.getName());
								writer.write(" " + length);
								length = 0.0F;
							}
							writer.write("\n");
							break;
						}
					}
				}
			}
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			Scanner sc = new Scanner(System.in);
			// Read XMl file

			DocumentBuilder db = dbf.newDocumentBuilder();

			Document Rdoc = db.parse(new File(FILENAME));

			Rdoc.getDocumentElement().normalize();

			NodeList listlane = Rdoc.getElementsByTagName("lane");
			NodeList listjunction = Rdoc.getElementsByTagName("junction");

			handle(listlane, listjunction);

			sc.close();

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}