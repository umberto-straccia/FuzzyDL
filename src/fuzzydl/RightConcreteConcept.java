package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.*;

/**
 * Fuzzy concrete concept defined with a right shoulder function.
 * @author Fernando Bobillo
 */
public class RightConcreteConcept extends FuzzyConcreteConcept
{
	private static final long serialVersionUID = 164672680674063380L;
	
	/**
	 * Parameters of the funcion
	 */
	double a, b;


	public RightConcreteConcept(String name, double k1, double k2, double a, double b) throws FuzzyOntologyException
	{
		super(name);
		if (a > b)
			Util.error("Error: Right functions require " + a + " <= " + b);	
		if (k1 > a)
			Util.error("Error: Right functions require " + k1 + " <= " + a);	
		if (k2 < b)
			Util.error("Error: Right functions require " + k2 + " >= " + b);

		this.k1 = k1;
		this.k2 = k2;
		this.a = a;
		this.b = b;
	}


	public RightConcreteConcept(String name, int type, double k1, double k2, double a, double b) throws FuzzyOntologyException
	{
		this(name,k1,k2,a,b);
		setType(type);
	}


	@Override
	public Concept complement() throws FuzzyOntologyException
	{
		if(getType() == Concept.CONCRETE)
			return new RightConcreteConcept(name, Concept.CONCRETE_COMPLEMENT, k1, k2, a, b);
		else //if(this.getType() == Concept.CONCRETE_COMPLEMENT)
			return new RightConcreteConcept(getNameWithoutNot(), Concept.CONCRETE, k1, k2, a, b);
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
		
		// y1 + y2 + y3 = 1
		kb.milp.addNewConstraint(new Expression(new Term(1,y1),new Term(1,y2),new Term(1,y3)), Inequation.EQ, 1);

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
		 *   xC \leq k2
		 *   xC \geq b
		 *   xAss = 1
		 * 
		 */

		// x_c + (k_1 - a) y_2 \geq k_1
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k1-a,y2)), Inequation.GE, k1); 

		// x_c + (k_1 - b) y_3 \geq k_1
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k1-b,y3)), Inequation.GE, k1); 

		// x_c + (k_2 - a) y_1 \leq k_2
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k2-a,y1)), Inequation.LE, k2); 

		// x_c + (k_2 - b) y_2 \leq k_2
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k2-b,y2)), Inequation.LE, k2); 

		// xAss \leq 1 - y_1
		kb.milp.addNewConstraint(new Expression(new Term(1,xAss),new Term(1,y1)), Inequation.LE, 1); 

		// xAss \geq y_3
		kb.milp.addNewConstraint(new Expression(new Term(1,xAss),new Term(-1,y3)), Inequation.GE); 

		// xC + (a-b) xAss + (k2 - a) y2 \leq k2
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(a-b,xAss),new Term(k2 - a,y2)), Inequation.LE, k2);

		// xC + (a-b) xAss + (k1 - b) y2  \geq k1 + a - b
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(a-b,xAss),new Term(k1 - b,y2)), Inequation.GE, k1 + a  - b);
	}


	@Override
	public double getMembershipDegree(double x)
	{
		if (x <= a)
			return 0;
		else if (x >= b)
			return 1;
		else
			return (x - a) / (b - a);
	}


	@Override
	public String getName()
	{
		if(this.getType() == Concept.CONCRETE)
			return "right-shoulder(" + k1 + ", " + k2 + ", " + a + ", " + b + ")";
		else
			return "(not right-shoulder(" + k1 + ", " + k2 + ", " + a + ", " + b + ") )";
	}

}
