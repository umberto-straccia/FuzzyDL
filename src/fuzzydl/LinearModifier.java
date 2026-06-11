package fuzzydl;

import fuzzydl.milp.*;

/**
 * Linear modifier.
 * @author Fernando Bobillo
 */
public class LinearModifier extends Modifier
{
	private static final long serialVersionUID = -8089946406815836130L;

	/**
	 * Parameter c.
	 */
	private double c;

	private double a;
	private double b;


	public LinearModifier(String name, double c)
	{
		super(name);
		this.c = c;
		a = c / (c + 1);
		b = 1 / (c + 1);
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
		return new LinearlyModifiedConcept(c, this);
	}


	@Override
	public String getName()
	{
		return "linear-modifier(" + c + ")";
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
	public void solveAssertion(Individual ind, Concept con, Degree lowerLimit, KnowledgeBase kb)
	{
		Concept modified;
		Variable xAisC, xAisModC;
		if (con instanceof ModifiedConcreteConcept)
		{
			modified = ((ModifiedConcreteConcept) con).getModified();
			xAisC = kb.milp.getVariable(ind, modified);
			kb.addAssertion(ind, modified, Degree.getDegree(xAisC));
			xAisModC = kb.milp.getVariable(ind, con);
		}
		else
		{
			xAisC = kb.milp.getVariable(ind, con);
			kb.addAssertion(ind, con, Degree.getDegree(xAisC));
			modified = new TriangularlyModifiedConcept(con, this);
			xAisModC = kb.milp.getVariable(ind, modified);
		}

		Variable y = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);

		// If y = 0, xAisC <= a, xAisC = a/b xAisModC
		kb.milp.addNewConstraint(new Expression(new Term(1,xAisC), new Term(-1,y)), Inequation.LE, a);
		kb.milp.addNewConstraint(new Expression(new Term(-a/b,xAisModC), new Term(1,xAisC), new Term(a/b,y)), Inequation.GE);
		kb.milp.addNewConstraint(new Expression(new Term(-a/b,xAisModC), new Term(1,xAisC), new Term(-1,y)), Inequation.LE);

		// If y = 1, xAisC >= a, (1-b) xAisC = (1-a) xAisModC + (a-b)
		kb.milp.addNewConstraint(new Expression(new Term(1,xAisC), new Term(-a,y)), Inequation.GE);
		kb.milp.addNewConstraint(new Expression(new Term(a-1,xAisModC), new Term(1-b,xAisC), new Term(b-a+2,y)), Inequation.LE, 2);
		kb.milp.addNewConstraint(new Expression(new Term(a-1,xAisModC), new Term(1-b,xAisC), new Term(b-a-2,y)), Inequation.GE, -2);			
	}

}
