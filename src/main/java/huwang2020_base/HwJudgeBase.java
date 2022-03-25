package huwang2020_base;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.log4j.Logger;
import org.testng.Reporter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import baseApi.Client;

public class HwJudgeBase {

	private Logger logger = Logger.getLogger(HwJudgeBase.class);
	private String Cookie = "";
	private String url = "";
	private String username = "";
	private String password = "";
	private String captcha = "111111";
	private String checked = "true";
	private String __crumb__ = "111111";
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

	private final HashMap<String, String> apiMap = new HashMap<String, String>();

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
						username = infoString.split(": ")[1];
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
			logger.info("Open config.txt failed.");
		}
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
			logger.info("Open api.txt failed.");
		}
		Reporter.log("Before suit,init api.", true);
		logger.info("Before suit,init api.");
		return apiMap;
	}

	public String userLogin() {
		logger.info(username);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("username", username);
		jsonObject.addProperty("password", password);
		jsonObject.addProperty("captcha", captcha);
		jsonObject.addProperty("checked", checked);
		jsonObject.addProperty("__crumb__", __crumb__);
		// logger.info(jsonObject);
		String cookieWithoutCrumb = HwLogin.LoginHw(url, jsonObject.toString());
		// logger.info("before class: " + cookieWithoutCrumb);
		Reporter.log("Before class,login user.", true);
		return cookieWithoutCrumb;
	}

	public void getMyCrumb(String cookieWithoutCrumb) {
		__crumb__ = huwang2020_base.HwCrumb.getCrumb(url, cookieWithoutCrumb);
		Cookie = cookieWithoutCrumb + "__crumb__=" + __crumb__;
		// Reporter.log("Before each method,get crumb.",true);
	}

	public void userLogout(String cookieWithoutCrumb) {
		getMyCrumb(cookieWithoutCrumb);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("__crumb__", __crumb__);
		HwLogout.LogoutHw(url, Cookie, jsonObject.toString());
		Reporter.log("After class,logout user.", true);
	}

	public CloseableHttpResponse judgeUiBase(String[] argString) {
		String cookieWithoutCrumb = argString[0];
		String api = apiMap.get(argString[1]);
		getMyCrumb(cookieWithoutCrumb);
		CloseableHttpResponse response = baseApi.Client.getGetResponse(url + api, api, api, Cookie);
		return response;

	}

	public List<String> getReportId(String[] args) throws ParseException, IOException {
		String apiString = "";
		if (args[0].equals("attacker")) {
			apiString = "adminapiAttackerReportJudgeStatus2";
		} else {
			apiString = "adminapiVictimReportJudgeStatus2";
		}
		List<String> reportList = new ArrayList<String>();
		String cookieWithoutCrumb = args[1];
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString) + "page=1&";
		CloseableHttpResponse response = baseApi.Client.getGetResponse(url + api, api, apiMap.get(apiString), Cookie);
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			JsonObject dataObject = jObject.get("data").getAsJsonObject();
			Double reportNumString = 0.0;
			if (dataObject.has("total_num")) {
				reportNumString = jObject.get("data").getAsJsonObject().get("total_num").getAsDouble();
			} else if (dataObject.has("total")) {
				reportNumString = jObject.get("data").getAsJsonObject().get("total").getAsDouble();
			}
			int pagesNum = (int) Math.ceil(reportNumString / 10);
			for (int i = 0; i < pagesNum; i++) {
				String apiPages = apiMap.get(apiString) + "page=" + (i + 1) + "&";
				CloseableHttpResponse pagesResponse = Client.getGetResponse(url + apiPages, apiPages,
						apiMap.get(apiString), Cookie);
				JsonObject pagesObject = Client.getResponseDataJson(pagesResponse);
				JsonArray ReportArray = pagesObject.get("data").getAsJsonObject().get("list").getAsJsonArray();
				int singlePageReportNum = ReportArray.size();
				for (int j = 0; j < singlePageReportNum; j++) {
					String id = ReportArray.get(j).getAsJsonObject().get("id").getAsString().replace("\"", "");
					String status = ReportArray.get(j).getAsJsonObject().get("status").getAsString().replace("\"", "");
					if ("2".equals(status)) {
						reportList.add(id);
					}
				}
			}
		}
		return reportList;
	}

	public JsonObject getReportNode(String[] args) throws ParseException, IOException {
		String apiString = "adminapiReportJudgeId";
		String reportIdString = args[0];
		String cookieWithoutCrumb = args[1];
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString) + reportIdString + "?status=2?";
		JsonArray linkArray = new JsonArray();
		JsonArray otherArray = new JsonArray();
		CloseableHttpResponse response = baseApi.Client.getGetResponse(url + api, api, apiMap.get(apiString), Cookie);
		JsonObject jsonObject = new JsonObject();
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			JsonObject dataObject = jObject.get("data").getAsJsonObject();
			JsonObject nodeObject = dataObject.get("nodes").getAsJsonObject();
			linkArray = nodeObject.get("link").getAsJsonArray();
			otherArray = nodeObject.get("other").getAsJsonArray();
			String reportType = dataObject.get("type").getAsString();
			int linkNum = linkArray.size();
			int otherNum = otherArray.size();
			JsonArray linkNodesArray = new JsonArray();
			JsonArray otherNodesArray = new JsonArray();
			JsonArray vulArray = new JsonArray();

			for (int i = 0; i < linkNum; i++) {
				JsonObject nodeelementObject = linkArray.get(i).getAsJsonObject();

				// node��annexes�ڵ㣬����images�б������������ط����涼��
				JsonArray annexesArray = nodeelementObject.get("annexes").getAsJsonArray();
				int annexesNum = annexesArray.size();
				for (int j = 0; j < annexesNum; j++) {
					JsonObject annexesObject = annexesArray.get(j).getAsJsonObject();
					String url = annexesObject.get("url").getAsString();
					JsonArray imagesArray = new JsonArray();
					imagesArray.add(url);
					nodeelementObject.add("images", imagesArray);
				}
				// �޸�ÿ��ram�ķ���Ϊ0-9�֣������������ط�����
				JsonArray ramsArray = nodeelementObject.get("rams").getAsJsonArray();
				int ramsNum = ramsArray.size();
				for (int k = 0; k < ramsNum; k++) {
					JsonObject ramsObject = ramsArray.get(k).getAsJsonObject();
					int item_score = ramsObject.get("item_score").getAsInt() % 9;
					ramsObject.addProperty("item_score", item_score);
				}
				if (nodeelementObject.has("tags")) {
					// ������©����֣�ֻ�й������������
					JsonArray tagsArray = nodeelementObject.get("tags").getAsJsonArray();
					int tagsNum = tagsArray.size();
					for (int l = 0; l < tagsNum; l++) {
						JsonObject tagsObject = tagsArray.get(l).getAsJsonObject();
						String tagId = tagsObject.get("id").getAsString();
						if (tagId.equals("56") || tagId.equals("57")) {
							JsonArray vulnerabilityArray = tagsObject.get("vulnerability").getAsJsonArray();
							int vulnerabilityNum = vulnerabilityArray.size();
							for (int m = 0; m < vulnerabilityNum; m++) {
								JsonObject vulObject = new JsonObject();
								JsonObject vulnerabilityObject = vulnerabilityArray.get(m).getAsJsonObject();
								String vulnerabilityId = vulnerabilityObject.get("id").getAsString();
								String score = "";
								if (vulnerabilityObject.has("score")) {
									score = vulnerabilityObject.get("score").getAsString();
									if (!score.isEmpty()) {
										score = "10";
									}
								}
								vulnerabilityObject.addProperty("score", score);
								vulObject.addProperty("id", vulnerabilityId);
								vulObject.addProperty("score", score);
								vulArray.add(vulObject);
							}

						}
					}
					nodeelementObject.addProperty("index", "0");
				}
				linkNodesArray.add(nodeelementObject);
			}

			for (int i = 0; i < otherNum; i++) {
				JsonObject nodeelementObject = otherArray.get(i).getAsJsonObject();

				// node��annexes�ڵ㣬����images�б������������ط�����
				JsonArray annexesArray = nodeelementObject.get("annexes").getAsJsonArray();
				int annexesNum = annexesArray.size();
				for (int j = 0; j < annexesNum; j++) {
					JsonObject annexesObject = annexesArray.get(j).getAsJsonObject();
					String url = annexesObject.get("url").getAsString();
					JsonArray imagesArray = new JsonArray();
					imagesArray.add(url);
					nodeelementObject.add("images", imagesArray);
				}
				// �޸�ÿ��ram�ķ���Ϊ0-9�֣������������ط�����
				JsonArray ramsArray = nodeelementObject.get("rams").getAsJsonArray();
				int ramsNum = ramsArray.size();
				for (int k = 0; k < ramsNum; k++) {
					JsonObject ramsObject = ramsArray.get(k).getAsJsonObject();
					int item_score = ramsObject.get("item_score").getAsInt() % 9;
					ramsObject.addProperty("item_score", item_score);
				}
				if (nodeelementObject.has("tags")) {
					// ������©�����
					JsonArray tagsArray = nodeelementObject.get("tags").getAsJsonArray();
					int tagsNum = tagsArray.size();
					for (int l = 0; l < tagsNum; l++) {
						JsonObject tagsObject = tagsArray.get(l).getAsJsonObject();
						String tagId = tagsObject.get("id").getAsString();
						if (tagId.equals("56") || tagId.equals("57")) {
							JsonArray vulnerabilityArray = tagsObject.get("vulnerability").getAsJsonArray();
							int vulnerabilityNum = vulnerabilityArray.size();
							for (int m = 0; m < vulnerabilityNum; m++) {
								JsonObject vulObject = new JsonObject();
								JsonObject vulnerabilityObject = vulnerabilityArray.get(m).getAsJsonObject();
								String vulnerabilityId = vulnerabilityObject.get("id").getAsString();
								String score = "";
								if (vulnerabilityObject.has("score")) {
									score = vulnerabilityObject.get("score").getAsString();
									if (!score.isEmpty()) {
										score = "10";
									}
								}
								vulnerabilityObject.addProperty("score", score);
								vulObject.addProperty("id", vulnerabilityId);
								vulObject.addProperty("score", score);
								vulArray.add(vulObject);
							}

						}
					}
					nodeelementObject.addProperty("index", "0");
				}

				otherNodesArray.add(nodeelementObject);
			}
			jsonObject.addProperty("status", 5);
			String privilege_level = dataObject.get("privilege_level").getAsString();
			if (privilege_level.equals("0")) {
				jsonObject.addProperty("privilege_level", "1");
			} else {
				jsonObject.addProperty("privilege_level", "0");
			}
			jsonObject.addProperty("score", 0);
			jsonObject.addProperty("comments", "comments");
			jsonObject.addProperty("last_update_ts", dataObject.get("update_ts").getAsString());
			if (reportType.equals("1")) {
				jsonObject.addProperty("has_technical_tactical", "Describe the attack process.");
				jsonObject.addProperty("org_dst_id", dataObject.get("org_dst_id").getAsString());
				jsonObject.addProperty("attacker_ip", dataObject.get("attacker_ip").getAsString());
				jsonObject.addProperty("has_target", dataObject.get("has_target").getAsString());
				jsonObject.addProperty("problem", dataObject.get("problem").getAsString());
				jsonObject.addProperty("suggestion", dataObject.get("suggestion").getAsString());
				jsonObject.addProperty("org_dst_name", dataObject.get("org_dst_name").getAsString());
				jsonObject.addProperty("path_desc", dataObject.get("path_desc").getAsString());
				jsonObject.add("vulnerability", vulArray);
			}
			jsonObject.add("linkNodes", linkNodesArray);
			jsonObject.add("otherNodes", otherNodesArray);
			jsonObject.addProperty("id", dataObject.get("id").getAsString());
			jsonObject.addProperty("is_continue", "false");
			jsonObject.addProperty("__crumb__", __crumb__);
		}
		return jsonObject;
	}

	public CloseableHttpResponse JudgeReport(String[] args) throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		String contentType = "application/json";
		String reportId = args[0];
		String jsonString = args[1];
		String cookieWithoutCrumb = args[2];
		String apiString = "ReportJudgePost";
		String api = apiMap.get(apiString) + reportId;
		getMyCrumb(cookieWithoutCrumb);
		String uploadStrings[] = { contentType, jsonString };
		try {
			response = Client.getPostResponse(url + api, api, apiMap.get(apiString), Cookie, uploadStrings);
			logger.info(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
}
