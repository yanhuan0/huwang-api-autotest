package huwang2020_base;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.log4j.Logger;
import org.testng.Reporter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import baseApi.Client;

public class HwVictimBase {

	private static Logger logger = Logger.getLogger(HwVictimBase.class);
	public static String Cookie = "";
	public static String url = "";
	public static String username = "";
	public static String password = "";
	public static String captcha = "111111";
	public static String checked = "true";
	public static String __crumb__ = "111111";

	public static final HashMap<String, String> apiMap = new HashMap<String, String>();

	public static void initConfig(String... fileString) {
		String configFileString = "";
		String apiFileString = "";
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
		Reporter.log("Init url,user,passord successfully.", true);

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
		Reporter.log("Init api url.", true);
	}

	public static String userLogin() {
		logger.info(username);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("username", username);
		jsonObject.addProperty("password", password);
		jsonObject.addProperty("captcha", captcha);
		jsonObject.addProperty("checked", checked);
		jsonObject.addProperty("__crumb__", __crumb__);
		// logger.info(jsonObject);
		// ��¼�û���ȡCookie
		String cookieWithoutCrumb = HwLogin.LoginHw(url, jsonObject.toString());
		// logger.info("before class: " + cookieWithoutCrumb);
		Reporter.log("Login victim user.", true);
		return cookieWithoutCrumb;
	}

	public static void getMyCrumb(String cookieWithoutCrumb) {
		// ��¼�û���ȡCookie
		__crumb__ = huwang2020_base.HwCrumb.getCrumb(url, cookieWithoutCrumb);
		Cookie = cookieWithoutCrumb + "__crumb__=" + __crumb__;
		// Reporter.log("Before each method,get crumb.",true);
	}

	public static void userLogout(String cookieWithoutCrumb) {
		getMyCrumb(cookieWithoutCrumb);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("__crumb__", __crumb__);
		// �ǳ��û�
		HwLogout.LogoutHw(url, Cookie, jsonObject.toString());
		Reporter.log("Logout victim user.", true);
	}

