package fuzzydl.milp;

import java.io.*;
import java.util.* ;

/**
 * Linear expression of the form c + c1 * x1 + c2 * x2 + ... + cN * xN
 * @author Fernando Bobillo
 */
public class Expression implements Serializable
{
	private static final long serialVersionUID = -8505873260495194552L;

	// Coefficient c
	private double constant;

	// Terms c1 * x1 + c2 * x2 + ...
	private List<Term> terms = new ArrayList<Term>(); // a hashset would have been appropriate but it does not have get.


	public Expression(double constant) 
	{
		this.constant = constant ;
	}


	public Expression(double constant, Term... terms) 
	{
		this.constant = constant ;
		for(Term term : terms)
			this.terms.add(term);
	}


	public Expression(Term... terms) 
	{
		this(0, terms);
	}


	public Expression(Expression expr) 
	{
		constant = expr.constant;
		for(Term term : expr.getTerms())
			terms.add(term);
	}


	public Expression(Vector<Variable> v) 
	{
		constant = 0;
		for(Variable var : v) 
		{
			Term term = new Term(1, var);
			terms.add(term);
		}
	}


	public Expression(Set<Variable> v) 
	{
		constant = 0;
		for(Variable var : v) 
		{
			Term term = new Term(1, var);
			terms.add(term);
		}
	}


	/**
	 * Gets a copy of an expression.
	 * @return A copy of the expression.
	 */
	@Override
	public Expression clone ()
	{
		Expression e = new Expression();
		e.constant = constant;
		e.terms = new ArrayList<Term>();
		for (Term t : terms)
		{
			Term newTerm = new Term(t.getCoeff(), t.getVar()); 
			e.terms.add(newTerm);
		}

		return e;
	}


	/**
	 * Gets the terms of the expression.
	 * @return Terms of the expression.
	 */
	public List<Term> getTerms() 
	{
		return terms;
	}


	/**
	 * Gets the constant of the expression.
	 * @return Constant of the expression.
	 */
	public double getConstant() 
	{
		return constant ;
	}


	/**
	 * Changes the sign of all the elements of an expression.
	 * @param expr An expression.
	 * @return A new (negated) expression. 
	 */
	public static Expression negateExpression(Expression expr) 
	{
		Expression result = new Expression();
		result.constant = -expr.constant;
		for(Term term : expr.terms)
			result.terms.add(new Term(-term.getCoeff(), term.getVar()));
		return result;
	}


	/**
	 * Adds a double constant to an expression.
	 * @param expr An expression.
	 * @param constant A double constant.
	 * @return A new expression with the sum.
	 */
	public static Expression addConstant(Expression expr, Double constant) 
	{
		Expression result = new Expression(expr);
		result.constant += constant;
		return result;
	}


	/**
	 * Sets a double constant to an expression.
	 * @param constant A double constant.
	 */
	public void setConstant(double constant) 
	{
		this.constant = constant;
	}


	/**
	 * Increments the double constant in one.
	 */
	public void incrementConstant() 
	{
		constant = constant + 1;
	}


	/**
	 * Adds a term to an expression.
	 * @param term A term.
	 */
	public void addTerm(Term term) 
	{
		int index = terms.indexOf(term);
		if(index == -1)
			terms.add(term);
		else {
			terms.set(index, new Term(term.getCoeff() + terms.get(index).getCoeff() , term.getVar()));
		}
	}


	/**
	 * Adds a term to an expression.
	 * @param expr An expression.
	 * @param term A term.
	 * @return A new expression with the sum.
	 */
	public static Expression addTerm(Expression expr, Term term) 
	{
		Expression result = new Expression(expr);
		result.addTerm(term);
		return result;
	}


	/**
	 * Adds two expressions.
	 * @param expr1 An expression.
	 * @param expr2 Another expression.
	 * @return A new expression with the sum.
	 */
	public static Expression addExpressions(Expression expr1, Expression expr2) 
	{
		Expression result = new Expression(expr1);
		for(Term term : expr2.getTerms())
			result.addTerm(term);
		result.constant += expr2.constant;
		return result;
	}


	/**
	 * Substracts two expressions.
	 * @param expr1 First expression.
	 * @param expr2 Second expression.
	 * @return A new expression with the sum.
	 */
	public static Expression subtractExpressions(Expression expr1, Expression expr2) 
	{
		return addExpressions(expr1, negateExpression(expr2));
	}


	/**
	 * Multiplies a double constant and an expression.
	 * @param expr An expression.
	 * @param constant A double constant.
	 * @return A new expression with the product.
	 */
	public static Expression multiplyConstant(Expression expr, Double constant) {
		
		Expression result = new Expression();
		result.constant = expr.constant * constant;
		for(Term term : expr.getTerms())
			result.terms.add(new Term(constant*term.getCoeff(), term.getVar()));
		return result;
	}


	/**
	 * Given a variable, gets its coefficient in the expression.
	 * @param var A variable.
	 * @return Coefficient in the expression.
	 */
	public double getConstantTerm(Variable var)
	{
		for(Term t : terms) 
			if (t.getVar().toString().equals(var.toString()))
				return t.getCoeff();

		return 0;
	}


	/**
	 * Gets a printable name of the expression.
	 * @return Name of the expression.
	 */
	@Override
	public String toString()
	{
		String expression = "";
		if (constant != 0)
			expression += constant; 

		for(Term term : terms) {
			double n = term.getCoeff();
			if (n != 0)
			{
				if ((expression.length() > 0) && (n > 0))
					expression += "+";		  
				if (n == 1)   
					expression += term.getVar();
				else
					if (n == -1)   
						expression += "-" + term.getVar();					
					else
						expression += n + "*" + term.getVar();
			}
		}
		return expression ;
	}

}
