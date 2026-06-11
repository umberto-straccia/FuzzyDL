package fuzzydl;

import java.io.*;

/**
 * Concept equivalence axiom.
 * @author Fernando Bobillo
 */
public class ConceptEquivalence implements Serializable
{
	private static final long serialVersionUID = 7527421216028797039L;

	// First concept
	private Concept c1;

	// Second concept
	private Concept c2;


	public ConceptEquivalence(Concept c1, Concept c2)
	{
		this.c1 = c1;
		this.c2 = c2;
	}


	/**
	 * Gets the first concept.
	 * @return first concept.
	 */
	public Concept getC1()
	{
		return c1;
	}


	/**
	 * Gets the second concept.
	 * @return second concept.
	 */
	public Concept getC2()
	{
		return c2;
	}

}
