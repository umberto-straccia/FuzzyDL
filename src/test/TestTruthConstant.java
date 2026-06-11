package test;
import junit.framework.TestCase;


public class TestTruthConstant extends TestCase 
{

	public void testQuery() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/truthconstant.txt");
		assertEquals("TestTruthConstant", 0.2, p.solve());
	}
	
}


