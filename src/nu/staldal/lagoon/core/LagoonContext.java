/*
 * Copyright (c) 2002, Mikael Ståldal
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

/**
 * Defines project-wide services for components.
 */
public interface LagoonContext
{

	/**
	 * Get the temp directory.
	 */
	public File getTempDir();


    /**
     * Read from a file in the repository.
     * Read from the returned InputStream and close() it.
     *
     * @param key  the key to locate the file
     *
     * @return an InputStream to read the file from, or <code>null</code>
     * if the file wasn't found.
     */
    public InputStream readFileFromRepository(String key);


    /**
     * Store a file in the repository.
     * Write to the returned OutputStream and close() it.
     *
     * @param key  the key to locate the file
     *
     * @return  an OutputStream to write to the file
     */
    public OutputStream storeFileInRepository(String key)
        throws IOException;


	/**
     * Get an object from the repository.
     *
     * @param key  the key to locate the object
     *
     * @return  the object, or <code>null</code> if not found
     */
    public Object getObjectFromRepository(String key)
        throws IOException;


	/**
     * Store an object into the repository.
     *
     * @param key  the key to locate the object
     * @param obj  the object to store, must be Serializable
     */
    public void putObjectIntoRepository(String key, Object obj)
        throws IOException;
		

    /**
     * Tell whether the given source can be checked for dependency.
	 *
	 * @param url  URL to the file
     */
	public boolean canCheckFileHasBeenUpdated(String url);
	

    /**
     * Get an absolute File object representing the source root directory.
     */
    public File getSourceRootDir();


    /**
     * Get an URL representing the given file or directory.
	 *
	 * @param url  URL to the file, if relative it's searched for relative to
	 * the base parameter.
	 *
	 * @param base  base URL, must be pseudo-absolute
	 *
	 * @return an absolute or pseudo-absolute URL
	 * 		   (the url parameter unchanged unless it's relative)
     */
    public String getFileURLRelativeTo(String url, String base);
}

