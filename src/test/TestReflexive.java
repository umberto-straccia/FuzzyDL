package test;
import junit.framework.TestCase;


public class TestReflexive extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/reflexive1.txt");
		assertEquals("TestReflexive", 0.8, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/reflexive2.txt");
		assertEquals("TestReflexive", 0.8, p.solve());
	}

}
