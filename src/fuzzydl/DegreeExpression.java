package fuzzydl;

import fuzzydl.milp.*;

/**
 * Degree defined using an expression.
 * @author Fernando Bobillo
 */
public class DegreeExpression extends Degree
{
	private static final long serialVersionUID = -9211283448526107711L;

	/**
	 * Expression which defines the degree.
	 */
	private Expression expr;


	public DegreeExpression(Expression expr) {
		this.expr = expr;
	}

	/**
	 * Gets the expression which defines the degree.
	 * @return Expression which defines the degree.
	 */
	public Expression getExpression()
	{
		return expr;
	}


	@Override
	public Inequation createInequalityWithDegreeRHS(Expression expr, char inequationType) {
		return new Inequation(Expression.subtractExpressions(expr, this.expr), inequationType);
	}


	@Override
	public String toString() {
		return expr.toString();
	}


	@Override
	public boolean isNumeric() {
		return false;
	}


	@Override
	public Expression addToExpression(Expression expr) {
		return Expression.addExpressions(expr, this.expr);
	}


	@Override
	public Expression subtractFromExpression(Expression expr) {
		return Expression.subtractExpressions(expr, this.expr);
	}


	@Override
	public Expression multiplyConstant(Double constant) {
		return Expression.multiplyConstant(expr, constant);
	}


	@Override
	public boolean equals(Degree d)
	{
		if (d instanceof DegreeExpression)
		{
			DegreeExpression de = (DegreeExpression) d;
			return de.equals(getExpression());
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
