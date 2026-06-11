package fuzzydl;

import java.io.*;

/**
 * New concrete individual being a representative of a set of individuals.
 * Given an individual p and a fuzzy number F, a representative individual
 * is the set of individuals that are greater or equal (or less or equal) than
 * F. Then, p is related to the representative individual in some way.
 * 
 * @author Fernando Bobillo
 */
public class RepresentativeIndividual implements Serializable
{
	private static final long serialVersionUID = 3041597560311627385L;

	final static int GREATER_EQUAL = 0;

	final static int LESS_EQUAL = 1;

	// Type of the individual
	private int type;

	// Name of the feature for which the individual is a filler.
	private String fName;

	// Reference individual
	private CreatedIndividual ind;

	// Fuzzy number
	private TriangularFuzzyNumber f;


	/**
	 * Constructor.
	 * @param type Type of the representative individual (GREATER_EQUAL, LESS_EQUAL).
	 * @param fName Name of the feature for which the individual is a filler.
	 * @param f Fuzzy number.
	 * @param ind Reference individual.
	 */
	public RepresentativeIndividual (int type, String fName, TriangularFuzzyNumber f, CreatedIndividual ind)
	{
		this.type = type;
		this.fName = fName;
		this.f = f;
		this.ind = ind;
	}


	/**
	 * Gets the type.
	 * @return Type.
	 */
	public int getType()
	{
		return type;
	}


	/**
	 * Gets the feature name for which the individual is a filler.
	 * @return Feature name.
	 */
	public String getFeatureName()
	{
		return fName;
	}


	/**
	 * Gets the fuzzy number.
	 * @return Fuzzy number.
	 */
	public TriangularFuzzyNumber getFuzzyNumber()
	{
		return f;
	}


	/**
	 * Gets the reference individual.
	 * @return Refenrece individual.
	 */
	public CreatedIndividual getIndividual()
	{
		return ind;
	}


}