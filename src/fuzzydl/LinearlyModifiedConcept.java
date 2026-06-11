package fuzzydl;

import fuzzydl.exception.FuzzyOntologyException;

/**
 * Fuzzy concept modified with a linear modifier.
 * @author Fernando Bobillo
 */
public class LinearlyModifiedConcept extends ModifiedConcept
{
	private static final long serialVersionUID = -8810894227008154332L;


	public LinearlyModifiedConcept(Concept c, Modifier mod)
	{
		super(c, mod);
	}


	@Override
	public Concept complement()
	{
		Concept aux = new LinearlyModifiedConcept(c1, mod);
		if(getType() == MODIFIED)
			aux.setType(MODIFIED_COMPLEMENT);
		return aux;
	}


	@Override
	public Concept replace(Concept a, Concept c) throws FuzzyOntologyException
	{
		Concept aux = new LinearlyModifiedConcept(c1.replace(a, c), mod);
		if(getType() == MODIFIED)
			aux.setType(MODIFIED_COMPLEMENT);
		return aux;
	}
}
