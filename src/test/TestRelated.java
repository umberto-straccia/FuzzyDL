package test;
import junit.framework.TestCase;


public class TestRelated extends TestCase 
{

	public void testQuery() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/related.txt");
		assertEquals("TestRelated", 0.5, p.solve());
	}

}
