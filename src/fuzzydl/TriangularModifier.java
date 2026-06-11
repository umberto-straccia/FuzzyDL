package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.Util;


/**
 * Triangular modifier.
 * @author Fernando Bobillo
 */
public class TriangularModifier extends Modifier
{
	private static final long serialVersionUID = -6863455843756310768L;

	/**
	 * Parameters of the modifier.
	 */
	private double a, b, c;


	public TriangularModifier(String name, double a, double b, double c) throws FuzzyOntologyException
	{
		super(name);
		if ((a > b) || (b > c))
			Util.error("Error: Triangular functions require " + a + " <= " + b + " <= " + c);
		this.a = a;
		this.b = b;
		this.c = c;
	}

	

	/**
	 * Gets parameter a.
	 * @return Parameter a.
	 */
	public double getA()
	{
		return a;
	}


	/**
	 * Gets parameter b.
	 * @return Parameter b.
	 */
	public double getB()
	{
		return b;
	}


	/**
	 * Gets parameter c.
	 * @return Parameter c.
	 */
	public double getC()
	{
		return c;
	}


	@Override
	public Concept modify(Concept c)
	{
		return new TriangularlyModifiedConcept(c, this);
	}


	@Override
	public String getName()
	{
		return "triangular-modifier(" + a + ", " + b + ", " + c + ")";
	}


	@Override
	public double getMembershipDegree(double x)
	{
		if ((x <= a) || (x >= c))
			return 0;
		else if (x <= b)
			return (x - a) / (b - a);
		else // (x > b)
			return (c - x) / (c - b);
	}


	@Override
	public void solveAssertion(Individual ind, Concept con, Degree lowerLimit, KnowledgeBase kb)
	{	
		Variable xAisC, xAisModC;
		if (con instanceof ModifiedConcreteConcept)
		{
			Concept modified = ((ModifiedConcreteConcept) con).getModified();
			xAisC = kb.milp.getVariable(ind, modified);
			kb.addAssertion(ind, modified, Degree.getDegree(xAisC));
			xAisModC = kb.milp.getVariable(ind, con);
		}
		else
		{
			Concept modified = new TriangularlyModifiedConcept(con, this);
			xAisC = kb.milp.getVariable(ind, con);
			kb.addAssertion(ind, con, Degree.getDegree(xAisC));
			xAisModC = kb.milp.getVariable(ind, modified);
		}

		Variable y1 = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);
		Variable y2 = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);
		Variable y3 = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);
		Variable y4 = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);

		// y1 + y2 + y3 + y4 = 1
		kb.milp.addNewConstraint(new Expression(new Term(1,y1),new Term(1,y2),new Term(1,y3),new Term(1,y4)), Inequation.EQ, 1);

		/*
		 * IF y1 = 1
		 *   xAisC \leq a
		 *   xAisC \geq 0
		 *   xAisModC = 0
		 *
		 * IF y2 = 1
		 *   xAisC \leq b
		 *   xAisC \geq a
		 *   xAisC = (b-a) xAisModC + a
		 *
		 * IF y3 = 1
		 *   xAisC \leq c
		 *   xAisC \geq b
		 *   xAisC = (b-c) xAisModC + c
		 *
		 * IF y4 = 1
		 *   xAisC \leq 1
		 *   xAisC \geq c
		 *   xAisModC = 0
		 * 
		 */

		// xAisC - a y_2 \geq 0
		kb.milp.addNewConstraint(new Expression(new Term(1,xAisC),new Term(-a,y2)), Inequation.GE); 

		// xAisC - b y_3 \geq 0
		kb.milp.addNewConstraint(new Expression(new Term(1,xAisC),new Term(-b,y3)), Inequation.GE); 

		// xAisC - c y_4 \geq 0
		kb.milp.addNewConstraint(new Expression(new Term(1,xAisC),new Term(-c,y4)), Inequation.GE); 

		// xAisC + (1 - a) y_1 \leq 1
		kb.milp.addNewConstraint(new Expression(new Term(1,xAisC),new Term(1-a,y1)), Inequation.LE, 1); 

		// xAisC + (1 - b) y_2 \leq 1
		kb.milp.addNewConstraint(new Expression(new Term(1,xAisC),new Term(1-b,y2)), Inequation.LE, 1); 

		// xAisC + (1 - c) y_3 \leq 1
		kb.milp.addNewConstraint(new Expression(new Term(1,xAisC),new Term(1-c,y3)), Inequation.LE, 1); 

		// xAisModC \leq 1 - y_1 - y_4
		kb.milp.addNewConstraint(new Expression(new Term(1,xAisModC),new Term(1,y1),new Term(1,y4)), Inequation.LE, 1); 

		// xAisC + (a-b) xAisModC + (1 - a) y2 \leq 1
		kb.milp.addNewConstraint(new Expression(new Term(1,xAisC),new Term(a-b,xAisModC),new Term(1 - a,y2)), Inequation.LE, 1);

		// xAisC + (a-b) xAisModC  - b y2  \geq a - b
		kb.milp.addNewConstraint(new Expression(new Term(1,xAisC),new Term(a-b,xAisModC),new Term(- b,y2)), Inequation.GE, a  - b);

		// xAisC + (c-b) xAisModC + (1 - b) y3  \leq 1 + c - b
		kb.milp.addNewConstraint(new Expression(new Term(1,xAisC),new Term(c-b,xAisModC),new Term(1 - b,y3)), Inequation.LE, 1 + c - b);

		// xAisC + (c-b) xAisModC - c y3 \geq 0
		kb.milp.addNewConstraint(new Expression(new Term(1,xAisC),new Term(c-b,xAisModC),new Term(- c,y3)), Inequation.GE);
	}

}
