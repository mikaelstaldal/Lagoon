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

package nu.staldal.lagoon.filestorage;

import nu.staldal.lagoon.core.FileStorage;
import nu.staldal.lagoon.core.LagoonProcessor;

import java.io.*;
import java.net.MalformedURLException;

/**
 * A FileStorage which transfers files to a remote site using SSH.
 */
public class SSHFileStorage extends RemoteFileStorage
{
	private static final boolean DEBUG = false;

	private String host;
    private int port;
	private String username;
	private String rootPath;

	private Runtime rt;

	private String currentPath = null;
	private Process currentProc = null;


	private Process runSSH(String[] command)
		throws IOException
	{
		String[] cmdline = new String[command.length + ((port>0) ? 9 : 7)];

		cmdline[0] = "ssh";
		cmdline[1] = "-q";
		cmdline[2] = "-e";
		cmdline[3] = "none";
		cmdline[4] = "-l";
		cmdline[5] = username;
		cmdline[6] = host;
        if (port > 0)
        {
    		cmdline[7] = "-p";
	    	cmdline[8] = Integer.toString(port);
        }

		System.arraycopy(command, 0,
                         cmdline, ((port>0) ? 9 : 7), command.length);

        if (DEBUG)
        {
            System.out.println("Executing: ");
            for (int i = 0; i<cmdline.length; i++)
                System.out.println(cmdline[i]);
            System.out.println();
        }

		return rt.exec(cmdline);
	}

    /**
     * Default constructor.
     */
    public SSHFileStorage()
    {
    }

    public boolean needPassword()
    {
        return false;
    }

    public void open(String url, LagoonProcessor processor, String passoword)
        throws MalformedURLException, IOException
    {
		if (!url.startsWith("ssh://"))
			throw new MalformedURLException(url);

		int userPos = 5;
		int hostPos = url.indexOf('@', userPos+1);
		if (hostPos < 0)
			throw new MalformedURLException(url);
		int portPos = url.indexOf(':', hostPos+1);
		int pathPos = url.indexOf('/', ((portPos<0) ? hostPos : portPos)+1);

		String path;

		try
		{
			username = url.substring(userPos+1,hostPos);
			host = url.substring(hostPos+1,(portPos<0) ? pathPos : portPos);
			port = (portPos<0) ? 0 :
                Integer.parseInt(url.substring(portPos+1,pathPos));
			path = url.substring(pathPos+1);
		}
		catch (NumberFormatException e)
		{
			throw new MalformedURLException(url);
		}

		if (port < 0 || port > 65535
                || username.length() < 1
                || host.length() < 1
                || (path.length() > 0 && path.charAt(path.length()-1) != '/'))
			throw new MalformedURLException(url);

		if ((path.length() == 0)
                || (path.charAt(path.length()-1) != '/'))
		{
			this.rootPath = path;
		}
		else
		{
			this.rootPath = path.substring(0,path.length()-1);
		}

		this.rt = Runtime.getRuntime();

        openDateFile(processor);
    }


    /**
     * Close the file system and release any resources it holds.
     *
     * After this method has been invoked, no other method may be invoked.
     */
    public void close()
        throws IOException
    {
        closeDateFile();
	}

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
    public OutputStream createFile(String path)
        throws java.io.IOException
    {
		currentPath = path;
		currentProc = runSSH(new String[] { "mkdir", "-p",
            "`dirname", rootPath+path + "`",
            "&&", "rm", "-f", rootPath+path,
            "&&", "cat", ">" + rootPath+path });

		return currentProc.getOutputStream();
	}

    /**
     * Finishing writing to a file and commits it.
     * Must be invoked when finished writing to the OutputStream
     * createFile has returned.
     *
     * @see #createFile
     */
	public void commitFile()
		// throws java.io.IOException
	{
		try {
			currentProc.waitFor();
		}
		catch (InterruptedException e) {}

        fileModified(currentPath);

		currentProc = null;
		currentPath = null;
	}

    /**
     * Discards a new file and delete it.
     *
     * @see #createFile
     */
    public void discardFile()
        throws java.io.IOException
    {
		try {
			currentProc.waitFor();
		}
		catch (InterruptedException e) {}

		currentProc = null;

		Process proc = runSSH(new String[] {
            "rm", "-f", rootPath+currentPath });

        proc.getOutputStream().close();

		try {
			proc.waitFor();
		} catch (InterruptedException e) {}

		currentPath = null;
	}

    /**
     * Deletes a file.
     * Does not signal any error if the file doesn't exist.
	 *
     * @param path  path to the file
     */
    public void deleteFile(String path)
        throws java.io.IOException
    {
		Process proc = runSSH(new String[] {
            "rm", "-f", rootPath+path });

        proc.getOutputStream().close();

		try {
			proc.waitFor();
		} catch (InterruptedException e) {}
	}

}
