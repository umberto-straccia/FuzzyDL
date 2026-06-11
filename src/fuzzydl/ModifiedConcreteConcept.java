package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.Variable;

/**
 * Modified concrete concept.
 * @author Fernando Bobillo
 */
public class ModifiedConcreteConcept extends FuzzyConcreteConcept
{
	private static final long serialVersionUID = 2561023859009842296L;
	
	/**
	 * Parameters of the funcion
	 */
	private Modifier mod;
	private FuzzyConcreteConcept modified;


	public ModifiedConcreteConcept(String name, Modifier modifier, FuzzyConcreteConcept f)  throws FuzzyOntologyException
	{
		super(name);
		k1 = 0;
		k2 = 1;
		mod = modifier;
		modified = f;
	}


	public ModifiedConcreteConcept(String name, int type, Modifier modifier, FuzzyConcreteConcept f)  throws FuzzyOntologyException
	{
		this(name, modifier, f);
		this.setType(type);
	}


	/**
	 * Gets the concept that is being modified.
	 * @return Concept that is being modified.
	 */
	public FuzzyConcreteConcept getModified()
	{
		return modified;
	}


	/**
	 * Gets the modifier.
	 * @return Modifier.
	 */
	public Modifier getModifier()
	{
		return mod;
	}


	@Override
	public Concept complement() throws FuzzyOntologyException
	{
		if(this.getType() == Concept.CONCRETE)
			return new ModifiedConcreteConcept(this.getName(), Concept.CONCRETE_COMPLEMENT, mod, modified);
		else // if(this.getType() == Concept.CONCRETE_COMPLEMENT)
			return new ModifiedConcreteConcept(this.getName(), Concept.CONCRETE, mod, modified);
	}


	@Override
	public void solveAssertion(CreatedIndividual ind, Degree lowerLimit, KnowledgeBase kb)
	{
		mod.solveAssertion(ind, this, lowerLimit, kb);
	}


	public void addEquation(Variable xC, Variable xAss, KnowledgeBase kb)
	{
		System.out.println("Method currently not supported fpr modified concepts");
	}


/*
	@Override
	public void solveComplementAssertion(CreatedIndividual ind, Degree lowerLimit, KnowledgeBase kb)
	{
		mod.solveComplementAssertion(ind, modified, lowerLimit, kb);
	}
*/

	@Override
	public double getMembershipDegree(double x)
	{
		if ((x <= 0) || (x > 1))
			return 0;
		else
		{
			double y = modified.getMembershipDegree(x);
			return mod.getMembershipDegree(y);
		}
	}

	
	@Override
	public String getName()
	{
		return "modified(" + mod + " " + modified + ")";
	}


	@Override
	public String toString()
	{
		if (type == CONCRETE)
			return name;
		else // getType(= == CONCRETE_COMPLEMENT)
			return "(not " + name + ")";
	}
}
