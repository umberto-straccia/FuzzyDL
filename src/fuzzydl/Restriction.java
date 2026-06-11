package fuzzydl;

import java.io.*;

/**
 * Universal restriction formed by a role, a concept and a lower bound degree.
 * @author Fernando Bobillo
 */
public class Restriction implements Serializable
{
	private static final long serialVersionUID = -7992802796966398114L;
	
	private Concept concept;
	protected Degree degree;
	protected String roleName;


	public Restriction(String roleName, Concept concept, Degree degree)
	{
		this.roleName = roleName;
		this.concept = concept;
		this.degree = degree;
	}
	

	/**
	 * Gets the name of the role.
	 * @return Name of the role.
	 */
	public String getRoleName()
	{
		return roleName;
	}


	/**
	 * Gets the lower bound for the degree.
	 * @return Lower bound for the degree.
	 */
	public Degree getDegree()
	{
		return degree;
	}


	/**
	 * Gets the fuzzy concept.
	 * @return Fuzzy concept.
	 */
	public Concept getConcept()
	{
		return concept;
	}


	/**
	 * Gets the name of the restriction.
	 * @return Name of the restriction.
	 */
	@Override
	public String toString()
	{
		return getNameWithoutDegree() + " >= " + degree;
	}


	/**
	 * Gets the name of the restriction without the degree.
	 * @return Name of the restriction without the degree.
	 */
	public String getNameWithoutDegree()
	{
		return "(all " + roleName + " " + concept + ")";
	}

}
