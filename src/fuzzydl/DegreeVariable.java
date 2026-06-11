package fuzzydl;

import fuzzydl.milp.*;

/**
 * Degree defined using a variable.
 * @author Fernando Bobillo
 */
public class DegreeVariable extends Degree 
{
	private static final long serialVersionUID = -4076356690833786721L;
	
	/**
	 * Variable which defines the degree.
	 */
	private Variable variable; 


	DegreeVariable(Variable variable) {
		this.variable = variable;
	}


	/**
	 * Gets the variable which defines the degree.
	 * @return Variable which defines the degree.
	 */
	public Variable getVariable()
	{
		return variable;	
	}


	@Override
	public Inequation createInequalityWithDegreeRHS(Expression expr, char inequationType ) {
		return new Inequation(Expression.addTerm(expr, new Term(-1, variable)), inequationType);
	}


	@Override
	public String toString() {
		return variable.toString();
	}

/*
	@Override
	public double getSolutionValue() {
		return variable.getSolutionValue();
	}
*/

	@Override
	public boolean isNumeric() {
		return false;
	}

	@Override
	public Expression addToExpression(Expression expr) {
		return Expression.addTerm(expr, new Term(1, variable));
	}


	@Override
	public Expression subtractFromExpression(Expression expr) {
		return Expression.addTerm(expr, new Term(-1, variable));
	}


	public Expression multiplyConstant(Double constant) {
		return new Expression(new Term(constant, variable));
	}


	@Override
	public boolean equals(Degree d)
	{
		if (d instanceof DegreeVariable)
		{
			DegreeVariable dv = (DegreeVariable) d;
			return dv.getVariable().toString().equals(getVariable().toString());
		}
		else
			return false;
	}


	@Override
	public boolean isNumberNotOne()
	{
		return false;
	}


	@Override
	public boolean isNumberZero()
	{
		return false;
	}

}
