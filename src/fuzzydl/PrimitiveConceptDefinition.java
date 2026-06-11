package fuzzydl;

import java.io.*;

/**
 * General concept inclusion axiom.
 * @author Fernando Bobillo
 */
public class PrimitiveConceptDefinition implements Serializable
{
	private static final long serialVersionUID = -8691319585960859251L;

	// Subsumer concept
	private String defined;

	// Subsumed concept
	private Concept definition;

	// Type (depends on the fuzzy implication)
	private int implication;

	// Lower bound degree
	private double degree;


	public PrimitiveConceptDefinition(String defined, Concept definition, int implication, double degree)
	{
		this.defined = defined;
		this.definition = definition;
		this.degree = degree;
		this.implication = implication;
	}


	/**
	 * Gets the name of the defined concept
	 * @return Name of the defined concept.
	 */
	public String getDefinedConcept()
	{
		return defined;
	}


	/**
	 * Gets the definition.
	 * @return Definition.
	 */
	public Concept getDefinition()
	{
		return definition;
	}


	/**
	 * Sets the definition.
	 * @param definition Definition.
	 */
	public void setDefinition(Concept definition)
	{
		this.definition = definition;
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
	 * Sets the degree.
	 * @param deg The degree.
	 */
	public void setDegree(double deg)
	{
		degree = deg;
	}


	/**
	 * Gets the type of the axiom.
	 * @return Type of the axiom.
	 */
	public int getType()
	{
		return implication;
	}


	@Override
	public String toString()
	{	
		return defined.toString() + "  =>_" + implication + "  " + definition.toString() + " >= " + degree;
	}
}
