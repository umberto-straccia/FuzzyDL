package fuzzydl.milp;

import fuzzydl.*;

import java.io.*;
import java.util.*;

/**
 * Manages variables shown to the user.
 * @author Fernando Bobillo
 */
public class ShowVariablesHelper implements Serializable
{
	private static final long serialVersionUID = -5694328943294536914L;

	// Show the instances of these concepts
	private HashSet<String> concepts;

	// Show the membership degree of these individual to every atomic concept
	private HashSet<String> individuals;

	// Show these concreteFillers, for every individual
	private HashSet<String> globalAbstractFillers;

	// Show these concreteFillers, for every individual
	private HashSet<String> globalConcreteFillers;

	// For every filler, the list of individuals for which the filler has to be shown
	private Hashtable<String, HashSet<String>> abstractFillers;

	// For every filler, the list of individuals for which the filler has to be shown
	private Hashtable<String, HashSet<String>> concreteFillers;

	// Show these variables
	private Hashtable<Variable, String> variables;

	// For every concrete filler, show the membership degree to these fuzzy concrete concepts
	private Hashtable<String, ArrayList<FuzzyConcreteConcept>> labelsForFillers;


	public ShowVariablesHelper()
	{
		abstractFillers = new Hashtable<String, HashSet<String>>();
		concepts = new HashSet<String>();
		concreteFillers = new Hashtable<String, HashSet<String>>();
		globalAbstractFillers = new HashSet<String>();
		globalConcreteFillers = new HashSet<String>();
		individuals = new HashSet<String>();
		labelsForFillers = new Hashtable<String, ArrayList<FuzzyConcreteConcept>>();
		variables = new Hashtable<Variable, String>();
	}


	/**
	 * Gets a copy of the object.
	 * @return A copy of the object.
	 */
	@Override
	public ShowVariablesHelper clone()
	{
		ShowVariablesHelper s = new ShowVariablesHelper();
		s.abstractFillers = new Hashtable<String, HashSet<String>>(abstractFillers);
		s.concepts = new HashSet<String>(concepts);
		s.concreteFillers = new Hashtable<String, HashSet<String>>(concreteFillers);
		s.globalAbstractFillers = new HashSet<String>(globalAbstractFillers);
		s.globalConcreteFillers = new HashSet<String>(globalConcreteFillers);
		s.individuals = new HashSet<String>(individuals);
		s.labelsForFillers = new Hashtable<String, ArrayList<FuzzyConcreteConcept>>(labelsForFillers);
		s.variables = new Hashtable<Variable, String>(variables);

		return s;
	}


	/**
	 * Gets the name of a variable.
	 * @param var Variable.
	 * @return Name of the variable.
	 */
	public String getName(Variable var)
	{
		return variables.get(var);
	}


	/**
	 * Shows the value of a variable.
	 * @param var Variable.
	 * @return Return whether the variable exists or not.
	 */
	public boolean showVariable(Variable var)
	{
		return variables.containsKey(var);
	}


	/**
	 * Shows the value of an individual to every atomic concept.
	 * @param indName Name of the individual.
	 */
	public void addIndividualToShow(String indName)
	{
		individuals.add(indName);
	}


	/**
	 * Gets whether an individual is marked to be shown or not.
	 * @param indName Name of the individual.
	 * @return true if the individual is marked to be shown; false otherwise.
	 */
	public boolean showIndividuals(String indName)
	{
		return individuals.contains(indName);
	}


	/**
	 * Shows the value of the fillers of a concrete feature.
	 * @param fName Name of the concrete feature.
	 */
	public void addConcreteFillerToShow(String fName)
	{
		globalConcreteFillers.add(fName);
		if (concreteFillers.contains(fName))
			concreteFillers.remove(fName);
	}


	/**
	 * Shows the value of the fillers of a concrete feature for an individual.
	 * @param fName Name of the concrete feature.
	 * @param indName Name of the individual.
	 */
	public void addConcreteFillerToShow(String fName, String indName)
	{
		if (! globalConcreteFillers.contains(fName))
		{
			HashSet<String> hs = concreteFillers.get(fName);
			if (hs == null)
				hs = new HashSet<String>();
			hs.add(indName);
			concreteFillers.put(fName, hs);
		}
	}


