
package fuzzydl;

import java.io.*;

/**
 * Role domain axiom
 * @author Fernando Bobillo
 */
public class DomainAxiom implements Serializable
{
	private static final long serialVersionUID = -4987223011773740879L;

	String role;
	Concept concept;


	public DomainAxiom(String role, Concept concept)
	{
		this.role = role;
		this.concept = concept;
	}

}
