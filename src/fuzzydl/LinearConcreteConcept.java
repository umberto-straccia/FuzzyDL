package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.*;

/**
 * Fuzzy concrete concept defined with a left shoulder function.
 * @author Fernando Bobillo
 */
public class LinearConcreteConcept extends FuzzyConcreteConcept
{
	private static final long serialVersionUID = 3981198814239631004L;

	/**
	 * Parameters of the funcion
	 */
	private double a, b;


	public LinearConcreteConcept(String name, double k1, double k2, double a, double b) throws FuzzyOntologyException
	{
		super(name);
		if (k1 > a)
			Util.error("Error: Linear functions require " + k1 + " <= " + a);
		if (b > 1)
			Util.error("Error: Linear functions require " + b + " <= " + 1);

		this.k1 = k1;
		this.k2 = k2;
		this.a = a;
		this.b = b;
	}


	public LinearConcreteConcept(String name, int type, double k1, double k2, double a, double b) throws FuzzyOntologyException
	{
		this(name,k1,k2,a,b);
		setType(type);
	}


	@Override
	public Concept complement() throws FuzzyOntologyException
	{
		if(getType() == Concept.CONCRETE)
			return new LinearConcreteConcept(this.getName(), Concept.CONCRETE_COMPLEMENT, k1, k2, a , b);
		else // if(getType() == Concept.CONCRETE_COMPLEMENT)
			return new LinearConcreteConcept(this.getName(), Concept.CONCRETE, k1, k2, a , b);
	}


	@Override
	public void solveAssertion(CreatedIndividual ind, Degree lowerLimit, KnowledgeBase kb)
	{
		Variable xAisC = kb.milp.getVariable(ind);
		Variable xAss = kb.milp.getVariable(ind, this);
		addEquation(xAisC, xAss, kb);
	}
	
	
	public void addEquation(Variable xAisC, Variable xAss, KnowledgeBase kb)
	{
		Variable y = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);

		//if y=0:		xc <= a,		b xc  - (a - k1) xass  = b k1
		//if y=1:		xc >= a,		(1 - b) xc -  (k2 - a) xass =  a - b k2 
		
		// xc + (a - k2) y <= a
		kb.milp.addNewConstraint(new Expression(new Term(1,xAisC), new Term(a-k2,y)), Inequation.LE, a);

		// xc + (k1-a)y  >= k1
		kb.milp.addNewConstraint(new Expression(new Term(1,xAisC), new Term(k1-a,y)), Inequation.GE, k1);

		// b xc  - (a - k1) xass + (a - k1) y >=  b k1
		kb.milp.addNewConstraint(new Expression(new Term(k1-a,xAss), new Term(a - k1,y), new Term(b,xAisC)), Inequation.GE, b*k1);

		// b xc  - (a - k1) xass - b (k2 - k1) y <=  b k1
		kb.milp.addNewConstraint(new Expression(new Term(k1-a,xAss), new Term(b*(k1-k2),y), new Term(b,xAisC)), Inequation.LE, b*k1);

		// (1-b) xc - (k2 - a) xass - (1-b)(k2 - k1) y >=  a - k2 - k1 b + k1
		kb.milp.addNewConstraint(new Expression(new Term(a-k2,xAss), new Term((1-b)*(k1-k2),y), new Term(1-b,xAisC)), Inequation.GE, a - k2 - k1 * b + k1);

		// (1-b) xc - (k2 - a) xass  - (a - k2) y  <= k2 - b k2
		kb.milp.addNewConstraint(new Expression(new Term(a-k2,xAss), new Term(k2-a,y), new Term(1-b,xAisC)), Inequation.LE, k2 - b * k2);
	}


	@Override
	public double getMembershipDegree(double x)
	{
		if (x <= 0)
			return 0;
		else if (x >= 1)
			return 1;
		else if (x <= a)
			return (b / a) * x;
		else // (x > a)
			return (x * (1 - b) + (b - a) ) / (1 - a);
	}


	@Override
	public String getName()
	{
		if(this.getType() == Concept.CONCRETE)
			return "linear(" + k1 + ", " + k2 + ", " + a + ", " + b + ")";
		else
			return "(not linear(" + k1 + ", " + k2 + ", " + a + ", " + b + ") )";
	}

}
