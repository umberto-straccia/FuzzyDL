package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.*;


/**
 * Fuzzy concrete concept defined with a trapezoidal function.
 * @author Fernando Bobillo
 */
public class TrapezoidalConcreteConcept extends FuzzyConcreteConcept
{
	private static final long serialVersionUID = -4912998455805902786L;

	/**
	 * Parameters of the funcion
	 */
	double a, b, c, d;


	public TrapezoidalConcreteConcept(String name, double k1, double k2, double a, double b, double c, double d)  throws FuzzyOntologyException
	{
		super(name);
		if ((a > b) || (b > c) || (c > d))
			Util.error("Error: Trapezoidal functions require " + a + " <= " + b + " <= " + c + " <= " + d);	
		if (k1 > a)
			Util.error("Error: Trapezoidal functions require " + k1 + " <= " + a);	
		if (k2 < b)
			Util.error("Error: Trapezoidal functions require " + k2 + " >= " + b);

		this.k1 = k1;
		this.k2 = k2;
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}


	public TrapezoidalConcreteConcept(String name, int type, double k1, double k2, double a, double b, double c, double d) throws FuzzyOntologyException
	{
		this(name,k1,k2,a,b,c,d);
		setType(type);
	}


	@Override
	public Concept complement() throws FuzzyOntologyException
	{
		if(getType() == Concept.CONCRETE)
			return new TrapezoidalConcreteConcept(name, Concept.CONCRETE_COMPLEMENT, k1, k2, a, b, c, d);
		else //if(this.getType() == Concept.CONCRETE_COMPLEMENT)
			return new TrapezoidalConcreteConcept(getNameWithoutNot(), Concept.CONCRETE, k1, k2, a, b, c, d);
	}


	@Override
	public void solveAssertion(CreatedIndividual ind, Degree lowerLimit, KnowledgeBase kb)
	{
		Variable xC = kb.milp.getVariable(ind);
		Variable xAss = kb.milp.getVariable(ind, this);		addEquation(xC, xAss, kb);
	}

	
	public void addEquation(Variable xC, Variable xAss, KnowledgeBase kb)
	{
		Variable y1 = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);
		Variable y2 = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);
		Variable y3 = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);
		Variable y4 = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);
		Variable y5 = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);
		
		// y1 + y2 + y3 + y4 + y5 = 1
		kb.milp.addNewConstraint(new Expression(new Term(1,y1),new Term(1,y2),new Term(1,y3),new Term(1,y4),new Term(1,y5)), Inequation.EQ, 1);

		/*
		 * IF y1 = 1
		 *   xC \leq a
		 *   xC \geq k1
		 *   xAss = 0
		 *
		 * IF y2 = 1
		 *   xC \leq b
		 *   xC \geq a
		 *   xC = (b-a)xAss + a
		 *
		 * IF y3 = 1
		 *   xC \leq c
		 *   xC \geq b
		 *   xAss = 1
		 *
		 * IF y4 = 1
		 *   xC \leq d
		 *   xC \geq c
		 *   xC = (c-d)xAss + d
		 *
		 * IF y5 = 1
		 *   xC \leq k2
		 *   xC \geq d
		 *   xAss = 0
		 * 
		 */

		// x_c + (k_1 - a) y_2 \geq k_1
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k1-a,y2)), Inequation.GE, k1); 

		// x_c + (k_1 - b) y_3 \geq k_1
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k1-b,y3)), Inequation.GE, k1); 

		// x_c + (k_1 - c) y_4 \geq k_1
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k1-c,y4)), Inequation.GE, k1); 

		// x_c + (k_1 - d) y_5 \geq k_1
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k1-d,y5)), Inequation.GE, k1); 

		// x_c + (k_2 - a) y_1 \leq k_2
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k2-a,y1)), Inequation.LE, k2); 

		// x_c + (k_2 - b) y_2 \leq k_2
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k2-b,y2)), Inequation.LE, k2); 

		// x_c + (k_2 - c) y_3 \leq k_2
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k2-c,y3)), Inequation.LE, k2); 

		// x_c + (k_2 - d) y_4 \leq k_2
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k2-d,y4)), Inequation.LE, k2); 

		// xAss \leq 1 - y_1 - y_5
		kb.milp.addNewConstraint(new Expression(new Term(1,xAss),new Term(1,y1),new Term(1,y5)), Inequation.LE, 1); 

		// xAss \geq y_3
		kb.milp.addNewConstraint(new Expression(new Term(1,xAss),new Term(-1,y3)), Inequation.GE); 

		// xC + (a-b) xAss + (k2 - a) y2 \leq k2
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(a-b,xAss),new Term(k2 - a,y2)), Inequation.LE, k2);

		// xC + (a-b) xAss + (k1 - b) y2  \geq k1 + a - b
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(a-b,xAss),new Term(k1 - b,y2)), Inequation.GE, k1 + a  - b);

        // xC + (d-c) xAss + (k2 - c) y4  \leq k2 + d - c
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(d-c,xAss),new Term(k2 - c,y4)), Inequation.LE, k2 + d - c);

		// xC + (d-c) xAss + (k1 - d) y4 \geq k1
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(d-c,xAss),new Term(k1 - d,y4)), Inequation.GE, k1); 
	}


	@Override
	public double getMembershipDegree(double x)
	{
		if ((x <= a) || (x >= d))
			return 0;
		else if ((x >= b) && (x <= c))
			return 1;
		else if (x >= a)
			return (x - a) / (b - a);
		else // (x > c)
			return (d - x) / (d - c);
	}



	@Override
	public String getName()
	{		
		if(this.getType() == Concept.CONCRETE)
			return "trapezoidal(" + k1 + ", " + k2 + ", " + a + ", " + b + ", " + c + ", " + d + ")";
		else
			return "(not trapezoidal(" + k1 + ", " + k2 + ", " + a + ", " + b + ", " + c + ", " + d + ") )";
	}

}
