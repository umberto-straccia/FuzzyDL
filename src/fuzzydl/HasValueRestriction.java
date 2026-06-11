package fuzzydl;

/**
 * Universal restriction formed by a role, a individual and a lower bound degree.
 * @author Fernando Bobillo
 */
public class HasValueRestriction extends Restriction
{
	private static final long serialVersionUID = -8041864206722550052L;
	
	private String indName;


	public HasValueRestriction(String roleName, String individual, Degree degree)
	{
		super(roleName, null, degree);
		this.indName = individual;
	}
	

	/**
	 * Gets the individual.
	 * @return Individual.
	 */
	public String getIndividual()
	{
		return indName;
	}


	/**
	 * Gets the name of the restriction without the degree.
	 * @return Name of the restriction without the degree.
	 */
	public String getNameWithoutDegree()
	{
		return "(not (b-some " + roleName + " " + indName + ") )";
	}

}
