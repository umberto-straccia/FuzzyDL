package test;
import junit.framework.TestCase;


public class TestAbsorption extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/absorption1.txt");
		assertEquals("TestAbsorption", 0.7, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/absorption2.txt");
		assertEquals("TestAbsorption", 0.7, p.solve());
	}

	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/absorption3.txt");
		assertEquals("TestAbsorption", 0.7, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/absorption4.txt");
		assertEquals("TestAbsorption", 0.5, p.solve());
	}

	public void testQuery5() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/absorption5.txt");
		assertEquals("TestAbsorption", 0.5, p.solve());
	}

}
