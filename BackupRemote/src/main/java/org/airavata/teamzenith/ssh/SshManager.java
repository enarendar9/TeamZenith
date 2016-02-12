package org.airavata.teamzenith.ssh;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.airavata.teamzenith.config.SSHPropertyHandler;
import org.airavata.teamzenith.exceptions.ExceptionHandler;
import org.airavata.teamzenith.utils.PbsConstants;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SshManager {

	public SshManager() {

	}

	public boolean transferFile(Session session, String fileName, String dirPath,String dest)
			throws IOException, JSchException, ExceptionHandler {
		SshUtil ssh = new SshUtil();
		try {

			SSHPropertyHandler sph;
			sph = new SSHPropertyHandler();
			/* Transfer related files to remote machine */
			Properties pr = sph.getPropertyMap();
//			String scriptSource = pr.getProperty("scriptdirectory") + fileName;
			String scriptSource = dirPath+fileName;

			if (dest == null)
				dest = pr.getProperty("destination");
			String scriptDest = dest + fileName;
			// File f=new File(getClass().getResource("/a.c").getFile());
			// System.out.println(f);
			if (ssh.ScpTo(session, scriptSource, scriptDest) != true) {
				System.out.println("Script file copy failed");
				return false;
			}

			return true;
		} 
		catch(ExceptionHandler e){
			throw new ExceptionHandler(e);
		}finally {
		}

	}

	public boolean compileSource(Session session, String language, String artifact)
			throws IOException, JSchException, ExceptionHandler {

		SshUtil ssh = new SshUtil();
		try {
			SSHPropertyHandler sph;
			Properties pr;
			sph = new SSHPropertyHandler();
			pr = sph.getPropertyMap();
			String destSource = pr.getProperty("destination") + artifact;
			String compileCommand = PbsConstants.compileCmd + " " + destSource + " -o Assignment1/" + artifact + ".out";
			System.out.println("Compile command is " + compileCommand);
			if (ssh.executeCommand(session, compileCommand) != true) {
				System.out.println("Input file compilation failed");
				return false;
			}

			return true;
		} finally {
		}

	}

	public boolean submitJob(Session session, String artifact) throws IOException, JSchException, ExceptionHandler {
		SshUtil ssh = new SshUtil();
		try {
			SSHPropertyHandler sph;
			Properties pr;
			sph = new SSHPropertyHandler();
			pr = sph.getPropertyMap();
			String qsubCommand = PbsConstants.torqueCmd + " " + pr.getProperty("destination") + artifact;
			System.out.println("Command is " + qsubCommand);
			if (ssh.executeCommand(session, qsubCommand) != true) {
				System.out.println("Job Scheduling Failed");
				return false;
			}

			return true;
		} 
		catch(ExceptionHandler e){
			throw new ExceptionHandler(e);
		}
		finally {
		}
	}
}

