package fuzzydl;

import java.io.*;

/**
 * Assertion of the form (individual, Concept, lowerDegree).
 * @author Fernando Bobillo
 */
public class Assertion implements Serializable
{

	private static final long serialVersionUID = -820534635283570069L;

	// Individual
	private Individual a;

	// Concept
	private Concept c;

	// Lower bound degree
	private Degree l;


	public Assertion(Individual a, Concept c, Degree l)
	{
		this.a = a;
		this.c = c;
		this.l = l;
			
	} 


	/**
	 * Gets the type of the concept in the assertion.
	 * @return Type of the concept in the assertion
	 */
	public int getType()
	{
		return c.getType();
	}


	/**
	 * Gets the lower bound degree.
	 * @return Lower bound degree.
	 */
	public Degree getLowerLimit()
	{
		return l;
	}


	/**
	 * Gets the concept.
	 * @return Concept.
	 */
	public Concept getConcept()
	{
		return c;
	}


	/**
	 * Gets the individual.
	 * @return Individual.
	 */
	public Individual getIndividual()
	{
		return a;
	}

	/**
	 * Sets the individual of the assertion.
	 * @param ind An individual.
	 */
	public void setIndividual(Individual ind)
	{
		a = ind;
	}



	/**
	 * Sets the lower bound degree.
	 * @param deg Lower bound degree.
	 */
	public void setLowerLimit(Degree deg)
	{
		l = deg;
	}


	/**
	 * Gets the name of the assertion.
	 * @return Name of the assertion.
	 */
	@Override
	public String toString()
	{
		return getNameWithoutDegree() + " >= " + l;
	}


	/**
	 * Gets a printable name of the assertion without the lower bound.
	 * @return Name of the assertion without the lower bound.
	 */
	public String getNameWithoutDegree()
	{
		return a + ":" + c;
	}


	/**
	 * Indicates whether some other assertion is "equal to" this one.
	 * @param ass The reference assertion with which to compare.
	 * @return true if this object is the same as the argument; false otherwise.
	 */
	public boolean equals(Assertion ass)
	{
		boolean same = false;
		if((this.toString()).equals(ass.toString()))
			same = true;
		else if((this.getNameWithoutDegree()).equals(ass.getNameWithoutDegree())
			&& (this.getLowerLimit() instanceof DegreeNumeric) 
			&& (ass.getLowerLimit() instanceof DegreeNumeric))
		{
			double lnew = ((DegreeNumeric)this.getLowerLimit()).getNumericalValue();
			double lold = ((DegreeNumeric)ass.getLowerLimit()).getNumericalValue();
			if(lnew < lold)
			{
				same = true;
			}
		}
		return same;
	}

}
