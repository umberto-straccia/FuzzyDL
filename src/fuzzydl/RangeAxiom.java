
package fuzzydl;

import java.io.*;

/**
 * Role range axiom
 * @author Fernando Bobillo
 */
public class RangeAxiom implements Serializable
{
	private static final long serialVersionUID = -5774232666271999124L;
	
	String role;
	Concept concept;


	public RangeAxiom(String role, Concept concept)
	{
		this.role = role;
		this.concept = concept;
	}

}
