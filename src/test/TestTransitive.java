package test;
import junit.framework.TestCase;


public class TestTransitive extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/transitive1.txt");
		assertEquals("TestTransitive", 0.2, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/transitive2.txt");
		assertEquals("TestTransitive", 0.5, p.solve());
	}

	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/transitive3.txt");
		assertEquals("TestTransitive", 0.9, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/transitive4.txt");
		assertEquals("TestTransitive", 0.9, p.solve());
	}

}
