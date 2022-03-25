package huwang2020_base;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;

import baseApi.Utils;

public class HwLogin {

	public static String LoginHw(String url, String jsonStr) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = Utils.createSSLClientDefault();
			HttpPost httpPost = new HttpPost("https://" + url + "/adminapi/login");
			httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
			httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
			httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8,en-US;q=0.7,en;q=0.6");
			httpPost.setHeader("Connection", "keep-alive");
			httpPost.setHeader("Cookie", "");
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

			// 获得cookie并返回
			Header[] headers = response.getHeaders("Set-Cookie");
			String Cookie = "";
			String PHPSESSID = "";
			String T = "";
			String refer = "";
			if (headers != null) {
				for (Header header : headers) {
					Pattern rPHPSESSID = Pattern.compile("(PHPSESSID=.*?;)");
					Matcher mPHPSESSID = rPHPSESSID.matcher(header.toString());
					if (mPHPSESSID.find()) {
						PHPSESSID = mPHPSESSID.group(0);
					}
					Pattern rT = Pattern.compile("(T=.*?;)");
					Matcher mT = rT.matcher(header.toString());
					if (mT.find()) {
						T = mT.group(0);
					}
					Pattern rrefer = Pattern.compile("(refer=.*?;)");
					Matcher mrefer = rrefer.matcher(header.toString());
					if (mrefer.find()) {
						refer = mrefer.group(0);
					}
				}
				Cookie = PHPSESSID + T + refer;
				return Cookie;
			}

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
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
