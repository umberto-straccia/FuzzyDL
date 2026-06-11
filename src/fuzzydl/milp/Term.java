package fuzzydl.milp;

import java.io.Serializable;

/**
 * Term, a pair (coefficient, variable).
 * @author Fernando Bobillo
 */
public class Term implements Serializable
{
	private static final long serialVersionUID = 955174045407376556L;

	// Coefficient
	private double coeff;

	// Variable
	private Variable var;


	public Term(double coeff, Variable var) {
		this.var = var;
		this.coeff = coeff;
	}


	/**
	 * Gets the variable of the term.
	 * @return Variable.
	 */
	public Variable getVar() {
		return var;
	}


	/**
	 * Gets the coefficient of the term.
	 * @return Coefficient.
	 */
	public double getCoeff() {
		return coeff;
	}


	/**
	 *  Indicates whether some other object is "equal to" this one.
	 *  @param term The reference object with which to compare.
	 *  @return If this object is the same as the term argument; false otherwise.
	 */
	@Override
	public boolean equals(Object term)
	{
		if (!(this.getClass().equals(term.getClass())))
			return false;
		else
			return this.var.equals(((Term)term).var);
	}

	@Override
	public String toString()
	{
		return coeff + " * " + var;
	}
}
