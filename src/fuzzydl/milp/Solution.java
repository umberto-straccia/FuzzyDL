package fuzzydl.milp;

import java.io.*;
import java.util.*;


/**
 * Solution to some query over a fuzzy KB.
 * @author Fernando Bobillo
 */
public class Solution implements Serializable
{
	private static final long serialVersionUID = 8688844884974760252L;

	/**
	 * Indicates whether the fuzzy KB is consistent.
	 */
	public static final boolean CONSISTENT_KB = true;

	/**
	 * Indicates whether the fuzzy KB is inconsistent.
	 */
	public static final boolean INCONSISTENT_KB = false;

	/**
	 * Consistency of the fuzzy KB.
	 */
	private boolean consistent;
	

	/**
	 * Numerical value of the solution.
	 */
	private double sol;

	
	/**
	 * Value of the showed variables
	 */
	private Hashtable<String, Double> showedVariables;


	/**
	 * Creates a new Solution, indicating whether the original KB is consistent.
	 * @param consistent True if the original KB is consistent, false if contrary.
	 */
	public Solution (boolean consistent)
	{
		this.sol = 0;
		this.consistent = consistent;
		showedVariables = new Hashtable<String, Double> (); 
	}


	/**
	 * Creates a new Solution, with the result of a query over a consistent KB.
	 * @param sol Solution to some query over a consistent KB.
	 */
	public Solution (double sol)
	{
		this.sol = sol;
		this.consistent = true;
		showedVariables = new Hashtable<String, Double> (); 
	}


	/**
	 * Indicates whether the original KB is consistent or not.
	 * @return True if the original KB is consistent, false if contrary.
	 */
	public boolean isConsistentKB()
	{
		return consistent;
	}


	/**
	 * Gets the solution to some query over a consistent KB.
	 * @return Solution to some query over a consistent KB.
	 */
	public double getSolution()
	{
		return sol;
	}


	/**
	 * Gets the values of some variables after solving a query over a consistent KB.
	 * @return Values of some showed variables.
	 */
	public Hashtable<String, Double> getShowedVariables()
	{
		return showedVariables;
	}


	/**
	 * Sets the value of a showed variable.
	 * @param varName Name of the variable.
	 * @param value Value of the variable.
	 */
	public void addShowedVariable(String varName, double value)
	{
		showedVariables.put(varName, value);
	}


	/**
	 * Gets a printable name of the solution.
	 * @return Name of the solution.
	 */
	@Override
	public String toString()
	{
		if (consistent)
			return "" + sol;
		else
			return "Inconsistent KB";
	}

}
