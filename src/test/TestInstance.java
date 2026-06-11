package test;
import junit.framework.TestCase;


public class TestInstance extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/instance1.txt");
		assertEquals("TestInstance", 0.7, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/instance2.txt");
		assertEquals("TestInstance", 0.3, p.solve());
	}

}