	public static CloseableHttpResponse victimUiBase(String[] argString) {
		String cookieWithoutCrumb = argString[0];
		String api = apiMap.get(argString[1]);
		getMyCrumb(cookieWithoutCrumb);
		CloseableHttpResponse response = baseApi.Client.getGetResponse(url + api, api, api, Cookie);
		return response;

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
		jsonObject.addProperty("__crumb_", __crumb__);
		String jsonStr = jsonObject.toString();

		String[] uploadStrings = { "formData", jsonStr, "attachment", filePath, "image/jpeg", fileName };
		try {
			response = Client.getPostResponse(url + api, api, api, Cookie, uploadStrings);
			// logger.info(response);
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

	public static CloseableHttpResponse victimReport(String[] args) throws ParseException, IOException, SQLException {
		int LinkNodesNum = Integer.parseInt(args[0]);
		String logoPath = args[1];
		String cookieWithoutCrumb = args[2];
		String[] uploadStrings = { logoPath, "report", cookieWithoutCrumb }; // �ϴ��ļ�λ��
		String[] uploadResultStrings = uploadFile(uploadStrings);
		String attachmentString = uploadResultStrings[0];
		getMyCrumb(cookieWithoutCrumb);
		// ============================>>��ʼ����linkNodes<<===========================
		JsonArray victimLinkNodesArray = new JsonArray();
		JsonObject victimLinkNodesObject = new JsonObject();
		String targetName[] = new String[LinkNodesNum];
		String targetIp[] = new String[LinkNodesNum];
		String targetUrl[] = new String[LinkNodesNum];
		for (int i = 0; i < LinkNodesNum; i++) {
			targetName[i] = "ϵͳ����" + i;
			targetIp[i] = "120.137.12." + i;
			targetUrl[i] = "https://www.testurl" + i + ".com/";
			victimLinkNodesObject = getVictimLinkNodesObject(attachmentString, targetName[i], targetIp[i],
					targetUrl[i]);
			victimLinkNodesArray.add((JsonElement) victimLinkNodesObject);
		}
		// ============================>>��������linkNodes<<===========================
		// ============================>>��ʼ������������<<==========================================
		JsonObject jsonObject = new JsonObject(); // ����ɼ�����Ķ���jsonObject
		// ===========>>��ʼ���ɼ�Ԫ�ظ�����2<<=======================================
		jsonObject.addProperty("type", 2); // ���ط��ɼ�
		jsonObject.addProperty("__crumb__", __crumb__);
		// ===========>>�������ɼ�Ԫ�ظ�����2<<=======================================
		// ===========>>��ʼ�����б�Ԫ�ظ�����1<<=======================================
		jsonObject.add("linkNodes", victimLinkNodesArray); // ��������ڵ��б�
		// ===========>>���������б�Ԫ�ظ�����1<<=======================================
		// ============================>>����������������<<==========================================
		String jsonStr = jsonObject.toString();
		String apiString = "reportCreate";
		String api = apiMap.get(apiString);
		String contentType = "application/json";
		logger.info(jsonStr);
		String[] uploadString = { contentType, jsonStr };
		getMyCrumb(cookieWithoutCrumb);
		CloseableHttpResponse response = baseApi.Client.getPostResponse(url + api, api, api, Cookie, uploadString);
		return response;
	}

	public static JsonObject getVictimLinkNodesObject(String... argString) {
		// ===========================>>��ʼ�����ò���<<==========================================
		int ramsNum = 5; // ������в���͸���rams
		// ===========================>>���������ò���<<==========================================
		String attachmentString = argString[0]; // �ϴ���ͼƬ��ϵͳ�е�����
		String nameString = argString[1]; // �ڵ��ϵͳ����
		String ipString = argString[2]; // �ڵ��IP
		String urlString = argString[3]; // �ڵ��URL
		JsonArray ramsArray = new JsonArray();
		JsonArray tagsArray = new JsonArray();
		JsonArray informationsArray = new JsonArray();
		JsonArray exist_questionsArray = new JsonArray();
		JsonArray imagesArray = new JsonArray();
		Random random = new Random();
		// ========================>>>��ʼ����linkNodes<<<=========================================
		JsonObject victimLinkNodesObject = new JsonObject();
		// ===============>>������Ԫ��12:String,integer,boolean<<========================
		victimLinkNodesObject.addProperty("currId", "new1");
		victimLinkNodesObject.addProperty("name", nameString);
		victimLinkNodesObject.addProperty("asset_category_id", String.valueOf(1 + random.nextInt(4)));
		victimLinkNodesObject.addProperty("ip", ipString);
		victimLinkNodesObject.addProperty("url", urlString);
		victimLinkNodesObject.addProperty("status", "0");
		victimLinkNodesObject.addProperty("process_description",
				"<div><img src=\"" + attachmentString + "\"><br></div><div></div>");
		victimLinkNodesObject.addProperty("nameShow", false);
		victimLinkNodesObject.addProperty("urlShow", false);
		victimLinkNodesObject.addProperty("attacker_ip", "139.222.15.13");
		victimLinkNodesObject.addProperty("attack_method", "attack_method");
		victimLinkNodesObject.addProperty("defense_method", "defense_method");
		// ==========>>������Ԫ��12<<=====================================================
		// ==========>>�����б�Ԫ��5:rams,tags,informations,exist_questions,images<<========
		// 1=========>>��ʼ�����б�rams<<===================================================
		ramsArray = getRamsArray(ramsNum);
		victimLinkNodesObject.add("rams", ramsArray);
		// 1==========>>���������б�rams<<==================================================
		// 2=========>>��ʼ�����б�tags<<===================================================
		victimLinkNodesObject.add("tags", tagsArray);
		// 2=========>>���������б�tags<<===================================================
		// 3=========>>��ʼ�����б�informations<<===========================================
		victimLinkNodesObject.add("informations", informationsArray);
		// 3=========>>���������б�informations<<===========================================
		// 4=========>>��ʼ�����б�exist_questions<<========================================
		victimLinkNodesObject.add("exist_questions", exist_questionsArray);
		// 4=========>>���������б�exist_questions<<========================================
		// 5=========>>��ʼ�����б�images<<=================================================
		imagesArray.add(attachmentString);
		victimLinkNodesObject.add("images", imagesArray);
		// 5=========>>���������б�images<<=================================================
		// ========================>>>��������linkNodes<<<=========================================
		return victimLinkNodesObject;
	}

	public static JsonArray getRamsArray(int ramsNum) {
		JsonArray ramsArray = new JsonArray(); // �����б�rams
		String ramsString = "{\"51\":[51],\"52\":[53,54,55,56,57,58,59,60],\"61\":[61],\"62\":[62],"
				+ "\"63\":[63],\"64\":[64],\"66\":[66],\"67\":[67],\"71\":[71]}"; // ���еĿ�ѡ��9��rams
		JsonObject ramsStringObject = new JsonParser().parse(ramsString).getAsJsonObject();
		String[] ramsParentId = new String[10]; // ��������rams�ĳ��ȶ���
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

	public static JsonElement getRamsObject(String parentId, int[] value) {
		Random random = new Random();
		JsonObject ramsElement = new JsonObject();
		JsonArray childrenArray = new JsonArray();
		ramsElement.addProperty("parent_id", parentId);
		for (int i = 0; i < value.length; i++) {
			JsonObject childrenObject = new JsonObject();
			childrenObject.addProperty("id", String.valueOf(value[i]));
			if (value[i] >= 66) {
				childrenObject.addProperty("value", String.valueOf(3 + random.nextInt(5) + "�ı�"));
			} else {
				childrenObject.addProperty("value", 3 + random.nextInt(5));
			}
			childrenArray.add((JsonElement) childrenObject);
		}
		ramsElement.add("children", (JsonElement) childrenArray);
		return ramsElement;
	}

	public static String getReportTask(String[] args) throws ParseException, IOException {
		String apiString = "adminapiReportTaskGet";
		String reportData = args[0];
		String api = apiMap.get(apiString) + reportData + "?";
		String cookieWithoutCrumb = args[1];
		getMyCrumb(cookieWithoutCrumb);
		CloseableHttpResponse response = baseApi.Client.getGetResponse(url + api, api, apiMap.get(apiString), Cookie);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject);
			message = jObject.get("message").getAsString();
		}
		return message;
	}
}
