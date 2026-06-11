package fuzzydl;

import java.io.*;

import fuzzydl.milp.*;

/**
 * Degree.
 * @author Fernando Bobillo
 */
public abstract class Degree implements Serializable
{
	private static final long serialVersionUID = 4065331554940251294L;

	public static final Degree ONE = new DegreeNumeric(1.0);

	/**
	 * Constructor for a numeric degree.
	 * @param numeric A double number in [0,1].
	 * @return A numeric degree.
	 */
	public static Degree getDegree(Double numeric) {
		return new DegreeNumeric(numeric);
	}


	/**
	 * Constructor for a variable degree.
	 * @param variable A variable.
	 * @return A variable degree.
	 */
	public static Degree getDegree(Variable variable) {
		return new DegreeVariable(variable);
	}


	/**
	 * Constructor for an expression degree.
	 * @param expr An expression.
	 * @return A expression degree.
	 */
	public static Degree getDegree(Expression expr) {
		return new DegreeExpression(expr);
	}


	/**
	 * Checks if the object is a numeric degree.
	 * @return true if the object is a numeric degree; false otherwise.
	 */
	public abstract boolean isNumeric();


	/**
	 * Gets an inequality of the form (expression, type, degree).
	 * @param expr An expression.
	 * @param inequationType A type of inequality.
	 * @return An inequality of the form (expression, type, degree).
	 */
	public abstract Inequation createInequalityWithDegreeRHS(Expression expr, char inequationType);


	/**
	 * Adds the degree to an expression.
	 * @param expr An expression.
	 * @return A new expression with the sum.
	 */
	public abstract Expression addToExpression(Expression expr);


	/**
	 * Subtracts the degree to an expression.
	 * @param expr An expression.
	 * @return A new expression with the subtraction expr - this.
	 */
	public abstract Expression subtractFromExpression(Expression expr);


	/**
	 * Multiplies the degree and a real number.
	 * @param constant A real number.
	 * @return A new expression with the product.
	 */
	public abstract Expression multiplyConstant(Double constant);


	/**
	 * Gets the name of the degree.
	 * @return Name of the degree.
	 */
	@Override
	public abstract String toString();

	
	/**
	 * Checks if two degrees are equal.
	 * @param d A degree.
	 * @return true if the degrees are equal; false otherwise.
	 */
	public abstract boolean equals(Degree d);


	/**
	 * Checks if the degree is a number different than 1.0.
	 * @return true if the degree is a number different than 1.0; false otherwise.
	 */
	abstract boolean isNumberNotOne();


	/**
	 * Checks if the degree is number 0.0.
	 * @return true if the degree is 0.0; false otherwise.
	 */
	abstract boolean isNumberZero();

}
