package test;
import junit.framework.TestCase;


public class TestSymmetric extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/symmetric1.txt");
		assertEquals("TestSymmetric", 0.7, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/symmetric2.txt");
		assertEquals("TestSymmetric", 0.7, p.solve());
	}
}