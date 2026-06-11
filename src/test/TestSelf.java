package test;
import junit.framework.TestCase;


public class TestSelf extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/self1.txt");
		assertEquals("TestSelf", 0.8, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/self2.txt");
		assertEquals("TestSelf", 0.8, p.solve());
	}

}