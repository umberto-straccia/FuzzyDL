package fuzzydl.milp;

//import gurobi.GRB;
import com.gurobi.gurobi.*;
import java.io.*;
import java.util.*;

/**
 * Inequality of the form c + c1 * x1 + c2 * x2 + ... &gt;= 0.
 * @author Fernando Bobillo
 */
public class Inequation implements Serializable
{
	private static final long serialVersionUID = -4248266976405134274L;


	/**
	 * Greater or equal sign (&gt;=)
	 */
	public static final char GE = GRB.GREATER_EQUAL;


	/**
	 * Less or equal sign (&lt;=)
	 */
	public static final char LE = GRB.LESS_EQUAL;


	/**
	 * Equal sign (=)
	 */
	public static final char EQ = GRB.EQUAL;

	 
	/**
	 *  Expression
	 */ 
	private Expression expr;

 
	/**
	 *  Type of the inequality
	 */
	private char type;


	public Inequation(Expression expr, char type) 
	{
		this.type = type;
		this.expr = expr;
	}
	
	
	/**
	 * Gets the terms of the expression.
	 * @return Terms of the expression.
	 */
	public List<Term> getTerms() 
	{
		return expr.getTerms();
	}


	/**
	 * Gets the constant of the expression.
	 * @return Constant of the expression.
	 */
	public double getConstant() 
	{
		return - expr.getConstant();
	}


	/**
	 * Gets the type of the inequality.
	 * @return Type of the inequality.
	 */
	public char getType() 
	{
		return type;
	}


	/**
	 * Gets a string representation of the type.
	 * @return String representation of the type.
	 */
	public String getStringType() 
	{
		switch(type) 
		{
			case EQ :
				return "=";

			case LE :
				return "<=";

			default:
				assert(type == GE);
				return ">=";
		}
	}


	/**
	 * Gets a printable name of the object.
	 * @return Name of the object.
	 */
	@Override
	public String toString() 
	{
		return expr + " " + getStringType() + " 0";
	}

}
