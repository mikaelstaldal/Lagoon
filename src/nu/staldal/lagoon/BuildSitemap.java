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

package nu.staldal.lagoon;

import java.io.*;

import org.apache.xml.serialize.*;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;

import nu.staldal.lagoon.util.DocumentHandlerAdapter;

public class BuildSitemap
{
    private static final String syntaxMsg =
        "Syntax: nu.staldal.lagoon.BuildSitemap <source_dir> <sitemap_file>";

	public static void main(String args[]) throws Exception
	{
        if (args.length < 2)
        {
            System.out.println(syntaxMsg);
            return;
        }

		OutputFormat of = new OutputFormat();
		of.setDoctype(null, null);
		of.setEncoding("iso-8859-1");
		of.setIndenting(true);

		FileOutputStream fos = new FileOutputStream(args[1]);
		XMLSerializer ser = new XMLSerializer(fos, of);
		ContentHandler ch = new DocumentHandlerAdapter(ser.asDocumentHandler());

		ch.startDocument();
		AttributesImpl atts = new AttributesImpl();
		ch.startElement("", "sitemap", "", atts);

		processDirectory(new File(args[0]), ch);

		ch.endElement("", "sitemap", "");
		ch.endDocument();

		fos.close();
	}

	public static void processDirectory(File dir, ContentHandler ch)
		throws SAXException
	{
		String[] dirList = dir.list();
		for (int i = 0; i<dirList.length; i++)
		{
			File ent = (dir == null) ? new File(dirList[i]) : new File(dir, dirList[i]);
			if (ent.isDirectory())
				processDirectory(ent, ch);
			else if (ent.isFile())
			{
				AttributesImpl atts = new AttributesImpl();
                String url = "/" + ent.getPath().replace(
                    File.separatorChar, '/');

				atts.addAttribute("","target","","CDATA", url);

				ch.startElement("", "file", "", atts);

                if (url.endsWith(".html") || url.endsWith(".htm"))
                {
    				AttributesImpl formatAtts = new AttributesImpl();

				    formatAtts.addAttribute("","type","","CDATA", "html");

                    ch.startElement("", "format", "", formatAtts);
                    ch.startElement("", "source", "", new AttributesImpl());
	    			ch.endElement("", "source", "");
                    ch.endElement("", "format", "");
                }
                else if (url.endsWith(".xml"))
                {
    				AttributesImpl formatAtts = new AttributesImpl();

				    formatAtts.addAttribute("","type","","CDATA", "xml");

                    ch.startElement("", "format", "", formatAtts);
                    ch.startElement("", "source", "", new AttributesImpl());
	    			ch.endElement("", "source", "");
                    ch.endElement("", "format", "");
                }
                else
                {
                    ch.startElement("", "read", "", new AttributesImpl());
	    			ch.endElement("", "read", "");
                }

				ch.endElement("", "file", "");
			}
		}
	}

}
