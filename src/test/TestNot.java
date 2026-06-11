package test;
import junit.framework.TestCase;


public class TestNot extends TestCase 
{

	public void testQuery() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/not.txt");
		assertEquals("TestNot", 1.0, p.solve());
	}

}
