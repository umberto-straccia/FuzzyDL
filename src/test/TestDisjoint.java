package test;
import junit.framework.TestCase;


public class TestDisjoint extends TestCase 
{

	public void testQuery() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/disjoint.txt");
		assertEquals("TestDisjoint", 1.0, p.solve());
	}

}
