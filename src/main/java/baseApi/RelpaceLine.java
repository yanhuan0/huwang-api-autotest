package baseApi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RelpaceLine {
	
	public static void Relpace(String[] argsStrings) {
		String filePahtDst = argsStrings[0];
		String fileLineSrc = argsStrings[1];
		
		String line = null;
		BufferedReader oldFileBF = null;
		StringBuffer newFileBF = new StringBuffer();
		try {
			oldFileBF = new BufferedReader(new FileReader(filePahtDst));
			while ((line = oldFileBF.readLine()) != null) {
				if (line.startsWith("username")) {
					newFileBF.append("username: "+fileLineSrc+"\r\n");
				} else {
					newFileBF.append(line+"\r\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				oldFileBF.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		BufferedWriter fileBufferedWriter = null;
		try {
			fileBufferedWriter = new BufferedWriter(new FileWriter(filePahtDst));
			fileBufferedWriter.write(newFileBF.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (fileBufferedWriter != null) {
					fileBufferedWriter.close();
				}
			} catch (IOException e2) {
				fileBufferedWriter = null;
			}
		}
	}
}
