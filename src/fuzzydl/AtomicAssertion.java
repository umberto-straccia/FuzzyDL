package fuzzydl;

import java.io.*;

/**
 * Atomic assertion of the form (AtomicConcept, lowerDegree).
 * @author Fernando Bobillo
 */
public class AtomicAssertion  implements Serializable
{
	private static final long serialVersionUID = 2151426804080185605L;

	// Atomic concept
	private	Concept c;
	
	// Lower bound degree
	private Degree degree;


	public AtomicAssertion(Concept c , Degree degree)
	{
		this.c = c;
		this.degree = degree;
	}


	/**
	 * Gets the concept name.
	 * @return Concept name.
	 */
	public String getConceptName()
	{
		return c.toString();
	}


	/**
	 * Gets the lower bound degree.
	 * @return Lower bound degree.
	 */
	public Degree getDegree()
	{
		return degree;
	}


	/**
	 * Gets a printable name of the assertion.
	 * @return Name of the assertion.
	 */
	@Override
	public String toString()
	{
		return "< " + c + " " + degree + " >";
	}

}
