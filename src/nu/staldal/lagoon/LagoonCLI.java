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
import java.util.Properties;

import nu.staldal.lagoon.core.*;

/**
 * A command line interface to LagoonProcessor
 *
 * @see nu.staldal.lagoon.core.LagoonProcessor
 */
public class LagoonCLI
{
    private static final String syntaxMsg =
        "Syntax: nu.staldal.lagoon.LagoonCLI <property_file> "
      + "[<interval>|build|force]";

    private static Properties properties;

    /**
     * The application main method
     */
	public static void main(String[] args)
	{
        boolean force = false;
        boolean build = false;
        long interval = 0;

        if (args.length < 1)
        {
            System.out.println(syntaxMsg);
            return;
        }
        else if ((args.length > 1) && args[1].equals("build"))
            build = true;
        else if ((args.length > 1) && args[1].equals("force"))
            force = true;
        else if (args.length > 1)
        {
            try {
                interval = 1000*Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e)
            {
                System.out.println(syntaxMsg);
                return;
            }
            if (interval < 1)
            {
                System.out.println(syntaxMsg);
                return;
            }
        }

        LagoonProcessor processor;
        try {
            File propertyFile = new File(args[0]);
            properties = new Properties();
            properties.load(new FileInputStream(propertyFile));

            System.out.println("Initializing Lagoon...");

            File sitemapFile = new File(getProperty("sitemapFile"));
            InputStream sitemapStream = new FileInputStream(sitemapFile);

            processor = new LagoonProcessor(getProperty("targetURL"));

            String password = null;
            if (processor.needPassword())
            {
                password = properties.getProperty("password");
                if (password == null)
                    throw new LagoonException(
                        "Password is required but not specified");
            }

            processor.init(sitemapStream,
                           sitemapFile.lastModified(),
                           new File(getProperty("sourceDir")),
                           new File(getProperty("repositoryDir")),
                           password);

            sitemapStream.close();
        }
        catch (AuthenticationException e)
        {
            System.err.println("Error while initializing Lagoon:");
            System.err.println("Incorrect password");
            return;
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Error while initializing Lagoon:");
            System.err.println("File not found: " + e.getMessage());
            return;
        }
        catch (IOException e)
        {
            System.err.println("Error while initializing Lagoon:");
            System.err.println("I/O error: " + e.getMessage());
            return;
        }
        catch (LagoonException e)
        {
            System.err.println("Error while initializing Lagoon:");
            System.err.println(e.getMessage());
            return;
        }

        System.out.println("Lagoon initialized successfully");

        try {
            if (build)
            {
                System.out.println("Building website...");
                long timeBefore = System.currentTimeMillis();
                processor.build(false);
                long timeElapsed = System.currentTimeMillis()-timeBefore;
                System.out.println("in " + timeElapsed + " ms");
            }
            else if (force)
            {
                System.out.println("Force building website...");
                long timeBefore = System.currentTimeMillis();
                processor.build(true);
                long timeElapsed = System.currentTimeMillis()-timeBefore;
                System.out.println("in " + timeElapsed + " ms");
            }
            else if (interval > 0)
            {
                while (true)
                {
                    System.out.println("Building website...");
                    long timeBefore = System.currentTimeMillis();
                    processor.build(false);
                    long timeElapsed = System.currentTimeMillis()-timeBefore;
                    System.out.println("in " + timeElapsed + " ms");
                    if (timeElapsed < interval)
                    try { Thread.sleep(interval-timeElapsed); } catch (InterruptedException e) {}
                }
            }
            else
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                while (true)
                {
                    System.out.println("Ready");
                    String yn = in.readLine();
					if (yn.length() < 1) break;
                    if (yn.charAt(0) == 'b')
                    {
                        System.out.println("Building website...");
                        long timeBefore = System.currentTimeMillis();
                        processor.build(false);
                        long timeElapsed = System.currentTimeMillis()-timeBefore;
                        System.out.println("in " + timeElapsed + " ms");
                    }
                    else if (yn.charAt(0) == 'f')
                    {
                        System.out.println("Force building website...");
                        long timeBefore = System.currentTimeMillis();
                        processor.build(true);
                        long timeElapsed = System.currentTimeMillis()-timeBefore;
                        System.out.println("in " + timeElapsed + " ms");
                    }
                    else break;
                }
            }

            System.out.println("Exiting Lagoon...");
            processor.destroy();
        }
        catch (FileNotFoundException e)
        {
            System.err.println("File not found: " + e.getMessage());
        }
        catch (IOException e)
        {
            System.err.println("I/O error: " + e.getMessage());
        }
        catch (LagoonException e)
        {
            System.err.println("Error: " + e.getMessage());
        }
        catch (org.xml.sax.SAXException e)
        {
            e.printStackTrace();
        }
	}

    private static String getProperty(String name)
        throws LagoonException
    {
        String value = properties.getProperty(name);
        if (value == null)
            throw new LagoonException("Property " + name + " not specified");

        return value.trim();
    }

}
