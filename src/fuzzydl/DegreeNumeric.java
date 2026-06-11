package fuzzydl;

import fuzzydl.milp.*;

/**
 * Degree defined using a real number in [0,1].
 * @author Fernando Bobillo
 */
public class DegreeNumeric extends Degree
{
	private static final long serialVersionUID = -1557247320931484659L;
	
	/**
	 * Numerical value which defines the degree.
	 */
	private double value;


	public DegreeNumeric(double numeric) {
		this.value = numeric;
	}


	/**
	 * Gets the numerical value which defines the degree.
	 * @return Numerical value which defines the degree.
	 */
	public double getNumericalValue() {
		return value;
	}


	@Override
	public Inequation createInequalityWithDegreeRHS(Expression expr, char inequationType ) {
		return new Inequation( Expression.addConstant(expr, -1 * value), inequationType);
	}


	@Override
	public String toString() {
		return "" + value;
	}


	@Override
	public boolean isNumeric() {
		return true;
	}


	@Override
	public Expression addToExpression(Expression expr) {
		return Expression.addConstant(expr, value);
	}


	@Override
	public Expression subtractFromExpression(Expression expr) {
		return Expression.addConstant(expr, -value);
	}


	@Override
	public Expression multiplyConstant(Double constant) {
		return new Expression(value * constant.doubleValue());
	}


	@Override
	public boolean equals(Degree d)
	{
		if (d instanceof DegreeNumeric)
		{
			DegreeNumeric dn = (DegreeNumeric) d;
			return value == dn.getNumericalValue();
		}
		else
			return false;
	}

	@Override
	public boolean isNumberNotOne()
	{
		return value != 1;
	}


	@Override
	public boolean isNumberZero()
	{
		return value == 0;
	}

}
