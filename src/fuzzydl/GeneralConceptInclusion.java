package fuzzydl;

import java.io.*;

/**
 * General concept inclusion axiom.
 * @author Fernando Bobillo
 */
public class GeneralConceptInclusion implements Serializable
{
	private static final long serialVersionUID = -4969039823748216905L;

	// Subsumer concept
	private Concept subsumer;

	// Subsumed concept
	private Concept subsumed;

	// Type (depends on the fuzzy implication)
	private int type;

	// Lower bound degree
	private Degree degree;


	/**
	 * Lukasiewicz implication
	 */
	public final static int LUKASIEWICZ = 0;


	/**
	 * Goedel implication
	 */
	public final static int GOEDEL = 1;


	/**
	 * Kleene-Dienes implication
	 */
	public final static int KLEENE_DIENES = 2;


	/**
	 * Zadeh'set inclusion
	 */
	public final static int ZADEH = 3;


	public GeneralConceptInclusion(Concept subsumer, Concept subsumed, Degree degree, int type)
	{
		this.subsumer = subsumer;
		this.subsumed = subsumed;
		this.degree = degree;
		this.type = type;
	}


	/**
	 * Gets the subsumer concept.
	 * @return Subsumer concept.
	 */
	public Concept getSubsumer()
	{
		return subsumer;
	}


	/**
	 * Gets the subsumed concept.
	 * @return Subsumed concept.
	 */
	public Concept getSubsumed()
	{
		return subsumed;
	}	

	/**
	 * Gets the type of the GCI (which depends on the fuzzy implication).
	 * @return Type of the GCI.
	 */
	public int getType()
	{
		return type;
	}

	
	/**
	 * Gets the lower bound for the degree.
	 * @return lLwer bound for the degree
	 */
	public Degree getDegree()
	{
		return degree;
	}


	/**
	 * Sets the lower bound for the degree.
	 * @param deg Lower bound for the degree
	 */
	public void setDegree(Degree deg)
	{
		degree = deg;
	}


	@Override
	public String toString()
	{	
		return subsumed.toString() + " => " + subsumer.toString() + " >= " + degree;
	}


	/**
	 * Sets the subsumer concept.
	 * @param newConcept New subsumer concept.
	 */
	public void setSubsumer(Concept newConcept)
	{
		subsumer = newConcept;
	}


	/**
	 * Sets the subsumed concept.
	 * @param newConcept New subsumed concept.
	 */
	public void setSubsumed(Concept newConcept)
	{
		subsumed = newConcept;
	}
	

}
