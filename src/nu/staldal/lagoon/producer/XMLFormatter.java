/*
 * Copyright (c) 2001, Mikael Ståldal
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright 
 * notice, this list of conditions and the following disclaimer in the 
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the author nor the names of its contributors 
 * may be used to endorse or promote products derived from this software 
 * without specific prior written permission. 
 * 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR 
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY 
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * 
 * Note: This is known as "the modified BSD license". It's an approved 
 * Open Source and Free Software license, see 
 * http://www.opensource.org/licenses/ 
 * and
 * http://www.gnu.org/philosophy/license-list.html
 */

package nu.staldal.lagoon.producer;

import java.io.*;

import org.xml.sax.*;
import org.apache.xml.serialize.*;

import nu.staldal.lagoon.core.*;
import nu.staldal.lagoon.util.DocumentHandlerAdapter;

// *** Testa med Xalan2's serialiserare

public class XMLFormatter extends Format
{
    BaseMarkupSerializer ser;
    OutputFormat of;

    public void init() throws LagoonException
    {
        of = new OutputFormat();

        String method = getParam("method");
        if (method == null)
            method = "XML";

        int _html;
        String html = getParam("html");
        if (html == null)
            _html = 1;
        else if (html.equals("transitional"))
            _html = 1;
        else if (html.equals("frameset"))
            _html = 2;
        else if (html.equals("strict"))
            _html = 3;
        else
            throw new LagoonException("Unknown html variant");

        if (method.equals("XML"))
        {
            ser = new XMLSerializer();
            of.setDoctype(null, null);
            of.setEncoding("UTF-8");
        }
        else if (method.equals("HTML"))
        {
            ser = new HTMLSerializer();
            switch (_html)
            {
            case 1: of.setDoctype("-//W3C//DTD HTML 4.01 Transitional//EN",
                   	        	  "http://www.w3.org/TR/html4/loose.dtd");
                    break;
            case 2: of.setDoctype("-//W3C//DTD HTML 4.01 Frameset//EN",
                   	        	  "http://www.w3.org/TR/html4/frameset.dtd");
                    break;
            case 3: of.setDoctype("-//W3C//DTD HTML 4.01//EN",
                   	        	  "http://www.w3.org/TR/html4/strict.dtd");
                    break;
            }
            of.setEncoding("iso-8859-1");
        }
        else if (method.equals("XHTML"))
        {
            ser = new XHTMLSerializer();
            switch (_html)
            {
            case 1: of.setDoctype("-//W3C//DTD XHTML 1.0 Transitional//EN",
                                  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
                    break;
            case 2: of.setDoctype("-//W3C//DTD XHTML 1.0 Frameset//EN",
                   	        	  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd");
                    break;
            case 3: of.setDoctype("-//W3C//DTD XHTML 1.0 Strict//EN",
                   	        	  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
                    break;
            }
            of.setEncoding("iso-8859-1");
        }
        else if (method.equals("TEXT"))
        {
            ser = new TextSerializer();
            of.setEncoding("iso-8859-1");
        }
        else
            throw new LagoonException("Unknown serializing method");

        String enc = getParam("encoding");
        if (enc != null) of.setEncoding(enc);

        String docPub = getParam("doctype-public");
        String docSys = getParam("doctype-system");
        if ((docPub != null) || (docSys != null))
            of.setDoctype(docPub, docSys);

        String indent = getParam("indent");
        if (indent != null)
        try
        {
			of.setIndent(Integer.parseInt(indent));
		}
		catch (NumberFormatException e)
		{
			of.setIndenting(true);
		}
    }

    public void start(OutputStream bytes, Target target)
        throws IOException, SAXException
    {
        ser.reset();
        ser.setOutputFormat(of);
        ser.setOutputByteStream(bytes);

        getNext().start(new DocumentHandlerAdapter(ser.asDocumentHandler()), 
			target);
	}

    public boolean hasBeenUpdated(long when)
        throws LagoonException, IOException
    {
        return getNext().hasBeenUpdated(when);
    }

}
