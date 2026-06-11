package test;
import junit.framework.TestCase;


public class TestImplies extends TestCase 
{

	public void testQueryG1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/impliesG1.txt");
		assertEquals("TestImplies", 0.4, p.solve());
	}

	public void testQueryG2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/impliesG2.txt");
		assertEquals("TestImplies", 0.8, p.solve());
	}

	public void testQueryKd1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/impliesKd1.txt");
		assertEquals("TestImplies", 0.9, p.solve());
	}

	public void testQueryKd2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/impliesKd2.txt");
		assertEquals("TestImplies", 0.9, p.solve());
	}

	public void testQueryL1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/impliesL1.txt");
		assertEquals("TestImplies", 0.5, p.solve());
	}

	public void testQueryL2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/impliesL2.txt");
		assertEquals("TestImplies", 0.7, p.solve());
	}

	public void testQueryL3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/impliesL3.txt");
		assertEquals("TestImplies", 0.7, p.solve());
	}

	public void testQueryZ() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/impliesZ.txt");
		assertEquals("TestImplies", 0.8, p.solve());
	}

}
