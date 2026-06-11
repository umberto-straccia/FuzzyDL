package fuzzydl;

import java.io.*;
import java.util.*;

public class ClassificationNode  implements Serializable
{
	private static final long serialVersionUID = -5574012750112981213L;
/*
	boolean isThing;
	boolean isNothing;
*/
	Set<String> eqNames;
	Hashtable<ClassificationNode, Double> inEdges;
	Hashtable<ClassificationNode, Double> outEdges;


	ClassificationNode(String name)
	{
/*
		isThing = name.compareTo("*top*") == 0;
		isNothing = name.compareTo("*bottom*") == 0;
*/			eqNames = new HashSet<String> ();
		eqNames.add(name);
		inEdges = new Hashtable<ClassificationNode, Double> ();
		outEdges = new Hashtable<ClassificationNode, Double> ();
	}

	boolean isThing()
	{
		return hasName("*top*");
	}

	boolean isNoThing()
	{
		return hasName("*bottom*");
	}

	void addInEdge(ClassificationNode node, Double n)
	{
		// It is not possible that the edge already exists
		inEdges.put(node, n);
	}
	
	void addOutEdge(ClassificationNode node, Double n)
	{
		// It is not possible that the edge already exists
		outEdges.put(node, n);
	}

	void removeInEdge(ClassificationNode node, Double n)
	{
		Double value = inEdges.get(node);
		if ( (value != null) && (value <= n) )
			inEdges.remove(node);			
	}

	void removeOutEdge(ClassificationNode node, Double n)
	{
		Double value = outEdges.get(node);
		if ( (value != null) && (value <= n) )
			outEdges.remove(node);			
	}

	boolean hasName(String name)
	{
		for (String s : eqNames)
			if (s.compareTo("name") == 0)
				return true;
		return false;
	}
	
	void addLabel(String c)
	{
		eqNames.add(c);
	}
/*
	Hashtable<Node, Double> getInEdges()
	{
		return inEdges;
	}
*/
	Hashtable<ClassificationNode, Double> getOutEdges()
	{
		return outEdges;
	}
	
	Set<ClassificationNode> getInmediateSucessors ()
	{
		return inEdges.keySet();
	}
	
	Set<ClassificationNode> getInmediatePredecessors ()
	{
		return outEdges.keySet();
	}

	public String toString()
	{
		return eqNames.iterator().next();
	}

	public String getFullName()
	{
		if (eqNames.size() == 1)
			return eqNames.iterator().next();
		String list = "{ ";
		for (String name : eqNames)
			list += name + " "; 
		return list + "}";
	}
	
}
