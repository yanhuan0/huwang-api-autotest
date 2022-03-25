package huwang2020_base;

import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import baseApi.CalculateResponseTime;

public class HwCrumb {

	private static Logger logger = Logger.getLogger(HwCrumb.class);
	private static String charSet = "UTF-8";
	private static CloseableHttpClient httpClient = null;
	private static CloseableHttpResponse response = null;

	public static String getCrumb(String url, String Cookie) {
		try {

			long timestamp = new Date().getTime();
			httpClient = baseApi.Utils.createSSLClientDefault();
			HttpGet httpGet = new HttpGet("https://" + url + "/adminapi/crumb?_=" + timestamp);
			httpGet.setHeader("Content-Type", "application/json; charset=UTF-8");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8,en-US;q=0.7,en;q=0.6");
			httpGet.setHeader("Connection", "keep-alive");
			httpGet.setHeader("Cookie", Cookie);
			httpGet.setHeader("Host", url);
			httpGet.setHeader("Referer", "https://" + url + "/login");
			httpGet.setHeader("Sec-Fetch-Dest", "empty");
			httpGet.setHeader("Sec-Fetch-Mode", "cors");
			httpGet.setHeader("Sec-Fetch-Site", "same-origin");
			httpGet.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36");
			httpGet.setHeader("X-Requested-With", "XMLHttpRequest");

			// response = httpClient.execute(httpGet);
			logger.info(httpGet.getRequestLine().toString());
			response = CalculateResponseTime.executeGet(httpClient, httpGet, "/adminapi/crumb?_=");
			if (response != null) {
				HttpEntity httpEntity = response.getEntity();
				if (httpEntity != null) {
					String entityString = EntityUtils.toString(httpEntity, charSet);
					EntityUtils.consume(httpEntity);
					String entityjsonString = toJsonString(entityString);
					JsonParser jParser = new JsonParser();
					JsonObject jObject = jParser.parse(entityjsonString).getAsJsonObject();
					String dataString = jObject.get("data").getAsString();
					return dataString;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;

	}

	private static String toJsonString(String json) {
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(jsonObject);
	}

}
