package test;
import junit.framework.TestCase;


public class TestFuzzyConcreteDomain extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/fcd1.txt");
		assertEquals("TestFuzzyConcreteDomain", 1.0, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/fcd2.txt");
		assertEquals("TestFuzzyConcreteDomain", 1.0, p.solve());
	}

	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/fcd3.txt");
		assertEquals("TestFuzzyConcreteDomain", 1.0, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/fcd4.txt");
		assertEquals("TestFuzzyConcreteDomain", 1.0, p.solve());
	}

	public void testQuery5() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/fcd5.txt");
		assertEquals("TestFuzzyConcreteDomain", 0.25, p.solve());
	}

	public void testQuery6() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/fcd6.txt");
		assertEquals("TestFuzzyConcreteDomain", 0.0, p.solve());
	}

}
