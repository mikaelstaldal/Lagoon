package nu.staldal.xmlutil;

import java.io.*;

import org.xml.sax.*;

import org.apache.xerces.parsers.SAXParser;
import org.apache.xml.serialize.*;

import junit.framework.*;

public class TestDocumentHandlerAdapter extends TestCase
{
    private SAXParser saxParser;
	private OutputFormat of;

	public TestDocumentHandlerAdapter(String name)
	{
		super(name);
	}

    protected void setUp()
    {
		saxParser = new SAXParser();

		of = new OutputFormat();
		of.setEncoding("iso-8859-1");
		of.setIndent(4);
    }

    protected void tearDown()
    {
        // nothing to do
    }

    public void testDHA() throws Exception
	{
		Serializer ser = new XMLSerializer(new FileOutputStream("dha.out"), of);

        saxParser.reset();
        saxParser.setContentHandler(
			new DocumentHandlerAdapter(ser.asDocumentHandler()));
		saxParser.parse(new InputSource(getClass().getResourceAsStream(
            "dha.xml")));
	}

}
