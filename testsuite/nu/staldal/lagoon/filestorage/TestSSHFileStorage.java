package nu.staldal.lagoon.filestorage;

import nu.staldal.lagoon.core.FileStorage;

import java.io.*;

public class TestSSHFileStorage
{
    public static void main(String[] args) throws Exception
    {
		if (args.length < 5)
		{
			System.out.println(
				"Syntax: SSHFileStorage <url> "
				+ "<file to create> <file to check> <file to abort> "
				+ "<file to delete>");
			return;
		}

		FileStorage fs = new SSHFileStorage();
        fs.open(args[0], null, null); // ***
        System.out.println("FileStorage opened");

    	OutputStream os = fs.createFile(args[1]);
        System.out.println("createFile returned");

        PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
		pw.println("<html><head><title>Test</title></head>");
		pw.println("<body><p><i>Testar SSHFileStorage: " + System.currentTimeMillis() + "</i></p></body></html>");
		pw.close();
		fs.commitFile();
		System.out.println("commitFile returned");

		long d = fs.fileLastModified(args[2]);
		System.out.println("fileLastModified(" + args[2] + "): " + d);

		os = fs.createFile(args[3]);
        System.out.println("createFile returned");
		os.write(56);
		os.close();
		fs.discardFile();
		System.out.println("discardFile returned");

		fs.deleteFile(args[4]);
		System.out.println("deleteFile returned");

		fs.close();
		System.out.println("FileStorage closed");
    }
}
