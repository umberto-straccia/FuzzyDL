package test;
import junit.framework.TestCase;


public class TestModifier extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/modifier1.txt");
		assertEquals("TestModifier", 0.5, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/modifier2.txt");
		assertEquals("TestModifier", 0.9, p.solve());
	}

}
