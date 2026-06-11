package fuzzydl;

import java.io.*;

/**
 * Pair of elements (role, degree in [0,1].
 * Given a role, represents a role parent and the inclusion degree.
 * 
 * @author Fernando Bobillo
 */
public class RoleParentWithDegree implements Serializable
{
	private static final long serialVersionUID = 6342747084171249795L;

	// Parent
	private String parent;

	// Degree
	private double degree;


	public RoleParentWithDegree(String parent, double degree)
	{
		this.degree = degree;
		this.parent = parent;
	}
   

	/**
	* Gets the degree.
	* @return Degree.
	*/
	public double getDegree()
	{
		return degree;
	}


	/**
	* Gets the parent.
	* @return Parent.
	*/
	public String getParent()
	{
		return parent;
	}
}
