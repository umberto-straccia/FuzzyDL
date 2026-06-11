package test;
import junit.framework.Test;
import junit.framework.TestSuite;


// Invocation:

//java -cp ../../dist/lib/junit-4.10.jar org.junit.runner.JUnitCore Test

public class AllTests {
	

	/**
	 * Maximum allowed difference between the expected and the actual output.
	 */
	public static final double ERROR = 1e-4;


	public Test suite()
	{
		TestSuite suite = new TestSuite("Test for default package");
		//$JUnit-BEGIN$
		//suite.addTestSuite(TestAbsorption.class);
		suite.addTestSuite(TestAggregation.class);
		suite.addTestSuite(TestAll.class);
		suite.addTestSuite(TestAnd.class);
		suite.addTestSuite(TestBlocking.class);
		suite.addTestSuite(TestDatatype.class);
		suite.addTestSuite(TestDisjoint.class);
		suite.addTestSuite(TestFunctional.class);
		suite.addTestSuite(TestFuzzyConcreteDomain.class);
		suite.addTestSuite(TestFuzzyNumber.class);
		suite.addTestSuite(TestImplies.class);
		suite.addTestSuite(TestImpliesRole.class);
		suite.addTestSuite(TestInconsistency.class);
		suite.addTestSuite(TestInstance.class);
		suite.addTestSuite(TestInverse.class);
		suite.addTestSuite(TestModifier.class);
		suite.addTestSuite(TestNot.class);
		suite.addTestSuite(TestOr.class);
		suite.addTestSuite(TestReflexive.class);
		suite.addTestSuite(TestRelated.class);
		suite.addTestSuite(TestRoughSets.class);
		suite.addTestSuite(TestSat.class);
		suite.addTestSuite(TestSelf.class);
		suite.addTestSuite(TestShowStatement.class);
		suite.addTestSuite(TestSome.class);
		suite.addTestSuite(TestSubsumption.class);
		suite.addTestSuite(TestSymmetric.class);;
		suite.addTestSuite(TestTbox.class);
		suite.addTestSuite(TestThresholdConcept.class);
		suite.addTestSuite(TestTransitive.class);
		suite.addTestSuite(TestTruthConstant.class);
		suite.addTestSuite(TestWeightedConcept.class);
		suite.addTestSuite(TestWeightedSum.class);
		//$JUnit-END$
		return suite;
	}

}
