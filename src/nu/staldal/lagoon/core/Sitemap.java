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

package nu.staldal.lagoon.core;

import java.util.*;

import org.xml.sax.*;

import nu.staldal.lagoon.util.XMLConfig;

/**
 * Contains the information needed to (re)build a website.
 *
 * Initialized with a sitemap description file.
 * Can then be used to (re)build the website several times.
 */
class Sitemap extends XMLConfig
{
    protected String getPublicId()
    {
        return null;
    }

    protected java.io.InputStream getDTD()
    {
        return null;
    }

    // Associations
    protected Hashtable entries;

    // Attributes
    private LagoonProcessor processor;
    private java.io.File sourceDir;

    // Work attributes
    private StringBuffer charData;
    private Stack prodStack;
    private Stack prodNameStack;
    private String currentTargetName;
    private FileEntry currentFile;
    private FileStorage targetLocation;


    // SAX ContentHandler implementation

    public void startElement(String namespaceURI, String localName,
                             String qname, Attributes atts)
        throws SAXException
    {
        if (localName.equals("sitemap"))
        {
            // nothing to do
        }
        else if (localName.equals("file"))
        {
            currentTargetName = atts.getValue("","target");
            if (currentTargetName == null
                    || currentTargetName.length() < 1
                    || currentTargetName.charAt(0) != '/')
            {
                throw new LagoonException(
                    "invalid target specification: " + currentTargetName);
            }
			
			String theSource = atts.getValue("","source");
			if (theSource == null || theSource.length() < 1)
				theSource = currentTargetName;
				
            currentFile = new FileEntry(currentTargetName,
                                        theSource,
                                        sourceDir, targetLocation);
        }
        else if (localName.equals("format") || localName.equals("transform") ||
                 localName.equals("source") || localName.equals("read") ||
                 localName.equals("parse") || localName.equals("process"))
        {
            String type = atts.getValue("","type");
            if (type == null)
                type = "";

            String prodName = localName + '-' +
                                ((type.length() == 0) ?
                                 "(default)" :
                                 type);
            Producer prod = processor.createProducer(localName, type);
            if (prod == null)
                throw new LagoonException(
                    "Producer " + prodName + " not found");

            prod.setProcessor(processor);
            prod.setSourceManager(currentFile);
            prod.setPosition(prodStack.size());

			for (int i = 0; i < atts.getLength(); i++)
			{
				if (atts.getLocalName(i).equals("type"))
					continue;

				prod.addParam(atts.getLocalName(i), atts.getValue(i));
			}

            prodStack.push(prod);
            prodNameStack.push(prodName);

            charData = new StringBuffer();
        }
        else
        {
            throw new LagoonException("Error in Sitemap, unexpected element: "
            + localName);
        }
    }

    public void endElement(String namespaceURI, String localName,
                           String qname)
        throws SAXException
    {
        if (localName.equals("sitemap"))
            ;
        else if (localName.equals("file"))
        {
            entries.put(currentTargetName ,currentFile);
            currentTargetName = null;
            currentFile = null;
        }
        else if (localName.equals("format") || localName.equals("transform") ||
                 localName.equals("source") || localName.equals("read") ||
                 localName.equals("parse") || localName.equals("process"))
        {
            Producer prod = (Producer)prodStack.pop();
            String prodName = (String)prodNameStack.pop();
            String nameParam = (charData == null) ? "" : charData.toString().trim();
            if (nameParam.length() > 0)
                prod.addParam("name", nameParam);

            charData = null;

            try {
                if (prodStack.empty())
                {
                    currentFile.setMyProducer((ByteStreamProducer)prod);
                }
                else
                {
                    Producer lastProd = (Producer)prodStack.peek();

                    if (lastProd instanceof ByteStreamConsumer)
                    {
                        ((ByteStreamConsumer)lastProd).
                            setNext((ByteStreamProducer)prod);
                    }
                    else if (lastProd instanceof XMLStreamConsumer)
                    {
                        ((XMLStreamConsumer)lastProd).
                            setNext((XMLStreamProducer)prod);
                    }
                    else
                    {
                        throw new ClassCastException();
                    }
                }
            }
            catch (ClassCastException e)
            {
                throw new LagoonException(prodName +
                                          ": Inconsistent Producer chain");
            }

            try {
                prod.init();
            }
            catch (LagoonException e)
            {
                throw new LagoonException(prodName + ": " + e.getMessage());
            }
            catch (java.io.IOException e)
            {
                throw new SAXException(e);
            }
        }
    }

    public void startPrefixMapping(String prefix, String uri)
    {
        // nothing to do
    }

    public void endPrefixMapping(String prefix)
    {
        // nothing to do
    }

    public void characters(char ch[], int start, int length)
    {
        if (charData != null)
            charData.append(ch, start, length);
    }

    public void ignorableWhitespace(char ch[], int start, int length)
    {
        // nothing to do
    }

    public void processingInstruction(String target, String data)
    {
        // nothing to do
    }

    public void skippedEntity(String name)
    {
        // nothing to do
    }


    /**
     * Constructor.
     *
     * @param processor  the processor
     * @param input  where to read the sitemap from
     * @param sourceDir  where the source files are
     * @param targetLoc  where to store generated files
     */
    public Sitemap(LagoonProcessor processor,
                   java.io.InputStream input,
                   java.io.File sourceDir,
				   FileStorage targetLoc)
        throws java.io.IOException, LagoonException
    {
        this.processor = processor;
		this.targetLocation = targetLoc;

        this.sourceDir = sourceDir;
        entries = new Hashtable();

        prodStack = new Stack();
        prodNameStack = new Stack();
        charData = null;
        currentFile = null;

        try {
            parseXML(input, false);
        }
        catch (LagoonException e)
        {
            throw e;
        }
        catch (SAXException e)
        {
            Exception ee = e.getException();
            if (ee == null)
            {
                e.printStackTrace();
                throw new LagoonException(e.getMessage());
            }
            else if (ee instanceof java.io.IOException)
                throw (java.io.IOException)ee;
            else
            {
                ee.printStackTrace();
                throw new LagoonException(ee.getMessage());
            }
        }

        if (!prodStack.empty())
            throw new LagoonException(
                "Error in Sitemap, prodStack is not empty");

        prodStack = null;
        prodNameStack = null;
        charData = null;
        currentFile = null;
    }


    /**
     * Get an Enumeration of all targets in this sitemap.
     */
    public Enumeration getTargets()
    {
        return entries.keys();
    }


    /**
     * Get an Enumeration of all entries in this sitemap.
     */
    public Enumeration getEntries()
    {
        return entries.elements();
    }


    /**
     * Lookup a specific entry in the sitemap.
     *
     * @param  the target, a pseudo-absolute URL (starting with '/').
     *
     * @returns the entry for the specified target,
     *  or <code>null</code> if not found.
     */
    public FileEntry lookupEntry(String target)
    {
        return (FileEntry)entries.get(target);
    }

}
