package test;
import junit.framework.TestCase;


public class TestShowStatement extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/showStatement1.txt");
		assertEquals("TestShowStatement", 0.25, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/showStatement2.txt");
		assertEquals("TestShowStatement", 1.0, p.solve());
	}

}
