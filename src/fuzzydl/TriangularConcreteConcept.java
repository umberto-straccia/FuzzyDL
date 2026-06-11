package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.*;


/**
 * Fuzzy concrete concept defined with a triangular function.
 * @author Fernando Bobillo
 */
public class TriangularConcreteConcept extends FuzzyConcreteConcept
{
	private static final long serialVersionUID = -2507558886535942555L;

	/**
	 * Parameters of the funcion
	 */
	protected double a, b, c;


	public TriangularConcreteConcept(String name, double k1, double k2, double a, double b, double c) throws FuzzyOntologyException
	{
		super(name);
		if ((a > b) || (b > c))
			Util.error("Error: Triangular functions require " + a + " <= " + b + " <= " + c);
		if (k1 > a)
			Util.error("Error: Triangular functions require " + k1 + " <= " + a);	
		if (k2 < b)
			Util.error("Error: Triangular functions require " + k2 + " >= " + b);

		this.k1 = k1;
		this.k2 = k2;
		this.a = a;
		this.b = b;
		this.c = c;
	}


	public TriangularConcreteConcept(String name, int type, double k1, double k2, double a, double b, double c) throws FuzzyOntologyException
	{
		this(name,k1,k2,a,b,c);
		setType(type);
	}


	/**
	 * Gets parameter a of the triangular function.
	 * @return Parameter a of the triangular function.
	 */
	public double getA()
	{
		return a;
	}


	/**
	 * Gets parameter b of the triangular function.
	 * @return Parameter b of the triangular function.
	 */
	public double getB()
	{
		return b;
	}


	/**
	 * Gets parameter c of the triangular function.
	 * @return Parameter c of the triangular function.
	 */
	public double getC()
	{
		return c;
	}


	@Override
	public Concept complement() throws FuzzyOntologyException
	{
		if(getType() == Concept.CONCRETE)
			return new TriangularConcreteConcept(name, Concept.CONCRETE_COMPLEMENT, k1, k2, a, b, c);
		else //if(this.getType() == Concept.CONCRETE_COMPLEMENT)
			return new TriangularConcreteConcept(getNameWithoutNot(), Concept.CONCRETE, k1, k2, a, b, c);
	}




	@Override
	public void solveAssertion(CreatedIndividual ind, Degree lowerLimit, KnowledgeBase kb)
	{
		Variable xC = kb.milp.getVariable(ind);
		Variable xAss = kb.milp.getVariable(ind, this);		
		addEquation(xC, xAss, kb);
	}
	

	public void addEquation(Variable xC, Variable xAss, KnowledgeBase kb)
	{
		Variable y1 = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);
		Variable y2 = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);
		Variable y3 = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);
		Variable y4 = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);

		// y1 + y2 + y3 + y4 = 1
		kb.milp.addNewConstraint(new Expression(new Term(1,y1),new Term(1,y2),new Term(1,y3),new Term(1,y4)), Inequation.EQ, 1);

		/*
		 * IF y1 = 1
		 *   xC \leq a
		 *   xC \geq k1
		 *   xAss = 0
		 *
		 * IF y2 = 1
		 *   xC \leq b
		 *   xC \geq a
		 *   xC = (b-a) xAss + a
		 *
		 * IF y3 = 1
		 *   xC \leq c
		 *   xC \geq b
		 *   xC = (b-c) xAss + c
		 *
		 * IF y4 = 1
		 *   xC \leq k2
		 *   xC \geq c
		 *   xAss = 0
		 * 
		 */

		// x_c + (k_1 - a) y_2 \geq k_1
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k1-a,y2)), Inequation.GE, k1); 

		// x_c + (k_1 - b) y_3 \geq k_1
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k1-b,y3)), Inequation.GE, k1); 

		// x_c + (k_1 - c) y_4 \geq k_1
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k1-c,y4)), Inequation.GE, k1); 

		// x_c + (k_2 - a) y_1 \leq k_2
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k2-a,y1)), Inequation.LE, k2); 

		// x_c + (k_2 - b) y_2 \leq k_2
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k2-b,y2)), Inequation.LE, k2); 

		// x_c + (k_2 - c) y_3 \leq k_2
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k2-c,y3)), Inequation.LE, k2); 

		// xAss \leq 1 - y_1 - y_4
		kb.milp.addNewConstraint(new Expression(new Term(1,xAss),new Term(1,y1),new Term(1,y4)), Inequation.LE, 1); 

		// xC + (a-b) xAss + (k2 - a) y2 \leq k2
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(a-b,xAss),new Term(k2 - a,y2)), Inequation.LE, k2);

		// xC + (a-b) xAss + (k1 - b) y2  \geq k1 + a - b
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(a-b,xAss),new Term(k1 - b,y2)), Inequation.GE, k1 + a  - b);

		// xC + (c-b) xAss + (k2 - b) y3  \leq k2 + c - b
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(c-b,xAss),new Term(k2 - b,y3)), Inequation.LE, k2 + c - b);

		// xC + (c-b) xAss + (k1 - c) y3 \geq k1
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(c-b,xAss),new Term(k1 - c,y3)), Inequation.GE, k1);
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
	public String getName()
	{
		if(this.getType() == Concept.CONCRETE)
			return "triangular(" + k1 + ", " + k2 + ", " + a + ", " + b + ", " + c + ")";
		else
			return "(not triangular(" + k1 + ", " + k2 + ", " + a + ", " + b + ", " + c + ") )";
	}

}
