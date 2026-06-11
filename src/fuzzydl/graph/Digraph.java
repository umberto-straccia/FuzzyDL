
package fuzzydl.graph;

import java.util.*;

public class Digraph
{

	private final int V;
	private ArrayList<Set<Integer>> adj;


	/**
	 * Constructor of an empty digraph with V vertices.
	 * @param V Number of vertices.
	 */
	public Digraph(int V)
	{
		if (V < 0)
			throw new RuntimeException("Number of vertices must be nonnegative");
		this.V = V;

		adj = new ArrayList<Set<Integer>>();
		for (int v = 0; v < V; v++)
			adj.add(new HashSet<Integer>());
	}


	/**
	 * Gets the number of vertices in the digraph.
	 * @return Number of vertices in the digraph.
	 */
	public int numVertices()
	{
		return V;
	}


	/**
	 * Adds the directed edge v-w to the digraph.
	 * @param v Source node.
	 * @param w Target node.
	 */
	public void addEdge(int v, int w) 
	{
		adj.get(v).add(w);
	}


	/**
	 * Gets the array of elements adjacent to a node.
	 * @param v A node of the graph.
	 * @return An array of adjacent elements.
	 */
	Set<Integer> getAdj(int v)
	{
		return adj.get(v);
	}


	/**
	 * Removes the directed edge v-w to the digraph.
	 * @param v Source node.
	 * @param w Target node.
	 */
	public void removeEdge(int v, int w) 
	{
		adj.get(v).remove(w);
	}
}
