package baseApi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Utils {
	public static String getRandomIp() {
		int[][] range = { { 607649792, 608174079 }, // 36.56.0.0-36.63.255.255
				{ 1038614528, 1039007743 }, // 61.232.0.0-61.237.255.255
				{ 1783627776, 1784676351 }, // 106.80.0.0-106.95.255.255
				{ 2035023872, 2035154943 }, // 121.76.0.0-121.77.255.255
				{ 2078801920, 2079064063 }, // 123.232.0.0-123.235.255.255
		};

		Random rdint = new Random();
		int index = rdint.nextInt(5);
		String ip = num2ip(range[index][0] + new Random().nextInt(range[index][1] - range[index][0]));
		return ip;
	}

	public static String num2ip(int ip) {
		int[] b = new int[4];
		String x = "";
		b[0] = (int) ((ip >> 24) & 0xff);
		b[1] = (int) ((ip >> 16) & 0xff);
		b[2] = (int) ((ip >> 8) & 0xff);
		b[3] = (int) (ip & 0xff);
		x = Integer.toString(b[0]) + "." + Integer.toString(b[1]) + "." + Integer.toString(b[2]) + "."
				+ Integer.toString(b[3]);
		return x;
	}

	public static boolean isJson(String jsonStr) {
		JsonElement jsonElement;
		try {
			jsonElement = new JsonParser().parse(jsonStr);
		} catch (Exception e) {
			return false;
		}
		if (jsonElement == null) {
			return false;
		}
		if (!jsonElement.isJsonObject()) {
			return false;
		}
		return true;
	}

	public static String toJsonString(String json) {
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(jsonObject);
	}

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
					newFileBF.append("username: " + fileLineSrc + "\r\n");
				} else {
					newFileBF.append(line + "\r\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		} finally {
			try {
				if (fileBufferedWriter != null) {
					fileBufferedWriter.close();
				}
			} catch (IOException e2) {
				fileBufferedWriter = null;
			}
		}
	}

	public static CloseableHttpClient createSSLClientDefault() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}

}
