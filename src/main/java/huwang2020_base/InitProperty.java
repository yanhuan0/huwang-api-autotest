package huwang2020_base;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import org.testng.Reporter;

import com.google.gson.JsonObject;

import baseApi.CalculateResponseTime;
import baseApi.Client;

public class InitProperty {
	private static Logger logger = Logger.getLogger(InitProperty.class);
	private String Cookie;
	private String url;
	private String username;
	private String password;
	private String captcha = "111111";
	private String checked = "true";
	private String __crumb__;
	private long threadId;

	public String get__crumb__() {
		return __crumb__;
	}

	public String getCookie() {
		return Cookie;
	}

	public long getThreadId() {
		return threadId;
	}

	public String getUsername() {
		return this.username;
	}

	public String getUrl() {
		return url;
	}

	public HashMap<String, String> apiMap = new HashMap<String, String>();

	public HashMap<String, String> initConfig(String... fileString) {
		String configFileString = "";
		String apiFileString = "";
		threadId = Thread.currentThread().getId();

		try {
			// logger.info(fileString.length);
			if (fileString.length != 0) {
				configFileString = fileString[0];
				apiFileString = fileString[1];
			} else {
				logger.info("Missing file configfile and apifile.");
			}
			FileReader configFile = new FileReader(configFileString);
			BufferedReader userInfo = new BufferedReader(configFile);
			String infoString = "";
			while ((infoString = userInfo.readLine()) != null) {
				if (infoString.startsWith("url")) {
					try {
						url = infoString.split(": ")[1];
						continue;
					} catch (Exception e) {
						url = "";
					}
				}
				if (infoString.startsWith("username")) {
					try {
						this.username = infoString.split(": ")[1];
						continue;
					} catch (Exception e) {
						username = "";
					}
				}
				if (infoString.startsWith("password")) {
					try {
						password = infoString.split(": ")[1];
						continue;
					} catch (Exception e) {
						password = "";
					}
				}
			}
			userInfo.close();
			configFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("initConfig: " + configFileString + "\nusername: " + username + "\tthreadId: " + threadId);
		Reporter.log("Before suit,init url,user,passord successfully.", true);

		try {
			FileReader apiFile = new FileReader(apiFileString);
			BufferedReader apiInfo = new BufferedReader(apiFile);
			String infoString;
			while ((infoString = apiInfo.readLine()) != null) {
				apiMap.put(infoString.split(": ")[0], infoString.split(": ")[1]);
			}
			apiInfo.close();
			apiFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Reporter.log("Before suit,init api.", true);
		logger.info("Before suit,init api.");
		return apiMap;
	}

	public String userLogin() {
		logger.info("登录用户：" + username);
		// password = "e10adc3949ba59abbe56e057f20f883e"; // 123456
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("username", username);
		jsonObject.addProperty("password", password);
		jsonObject.addProperty("captcha", captcha);
		jsonObject.addProperty("checked", checked);
		jsonObject.addProperty("__crumb__", __crumb__);
		// 登录用户获取Cookie
		String cookieWithoutCrumb = LoginHw(url, jsonObject.toString());
		Reporter.log("Login user.", true);
		logger.info("Login user.");
		return cookieWithoutCrumb;
	}

	public void getMyCrumb(String cookieWithoutCrumb) {
		// 登录用户获取Cookie
		__crumb__ = huwang2020_base.HwCrumb.getCrumb(url, cookieWithoutCrumb);
		Cookie = cookieWithoutCrumb + "__crumb__=" + __crumb__;
		// Reporter.log("Before each method,get crumb.",true);
	}

	public void userLogout(String cookieWithoutCrumb) {
		getMyCrumb(cookieWithoutCrumb);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("__crumb__", __crumb__);
		// 登出用户
		LogoutHw(url, Cookie, jsonObject.toString());
		Reporter.log("Logout user.", true);
		logger.info("Logout user.");
	}

	public String LoginHw(String url, String jsonStr) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = baseApi.Utils.createSSLClientDefault();
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

			logger.info(httpPost.getRequestLine().toString());
			response = CalculateResponseTime.executePost(httpClient, httpPost, "/login");
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				logger.info(jObject);
			}
			// 获得cookie并返回
			Header[] headers = response.getHeaders("Set-Cookie");
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
		return Cookie;
	}

	public int LogoutHw(String url, String Cookie, String jsonStr) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		int statusCode = -1;
		try {
			httpClient = baseApi.Utils.createSSLClientDefault();
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

			logger.info(httpPost.getRequestLine().toString());
			response = CalculateResponseTime.executePost(httpClient, httpPost, "/login");
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				logger.info(jObject);
			}
			statusCode = response.getStatusLine().getStatusCode();
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
		return statusCode;
	}

	public CloseableHttpResponse editUserself(String[] args) throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		String contentType = "x-www-form-urlencoded";
		String cookieWithoutCrumb = args[0];
		JsonObject jsonObject = new JsonObject();
		String apiString = "";
		getMyCrumb(cookieWithoutCrumb);
		String uploadStrings[] = { "", "" };
		if (args[1].equals("edit-self")) {
			apiString = apiMap.get(args[1]);
			jsonObject.addProperty("new_password", args[2]);
			jsonObject.addProperty("new_password_confirm", args[2]);
			jsonObject.addProperty("old_password", "e10adc3949ba59abbe56e057f20f883e");
			jsonObject.addProperty("__crumb__", get__crumb__());
			uploadStrings[0] = contentType;
			uploadStrings[1] = jsonObject.toString();
		}
		try {
			response = Client.getPostResponse(getUrl() + apiString, apiString, apiString, getCookie(), uploadStrings);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
}
