package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.util.*;


/**
 * Fuzzy number defined with a triangular function.
 * @author Fernando Bobillo
 */
public class TriangularFuzzyNumber extends TriangularConcreteConcept
{
	private static final long serialVersionUID = 8634341560404530625L;

	/**
	 * Lower bound of the range of the fuzzy numbers.
	 */
	static double K1 = Double.NEGATIVE_INFINITY;

	/**
	 * Upper bound of the range of the fuzzy numbers.
	 */
	static double K2 = Double.POSITIVE_INFINITY;


	public TriangularFuzzyNumber(String name, int type, double a, double b , double c) throws FuzzyOntologyException
	{
		super(name, type, K1, K2, a, b, c);
	}


	public TriangularFuzzyNumber(String name, double a, double b, double c) throws FuzzyOntologyException
	{
		this(name, Concept.FUZZY_NUMBER, a, b, c);
	}


	public TriangularFuzzyNumber(double a, double b, double c) throws FuzzyOntologyException
	{
		this("(" + a + ", " + b + ", " + c + ")", Concept.FUZZY_NUMBER, a, b, c);
	}


	/**
	 * Adds two triangular fuzzy numbers.
	 * @param t1 A triangular fuzzy numbers.
	 * @param t2 Another triangular fuzzy numbers.
	 * @return Sum of the fuzzy numbers.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public static TriangularFuzzyNumber add(TriangularFuzzyNumber t1, TriangularFuzzyNumber t2) throws FuzzyOntologyException
	{
		return new TriangularFuzzyNumber(t1.a + t2.a, t1.b + t2.b, t1.c + t2.c );
	}


	/**
	 * Subtracts two triangular fuzzy numbers.
	 * @param t1 Subject of the subtraction.
	 * @param t2 Object of the subtraction.
	 * @return Subtraction of the fuzzy numbers.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public static TriangularFuzzyNumber minus(TriangularFuzzyNumber t1, TriangularFuzzyNumber t2) throws FuzzyOntologyException
	{
		return new TriangularFuzzyNumber(t1.a - t2.c, t1.b - t2.b, t1.c - t2.a );
	}


	/**
	 * Multiplies two triangular fuzzy numbers.
	 * @param t1 A triangular fuzzy numbers.
	 * @param t2 Another triangular fuzzy numbers.
	 * @return Product of the fuzzy numbers.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public static TriangularFuzzyNumber times(TriangularFuzzyNumber t1, TriangularFuzzyNumber t2) throws FuzzyOntologyException
	{
		return new TriangularFuzzyNumber(t1.a * t2.a, t1.b * t2.b, t1.c * t2.c );
	}


	/**
	 * Divides two triangular fuzzy numbers.
	 * @param t1 Subject of the division.
	 * @param t2 Object of the division.
	 * @return Division of the fuzzy numbers.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public static TriangularFuzzyNumber dividedBy(TriangularFuzzyNumber t1, TriangularFuzzyNumber t2) throws FuzzyOntologyException
	{
		System.out.println(t1);
		System.out.println(t2);
		
		if ((t2.a == 0) || (t2.b == 0) || (t2.c == 0))
		{
			Util.error("Error: Cannot divide by zero in fuzzy number (" + t2.a + ", " + t2.b + ", " + t2.c + ").");
			return null;
		}

		return new TriangularFuzzyNumber(t1.a / t2.c, t1.b / t2.b, t1.c / t2.a );
	}


	/**
	 * Checks if the range of the fuzzy numbers has been defined.
	 * @return true if the range of the fuzzy numbers has been defined; false otherwise.
	 */
	public static boolean hasDefinedRange()
	{
		return K1 != Double.NEGATIVE_INFINITY;
	}


	/**
	 * Sets the range of the fuzzy numbers.
	 * @param minRange Lower bound of the range.
	 * @param maxRange Upper bound of the range.
	 */
	public static void setRange(double minRange, double maxRange)
	{
		K1 = minRange;
		K2 = maxRange;
	}


	/**
	 * Gets the Best Non fuzzy Performance (BNP) of the fuzzy number.
	 * @return BNP of the fuzzy number.
	 */
	public double getBestNonFuzzyPerformance()
	{
		return Util.round((a + b + c) / 3);
	}

	
	/**
	 * Indicates whether some other fuzzy number is "equal to" this one.
	 * @param t The reference object with which to compare.
	 * @return true if this object is the same as the argument; false otherwise.
	 */
	public boolean equals(TriangularFuzzyNumber t)
	{
		return ((a == t.a) && (b == t.b) && (c == t.c));
	}


	@Override
	public Concept complement()
	{
		try
		{
			if(this.getType() == Concept.FUZZY_NUMBER)
				return new TriangularFuzzyNumber(this.getName(), FUZZY_NUMBER_COMPLEMENT, a , b , c);
			else if(this.getType() == Concept.FUZZY_NUMBER_COMPLEMENT)
				return new TriangularFuzzyNumber(this.getName(), FUZZY_NUMBER, a , b, c);
		}
		catch (FuzzyOntologyException e) { }

		return null;
	}


	@Override
	public String toString()
	{
		if(this.getType() == Concept.FUZZY_NUMBER)
			return "(" + k1 + "," + k2 + "; " + a + "," + b + "," + c + ")";
		else
			return "(not (" + k1 + "," + k2 + "; " + a + "," + b + "," + c + ") )";
	}

}
