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

import java.io.*;
import java.util.*;

/**
 * The main worker class of the Lagoon core.
 *
 * Initialized with InputStream for sitemap,
 * a source dir, a repository dir and a target storage URL.
 * Then building the website may be done several times,
 * until destroy() is invoked.
 *
 * This class is not thread-safe. The build() and destroy() methods must not
 * be invoked concurrently from different threads.
 */
public class LagoonProcessor
{
	private static final boolean DEBUG = false;

    private final String targetURL;
    private final FileStorage targetLocation;
    private File repositoryDir;
    private long sitemapLastUpdated;

    protected final Configuration config;
    protected Sitemap sitemap;


    /**
     * Constructor. Reads the configuration file.
     *
     * @param targetURL  where to put the generated files,
	 *                   must be an absolute URL or a local file path
     */
    public LagoonProcessor(String targetURL)
        throws IOException, LagoonException
    {
        this.targetURL = targetURL;
        config = new Configuration();

        InputStream configFile = getClass().getResourceAsStream("config.xml");
        if (configFile == null)
        {
            throw new LagoonException("Main configuration file not found");
        }
        else
        {
            config.readFile(configFile);
        }

        String configFileName =
            System.getProperty("nu.staldal.lagoon.ConfigFile");
        if (configFileName != null)
            config.readFile(new FileInputStream(configFileName));

        targetLocation = config.createFileStorage(targetURL);
    }

    /**
     * Ask if the file storage needs a password.
     *
     */
    public boolean needPassword()
    {
        return targetLocation.needPassword();
    }

    /**
     * Initialize this processor.
	 *
	 * The InputStream for the sitemap is not used after
	 * this method returns.
     *
     * @param sitemap  the Sitemap file
     * @param sitemapLastUpdated  when the Sitemap was last updated,
     *                            or -1 if unknown
     * @param sourceDir  where the source files are
     * @param repositoryDir  where to put the repository
     * @param password  password to access the target storage, or
     *                  <code>null</code> if not nessesary.
     */
    public void init(InputStream sitemap,
                     long sitemapLastUpdated,
                     File sourceDir,
                     File repositoryDir,
                     String password)
        throws IOException, LagoonException, AuthenticationException
    {
        if (!repositoryDir.isDirectory())
            throw new LagoonException(
                "repositoryDir must be an existing directory: "
                + repositoryDir);
        this.repositoryDir = repositoryDir;

        targetLocation.open(targetURL, this, password);

        this.sitemap = new Sitemap(this, sitemap, config,
                                   sourceDir, targetLocation);
        this.sitemapLastUpdated = sitemapLastUpdated;
    }

    /**
     * Perform the building of the website.
     * May be invoked multiple times.
     * Synchronous, returns when the building is complete.
     *
     * @param force force a rebuild of all files, otherwise dependency
     *        checking is used to check which files that needs rebuilding.
     */
    public void build(boolean force)
        throws IOException, org.xml.sax.SAXException
    {
        for (Enumeration e = sitemap.getEntries(); e.hasMoreElements(); )
        {
            FileEntry ent = (FileEntry)e.nextElement();
            ent.build(force);
        }
    }

	/**
	 * Dispose this object and release any resources it holds.
	 * This causes the FileStorage to be closed.
	 */
	public void destroy()
        throws IOException
	{
		targetLocation.close();
	}


    /**
     * Get the time when the sitemap was last updated.
     */
    public long getSitemapLastUpdated()
    {
        return sitemapLastUpdated;
    }


    /**
     * Read from a file in the repository.
     * Read from the returned InputStream and close() it.
     *
     * @param key  the key to locate the file
     *
     * @return an InputStream to read the file from, or <code>null</code>
     * if the file wasn't found.
     */
    public InputStream readFileFromRepository(String key)
    {
        return readFileFromRepository(null, key);
    }

    InputStream readFileFromRepository(String dir, String key)
    {
        File theDir = (dir == null)
                    ? repositoryDir
                    : new File(repositoryDir, dir);

        try {
            return new FileInputStream(new File(theDir, key));
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
    }

    /**
     * Store a file in the repository.
     * Write to the returned OutputStream and close() it.
     *
     * @param key  the key to locate the file
     *
     * @return  an OutputStream to write to the file
     */
    public OutputStream storeFileInRepository(String key)
        throws IOException
    {
        return storeFileInRepository(null, key);
    }

    OutputStream storeFileInRepository(String dir, String key)
        throws IOException
    {
        File theDir = (dir == null)
                    ? repositoryDir
                    : new File(repositoryDir, dir);
        theDir.mkdir();

        return new FileOutputStream(new File(theDir, key));
    }

    /**
     * Get an object from the repository.
     *
     * @param key  the key to locate the object
     *
     * @return  the object, or <code>null</code> if not found
     */
    public Object getObjectFromRepository(String key)
        throws IOException
    {
        return getObjectFromRepository(null, key);
    }

    Object getObjectFromRepository(String dir, String key)
        throws IOException
    {
        InputStream is = readFileFromRepository(dir, key);
        if (is == null) return null;

        ObjectInputStream ois = new ObjectInputStream(is);
        try {
            return ois.readObject();
        }
        catch (ClassNotFoundException e)
        {
            if (DEBUG) System.out.println(e);
            return null;
        }
        catch (ObjectStreamException e)
        {
            if (DEBUG) System.out.println(e);
            return null;
        }
        catch (ClassCastException e)
        {
            if (DEBUG) System.out.println(e);
            return null;
        }
    }

    /**
     * Store an object into the repository.
     *
     * @param key  the key to locate the object
     * @param obj  the object to store, must be Serializable
     */
    public void putObjectIntoRepository(String key, Object obj)
        throws IOException
    {
        putObjectIntoRepository(null, key, obj);
    }

    void putObjectIntoRepository(String dir, String key, Object obj)
        throws IOException
    {
        OutputStream os = storeFileInRepository(dir, key);

        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(obj);
    }
}
