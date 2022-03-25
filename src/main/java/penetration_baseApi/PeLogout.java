package penetration_baseApi;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;

import baseApi.Utils;

public class PeLogout {
	private static CloseableHttpClient httpClient = null;
	private static CloseableHttpResponse response = null;
	private static int statusCode;

	public static int Logout(String url, String Cookie, String jsonStr) {
		try {
			httpClient = Utils.createSSLClientDefault();
			HttpPost httpPost = new HttpPost("https://" + url + "/adminapi/logout");
			httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
			httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
			httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8,en-US;q=0.7,en;q=0.6");
			httpPost.setHeader("Connection", "keep-alive");
			httpPost.setHeader("Cookie", Cookie);
			httpPost.setHeader("Host", url);
			httpPost.setHeader("Referer", "https://" + url + "/login");
			httpPost.setHeader("Sec-Fetch-Dest", "empty");
			httpPost.setHeader("Sec-Fetch-Mode", "cors");
			httpPost.setHeader("Sec-Fetch-Site", "same-origin");
			httpPost.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36");
			httpPost.setHeader("X-Requested-With", "XMLHttpRequest");

			StringEntity se = new StringEntity(jsonStr);
			se.setContentType("text/json");
			se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
			httpPost.setEntity(se);

			response = httpClient.execute(httpPost);
			statusCode = response.getStatusLine().getStatusCode();
			return statusCode;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return statusCode;
	}
}
