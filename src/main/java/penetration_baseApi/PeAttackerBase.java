package penetration_baseApi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.testng.Reporter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import baseApi.Client;
import baseApi.GetIp;

public class PeAttackerBase {
	public static String Cookie = "";
	public static String url = "";
	public static String username = "";
	public static String password = "";
	public static String captcha = "111111";
	public static String checked = "1";
	public static String __crumb__ = "111111";

	public static final HashMap<String, String> apiMap = new HashMap<String, String>();

	public static void initConfig(String... fileString) {
		String configFileString = "";
		String apiFileString = "";
		try {
			// System.out.println(fileString.length);
			if (fileString.length != 0) {
				configFileString = fileString[0];
				apiFileString = fileString[1];
			} else {
				System.out.println("Missing file configfile and apifile.");
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
			System.out.println("Open config.txt failed.");
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
			System.out.println("Open api.txt failed.");
		}
		Reporter.log("Before suit,init api.", true);
	}

	public static String userLogin() {
		System.out.println(username);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("username", username);
		jsonObject.addProperty("password", password);
		jsonObject.addProperty("captcha", captcha);
		jsonObject.addProperty("is_agreement", checked);
		jsonObject.addProperty("__crumb__", __crumb__);
		// System.out.println(jsonObject);
		// 登录用户获取Cookie
		String cookieWithoutCrumb = PeLogin.LoginHw(url, jsonObject.toString());
		// System.out.println("before class: " + cookieWithoutCrumb);
		Reporter.log("Before class,login admin user.", true);
		return cookieWithoutCrumb;
	}

	public static void getMyCrumb(String cookieWithoutCrumb) {
		// 登录用户获取Cookie
		__crumb__ = PeCrumb.getCrumb(url, cookieWithoutCrumb);
		Cookie = cookieWithoutCrumb + "__crumb__=" + __crumb__;
		// Reporter.log("Before each method,get crumb.",true);
	}

	public static void userLogout(String cookieWithoutCrumb) {
		getMyCrumb(cookieWithoutCrumb);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("__crumb__", __crumb__);
		// 登出用户
		PeLogout.Logout(url, Cookie, jsonObject.toString());
		Reporter.log("After class,logout admin user.", true);
	}

	public static CloseableHttpResponse attackerUiBase(String[] argString) {
		String cookieWithoutCrumb = argString[0];
		String api = argString[1];
		getMyCrumb(cookieWithoutCrumb);
		CloseableHttpResponse response = PeBaseClass.getGetResponse(url, api, Cookie);
		return response;
	}

	public static String getActiveProject(String[] argString) {
		String activeProjectId = "";
		String apiString = argString[1];
		String cookieWithoutCrumb = argString[0];
		String api = apiMap.get(apiString);
		String[] uploadStrings = { cookieWithoutCrumb, api };
		CloseableHttpResponse response = attackerUiBase(uploadStrings);
		if (response != null) {
			try {
				JsonObject jObject = PeBaseClass.getResponseDataJson(response);
				activeProjectId = jObject.get("data").getAsString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return activeProjectId;
	}

	public static String[] uploadFile(String[] args) throws ParseException, IOException {
		CloseableHttpResponse response = null;
		String filePath = args[0];
		String apiString = "uploadFile";
		String fileName = filePath.split("\\\\")[filePath.split("\\\\").length - 1];
		String keyString = args[1];
		;
		String api = apiMap.get(apiString);
		getMyCrumb(args[2]);
		String[] resultString = { "", "" };
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("attachment", "(binary)");
		jsonObject.addProperty("key", keyString);
		// jsonObject.addProperty("__crumb_", __crumb__);
		String jsonStr = jsonObject.toString();
		// System.out.println(jsonStr);
		// System.out.println(filePath);
		String[] uploadStrings = { "formData", jsonStr, "attachment", filePath, "image/jpeg", fileName };
		try {
			response = PeBaseClass.getPostResponse(url, api, Cookie, uploadStrings);
			// System.out.println(response);
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				resultString[0] = jObject.get("data").getAsJsonObject().get("attachment").getAsString();
				resultString[1] = jObject.get("data").getAsJsonObject().get("name").getAsString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultString;
	}

	public static CloseableHttpResponse attackerReport(String[] args) throws ParseException, IOException {
		String logoPath = args[0];
		String cookieWithoutCrumb = args[1];
		getMyCrumb(cookieWithoutCrumb);
		String[] uploadStrings = { logoPath, "report", cookieWithoutCrumb }; // 上传文件位置
		String[] uploadResultStrings = uploadFile(uploadStrings);
		String attachmentString = uploadResultStrings[0];
		// ============================>>开始生成整个报告<<==========================================
		// ============================>>开始生成linkNodes<<===========================
		Random random = new Random();
		JsonObject attackLinkNodesObject = new JsonObject();
		String randomIp = GetIp.getRandomIp();
		String randomInt = String.valueOf(random.nextInt(200));
		String targetName = "XXX-Server" + randomInt;
		String targetIp = randomIp;
		String targetUrl = "https://www.OA-Server" + randomInt + ".com";
		attackLinkNodesObject = getAttackLinkNodesObject(attachmentString, targetName, targetIp, targetUrl);

		// ============================>>结束生成linkNodes<<===========================
		// ============================>>结束生成整个报告<<==========================================
		String jsonStr = attackLinkNodesObject.toString();
		String apiString = "reportCreate";
		String[] argStrings = { cookieWithoutCrumb, "active-project" };
		String projectIdString = getActiveProject(argStrings);
		String api = apiMap.get(apiString) + projectIdString;
		String contentType = "application/json";
		// System.out.println(jsonStr);
		String[] uploadString = { contentType, jsonStr };
		getMyCrumb(cookieWithoutCrumb);
		CloseableHttpResponse response = PeBaseClass.getPostResponse(url, api, Cookie, uploadString);
		return response;
	}

	public static JsonElement getRamsObject(String parentId, int[] value) {
		Random random = new Random();
		JsonObject ramsElement = new JsonObject();
		JsonArray childrenArray = new JsonArray();
		ramsElement.addProperty("parent_id", parentId);
		for (int i = 0; i < value.length; i++) {
			JsonObject childrenObject = new JsonObject();
			childrenObject.addProperty("id", String.valueOf(value[i]));
			if (value[i] >= 30) {
				childrenObject.addProperty("value", String.valueOf(3 + random.nextInt(5) + "文本"));
			} else {
				childrenObject.addProperty("value", 3 + random.nextInt(5));
			}
			childrenArray.add((JsonElement) childrenObject);
		}
		ramsElement.add("children", (JsonElement) childrenArray);
		return ramsElement;
	}

	public static JsonArray getRamsArray(int ramsNum) {
		JsonArray ramsArray = new JsonArray(); // 定义列表rams
		String ramsString = "{\"1\":[2,3],\"4\":[4],\"5\":[5],\"6\":[7,8],\"9\":[10,11],\"12\":[13,14],\"15\":[15],"
				+ "\"16\":[17],\"18\":[18],\"19\":[20,21],\"22\":[23,24],\"25\":[26],\"27\":[28,29]," + "\"30\":[30]}"; // 所有的可选的14个rams
		JsonObject ramsStringObject = new JsonParser().parse(ramsString).getAsJsonObject();
		String[] ramsParentId = new String[15]; // 根据所有rams的长度定义
		int i = 0;
		for (Entry<String, JsonElement> entry : ramsStringObject.entrySet()) {
			ramsParentId[i] = entry.getKey();
			i += 1;
		}
		List<Integer> ramsPidList = Client.getRandomNum(0, i - 1, ramsNum);
		for (int j = 0; j < ramsNum; j++) {
			String parentId = ramsParentId[ramsPidList.get(j)].toString();
			int childrenLength = ramsStringObject.get(parentId).getAsJsonArray().size();
			int[] childrenId = new int[childrenLength];
			for (int k = 0; k < childrenLength; k++) {
				childrenId[k] = (int) ramsStringObject.get(parentId).getAsJsonArray().get(k).getAsInt();
			}
			ramsArray.add((JsonElement) getRamsObject(parentId, childrenId));
		}

		return ramsArray;
	}

	public static JsonArray getTagsArray(int tagsNum) {
		JsonArray vulnerabilityArray = new JsonArray(); // 定义列表tags
		String[] tagId = { "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83" };
		String[] levelId = { "1", "2", "3", "4" };
		String[] loginId = { "1", "2", "3", "4", "5" };
		String[] permissionId = { "1", "2", "3", "4", "5", "6" };
		String[] systemId = { "1", "2", "3", "4", "5", "6" };
		List<Integer> tagList = Client.getRandomNum(0, tagId.length, tagsNum);
		List<Integer> levelList = Client.getRandomNum(0, levelId.length, tagsNum);
		List<Integer> loginList = Client.getRandomNum(0, loginId.length, tagsNum);
		List<Integer> permissionList = Client.getRandomNum(0, permissionId.length, tagsNum);
		List<Integer> systemList = Client.getRandomNum(0, systemId.length, tagsNum);
		java.text.Format formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");

		for (int j = 0; j < tagsNum; j++) {
			Random random = new Random();
			java.util.Date todayDate = new java.util.Date();
			long beforeTime = (todayDate.getTime() / 1000) - 60 * 60 * 24 * random.nextInt(3650);
			todayDate.setTime(beforeTime * 1000);
			String beforeDate = formatter.format(todayDate);
			long timestamp = new Date().getTime();

			String tag_id = tagId[tagList.get(j)].toString();
			String level = levelId[levelList.get(j)].toString();
			String login = loginId[loginList.get(j)].toString();
			String permission = permissionId[permissionList.get(j)].toString();
			String system = systemId[systemList.get(j)].toString();
			JsonObject vulnerability0 = new JsonObject(); // 填写漏洞的基础信息
			vulnerability0.addProperty("code", "CVE" + beforeDate + "-" + random.nextInt(999) + tag_id);
			vulnerability0.addProperty("description", "description1" + tag_id + "漏洞");
			vulnerability0.addProperty("discover_ts", beforeDate);
			vulnerability0.addProperty("level", level);
			vulnerability0.addProperty("login", login);
			vulnerability0.addProperty("permission", permission);
			vulnerability0.addProperty("name", "漏洞" + random.nextInt(999) + "-XXX-" + tag_id);
			vulnerability0.addProperty("tag_id", tag_id);
			vulnerability0.addProperty("version", "v10." + random.nextInt(99));
			vulnerability0.addProperty("tempId", timestamp);
			JsonArray vulnerability0_systemArray = new JsonArray(); // 填写系统类型列表
			vulnerability0_systemArray.add(system);
			vulnerability0.add("system", vulnerability0_systemArray);
			vulnerabilityArray.add((JsonElement) vulnerability0);
		}
		return vulnerabilityArray;
	}

	public static JsonArray getInformationsArray(int informationsNum) {
		JsonArray informationsArray = new JsonArray(); // 定义列表informations
		String[] informationsId = { "1", "8", "15", "20", "23", "25", "33", "39", "47", "51", "53" }; // 所有的可选的11个informations
		List<Integer> informationsList = Client.getRandomNum(0, informationsId.length, informationsNum);
		for (int j = 0; j < informationsNum; j++) {
			String id = informationsId[informationsList.get(j)].toString();
			informationsArray.add(id);
		}
		return informationsArray;
	}

	public static JsonArray getExist_questionArray(int exist_questionNum) {
		JsonArray exist_questionArray = new JsonArray(); // 定义列表exist_question
		String[] exist_questionId = { "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67",
				"68", "69", "70", "71", "72" }; // 所有的可选的18个exist_question
		List<Integer> exist_questionList = Client.getRandomNum(0, exist_questionId.length, exist_questionNum);
		for (int j = 0; j < exist_questionNum; j++) {
			String id = exist_questionId[exist_questionList.get(j)].toString();
			exist_questionArray.add(id);
		}
		return exist_questionArray;
	}

	public static JsonObject getAttackLinkNodesObject(String... argString) {
		// ===========================>>开始可配置参数<<==========================================
		int ramsNum = 5; // 定义威胁类型个数rams
		int tagsNum = 3; // 定义攻击手段个数tags
		int informationsNum = 3; // 定义信息系统类型个数informations
		int exist_questionsNum = 3; // 定义存在问题个数exist_questionsNum
		// ===========================>>结束可配置参数<<==========================================
		String attachmentString = argString[0]; // 上传的图片在系统中的名称
		String nameString = argString[1]; // 节点的系统名称
		String ipString = argString[2]; // 节点的IP
		String urlString = argString[3]; // 节点的URL
		JsonArray ramsArray = new JsonArray();
		JsonArray tagsArray = new JsonArray();
		JsonArray informationsArray = new JsonArray();
		JsonArray exist_questionsArray = new JsonArray();
		JsonArray imagesArray = new JsonArray();
		Random random = new Random();
		// ========================>>>开始生成linkNodes<<<=========================================
		JsonObject attackLinkNodesObject = new JsonObject();
		// ===============>>二级简单元素12:String,integer,boolean<<========================

		attackLinkNodesObject.addProperty("name", nameString);
		attackLinkNodesObject.addProperty("asset_category", String.valueOf(1 + random.nextInt(4)));
		attackLinkNodesObject.addProperty("ip", ipString);
		attackLinkNodesObject.addProperty("url", urlString);
		// attackLinkNodesObject.addProperty("status", "0");
		attackLinkNodesObject.addProperty("description", "description001");
		attackLinkNodesObject.addProperty("process_description",
				"<div><img src=\"" + attachmentString + "\"><br></div>");

		// ==========>>二级简单元素12<<=====================================================
		// ==========>>二级列表元素5:rams,tags,informations,exist_questions,images<<========
		// 1=========>>开始生成列表rams<<===================================================
		ramsArray = getRamsArray(ramsNum);
		attackLinkNodesObject.add("rams", ramsArray);
		// 1==========>>结束生成列表rams<<==================================================
		// 2=========>>开始生成列表tags<<===================================================
		tagsArray = getTagsArray(tagsNum);
		attackLinkNodesObject.add("vulnerabilities", tagsArray);
		// 2=========>>结束生成列表tags<<===================================================
		// 3=========>>开始生成列表informations<<===========================================
		informationsArray = getInformationsArray(informationsNum);
		attackLinkNodesObject.add("information", informationsArray);
		// 3=========>>结束生成列表informations<<===========================================
		// 4=========>>开始生成列表exist_questions<<========================================
		exist_questionsArray = getExist_questionArray(exist_questionsNum);
		attackLinkNodesObject.add("questions", exist_questionsArray);
		// 4=========>>结束生成列表exist_questions<<========================================
		// 5=========>>开始生成列表images<<=================================================
		imagesArray.add(attachmentString);
		attackLinkNodesObject.add("images", imagesArray);
		// 5=========>>结束生成列表images<<=================================================
		// ========================>>>结束生成linkNodes<<<=========================================
		return attackLinkNodesObject;
	}

	public static List<String> getReportId(String[] args) throws ParseException, IOException {
		String apiString = args[0];
		String cookieWithoutCrumb = args[1];
		List<String> reportList = new ArrayList<String>();

		// 更新crumb
		getMyCrumb(cookieWithoutCrumb);

		// 获取正在进行的ProjectID
		String[] argStrings = { cookieWithoutCrumb, "active-project" };
		String projectIdString = getActiveProject(argStrings);
		String api = apiMap.get(apiString) + projectIdString;
		// 第一次请求获取一共有多少条report
		CloseableHttpResponse response = PeBaseClass.getGetResponse(url, api + "&page=1&", Cookie);
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			Double reportNumString = jObject.get("data").getAsJsonObject().get("total_num").getAsDouble();
			int pagesNum = (int) Math.ceil(reportNumString / 10); // 计算有多少页
			for (int i = 0; i < pagesNum; i++) {
				String apiPages = api + "&page=" + (i + 1) + "&";
				CloseableHttpResponse pagesResponse = PeBaseClass.getGetResponse(url, apiPages, Cookie);
				JsonObject pagesObject = Client.getResponseDataJson(pagesResponse);
				JsonArray ReportArray = pagesObject.get("data").getAsJsonObject().get("list").getAsJsonArray();
				int singlePageReportNum = ReportArray.size();
				for (int j = 0; j < singlePageReportNum; j++) {
					String id = ReportArray.get(j).getAsJsonObject().get("id").getAsString();
					reportList.add(id);
				}
			}
		}
		return reportList;
	}

	public static CloseableHttpResponse retest(String[] args) throws SocketException, IOException {
		String apiString = args[0];
		String cookieWithoutCrumb = args[1];
		String reportIdString = args[2];
		// 获取正在进行的ProjectID
		String[] argProjectStrings = { cookieWithoutCrumb, "active-project" };
		String projectId = getActiveProject(argProjectStrings);
		String retestApi = apiMap.get(apiString) + reportIdString + "?project_id=" + projectId;
		Random random = new Random();
		String[] argStrings = { cookieWithoutCrumb,
				"/projectapi/retest/" + reportIdString + "?project_id=" + projectId + "&id=" + reportIdString };
		CloseableHttpResponse retest_index_response = attackerUiBase(argStrings);
		JsonObject jsonObject = new JsonObject();
		if (retest_index_response != null) {
			jsonObject = PeBaseClass.getResponseDataJson(retest_index_response).get("data").getAsJsonObject();
			jsonObject.addProperty("project_id", projectId);
			jsonObject.addProperty("rectify_desc", "成果复测：XXXXXX");
			jsonObject.addProperty("rectify_status", 1 + random.nextInt(3));
		}
		String jsonStr = jsonObject.toString();
		String contentType = "application/json";
		System.out.println(jsonStr);

		String[] uploadString = { contentType, jsonStr };
		getMyCrumb(cookieWithoutCrumb);
		CloseableHttpResponse response = PeBaseClass.getPutResponse(url, retestApi, Cookie, uploadString);
		return response;
	}
}
