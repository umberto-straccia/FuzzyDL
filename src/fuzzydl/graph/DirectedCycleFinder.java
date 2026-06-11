
package fuzzydl.graph;

public class DirectedCycleFinder
{
	private boolean[] marked;		// marked[v] = has vertex v been marked?
	private int[] edgeTo;					// edgeTo[v] = previous vertex on path to v
	private boolean[] onStack;		// onStack[v] = is vertex on the stack?
	private Stack<Integer> cycle;	// directed cycle (or null if no such cycle)


	/**
	 * Default constructor.
	 * @param G A digraph.
	 */
	public DirectedCycleFinder(Digraph G)
	{
		marked  = new boolean[G.numVertices()];
		onStack = new boolean[G.numVertices()];
		edgeTo  = new int[G.numVertices()];
		for (int v = 0; v < G.numVertices(); v++)
			if (!marked[v]) dfs(G, v);

		// check that digraph has a cycle
		assert check(G);
	}


	// Check that algorithm computes either the topological order or finds a directed cycle
	private void dfs(Digraph G, int v) 
	{
		onStack[v] = true;
		marked[v] = true;
		for (int w : G.getAdj(v)) {

			// short circuit if directed cycle found
			if (cycle != null) return;

			//found new vertex, so recur
			else if (!marked[w]) {
				edgeTo[w] = v;
				dfs(G, w);
			}

			// trace back directed cycle
			else if (onStack[w]) {
				cycle = new Stack<Integer>();
				for (int x = v; x != w; x = edgeTo[x]) {
					cycle.push(x);
				}
				cycle.push(w);
				cycle.push(v);
			}
		}

		onStack[v] = false;
	}

	/**
	 * Checks if there are cycles in the graph.
	 * @return true if there is a cycle; false otherwise.
	 */
	public boolean hasCycle()
	{
		return cycle != null;
	}


	// Certify that digraph is either acyclic or has a directed cycle
	private boolean check(Digraph G) 
	{
		if (hasCycle()) {
			// verify cycle
			int first = -1, last = -1;
			for (int v : cycle) {
				if (first == -1) first = v;
				last = v;
			}
			if (first != last) {
				System.err.printf("cycle begins with %d and ends with %d\n", first, last);
				return false;
			}
		}

		return true;
	}

}
