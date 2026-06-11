package fuzzydl;


/**
 * Fuzzy logic.
 * @author Fernando Bobillo
 */
public enum FuzzyLogic
{

	/**
	 * Classical logic
	 */
	CLASSICAL,


	/**
	 * Zadeh fuzzy logic
	 */
	ZADEH,


	/**
	 * Lukasiewicz fuzzy logic
	 */
	LUKASIEWICZ;


	/**
	 * Gets the name of the object.
	 * @return Name of the object.
	 */
	@Override
	public String toString()
	{
		switch (this)
		{
			case CLASSICAL:
				return "classical";

			case ZADEH:
				return "zadeh";

			default: // case LUKASIEWICZ:
				return "lukasiewicz";
		}
	}
}
