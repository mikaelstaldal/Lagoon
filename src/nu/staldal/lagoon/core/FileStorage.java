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

/**
 * Represent a FileStorage where generated files can be stored.
 *
 * File paths must be specified in UNIX style, i.e. with '/' used for
 * separating directories. File paths <em>must</em> begin with a '/',
 * they are all relative to the location set by the open() method.
 *
 * A FileStorage implementation must have a public no-arg constructor,
 * to enable instantiation with Class.newInstance().
 */
public interface FileStorage
{
    /**
     * Ask if this file storage needs a password.
     */
    public boolean needPassword();

	/**
	 * Open and initialize this file storage.
     *
     * @param url  The URL to the file storage,
     * @param processor  the LagoonProcessor.
     * @param password the password nessesary to access this file storage,
     *        or <code>null</code> if no password is nessesary.
     *
     * @throws AuthenticationException  if password was needed but incorrect.
	 */
	public void open(String url, LagoonProcessor processor, String password)
        throws java.net.MalformedURLException, java.io.IOException,
               AuthenticationException;

    /**
     * Close the file system and release any resources it holds.
     *
     * After this method has been invoked, no other method may be invoked.
     */
    public void close()
        throws java.io.IOException;

    /**
     * Check if a file exists and when it was last modified.
     *
     * @param path  path to the file
     *
     * @return  the time when the file was last modified,
     * or -1 if that information is not avaliable
     * or 0 if the file doesn't exists.
     */
    public long fileLastModified(String path)
        throws java.io.IOException;

    /**
     * Create a new file, or overwrite an existing file.
     * Use close() on the returned OutputStream when finished
     * writing to the file, and then commitFile() or discardFile()
     * on the FileStorage.
     *
     * @param path  path to the file
     *
     * @return an OutputStream to write to
     *
     * @see #commitFile
     * @see #discardFile
     */
    public java.io.OutputStream createFile(String path)
        throws java.io.IOException;


    /**
     * Finishing writing to a file and commits it.
     * Must be invoked when finished writing to the OutputStream
     * createFile has returned.
     *
     * @see #createFile
     */
	public void commitFile()
		throws java.io.IOException;

		
    /**
     * Discards a new file and delete it.
     *
     * @see #createFile
     */
    public void discardFile()
        throws java.io.IOException;

		
    /**
     * Deletes a file.
     * Does not signal any error if the file doesn't exist.
	 *
     * @param path  path to the file
     */
    public void deleteFile(String path)
        throws java.io.IOException;
}
