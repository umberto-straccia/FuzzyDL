package fuzzydl;

import java.io.Serializable;

/**
 * Label (weighted concept used in created individuals).
 * @author Fernando Bobillo
 */
public class Label implements Serializable
{
	private static final long serialVersionUID = -8715188369805348127L;

	// Concept
	private Concept concept;

	// Weight in [0,1]
	private Degree weight;


	public Label(Concept concept, Degree weight)
	{
		this.concept = concept;
		this.weight = weight;
	}


	/** 
	 * Gets the name of the label.
	 * @return Name of the label.
	 */
	@Override
	public String toString()
	{
		return concept + " " + weight;
	}


	/**
	 * Indicates whether some other object is "equal to" this one.
	 * @param cw The reference object with which to compare.
	 * @return true if this object is the same as the obj argument; false otherwise.
	 */
	public boolean equals(Label cw)
	{
		if(! this.concept.toString().equals(cw.concept.toString()))
		   return false;

		if(! weightsEqual(this.weight, cw.weight))
			return false;
		
		return true;
	}


	// Checks if two degrees are equal
	private static boolean weightsEqual(Degree w1, Degree w2)
	{
		if(! w1.getClass().equals(w2.getClass()) )
			return false;

		if( w1.isNumeric() && ! ( ((DegreeNumeric)w1).getNumericalValue() == ((DegreeNumeric)w2).getNumericalValue() ) )
			return false;

		return true;
	}

}
