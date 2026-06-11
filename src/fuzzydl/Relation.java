package fuzzydl;

import java.io.Serializable;

/**
 * Represents a role assertion of the form (object individual, role, lower bound
 * for the degree) with respect to a subject individual.
 * 
 * @author Fernando Bobillo
 */
public class Relation implements Serializable
{
	private static final long serialVersionUID = 2149507344907932114L;
	
	private Individual indA, indB;
	private String roleName;
	private Degree degree;


	public Relation(String roleName, Individual ind1, Individual ind2, Degree degree)
	{
		this.roleName = roleName;
		this.indA = ind1;
		this.indB = ind2;
		this.degree = degree;
	}


	/**
	 * Gets the subject individual.
	 * @return Subject individual.
	 */
	public Individual getSubjectIndividual()
	{
		return indA;
	}


	/**
	 * Gets the object individual.
	 * @return Object individual.
	 */
	public Individual getObjectIndividual()
	{
		return indB;
	}


	/**
	 * Sets the object individual.
	 * @param ind Object individual.
	 */
	public void setObjectIndividual(Individual ind)
	{
		this.indB = ind;
	}


	/**
	 * Sets the subject individual.
	 * @param ind Subject individual.
	 */
	public void setSubjectIndividual(Individual ind)
	{
		this.indA = ind;
	}


	/**
	 * Gets the role name.
	 * @return Role name.
	 */
	public String getRoleName()
	{
		return roleName;
	}


	/**
	 * Returns the lower bound for the degree.
	 * @return Lower bound for the degree.
	 */
	public Degree getDegree()
	{
		return degree;
	}


	/**
	 * Gets a printable name of the role assertion without the lower bound.
	 * @return Name of the role assertion without the lower bound.
	 */
	public String getNameWithoutDegree()
	{
		return "(" + indA + "," + indB + "):" + roleName;
	}


	/**
	 * Gets the name of the relation.
	 * @return Name of the relation.
	 */
	@Override
	public String toString()
	{
		return getNameWithoutDegree() + " >= " + degree;
	}


	/**
	 * Gets a relation (role, lower bound for the degree) for the subject individual.
	 * @param role An abstract role.
	 * @param degree A lower bound for the degree.
	 * @return A new relation (subject individual, role, lower bound for the degree).
	 */
	public Relation clone (String role, Degree degree)
	{
		return new Relation(role, indA, indB, degree);
	}

}
