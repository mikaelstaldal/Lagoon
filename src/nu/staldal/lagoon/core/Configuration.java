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

import nu.staldal.xtree.*;

/**
 * Represent the static configuration for Lagoon.
 *
 * @see nu.staldal.lagoon.core.LagoonProcessor
 * @see nu.staldal.lagoon.core.Sitemap
 */
class Configuration
{
    final Hashtable classDict;
    final Hashtable paramDict;
    final Hashtable filestorageDict;

    /**
     * Constructor
     */
    public Configuration()
    {
        classDict = new Hashtable();
        paramDict = new Hashtable();
        filestorageDict = new Hashtable();
    }

    /**
     * Read a configuration file. May be invoked multiple times.
     *
     * @param input  the configuration file to parse
     */
    public void readFile(java.io.InputStream input)
        throws java.io.IOException, LagoonException
    {
        String prodType;
        StringBuffer paramBuf;
        Hashtable params;

        Element root = null;

        try {
            XMLReader parser =
	    		new org.apache.xerces.parsers.SAXParser();

	        parser.setFeature("http://xml.org/sax/features/validation",
                false);
            parser.setFeature("http://xml.org/sax/features/namespaces",
                true);
            parser.setFeature("http://xml.org/sax/features/namespace-prefixes",
                false);

            TreeBuilder tb = new TreeBuilder();
            parser.setContentHandler(tb);
            parser.parse(new InputSource(input));

            root = tb.getTree();
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
            {
                throw (java.io.IOException)ee;
			}
            else
            {
                ee.printStackTrace();
                throw new LagoonException(ee.getMessage());
            }
        }

        if (!root.getLocalName().equals("config"))
            throw new LagoonException(
                "Configuration file root element must be <config>");

        for (int i = 0; i < root.numberOfChildren(); i++)
        {
            if (!(root.getChild(i) instanceof Element)) continue;
            Element el = (Element)root.getChild(i);

            if (el.getLocalName().equals("format")
                    || el.getLocalName().equals("transform")
                    || el.getLocalName().equals("source")
                    || el.getLocalName().equals("read")
                    || el.getLocalName().equals("parse")
                    || el.getLocalName().equals("process"))
            {
                String baseClassName =
                    "nu.staldal.lagoon.core." +
                    Character.toUpperCase(el.getLocalName().charAt(0)) +
                    el.getLocalName().substring(1);

                try {
                    Class cls = Class.forName(el.getAttrValue("class"));
                    if (!Class.forName(baseClassName).isAssignableFrom(cls))
                        throw new LagoonException(
                            el.getLocalName() + " class must derive from "
                            + baseClassName);

                    prodType = el.getAttrValue("type");
                    if (prodType == null) prodType = "";

                    classDict.put(el.getLocalName() + ":" + prodType, cls);
                }
                catch (ClassNotFoundException e)
                {
                    throw new LagoonException(
                        "Producer class cannot be found:" + e.getMessage());
                }

                params = new Hashtable();

                for (int j = 0; j < el.numberOfChildren(); j++)
                {
                    if (!(el.getChild(j) instanceof Element)) continue;
                    Element e = (Element)el.getChild(j);

                    String paramName = e.getAttrValue("name");
                    if (paramName == null || paramName.length() == 0)
                        throw new LagoonException(
                            "No parameter name given in "
                            + el.getLocalName() + "-" + prodType);
                    String paramValue = e.getTextContent();
                    if (paramValue == null)
                        throw new LagoonException(
                            "No parameter value given in "
                            + el.getLocalName() + "-" + prodType);

                    params.put(paramName, paramValue);
                }

                paramDict.put(el.getLocalName() + ":" + prodType, params);

            }
            else if (el.getLocalName().equals("filestorage"))
            {
                String className = el.getAttrValue("class");
                if (className == null || className.length() == 0)
                    throw new LagoonException(
                        "No class name given in filestorage");

                try {
                    Class cls = Class.forName(className);
                    if (!Class.forName("nu.staldal.lagoon.core.FileStorage").
                            isAssignableFrom(cls))
                        throw new LagoonException(
                            el.getLocalName() + " class must derive from "
                            + "nu.staldal.lagoon.core.FileStorage");

                    String urlPrefix = el.getAttrValue("urlprefix");
                    if (urlPrefix == null) urlPrefix = "";

                    filestorageDict.put(urlPrefix, cls);
                }
                catch (ClassNotFoundException e)
                {
                    throw new LagoonException(
                        "FileStorage class cannot be found:" + e.getMessage());
                }
            }
            else
            {
                throw new LagoonException(
                    "Unexpected element in configuration file: "
                    + el.getLocalName());
            }
        }
    }


    /**
     * Create a new producer.
     *
     * @param cat  the producer category (format, transform, source,
	 *             read, parse or process).
     * @param type the producer type, use "" for default.
     *
     * @return  a new Producer
     *          or <code>null</code> if it cannot be found.
     */
    public Producer createProducer(String cat, String type)
        throws LagoonException
    {
        Class cls = (Class)classDict.get(cat + ":" + type);
        if (cls == null) return null;

        try {
            Producer prod = (Producer)cls.newInstance();

            Hashtable params = (Hashtable)paramDict.get(cat + ":" + type);

            for (Enumeration e = params.keys(); e.hasMoreElements(); )
            {
                String name = (String)e.nextElement();
                String value = (String)params.get(name);
                prod.addParam(name, value);
            }

            return prod;
        }
        catch (IllegalAccessException e)
        {
            throw new LagoonException(
                "Unable to instantiate producer class (illegal access): " +
                e.getMessage());
        }
        catch (InstantiationException e)
        {
            throw new LagoonException(
                "Unable to instantiate producer class (instantiation failed): " +
                e.getMessage());
        }
    }


    /**
     * Create a new file storage
     *
     * @param url  the URL
     *
     * @return  a new FileStorage
     *          or <code>null</code> if it cannot be found.
     */
    public FileStorage createFileStorage(String url)
        throws LagoonException
    {
        Class cls = null;

        int colon = url.indexOf(':');
        if (colon < 1)
        {
            cls = (Class)filestorageDict.get("");
        }
        else
        {
            String prefix = url.substring(0, colon);
            cls = (Class)filestorageDict.get(prefix);
            if (cls == null)
            {
                cls = (Class)filestorageDict.get("");
            }
        }

        if (cls == null) return null;

        try {
            return (FileStorage)cls.newInstance();
        }
        catch (IllegalAccessException e)
        {
            throw new LagoonException(
                "Unable to instantiate file storage class (illegal access): "
                + e.getMessage());
        }
        catch (InstantiationException e)
        {
            throw new LagoonException(
                "Unable to instantiate file storage class (instantiation failed): "
                + e.getMessage());
        }
    }
}
