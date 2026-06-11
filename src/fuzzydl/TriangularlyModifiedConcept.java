package fuzzydl;

import fuzzydl.exception.FuzzyOntologyException;

/**
 * Fuzzy concept modified with a triangular modifier.
 * @author Fernando Bobillo
 */
public class TriangularlyModifiedConcept extends ModifiedConcept
{
	private static final long serialVersionUID = 1431239053489139518L;


	public TriangularlyModifiedConcept(Concept c, Modifier mod)
	{
		super(c, mod);
	}


	@Override
	public Concept complement()
	{
		Concept aux = new TriangularlyModifiedConcept(c1, mod);
		if(getType() == MODIFIED)
			aux.setType(MODIFIED_COMPLEMENT);
		return aux;
	}


	@Override
	public Concept replace(Concept a, Concept c) throws FuzzyOntologyException
	{
		Concept aux = new TriangularlyModifiedConcept(c1.replace(a, c), mod);
		if(getType() == MODIFIED)
			aux.setType(MODIFIED_COMPLEMENT);
		return aux;
	}
}
