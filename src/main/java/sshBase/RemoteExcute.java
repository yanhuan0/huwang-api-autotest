package sshBase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class RemoteExcute {

	public static List<String> remoteExcute(Session session,String command) {
		int CONNECT_TIMEOUT = 10000; 
		List<String> reslultLines = new ArrayList<>();
		ChannelExec channel = null;
		try {
			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			InputStream input = channel.getInputStream();
			channel.connect(CONNECT_TIMEOUT);
			try {
				BufferedReader inputReader = new BufferedReader(new InputStreamReader(input));
				String inputLine = null;
				System.out.println("# "+command);
				while ((inputLine = inputReader.readLine()) != null) {
					System.out.println(inputLine);
					reslultLines.add(inputLine);
				}
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (Exception e) {
						System.out.println("JSch inputStream close error.");
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if (channel != null) {
				try {
					channel.disconnect();
				} catch (Exception e2) {
					// TODO: handle exception
					System.out.println("JSch channel disconnect error.");
					e2.printStackTrace();
				}
			}
		}
		return reslultLines;
	}
	
	public static void main(String[] args) throws JSchException {
		int CONNECT_TIMEOUT = 10000;
		Remote remote = new Remote();
		remote.setHost("192.168.31.10");
		remote.setPassword("root");
		remote.setPort(2222);
		Session session = GetSession.getSession(remote);
		session.connect(CONNECT_TIMEOUT);
		if (session.isConnected()) {
			System.out.println("Host "+remote.getHost()+" connected.");
		}
		remoteExcute(session, "pwd");
		remoteExcute(session, "ls -l");
		session.disconnect();
	}
}
