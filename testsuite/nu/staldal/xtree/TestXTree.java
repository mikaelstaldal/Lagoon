package nu.staldal.xtree;

import java.io.*;

import org.xml.sax.*;

import org.apache.xerces.parsers.SAXParser;
import org.apache.xml.serialize.*;

import nu.staldal.lagoon.util.*;

import junit.framework.*;

public class TestXTree extends TestCase
{
    public TestXTree(String name)
    {
        super(name);
    }

    public void testXTree() throws Exception
	{
		XMLReader xreader = new SAXParser();

		OutputFormat of = new OutputFormat();
		of.setEncoding("iso-8859-1");
		Serializer ser = new XMLSerializer(new FileOutputStream("xtree.out"),
            of);

		TreeBuilder tb = new TreeBuilder(
			new java.net.URL("http://www.foo.com"));

		xreader.setContentHandler(tb);
		xreader.parse(new InputSource(getClass().getResourceAsStream(
            "xtree.xml")));

		Element el = tb.getTree();

		OutputStream fos = new FileOutputStream("xtree.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(el);
		fos.close();

		el = null;
		InputStream fis = new FileInputStream("xtree.ser");
		ObjectInputStream ois = new ObjectInputStream(fis);
		el = (Element)ois.readObject();
		fis.close();

		el.toSAX(new DocumentHandlerAdapter(ser.asDocumentHandler()));
	}

}