	/**
	 * Shows the membership degree to some fuzzy concrete concepts (representing
	 * linguistic labels of the feature), for the fillers of a concrete feature 
	 * of an individual.
	 * @param fName Name of the concrete feature.
	 * @param indName Name of the individual.
	 * @param ar Array of fuzzy concrete concepts.
	 */
	public void addConcreteFillerToShow(String fName, String indName, ArrayList<FuzzyConcreteConcept> ar)
	{
		if (! globalConcreteFillers.contains(fName))
		{
			HashSet<String> hs = concreteFillers.get(fName);
			if (hs == null)
				hs = new HashSet<String>();
			hs.add(indName);
			concreteFillers.put(fName, hs);
		}
		
		// Add labels to be shown
		String name = fName + "(" + indName + ")";
		ArrayList<FuzzyConcreteConcept> aux = getLabels(name);
		if (aux.size() > 0)
		{
			aux.addAll(ar);
			labelsForFillers.put(name, aux);
		}
		else
			labelsForFillers.put(name, ar);
	}


	/**
	 * Gets the fuzzy concrete concepts marked to be shown for a variable.
	 * @param varName Name of the variable, e.g. feature1(ind1).
	 * @return Array (possibly empty) f fuzzy concrete concepts.
	 */
	public ArrayList<FuzzyConcreteConcept> getLabels(String varName)
	{
		 ArrayList<FuzzyConcreteConcept> ar = labelsForFillers.get(varName);
		 return (ar != null) ? ar : new ArrayList<FuzzyConcreteConcept>();
	}


	/**
	 * Shows the membership degree to some atomic concepts of the fillers of an
	 * abstract role.
	 * @param roleName Name of the abstract role.
	 */
	public void addAbstractFillerToShow(String roleName)
	{
		globalAbstractFillers.add(roleName);
		if (abstractFillers.contains(roleName))
			abstractFillers.remove(roleName);
	}


	/**
	 * Shows the membership degree to some atomic concepts of the fillers of an 
	 * abstract role for some individual.
	 * @param roleName Name of the abstract role.
	 * @param indName Name of the individual.
	 */
	public void addAbstractFillerToShow(String roleName, String indName)
	{
		if (! globalAbstractFillers.contains(roleName))
		{
			HashSet<String> h = abstractFillers.get(roleName);
			if (h == null)
				h = new HashSet<String>();
			h.add(indName);
			abstractFillers.put(roleName, h);
		}
	}


	/**
	 * Returns whether a given individuals is marked for showing every filler of
	 * a concrete feature.
	 * @param fName Name of the concrete feature.
	 * @param indName Name of the individual.
	 * @return Whether the individuals is marked for showing every filler of fName.
	 */
	public boolean showConcreteFillers(String fName, String indName)
	{
		if (!globalConcreteFillers.contains(fName))
		{
			HashSet<String> hs = concreteFillers.get(fName);
			if (hs == null)
				return false;
			return hs.contains(indName);
		}
		return true;
	}


	/**
	 * Returns whether a given individuals is marked for showing every filler of
	 * an abstract role.
	 * @param roleName Name of the abstract role.
	 * @param indName Name of the individual.
	 * @return Whether the individuals is marked for showing every filler of roleName.
	 */
	public boolean showAbstractRoleFillers(String roleName, String indName)
	{
		if (!globalAbstractFillers.contains(roleName))
		{
			HashSet<String> hs = abstractFillers.get(roleName);
			if (hs == null)
				return false;
			return hs.contains(indName);
		}
		return true;
	}


	/**
	 * Show membership degree of every instance of an atomic concept.
	 * @param concName Name of atomic concept.
	 */
	public void addConceptToShow(String concName)
	{
		concepts.add(concName);
	}


	/**
	 * Returns whether an atomic concept is marked to show the membership degree of every individual.
	 * @param conceptName Name of atomic concept.
	 * @return true if the concept is marked to be shown; false otherwise.
	 */
	public boolean showConcepts(String conceptName)
	{
		return concepts.contains(conceptName);
	}


	/**
	 * Add a variable to shown, showing it with a given name.
	 * @param var Variable.
	 * @param nameToShow Name of the variable when shown.
	 */
	public void addVariable(Variable var, String nameToShow)
	{
		variables.put(var, nameToShow);
	}


	/**
	 * Gets the variables to be shown.
	 * @return Variables to be shown.
	 */
	public Collection<Variable> getVariables()
	{
		return variables.keySet();
	}

}
