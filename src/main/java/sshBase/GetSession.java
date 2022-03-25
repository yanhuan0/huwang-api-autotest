package sshBase;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;  

public class GetSession {
	
	public static Session getSession(Remote remote) throws JSchException {
		JSch jSch = new JSch();
		if (Files.exists(Paths.get(remote.getIdentity()))) {
			jSch.addIdentity(remote.getIdentity(),remote.getPassphrase());
		}
		Session session = jSch.getSession(remote.getUser(),remote.getHost(),remote.getPort());
		session.setPassword(remote.getPassword());
		session.setConfig("StrictHostKeyChecking","no");
		return session;
		
	}
	
	
	
	public static void main(String[] args) throws JSchException {
		int CONNECT_TIMEOUT = 10000;
		Remote remote = new Remote();
		remote.setHost("192.168.31.10");
		remote.setPassword("root");
		remote.setPort(2222);
		Session session = getSession(remote);
		session.connect(CONNECT_TIMEOUT);
		if (session.isConnected()) {
			System.out.println("Host "+remote.getHost()+" connected.");
		}
		session.disconnect();
	}
	
}
