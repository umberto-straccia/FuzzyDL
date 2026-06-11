package test;
import junit.framework.TestCase;


public class TestTbox extends TestCase 
{

	public void testQuery1() throws Exception 
	{
 		ParserInterface p = new ParserInterface("examples/TestSuite/domain.txt");
		assertEquals("TestTbox", 0.8, p.solve());
	}

	public void testQuery2() throws Exception 
	{
 		ParserInterface p = new ParserInterface("examples/TestSuite/range.txt");
		assertEquals("TestTbox", 0.8, p.solve());
	}

	public void testQuery3() throws Exception 
	{
 		ParserInterface p = new ParserInterface("examples/TestSuite/disjoint.txt");
		assertEquals("TestTbox", 1.0, p.solve());
	}

	public void testQuery4() throws Exception 
	{
 		ParserInterface p = new ParserInterface("examples/TestSuite/lazy.txt");
		assertEquals("TestTbox", 0.8, p.solve());
	}

}
