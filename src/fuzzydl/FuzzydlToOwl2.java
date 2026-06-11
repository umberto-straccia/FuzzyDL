package fuzzydl;

import java.io.*;
import java.util.*;

import fuzzydl.parser.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.*;


/**
 * This version of FuzzydlToOwl2 requires OWL API 3-4.
 */
public class FuzzydlToOwl2
{

	private OWLAnnotationProperty annProp;
	private Hashtable<String, OWLClassExpression> concepts;
	private Hashtable<String, OWLDatatype> datatypes;
	private OWLDataFactory df;
	private KnowledgeBase kb;
	private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	private Hashtable<String, OWLDatatype> modifiers;
	private int numClasses;
	private OWLOntology ontology;
	private String ontologyPath;
	private IRI ontologyIRI;
	String outputFile;


	/**
	 * Main method.
	 * @param args An input file with a fuzzyDL ontology and output file with a OWL 2 ontology.
	 */
	public static void main(String[] args)
	{
		if (args.length != 2)
		{
			System.err.println("Error. Use: java FuzzydlToOwl2 <fuzzyDLOntology> <Owl2Ontology>");
			System.exit(-1);
		}

		FuzzydlToOwl2 f = new FuzzydlToOwl2(args[0], args[1]);
		if (f.kb != null)
			f.run();			
	}

	
	/**
	 * Constructor. 
	 * @param inputFile Input file with a fuzzyDL ontology.
	 * @param outputFile Output file with a OWL 2 ontology.
	 */
	public FuzzydlToOwl2(String inputFile, String outputFile)
	{
		kb = Parser.getKB(inputFile);
		manager = OWLManager.createOWLOntologyManager();
		df = manager.getOWLDataFactory();
		concepts = new Hashtable<String, OWLClassExpression> ();
		datatypes = new Hashtable<String, OWLDatatype> ();
		modifiers = new Hashtable<String, OWLDatatype> ();

		ontologyPath = "http://www.semanticweb.org/ontologies/myOntologies.owl";
		ontologyIRI = IRI.create(outputFile);
		this.outputFile = outputFile;

        try
        {
            ontology = manager.createOntology(ontologyIRI);
        	annProp = df.getOWLAnnotationProperty(IRI.create(ontologyPath + "#" + "fuzzyLabel"));
        }
		catch (Exception ex)
		{
			System.err.println(ex);
		}
	}

	
	private void addOntologyAnnotation(String annotation)
	{
		OWLAnnotation commentAnno = df.getOWLAnnotation(
				annProp,
				df.getOWLLiteral(annotation, df.getOWLDatatype(IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral")))
			);
		/*List<OWLOntologyChange> changes=new ArrayList<OWLOntologyChange>();
		changes.add(new AddOntologyAnnotation(ontology, commentAnno));
		manager.applyChanges(changes);*/
		AddOntologyAnnotation ann = new AddOntologyAnnotation(ontology, commentAnno);
		manager.applyChange(ann);
	}


	private IRI iri(Object o)
	{
		return IRI.create(ontologyPath + "#" + o.toString());
	}


	private OWLClassExpression getClass(String name)
	{
		return df.getOWLClass(iri(name));
	}

	
	private OWLClass getNewAtomicClass(String name)
	{
		OWLClassExpression c = concepts.get(name);		
		if (c != null)
			return c.asOWLClass();
			
		numClasses++;
		OWLClass c2 = df.getOWLClass(iri("class__" + numClasses));
		concepts.put(name, c2);	
		return c2;
	}

	
	private OWLObjectProperty getObjectProperty(String role)
	{
		return df.getOWLObjectProperty(iri(role));
	}

	
	private OWLDataProperty getDataProperty(String role)
	{
		return df.getOWLDataProperty(iri(role));
	}


	private OWLNamedIndividual getIndividual(String ind)
	{
		return df.getOWLNamedIndividual(iri(ind));
	}


	private OWLClassExpression getClass(Concept c)
	{
		
		switch(c.getType())
		{
			case Concept.ATOMIC:
			case Concept.CONCRETE:
				return getClass(c.toString());

			case Concept.TOP:
				return df.getOWLThing();

			case Concept.BOTTOM:
				return df.getOWLNothing();

			case Concept.COMPLEMENT:
			case Concept.CONCRETE_COMPLEMENT:
			case Concept.NOT_SELF:
			case Concept.NOT_HAS_VALUE:
			case Concept.MODIFIED_COMPLEMENT:
			case Concept.NOT_AT_MOST_VALUE:
			case Concept.NOT_AT_LEAST_VALUE:
			case Concept.NOT_EXACT_VALUE:
			case Concept.NOT_WEIGHTED:
			case Concept.NOT_CHOQUET_INTEGRAL:
			case Concept.NOT_QUANTIFIED_OWA:
			case Concept.NOT_OWA:
			case Concept.NOT_W_SUM_ZERO:
			case Concept.NOT_W_SUM:
			case Concept.NOT_SUGENO_INTEGRAL:
			case Concept.NOT_QUASI_SUGENO_INTEGRAL:
			case Concept.NOT_W_MAX:
			case Concept.NOT_W_MIN:
				return df.getOWLObjectComplementOf(getClass(Concept.complement(c)));

			case Concept.AND:
			case Concept.G_AND:
			case Concept.L_AND:
				Set<OWLClassExpression> set = new HashSet<OWLClassExpression> ();
				for (Concept c1 : c.concepts)
					set.add(getClass(c1));
				return df.getOWLObjectIntersectionOf(set);

			case Concept.OR:
			case Concept.G_OR:
			case Concept.L_OR:
				set = new HashSet<OWLClassExpression> ();
				for (Concept c1 : c.concepts)
					set.add(getClass(c1));
				return df.getOWLObjectUnionOf(set);

			case Concept.SOME:
				if (c.c1.isConcrete() )
				{
					OWLDataProperty dp = getDataProperty(c.role);
					/*if (! datatypes.containsKey(c.c1.toString()))
					{
						ModifiedConcept mfc = (ModifiedConcept) c.c1;
						String mod = mfc.mod.toString();
						String d = mfc.c1.name;
						OWLDatatype owlDatatype = df.getOWLDatatype(iri(mod + d));
						datatypes.put(c.c1.toString(), owlDatatype);					
						String specific = "modified\" modifier=\"" + mod + "\" base=\"" + d;			
						// Datatype and range
						OWLDatatype doubleDatatype = df.getDoubleOWLDatatype();
						OWLDataRange greaterThan = df.getOWLDatatypeRestriction(doubleDatatype, OWLFacet.MIN_INCLUSIVE, df.getOWLLiteral(0.0) );
						OWLDataRange lessThan = df.getOWLDatatypeRestriction(doubleDatatype, OWLFacet.MAX_INCLUSIVE, df.getOWLLiteral(1.0) );
						OWLDataIntersectionOf unitInterval = df.getOWLDataIntersectionOf(greaterThan, lessThan);
						OWLAxiom definition = df.getOWLDatatypeDefinitionAxiom(owlDatatype, unitInterval);
						manager.applyChange(new AddAxiom(ontology, definition));
	
						// Annotation
						String annotation = "<fuzzyOwl2 fuzzyType=\"datatype\">\n<Datatype type=\"" + specific + "\" />\n</fuzzyOwl2>";
						addEntityAnnotation(annotation, owlDatatype);
					}*/
					OWLDatatype d = datatypes.get(c.c1.toString());
					return df.getOWLDataSomeValuesFrom(dp, d);	
				}
				else
				{
					OWLObjectProperty op = getObjectProperty(c.role);
					OWLClassExpression c2 = getClass(c.c1);
					return df.getOWLObjectSomeValuesFrom(op, c2);					
				}

			case Concept.ALL:
				if (c.c1.isConcrete() ) //datatypes.containsKey(c.c1.toString()) )
				{
					OWLDataProperty dp = getDataProperty(c.role);
/*					if (! datatypes.containsKey(c.c1.toString()))
					{
						ModifiedConcept mfc = (ModifiedConcept) c.c1;
						String mod = mfc.mod.toString();
						String d = mfc.c1.name;
						OWLDatatype owlDatatype = df.getOWLDatatype(iri(mod + d));
						datatypes.put(c.c1.toString(), owlDatatype);					
						String specific = "modified\" modifier=\"" + mod + "\" base=\"" + d;			
						// Datatype and range
						OWLDatatype doubleDatatype = df.getDoubleOWLDatatype();
						OWLDataRange greaterThan = df.getOWLDatatypeRestriction(doubleDatatype, OWLFacet.MIN_INCLUSIVE, df.getOWLLiteral(0.0) );
						OWLDataRange lessThan = df.getOWLDatatypeRestriction(doubleDatatype, OWLFacet.MAX_INCLUSIVE, df.getOWLLiteral(1.0) );
						OWLDataIntersectionOf unitInterval = df.getOWLDataIntersectionOf(greaterThan, lessThan);
						OWLAxiom definition = df.getOWLDatatypeDefinitionAxiom(owlDatatype, unitInterval);
						manager.applyChange(new AddAxiom(ontology, definition));
	
						// Annotation
						String annotation = "<fuzzyOwl2 fuzzyType=\"datatype\">\n<Datatype type=\"" + specific + "\" />\n</fuzzyOwl2>";
						addEntityAnnotation(annotation, owlDatatype);
					}*/
					OWLDatatype d = datatypes.get(c.c1.toString());
					return df.getOWLDataAllValuesFrom(dp, d);	
				}
				else
				{
					OWLObjectProperty op = getObjectProperty(c.role);
					OWLClassExpression c2 = getClass(c.c1);
					return df.getOWLObjectAllValuesFrom(op, c2);					
				}

			case Concept.MODIFIED:
				if (concepts.contains(c.toString()))
					return concepts.get(c.toString());
				OWLClass c4 = getNewAtomicClass(c.toString()).asOWLClass();
				OWLClass c3 = getBase(c.c1);
				concepts.put(c.toString(), c3);
				String mod = ((ModifiedConcept) c).mod.toString();
				String annotation = 	"<fuzzyOwl2 fuzzyType=\"concept\">\n" +
						"<Concept type=\"modified\" modifier=\"" + modifiers.get(mod) + 
						"\" base=\"" +  c3 + 
						"\" />\n </fuzzyOwl2>";			
				addEntityAnnotation(annotation, c4);
				return c4;

			case Concept.SELF:
				OWLObjectProperty op = getObjectProperty(c.role);
				return df.getOWLObjectHasSelf(op);

			case Concept.HAS_VALUE:
				op = getObjectProperty(c.role);
				OWLNamedIndividual ind = getIndividual((String) c.value);
				return df.getOWLObjectHasValue(op, ind);

			case Concept.AT_MOST_VALUE:
			case Concept.AT_LEAST_VALUE:

				OWLDatatype datatype;
				OWLLiteral literal;
				if (c.value instanceof Integer)
				{
					datatype = df.getIntegerOWLDatatype();
					literal = df.getOWLLiteral((Integer) c.value);
				}
				else if (c.value instanceof String)
				{
					datatype = df.getRDFPlainLiteral();
					literal = df.getOWLLiteral((String) c.value);
				}
				else
				{
					datatype = df.getDoubleOWLDatatype();
					literal = df.getOWLLiteral((Double) c.value);
				}
				
				OWLFacet facet;
				OWLDataRange dataRange;
				if (c.getType() == Concept.AT_MOST_VALUE)
					facet = OWLFacet.MIN_INCLUSIVE;
				else //if (c.getType() == Concept.AT_LEAST_VALUE)
					facet = OWLFacet.MAX_INCLUSIVE;

				dataRange = df.getOWLDatatypeRestriction(datatype, facet, literal);
			 	return df.getOWLDataSomeValuesFrom(getDataProperty(c.role), dataRange);

			case Concept.EXACT_VALUE:
				if (c.value instanceof Integer)
					literal = df.getOWLLiteral((Integer) c.value);
				else if (c.value instanceof String)
					literal = df.getOWLLiteral((String) c.value);
				else
					literal = df.getOWLLiteral((Double) c.value);
			 	return df.getOWLDataHasValue(getDataProperty(c.role), literal);


			case Concept.WEIGHTED:
				c4 = getNewAtomicClass(c.toString()).asOWLClass();
				c3 = getBase(c.c1);
				annotation = "<fuzzyOwl2 fuzzyType=\"concept\">\n<Concept type=\"weighted\" value=\"" + 
						c.weight + "\" base=\"" + c3 + "\" />\n </fuzzyOwl2>";
				addEntityAnnotation(annotation, c3);
				return c4;


			case Concept.W_SUM:
				WeightedSumConcept wsum = (WeightedSumConcept) c;	
				c3 = getNewAtomicClass(c.toString());
				c4 = getNewAtomicClass(c.toString()).asOWLClass();
//annotation = "<fuzzyOwl2 fuzzyType=\"concept\">\n<Concept type=\"" + "weightedSum" + "\" value=\"" + "\">\n ";
				annotation = "<fuzzyOwl2 fuzzyType=\"concept\">\n<Concept type=\"" + "weightedSum"  + "\">\n ";
				int n = wsum.concepts.size();
				for(int i=0; i<n; i++)
				{
					OWLClass c5 = getBase(wsum.concepts.get(i));
					annotation += "<Concept type=\"weighted\" value=\"" + wsum.weights.get(i) + "\" base=\"" + c5 + "\" />\n";
				}
				annotation +=  "</Concept>\n</fuzzyOwl2>";
				addEntityAnnotation(annotation, c3);
				return c4;

			case Concept.W_MAX:
				WeightedMaxConcept wmax = (WeightedMaxConcept) c;	
				c3 = getNewAtomicClass(c.toString());
				c4 = getNewAtomicClass(c.toString()).asOWLClass();
				annotation = "<fuzzyOwl2 fuzzyType=\"concept\">\n<Concept type=\"" + "weightedMaximum" + "\" value=\"" + "\">\n ";
				n = wmax.concepts.size();
				for(int i=0; i<n; i++)
				{
					OWLClass c5 = getBase(wmax.concepts.get(i));
					annotation += "<Concept type=\"weighted\" value=\"" + wmax.weights.get(i) + "\" base=\"" + c5 + "\" />\n";
				}
				annotation +=  "</Concept>\n</fuzzyOwl2>";
				addEntityAnnotation(annotation, c3);
				return c4;

			case Concept.W_MIN:
				WeightedMinConcept wmin = (WeightedMinConcept) c;	
				c3 = getNewAtomicClass(c.toString());
				c4 = getNewAtomicClass(c.toString()).asOWLClass();
				annotation = "<fuzzyOwl2 fuzzyType=\"concept\">\n<Concept type=\"" + "weightedMinimum" + "\" value=\"" + "\">\n ";
				n = wmin.concepts.size();
				for(int i=0; i<n; i++)
				{
					OWLClass c5 = getBase(wmin.concepts.get(i));
					annotation += "<Concept type=\"weighted\" value=\"" + wmin.weights.get(i) + "\" base=\"" + c5 + "\" />\n";
				}
				annotation +=  "</Concept>\n</fuzzyOwl2>";
				addEntityAnnotation(annotation, c3);
				return c4;


			case Concept.OWA:
				OwaConcept owa = (OwaConcept) c;
				c4 = getNewAtomicClass(c.toString());
				annotation = "<fuzzyOwl2 fuzzyType=\"concept\">\n<Concept type=\"owa\" >\n ";
				annotation +="<Weights>\n";
				for (Double d: owa.weights) 
					annotation += "<Weight>" + d + "</Weight>\n";
				annotation += "</Weights>\n";
				annotation += "<Names>\n";		
				for (Concept ci : owa.concepts)
				{
					OWLClass c5 = getBase(ci);
					annotation += "<Name>" + c5 + "</Name>\n";
				}
				annotation +="</Names>\n";
				annotation += "</Concept>\n</fuzzyOwl2>";
				return c4;

			case Concept.QUANTIFIED_OWA:
				QowaConcept qowa = (QowaConcept) c;
				c4 = getNewAtomicClass(c.toString());
				annotation = "<fuzzyOwl2 fuzzyType=\"concept\">\n<Concept type=\"qowa\" quantifier=\"" + qowa.quantifier.toString() + "\" >\n ";
				annotation += "<Names>\n";
				for (Concept ci : qowa.concepts)
				{
					OWLClass c5 = getBase(ci);
					annotation += "<Name>" + c5 + "</Name>\n";
				}
				annotation +="</Names>\n";
				annotation += "</Concept>\n</fuzzyOwl2>";
				return c4;

			case Concept.CHOQUET_INTEGRAL:
				ChoquetIntegral choquet = (ChoquetIntegral) c;
				c4 = getNewAtomicClass(c.toString());
				annotation = "<fuzzyOwl2 fuzzyType=\"concept\">\n<Concept type=\"choquet\" >\n ";
				annotation +="<Weights>\n";
				for (Double d: choquet.weights) 
					annotation += "<Weight>" + d + "</Weight>\n";
				annotation += "</Weights>\n";
				annotation += "<Names>\n";		
				for (Concept ci : choquet.concepts)
				{
					OWLClass c5 = getBase(ci);
					annotation += "<Name>" + c5 + "</Name>\n";
				}
				annotation +="</Names>\n";
				annotation += "</Concept>\n</fuzzyOwl2>";
				return c4;

			case Concept.SUGENO_INTEGRAL:
				SugenoIntegral si = (SugenoIntegral) c;
				c4 = getNewAtomicClass(c.toString());
				annotation = "<fuzzyOwl2 fuzzyType=\"concept\">\n<Concept type=\"sugeno\" >\n ";
				annotation +="<Weights>\n";
				for (Double d: si.weights) 
					annotation += "<Weight>" + d + "</Weight>\n";
				annotation += "</Weights>\n";
				annotation += "<Names>\n";		
				for (Concept ci : si.concepts)
				{
					OWLClass c5 = getBase(ci);
					annotation += "<Name>" + c5 + "</Name>\n";
				}
				annotation +="</Names>\n";
				annotation += "</Concept>\n</fuzzyOwl2>";
				return c4;

			case Concept.QUASI_SUGENO_INTEGRAL:
				QsugenoIntegral qsi = (QsugenoIntegral) c;
				c4 = getNewAtomicClass(c.toString());
				annotation = "<fuzzyOwl2 fuzzyType=\"concept\">\n<Concept type=\"quasiSugeno\" >\n ";
				annotation +="<Weights>\n";
				for (Double d: qsi.weights) 
					annotation += "<Weight>" + d + "</Weight>\n";
				annotation += "</Weights>\n";
				annotation += "<Names>\n";		
				for (Concept ci : qsi.concepts)
				{
					OWLClass c5 = getBase(ci);
					annotation += "<Name>" + c5 + "</Name>\n";
				}
				annotation +="</Names>\n";
				annotation += "</Concept>\n</fuzzyOwl2>";
				return c4;


			/**
			 * To do
			 */
/*
			case Concept.POS_THRESHOLD:
			case Concept.NOT_POS_THRESHOLD:
			case Concept.NEG_THRESHOLD:
			case Concept.NOT_NEG_THRESHOLD:
			case Concept.EXT_POS_THRESHOLD:
			case Concept.NOT_EXT_POS_THRESHOLD:
			case Concept.EXT_NEG_THRESHOLD:
			case Concept.NOT_EXT_NEG_THRESHOLD:
			case Concept.G_IMPLIES:
			case Concept.NOT_G_IMPLIES:
			case Concept.UPPER_APPROX:
			case Concept.LOOSE_UPPER_APPROX:
			case Concept.TIGHT_UPPER_APPROX:
			case Concept.LOWER_APPROX:
			case Concept.LOOSE_LOWER_APPROX:
			case Concept.TIGHT_LOWER_APPROX:
			case Concept.FUZZY_NUMBER:
			case Concept.FUZZY_NUMBER_COMPLEMENT:
			case Concept.W_SUM_ZERO:
*/
		}
		
		return df.getOWLClass(iri(c.toString()));	
		
	}
	
	
	private OWLClass getBase(Concept c)
	{
		if (c.isAtomic())
			return getClass(c.toString()).asOWLClass();
		else
			return getNewAtomicClass(c.toString());
	}


	private void addEntityAnnotation(String annotation, OWLEntity c)
	{
		OWLAnnotation commentAnno = df.getOWLAnnotation(annProp, df.getOWLLiteral(annotation, df.getOWLDatatype(IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral"))));
		OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(c.getIRI(), commentAnno);
		manager.applyChange(new AddAxiom(ontology, ax));
	}


	private Set<OWLAnnotation> getAnnotationsForAxiom(Degree deg)
	{
		DegreeNumeric num = (DegreeNumeric) deg;
		double n = num.getNumericalValue();
		return getAnnotationsForAxiom(n);
	}


	private Set<OWLAnnotation> getAnnotationsForAxiom(double n)
	{
		String annotation = "<fuzzyOwl2 fuzzyType=\"axiom\">\n"
		+ "<Degree value=\"" + n + "\" />\n"
		+ "</fuzzyOwl2>";
		
		OWLAnnotation commentAnno = df.getOWLAnnotation(annProp, df.getOWLLiteral(annotation, df.getOWLDatatype(IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral")) ) );
		Set<OWLAnnotation> newAnn = new HashSet<OWLAnnotation>();
		newAnn.add(commentAnno);
		return newAnn;
	}


	/**
	 * Transforms the input file into an output ontology.
	 */
	public void run()
	{	
		// Fuzzy logic
		String logic = "";
		if (KnowledgeBase.semantics == FuzzyLogic.LUKASIEWICZ)
			logic = "lukasiewicz";
		else if (KnowledgeBase.semantics == FuzzyLogic.ZADEH)
			logic = "zadeh";

		if (logic != "")
		{
			String annotation = "<fuzzyOwl2 fuzzyType=\"ontology\">\n";
			annotation += "<FuzzyLogic logic=\"" + logic + "\" />\n";
			annotation += "</fuzzyOwl2>";
			addOntologyAnnotation(annotation);				
		}

		// Modifiers
		for (Modifier mod : kb.modifiers.values())
		{
			String annotation;
			if (mod instanceof LinearModifier)
			{
				LinearModifier lm = (LinearModifier) mod;
				annotation = "<fuzzyOwl2 fuzzyType=\"modifier\">\n" +
						"<Modifier type=\"linear\" c=\"" + lm.getC() + "\" />\n" +
						"</fuzzyOwl2>";					
			}
			else // if (mod instanceof TriangularModifier)
			{
				TriangularModifier lm = (TriangularModifier) mod;
				annotation = "<fuzzyOwl2 fuzzyType=\"modifier\">\n" +
						"<Modifier type=\"triangular\" a=\"" + lm.getA() + "\" b=\"" + 
						lm.getB() + "\" c=\"" + lm.getC() + "\" />\n" +
						"</fuzzyOwl2>";		
			}

			OWLDatatype owlDatatype = df.getOWLDatatype(iri(mod));
			OWLDatatype doubleDatatype = df.getDoubleOWLDatatype();
			OWLDataRange greaterThan = df.getOWLDatatypeRestriction(doubleDatatype, OWLFacet.MIN_INCLUSIVE, df.getOWLLiteral(0.0) );
			OWLDataRange lessThan = df.getOWLDatatypeRestriction(doubleDatatype, OWLFacet.MAX_INCLUSIVE, df.getOWLLiteral(1.0) );
			OWLDataIntersectionOf unitInterval = df.getOWLDataIntersectionOf(greaterThan, lessThan);
			OWLAxiom definition = df.getOWLDatatypeDefinitionAxiom(owlDatatype, unitInterval);
			manager.applyChange(new AddAxiom(ontology, definition));
			
			modifiers.put(mod.toString(), owlDatatype);
			addEntityAnnotation(annotation, owlDatatype);		
		}


		// Fuzzy concrete concepts
		for (Concept c : kb.concreteConcepts.values())
		{
			OWLDatatype owlDatatype = df.getOWLDatatype(iri(c));
			datatypes.put(c.toString(), owlDatatype);

			String specific = "";
			FuzzyConcreteConcept fc = (FuzzyConcreteConcept) c;

			if (c instanceof CrispConcreteConcept)
			{
				CrispConcreteConcept cfc = (CrispConcreteConcept) c;
				specific = "crisp" + "\" a=\"" + cfc.a + "\" b=\"" + cfc.b;
			}
			else if (c instanceof LeftConcreteConcept)
			{
				LeftConcreteConcept cfc = (LeftConcreteConcept) c;
				specific = "leftshoulder" + "\" a=\"" + cfc.a + "\" b=\"" + cfc.b;
			}
			else if (c instanceof RightConcreteConcept)
			{
				RightConcreteConcept cfc = (RightConcreteConcept) c;
				specific = "rightshoulder" + "\" a=\"" + cfc.a + "\" b=\"" + cfc.b;
			}
			else if (c instanceof TriangularConcreteConcept)
			{
				TriangularConcreteConcept cfc = (TriangularConcreteConcept) c;
				specific = "triangular" + "\" a=\"" + cfc.a + "\" b=\"" + cfc.b + "\" c=\"" + cfc.c;
				//specific = "triangular" + "\" a=\"" + cfc.a + "\" b=\"" + cfc.b + "\" c=\"" + cfc.c + "\"";
			}
			else if (c instanceof TrapezoidalConcreteConcept)
			{
				TrapezoidalConcreteConcept cfc = (TrapezoidalConcreteConcept) c;
				specific = "trapezoidal" + "\" a=\"" + cfc.a + "\" b=\"" + cfc.b + "\" c=\"" + cfc.c + "\" d=\"" + cfc.d;
				//specific = "trapezoidal" + "\" a=\"" + cfc.a + "\" b=\"" + cfc.b + "\" c=\"" + cfc.c + "\" d=\"" + cfc.d + "\"";
			}
			else if ((c instanceof LinearlyModifiedConcept) || (c instanceof TriangularlyModifiedConcept))
			{
				ModifiedConcreteConcept mfc = (ModifiedConcreteConcept) c;
				String d = mfc.getModified().toString();
				String mod = mfc.getModifier().toString();
				specific = "modified\" modifier=\"" + mod + "\" base=\"" + d;			
			}
			// Datatype and range
			OWLDatatype doubleDatatype = df.getDoubleOWLDatatype();
			OWLDataRange greaterThan = df.getOWLDatatypeRestriction(doubleDatatype, OWLFacet.MIN_INCLUSIVE, df.getOWLLiteral(fc.k1) );
			OWLDataRange lessThan = df.getOWLDatatypeRestriction(doubleDatatype, OWLFacet.MAX_INCLUSIVE, df.getOWLLiteral(fc.k2) );
			OWLDataIntersectionOf unitInterval = df.getOWLDataIntersectionOf(greaterThan, lessThan);
			OWLAxiom definition = df.getOWLDatatypeDefinitionAxiom(owlDatatype, unitInterval);
			manager.applyChange(new AddAxiom(ontology, definition));

			// Annotation
			String annotation = "<fuzzyOwl2 fuzzyType=\"datatype\">\n<Datatype type=\"" + specific + "\" />\n</fuzzyOwl2>";
			addEntityAnnotation(annotation, owlDatatype);
		}


		// ABox

		for(Assertion ass : kb.assertions)
		{
			// Axiom
			OWLNamedIndividual i = getIndividual(ass.getIndividual().toString());
			OWLClassExpression c = getClass(ass.getConcept());

			// Annotation
			Degree deg = ass.getLowerLimit();
			if (deg.isNumberNotOne() )
			{
				Set<OWLAnnotation >newAnn = getAnnotationsForAxiom(deg);
				OWLClassAssertionAxiom axiom = df.getOWLClassAssertionAxiom(c, i, newAnn);
				manager.applyChange(new AddAxiom(ontology, axiom));
			}
			else
			{
				OWLClassAssertionAxiom axiom = df.getOWLClassAssertionAxiom(c, i);
				manager.applyChange(new AddAxiom(ontology, axiom));						
			}
		}

		for (Individual ind : kb.individuals.values() )
		{
			OWLNamedIndividual i = getIndividual(ind.toString());
			for (ArrayList<Relation> a : ind.roleRelations.values())
				for (Relation rel : a)
				{
					OWLObjectProperty r = getObjectProperty(rel.getRoleName());
					OWLNamedIndividual i2 = getIndividual(rel.getObjectIndividual().toString());
					Degree deg = rel.getDegree();
					if (deg.isNumberNotOne() )
					{
						Set<OWLAnnotation >newAnn = getAnnotationsForAxiom(deg);
						OWLObjectPropertyAssertionAxiom axiom = df.getOWLObjectPropertyAssertionAxiom(r, i, i2, newAnn);
						manager.applyChange(new AddAxiom(ontology, axiom));
					}
					else
					{
						OWLObjectPropertyAssertionAxiom axiom = df.getOWLObjectPropertyAssertionAxiom(r, i, i2);
						manager.applyChange(new AddAxiom(ontology, axiom));
					}
				}
		}


		// TBox

		for(String a : kb.axiomsAequivC.keySet())
		{
			OWLClassExpression c1 = getClass(a);
			for (Concept c : kb.axiomsAequivC.get(a))
			{
				OWLClassExpression c2 = getClass(c);
				OWLAxiom axiom = df.getOWLEquivalentClassesAxiom(c1, c2);
				manager.applyChange(new AddAxiom(ontology, axiom));
			}
		}

		for(String a : kb.axiomsAisaB.keySet())
		{
			OWLClassExpression c1 = getClass(a);
			for (PrimitiveConceptDefinition pcd : kb.axiomsAisaB.get(a))
				annotatePcd(c1, pcd);
		}

		for(String a : kb.axiomsAisaC.keySet())
		{
			OWLClassExpression c1 = getClass(a);
			for (PrimitiveConceptDefinition pcd : kb.axiomsAisaC.get(a))
				annotatePcd(c1, pcd);
		}

		for(HashSet<GeneralConceptInclusion> gcis: kb.axiomsCisaD.values() )
			for (GeneralConceptInclusion gci : gcis)
				annotateGci(gci);

		for(HashSet<GeneralConceptInclusion> gcis: kb.axiomsCisaA.values() )
			for (GeneralConceptInclusion gci : gcis)
				annotateGci(gci);

		for(ConceptEquivalence ce : kb.axiomsCequivD)
		{
			OWLClassExpression c1 = getClass(ce.getC1());
			OWLClassExpression c2 = getClass(ce.getC2());
			OWLAxiom axiom = df.getOWLEquivalentClassesAxiom(c1, c2);
			manager.applyChange(new AddAxiom(ontology, axiom));	
		}

		for(String a : kb.tDis.keySet()) 
		{
			OWLClassExpression c1 = getClass(a);
			for (String disjC : kb.tDis.get(a))
				if (a.compareTo(disjC) < 0)
				{
					OWLClassExpression c2 = getClass(disjC);
					OWLAxiom axiom = df.getOWLDisjointClassesAxiom(c1, c2);
					manager.applyChange(new AddAxiom(ontology, axiom));	
				}
		}	

		for(String r : kb.domainRestrictions.keySet())
		{
			if (kb.concreteRoles.contains(r))
			{
				OWLDataProperty dp = getDataProperty(r);
				for (Concept c : kb.domainRestrictions.get(r))
				{
					OWLClassExpression cl = getClass(c);
					OWLAxiom axiom = df.getOWLDataPropertyDomainAxiom(dp, cl);
					manager.applyChange(new AddAxiom(ontology, axiom));	
				}
			}
			else
			{
				OWLObjectProperty op = getObjectProperty(r);
				for (Concept c : kb.domainRestrictions.get(r))
				{
					OWLClassExpression cl = getClass(c);
					OWLAxiom axiom = df.getOWLObjectPropertyDomainAxiom(op, cl);
					manager.applyChange(new AddAxiom(ontology, axiom));	
				}
			}
		}

		for(String r : kb.rangeRestrictions.keySet())
		{
			OWLObjectProperty op = getObjectProperty(r);
			for (Concept c : kb.rangeRestrictions.get(r))
			{
				OWLClassExpression cl = getClass(c);
				OWLAxiom axiom = df.getOWLObjectPropertyRangeAxiom(op, cl);
				manager.applyChange(new AddAxiom(ontology, axiom));	
			}
		}


		// RBox

		for(String r : kb.reflexiveRoles)
		{
			OWLObjectProperty op = getObjectProperty(r);
			OWLAxiom axiom = df.getOWLReflexiveObjectPropertyAxiom(op);
			manager.applyChange(new AddAxiom(ontology, axiom));		
		}

		for(String r : kb.symmetricRoles)
		{
			OWLObjectProperty op = getObjectProperty(r);
			OWLAxiom axiom = df.getOWLSymmetricObjectPropertyAxiom(op);
			manager.applyChange(new AddAxiom(ontology, axiom));		
		}

		for(String r : kb.transRoles)
		{
			OWLObjectProperty op = getObjectProperty(r);
			OWLAxiom axiom = df.getOWLTransitiveObjectPropertyAxiom(op);
			manager.applyChange(new AddAxiom(ontology, axiom));		
		}

		for (String r : kb.invRoles.keySet())
		{
			OWLObjectProperty op = getObjectProperty(r);
			Set<String> inv = kb.invRoles.get(r);
			if (inv != null)
				for(String s : inv)
				{
					OWLObjectProperty op2 = getObjectProperty(s);
					OWLAxiom axiom = df.getOWLInverseObjectPropertiesAxiom(op, op2);
					manager.applyChange(new AddAxiom(ontology, axiom));	
				}
		}

		for (String r : kb.rolesWithParents.keySet())
		{
			OWLObjectProperty op = getObjectProperty(r);
			Hashtable<String, Double> par = kb.rolesWithParents.get(r);
			if (par != null)
				for(String s : par.keySet())
				{
					OWLObjectProperty op2 = getObjectProperty(s);
					OWLAxiom axiom = df.getOWLSubObjectPropertyOfAxiom(op, op2);
					manager.applyChange(new AddAxiom(ontology, axiom));	
				}
		}

		for(String r : kb.funcRoles)
		{
			OWLAxiom axiom;
			if (kb.concreteRoles.contains(r))
			//if (kb.concreteFeatures.containsKey(r) ) // Equivalent but probable less efficient
			{
				OWLDataProperty dp = getDataProperty(r);
				axiom = df.getOWLFunctionalDataPropertyAxiom(dp);
			}
			else
			{
				OWLObjectProperty op = getObjectProperty(r);
				axiom = df.getOWLFunctionalObjectPropertyAxiom(op);
			}
			manager.applyChange(new AddAxiom(ontology, axiom));		
		}

		// Concrete feature ranges
		for(String cfName : kb.concreteFeatures.keySet())
			{
				ConcreteFeature cf =  kb.concreteFeatures.get(cfName);
				int type = cf.getType();
				OWLAxiom axiom = null;
				OWLDataProperty dp;
				OWLDatatype dt;				
				switch (type)
				{
					case ConcreteFeature.BOOLEAN:
						dp = getDataProperty(cfName);
						dt = df.getBooleanOWLDatatype();
						axiom = df.getOWLDataPropertyRangeAxiom(dp, dt);
						break;
					
					case ConcreteFeature.INTEGER:
						dp = getDataProperty(cfName);
						dt = df.getIntegerOWLDatatype();
						axiom = df.getOWLDataPropertyRangeAxiom(dp, dt);
						break;
					
					case ConcreteFeature.REAL:
						dp = getDataProperty(cfName);
						dt = df.getDoubleOWLDatatype();
						axiom = df.getOWLDataPropertyRangeAxiom(dp, dt);
						break;
					
					/*case ConcreteFeature.STRING:
						dp = getDataProperty(cfName);
						dt = df.getStringOWLDatatype();
						axiom = df.getOWLDataPropertyRangeAxiom(dp, dt);
						break;*/
						
				}
				manager.applyChange(new AddAxiom(ontology, axiom));
			}
		try
		{
            File file = new File(outputFile);
            manager.saveOntology(ontology, IRI.create(file.toURI()));
	    }
		catch (Exception ex)
		{
			System.err.println(ex);
		}

	}


	private void annotateGci(GeneralConceptInclusion gci)
	{
		OWLClassExpression c1= getClass(gci.getSubsumed());
		OWLClassExpression c2= getClass(gci.getSubsumer());
		
		// Annotation
		Degree deg = gci.getDegree();
		if (deg.isNumberNotOne() )
		{
			Set<OWLAnnotation >newAnn = getAnnotationsForAxiom(deg);
			OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(c1, c2, newAnn);
			manager.applyChange(new AddAxiom(ontology, axiom));
		}
		else
		{
			OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(c1, c2);
			manager.applyChange(new AddAxiom(ontology, axiom));						
		}
		
	}


	private void annotatePcd(OWLClassExpression c1, PrimitiveConceptDefinition pcd)
	{
		Concept b = pcd.getDefinition();
		OWLClassExpression c2 = getClass(b);
		
		double n = pcd.getDegree();
		if (n != 1)
		{
			Set<OWLAnnotation> newAnn = getAnnotationsForAxiom(n);
			OWLAxiom axiom = df.getOWLSubClassOfAxiom(c1, c2, newAnn);
			manager.applyChange(new AddAxiom(ontology, axiom));
		}
		else
		{
			OWLAxiom axiom = df.getOWLSubClassOfAxiom(c1, c2);
			manager.applyChange(new AddAxiom(ontology, axiom));			
		}
	}

}

