package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;


/**
 * Concrete concept defined with a crisp interval.
 * @author Fernando Bobillo
 */
public class CrispConcreteConcept extends FuzzyConcreteConcept
{

	private static final long serialVersionUID = -8439000244104200460L;

	/**
	 * Parameters of the funcion
	 */
	double a, b;


	public CrispConcreteConcept(String name, double k1, double k2, double a, double b)
	{
		super(name);
		this.k1 = k1;
		this.k2 = k2;
		this.a = a;
		this.b = b;
	}
	
	public CrispConcreteConcept(String name, int type, double k1, double k2, double a, double b)
	{
		this(name, k1, k2, a, b);
		this.setType(type);
	}


	@Override
	public Concept complement() throws FuzzyOntologyException
	{
		if(getType() == Concept.CONCRETE)
			return new CrispConcreteConcept(name, Concept.CONCRETE_COMPLEMENT, k1, k2, a , b);
		else //if(this.getType() == Concept.CONCRETE_COMPLEMENT)
			return new CrispConcreteConcept(getNameWithoutNot(), Concept.CONCRETE, k1, k2, a, b);
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
		 *   xC < a
		 *   xC \geq k1
		 *   xAss = 0
		 *
		 * IF y2 = 1
		 *   xC \leq a
		 *   xC \geq b
		 *   xAss = 1
		 *
		 * IF y3 = 1
		 *   xC > k2
		 *   xC \geq b
		 *   xAss = 0
		 * 
		 */

		// x_c + (k_1 - a) y_2 \geq k_1
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k1-a,y2)), Inequation.GE, k1); 

		// x_c + (k_1 - b - Epsilon) y_3 \geq k_1
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k1-b-ConfigReader.EPSILON,y3)), Inequation.GE, k1); 

		// x_c + (k_2 - a + Epsilon) y_1 \leq k_2
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k2-a+ConfigReader.EPSILON,y1)), Inequation.LE, k2); 

		// x_c + (k_2 - b) y_2 \leq k_2
		kb.milp.addNewConstraint(new Expression(new Term(1,xC),new Term(k2-b,y2)), Inequation.LE, k2); 

		// xAss \leq 1 - y_1 - y_3
		kb.milp.addNewConstraint(new Expression(new Term(1,xAss),new Term(1,y1),new Term(1,y3)), Inequation.LE, 1); 

		// xAss \geq y_2
		kb.milp.addNewConstraint(new Expression(new Term(1,xAss),new Term(-1,y2)), Inequation.GE); 
	}	


	@Override
	public double getMembershipDegree(double x)
	{
		if ((x >= a) && (x <= b))
			return 1;
		else
			return 0;
	}


	@Override
	public String getName()
	{
		if(this.getType() == Concept.CONCRETE)
			return "crisp(" + k1 + ", " + k2 + ", " + a + ", " + b + ")";
		else
			return "(not crisp(" + k1 + ", " + k2 + ", " + a + ", " + b + ") )";
	}

}
