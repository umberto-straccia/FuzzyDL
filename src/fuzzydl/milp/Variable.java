package fuzzydl.milp;

import java.io.Serializable;

//import gurobi.*;
import com.gurobi.gurobi.*;


/**
 * Variable.
 * @author Fernando Bobillo
 */
public class Variable implements Serializable
{
	private static final long serialVersionUID = 6142407466934951044L;


	/**
	 * Binary variable
	 */
	 public static final char BINARY_VARIABLE = GRB.BINARY;


	/**
	 * Real variable
	 */
	 public static final char FREE_VARIABLE = GRB.CONTINUOUS;


	/**
	 * Integer variable
	 */
	 public static final char INTEGER_VARIABLE = GRB.INTEGER;


	/**
	 * [0-1] variable
	 */
	 public static final char UP_VARIABLE = GRB.SEMICONT;


	/**
	 * Lower bound of the variable.
	 */
	private double lowerBound;


	/**
	 * Name of the variable.
	 */
	private String name;


	/**
	 * Type of the variable
	 */
	private char type;


	/**
	 * Variable is filler value of datatype restriction
	 */
	private boolean datatypeFiller = false;


	/**
	 * Upper bound of the variable.
	 */
	private double upperBound;


	/**
	 * Name of new variables.
	 */
	private static final String VARIABLE_NAME = "y";

	/**
	 * Number of new variables.
	 */
	private static int VARIABLE_NUMBER = 0;

	
	/**
	 * Constructor.
	 * @param name Name of the variable.
	 * @param type Type of the variable.
	 */
	public Variable(String name, char type) 
	{
		this.name = name;
		setType(type);
	}


	/**
	 * Gets the lower bound of the variable.
	 * @return Lower bound of the variable.
	 */
	public double getLowerBound()
	{
		return lowerBound;
	}


	/**
	 * Gets the type of the variable.
	 * @return Type of the variable
	 */
	public char getType() 
	{
		return type;
	}


	/**
	 * Gets the datatypeFiller value of the variable.
	 * @return datatypeFiller of the variable
	 */
	public boolean getDatatypeFillerType() 
	{
		return datatypeFiller;
	}


	/**
	 * Gets the upper bound of the variable.
	 * @return Upper bound of the variable.
	 */
	public double getUpperBound()
	{
		return upperBound;
	}


	/**
	 * Makes the variable binary.
	 */
	public void setBinaryVariable() 
	{
		setType(BINARY_VARIABLE);
	}


	/**
	 * Makes the variable a datatypeFiller variable.
	 */
	public void setDatatypeFillerVariable() 
	{
		datatypeFiller = true;
	}


	/**
	 * Sets the name of the variable.
	 * @param name Name of the variable
	 */
	public void setName(String name) 
	{
		this.name = name;
	}


	/**
	 * Sets the type of the variable.
	 * @param type Type of the variable
	 */
	public void setType(char type) 
	{
		switch(type)
		{
			case BINARY_VARIABLE:
			case UP_VARIABLE:
				lowerBound = 0;
				upperBound = 1;
				break;

			default:
				lowerBound = Double.NEGATIVE_INFINITY;
				upperBound = Double.POSITIVE_INFINITY;
				break;
		}
		this.type = type;
	}


	/**
	 * Gets a new variable.
	 * @param type Type of the bound.
	 * @return A new variable.
	 */
	public static Variable getNewVariable(char type) 
	{
		VARIABLE_NUMBER++;
		return new Variable(VARIABLE_NAME + VARIABLE_NUMBER, type);
	}


	/**
	 * Gets the name of the variable.
	 * @return Name of the variable.
	 */
	@Override
	public String toString() 
	{
		return name;
	}

}
