
/**
 * Encapsulates an error during the execution of fuzzyDL.
 * @author Fernando Bobillo
 */
package fuzzydl.exception;


public class InconsistentOntologyException extends Exception {

	private static final long serialVersionUID = -1L;

	public InconsistentOntologyException(String message)
	{
		super(message);
	}

}
