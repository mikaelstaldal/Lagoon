package nu.staldal.xtree;

import java.io.*;

import org.xml.sax.*;

// import junit.framework.*;

public class TestSequentialTreeBuilder // extends TestCase
{
    public TestSequentialTreeBuilder(String name)
    {
        super(name);
    }

    public void testSequentialTreeBuilder() throws Exception
	{
		SequentialTreeBuilder.parseXML(
			new InputSource(getClass().getResourceAsStream("xtree.xml")), false,
			new ElementHandler() {
				public void processElement(Element el) throws SAXException
				{
						
				}
			});
	}

}

