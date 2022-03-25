package huwang2020_base;

import java.io.IOException;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import baseApi.Client;

public class HwAttackerBase extends InitProperty {

	private static Logger logger = Logger.getLogger(HwAttackerBase.class);

	public String[] uploadFile(String[] args) throws ParseException, IOException {
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
		jsonObject.addProperty("__crumb_", get__crumb__());
		String jsonStr = jsonObject.toString();

		String[] uploadStrings = { "formData", jsonStr, "attachment", filePath, "image/jpeg", fileName };
		try {
			response = Client.getPostResponse(getUrl() + api, api, api, getCookie(), uploadStrings);
			logger.info(response);
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

	public CloseableHttpResponse attackerReport(String[] args) throws ParseException, IOException, SQLException {
		int LinkNodesNum = Integer.parseInt(args[0]);
		String logoPath = args[1];
		String cookieWithoutCrumb = args[2];
		getMyCrumb(cookieWithoutCrumb);
		String[] uploadStrings = { logoPath, "report", cookieWithoutCrumb }; // 上传文件位置
		String[] uploadResultStrings = uploadFile(uploadStrings);
		String attachmentString = uploadResultStrings[0];
		String[] argsGetDefense = { "1", cookieWithoutCrumb };
		String orgDstId = getDefenseId(argsGetDefense).split(",")[0].split(":")[0]; // 获取防守单位ID
		String attackerIp = getAttackerIp(cookieWithoutCrumb); // 获取攻击出口IP
		// ============================>>开始生成linkNodes<<===========================
		JsonArray attackLinkNodesArray = new JsonArray();
		JsonObject attackLinkNodesObject = new JsonObject();
		JsonArray tagsArray1 = new JsonArray();
		JsonArray otherNodesArray1 = new JsonArray();
		String targetName[] = new String[LinkNodesNum];
		String targetIp[] = new String[LinkNodesNum];
		String targetUrl[] = new String[LinkNodesNum];
		for (int i = 0; i < LinkNodesNum; i++) {
			String randomIp = baseApi.Utils.getRandomIp();
			targetName[i] = randomIp;
			targetIp[i] = randomIp;
			targetUrl[i] = "https://" + randomIp + "/test/" + i;
			attackLinkNodesObject = getAttackLinkNodesObject(attachmentString, targetName[i], targetIp[i],
					targetUrl[i]);
			attackLinkNodesArray.add((JsonElement) attackLinkNodesObject);
		}
		// ============================>>结束生成linkNodes<<===========================
		// ============================>>开始生成整个报告<<==========================================
		JsonObject jsonObject = new JsonObject(); // 定义成绩报告的对象jsonObject
		// ===========>>开始生成简单元素个数：7<<=======================================
		jsonObject.addProperty("org_dst_id", orgDstId); // 防守单位
		jsonObject.addProperty("attacker_ip", attackerIp); // 攻击出口IP
		jsonObject.addProperty("has_target", 1); // 是否包含靶标系统
		jsonObject.addProperty("problem", "problem001"); // 存在问题
		jsonObject.addProperty("suggestion", "suggestion001"); // 整改建议
		jsonObject.addProperty("path_desc", "path_desc001"); // 目标渗透路径详细说明
		jsonObject.addProperty("type", 1); // 攻击方成绩
		jsonObject.addProperty("__crumb__", get__crumb__());
		// ===========>>结束生成简单元素个数：7<<=======================================
		// ===========>>开始生成列表元素个数：3<<=======================================
		jsonObject.add("tags", tagsArray1); // 报告的空标签tags
		jsonObject.add("otherNodes", otherNodesArray1); // 报告的其他节点列表
		jsonObject.add("linkNodes", attackLinkNodesArray); // 报告的主节点列表
		// ===========>>结束生成列表元素个数：3<<=======================================
		// ============================>>结束生成整个报告<<==========================================
		String jsonStr = jsonObject.toString();
		String apiString = "reportCreate";
		String api = apiMap.get(apiString);
		String contentType = "application/json";
		String[] uploadString = { contentType, jsonStr };
		getMyCrumb(cookieWithoutCrumb);
		CloseableHttpResponse response = baseApi.Client.getPostResponse(getUrl() + api, api, api, getCookie(),
				uploadString);
		return response;
	}

	public JsonElement getRamsObject(String parentId, int[] value) {
		Random random = new Random();
		JsonObject ramsElement = new JsonObject();
		JsonArray childrenArray = new JsonArray();
		ramsElement.addProperty("parent_id", parentId);
		for (int i = 0; i < value.length; i++) {
			JsonObject childrenObject = new JsonObject();
			childrenObject.addProperty("id", String.valueOf(value[i]));
			if (value[i] >= 33) {
				childrenObject.addProperty("value", String.valueOf(3 + random.nextInt(5) + "文本"));
			} else {
				childrenObject.addProperty("value", 3 + random.nextInt(5));
			}
			childrenArray.add((JsonElement) childrenObject);
		}
		ramsElement.add("children", (JsonElement) childrenArray);
		return ramsElement;
	}

	public JsonArray getRamsArray(int ramsNum) {
		JsonArray ramsArray = new JsonArray(); // 定义列表rams
		String ramsString = "{\"1\":[73,74],\"2\":[2],\"3\":[3],\"2\":[2],\"4\":[5,6],\"7\":[8,9],\"10\":[11,12],"
				+ "\"13\":[13],\"14\":[15],\"16\":[16],\"18\":[19,20],\"21\":[76,77],\"24\":[25],"
				+ "\"29\":[30,31],\"33\":[33],\"34\":[34],\"75\":[75],\"36\":[36],\"37\":[37],\"46\":[46],"
				+ "\"47\":[47],\"49\":[49],\"50\":[50]}"; // 所有的可选的22个rams
		JsonObject ramsStringObject = new JsonParser().parse(ramsString).getAsJsonObject();
		String[] ramsParentId = new String[25]; // 根据所有rams的长度定义
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

	public JsonArray getTagsArray(int tagsNum) {
		JsonArray tagsArray = new JsonArray(); // 定义列表tags
		String tagsString = "{\"56\":[1],\"57\":[1],\"58\":[],\"59\":[],\"60\":[],\"61\":[],\"62\":[],"
				+ "\"63\":[],\"64\":[],\"65\":[],\"66\":[],\"67\":[],\"68\":[],"
				+ "\"84\":[],\"85\":[],\"86\":[],\"87\":[],\"88\":[],\"89\":[]," + "\"90\":[]}"; // 所有的可选23个tags
		JsonObject tagsStringObject = new JsonParser().parse(tagsString).getAsJsonObject();
		String[] tagsId = new String[25]; // 根据所有rams的长度定义
		int i = 0;
		for (Entry<String, JsonElement> entry : tagsStringObject.entrySet()) {
			tagsId[i] = entry.getKey();
			i += 1;
		}
		List<Integer> tagsidList = Client.getRandomNum(0, i - 1, tagsNum);
		for (int j = 0; j < tagsNum; j++) {
			String id = tagsId[tagsidList.get(j)].toString();
			if (id.equals("56") || id.equals("57")) {
				JsonArray vulnerability0Array = new JsonArray(); // tags是一个列表
				JsonObject tags0 = new JsonObject(); // tags列表的第一个元素，id=56是个漏洞
				JsonObject vulnerability0 = new JsonObject(); // 填写漏洞的基础信息
				if (id.equals("56")) {
					vulnerability0.addProperty("type", "应用层漏洞利用");
					tags0.addProperty("id", "56"); // tags0的漏洞id56 应用层漏洞
				} else if (id.equals("57")) {
					vulnerability0.addProperty("type", "系统层漏洞利用");
					tags0.addProperty("id", "57"); // tags0的漏洞id57应用层漏洞
				}
				vulnerability0.addProperty("vulnerability_name", "vulnerability_name001");
				vulnerability0.addProperty("vulnerability_publish", "1");
				vulnerability0.addProperty("vulnerability_version", "223");
				vulnerability0.addProperty("report_node_tag_id", "91");
				vulnerability0.addProperty("vulnerability_description", "vulnerability_description11");
				vulnerability0.addProperty("zero_day_tags", "");
				JsonArray vulnerability0_systemArray = new JsonArray(); // 填写系统类型列表
				vulnerability0_systemArray.add("windows");
				vulnerability0_systemArray.add("linux");
				vulnerability0.add("vulnerability_system", vulnerability0_systemArray);
				vulnerability0Array.add((JsonElement) vulnerability0);
				tags0.add("vulnerability", vulnerability0Array); // tags0的漏洞具体信息列表
				tagsArray.add((JsonElement) tags0);
			} else {
				JsonArray vulnerability1Array = new JsonArray(); // tags是一个列表
				JsonObject tags1 = new JsonObject(); // tags列表的其它元素
				tags1.addProperty("id", id);
				tags1.add("vulnerability", vulnerability1Array);
				tagsArray.add((JsonElement) tags1);
			}
		}
		return tagsArray;
	}

	public JsonArray getInformationsArray(int informationsNum) {
		JsonArray informationsArray = new JsonArray(); // 定义列表informations
		String[] informationsId = { "7", "9", "12", "8", "10", "11", "32", "35", "36", "34", "33", "37", "27", "29",
				"28", "30", "2", "3", "5", "70", "72", "74", "82", "71", "73", "81", "22", "83", "25", "23", "24", "14",
				"16", "18", "20", "15", "17", "19", "78", "79", "80", "76", "103" }; // 所有的可选的42个informations
		List<Integer> informationsList = Client.getRandomNum(0, informationsId.length, informationsNum);
		for (int j = 0; j < informationsNum; j++) {
			String id = informationsId[informationsList.get(j)].toString();
			informationsArray.add(id);
		}
		return informationsArray;
	}

	public JsonArray getExist_questionArray(int exist_questionNum) {
		JsonArray exist_questionArray = new JsonArray(); // 定义列表exist_question
		String[] exist_questionId = { "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
				"51", "52", "53", "54", "55" }; // 所有的可选的18个exist_question
		List<Integer> exist_questionList = Client.getRandomNum(0, exist_questionId.length, exist_questionNum);
		for (int j = 0; j < exist_questionNum; j++) {
			String id = exist_questionId[exist_questionList.get(j)].toString();
			exist_questionArray.add(id);
		}
		return exist_questionArray;
	}

	public JsonObject getAttackLinkNodesObject(String... argString) {
		// ===========================>>开始可配置参数<<==========================================
		int ramsNum = 5; // 定义威胁类型个数rams
		int tagsNum = 5; // 定义攻击手段个数tags
		int informationsNum = 5; // 定义信息系统类型个数informations
		int exist_questionsNum = 5; // 定义存在问题个数exist_questionsNum
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
		attackLinkNodesObject.addProperty("currId", "new1");
		attackLinkNodesObject.addProperty("name", nameString);
		attackLinkNodesObject.addProperty("asset_category_id", String.valueOf(1 + random.nextInt(4)));
		attackLinkNodesObject.addProperty("ip", ipString);
		attackLinkNodesObject.addProperty("url", urlString);
		attackLinkNodesObject.addProperty("status", "0");
		attackLinkNodesObject.addProperty("description", "description001");
		attackLinkNodesObject.addProperty("process_description",
				"<div><img src=\"" + attachmentString + "\"><br></div><div></div>");
		attackLinkNodesObject.addProperty("nameShow", false);
		attackLinkNodesObject.addProperty("urlShow", false);
		attackLinkNodesObject.addProperty("attacker_ip", "");
		attackLinkNodesObject.addProperty("index", 0);
		// ==========>>二级简单元素12<<=====================================================
		// ==========>>二级列表元素5:rams,tags,informations,exist_questions,images<<========
		// 1=========>>开始生成列表rams<<===================================================
		ramsArray = getRamsArray(ramsNum);
		attackLinkNodesObject.add("rams", ramsArray);
		// 1==========>>结束生成列表rams<<==================================================
		// 2=========>>开始生成列表tags<<===================================================
		tagsArray = getTagsArray(tagsNum);
		attackLinkNodesObject.add("tags", tagsArray);
		// 2=========>>结束生成列表tags<<===================================================
		// 3=========>>开始生成列表informations<<===========================================
		informationsArray = getInformationsArray(informationsNum);
		attackLinkNodesObject.add("informations", informationsArray);
		// 3=========>>结束生成列表informations<<===========================================
		// 4=========>>开始生成列表exist_questions<<========================================
		exist_questionsArray = getExist_questionArray(exist_questionsNum);
		attackLinkNodesObject.add("exist_questions", exist_questionsArray);
		// 4=========>>结束生成列表exist_questions<<========================================
		// 5=========>>开始生成列表images<<=================================================
		imagesArray.add(attachmentString);
		attackLinkNodesObject.add("images", imagesArray);
		// 5=========>>结束生成列表images<<=================================================
		// ========================>>>结束生成linkNodes<<<=========================================
		return attackLinkNodesObject;
	}

	public CloseableHttpResponse applicationCreate(String[] args) throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		String apiString = "attackerApplication";
		String api = apiMap.get(apiString);
		String contentType = "x-www-form-urlencoded";
		String logoPath = args[0];
		String cookieWithoutCrumb = args[1];
		String[] uploadStrings = { logoPath, "attack_application", cookieWithoutCrumb }; // 上传文件位置
		String[] uploadResultStrings = uploadFile(uploadStrings);
		String attachmentString = uploadResultStrings[0];
		String[] argsGetDefense = { "1", cookieWithoutCrumb };
		String orgDstId = getDefenseId(argsGetDefense).split(",")[0].split(":")[0]; // 获取防守单位ID
		getMyCrumb(cookieWithoutCrumb);
		String jsonStr = "organization_id:" + orgDstId + ",system_name:system_name,ip:123.222.14.138,"
				+ "url:https://www.asd.com,content:content,attachment[]:" + attachmentString + ",temporary:0,__crumb__:"
				+ get__crumb__();
		String[] uploadString = { contentType, jsonStr };
		response = baseApi.Client.getPostResponse(getUrl() + api, api, api, getCookie(), uploadString);
		return response;
	}

	public CloseableHttpResponse wordUpload(String[] args) throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		String apiString = "uploadWord";
		String api = apiMap.get(apiString);
		String contentType = "formData";
		String filePath = args[0];
		String cookieWithoutCrumb = args[1];
		getMyCrumb(cookieWithoutCrumb);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("upload_word", "(binary)");
		jsonObject.addProperty("__crumb__", get__crumb__());
		String jsonString = jsonObject.toString();
		String fileName = filePath.split(":\\\\")[filePath.split(":\\\\").length - 1];
		String uploadString[] = { contentType, jsonString, "upload_word", filePath,
				"application/vnd.openxmlformats-officedocument.wordprocessingml.document", fileName };
		response = Client.getPostResponse(getUrl() + api, api, api, getCookie(), uploadString);
		return response;
	}

	public CloseableHttpResponse zeroDayAttackerUpload(String[] args) throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		String apiString = "zeroDayAttackerUpload";
		String api = apiMap.get(apiString);
		String contentType = "formData";
		String filePath = args[0];
		String cookieWithoutCrumb = args[1];
		getMyCrumb(cookieWithoutCrumb);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("zero_day", "(binary)");
		jsonObject.addProperty("__crumb__", get__crumb__());
		String jsonString = jsonObject.toString();
		String fileName = filePath.split(":\\\\")[filePath.split(":\\\\").length - 1];
		String uploadString[] = { contentType, jsonString, "zero_day", filePath, "application/zip", fileName };
		response = Client.getPostResponse(getUrl() + api, api, api, getCookie(), uploadString);
		return response;
	}

	public CloseableHttpResponse zeroDayAttackerCreate2020(String[] args)
			throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		JsonObject jsonObject = new JsonObject();
		JsonArray dataArray = new JsonArray();
		JsonArray affectArray = new JsonArray();
		JsonObject dataObject = new JsonObject();
		String apiString = "zeroDayAttackerCreate";
		String api = apiMap.get(apiString);
		String contentType = "application/json";
		String filePath = args[0];
		String fileName = args[1];
		String cookieWithoutCrumb = args[2];
		getMyCrumb(cookieWithoutCrumb);
		Random random = new Random();
		// ==================>>开始构建元素<<========================================
		jsonObject.addProperty("vulner_type", 1 + random.nextInt(5));
		jsonObject.addProperty("affect_software", "请填写受影响的软件/硬件的厂商");
		jsonObject.addProperty("status", 2);
		jsonObject.addProperty("remark", "可在此详细描述该漏洞与互联网内已公开的该产品其他漏洞的显著区别，以及该漏洞的实际影响力描述。");
		jsonObject.addProperty("__crumb__", get__crumb__());
		dataObject.addProperty("affect_version", "Windows版 V7 ～ V10");
		dataObject.addProperty("affect_module", "示例：查看好友动态模块");
		dataObject.addProperty("vulner_process", "描述触发漏洞的条件、输入数据要求和操作过程，达到能够复现的详细程度。");
		dataObject.addProperty("file_name", fileName);
		dataObject.addProperty("file_url", filePath);
		affectArray.add(String.valueOf(1 + random.nextInt(3)));
		dataObject.add("affect", affectArray);
		dataArray.add((JsonElement) dataObject);
		jsonObject.add("data", dataArray);
		// ==================>>结束构建元素<<========================================
		String jsonStr = jsonObject.toString();
		logger.info(jsonStr);
		String[] uploadString = { contentType, jsonStr };
		response = baseApi.Client.getPostResponse(getUrl() + api, api, api, getCookie(), uploadString);
		return response;
	}

	public JsonArray getNodesArray(int isBreak) {
		JsonArray nodesArray = new JsonArray(); // 定义列表nodesArray

		Random random = new Random();
		if (isBreak == 1) {
			JsonObject unbreakNodeObject = new JsonObject();
			JsonArray unbreakListArray = new JsonArray();
			unbreakListArray.add("1111");
			unbreakListArray.add("2222");
			unbreakListArray.add("3333");
			unbreakListArray.add("4444");
			unbreakNodeObject.addProperty("name", "互联网");
			unbreakNodeObject.add("list", unbreakListArray);
			nodesArray.add((JsonElement) unbreakNodeObject);
		}
		for (int i = 1; i < 4; i++) {
			int nodesNum = 1 + random.nextInt(5);
			JsonObject nodeObject = new JsonObject();
			JsonArray nodeListArray = new JsonArray();
			nodeObject.addProperty("name", "网络层级" + i);
			for (int j = 0; j < nodesNum; j++) {
				JsonObject nodeListObject = new JsonObject();
				JsonObject listAttackObject = new JsonObject();
				nodeListObject.addProperty("name", "List_name" + i + "_" + j);
				nodeListObject.addProperty("index", j);
				listAttackObject.addProperty("strategy", "strategy" + i + "_" + j);
				listAttackObject.addProperty("method", "method" + i + "_" + j);
				listAttackObject.addProperty("report", "report" + i + "_" + j);
				nodeListObject.add("attack", listAttackObject);
				nodeListArray.add((JsonElement) nodeListObject);
			}
			nodeObject.add("list", nodeListArray);
			nodesArray.add((JsonElement) nodeObject);
		}
		return nodesArray;
	}

	public CloseableHttpResponse techAttack(String[] args) throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		CloseableHttpResponse getTechResponse = null;
		String apiString = "techAttack";
		String api = apiMap.get(apiString);
		String contentType = "x-www-form-urlencoded";
		String cookieWithoutCrumb = args[0];
		String techId = "";
		String jsonStr = "";
		getMyCrumb(cookieWithoutCrumb);
		String apiGetTechId = apiMap.get("adminapiTechAttack");
		getTechResponse = Client.getGetResponse(getUrl() + apiGetTechId, apiGetTechId, apiGetTechId, getCookie());
		if (getTechResponse != null) {
			JsonObject jObject = Client.getResponseDataJson(getTechResponse);
			int total = jObject.get("data").getAsJsonObject().get("total").getAsInt();
			for (int i = 0; i < total; i++) {
				int Is_unfilled = jObject.get("data").getAsJsonObject().get("list").getAsJsonArray().get(i)
						.getAsJsonObject().get("is_unfilled").getAsInt();
				if (Is_unfilled == 1) {
					techId = jObject.get("data").getAsJsonObject().get("list").getAsJsonArray().get(i).getAsJsonObject()
							.get("id").getAsString();
					String orgDestTitle = jObject.get("data").getAsJsonObject().get("list").getAsJsonArray().get(i)
							.getAsJsonObject().get("org_dst_title").getAsString();
					String judgeName = jObject.get("data").getAsJsonObject().get("list").getAsJsonArray().get(i)
							.getAsJsonObject().get("judges").getAsJsonArray().get(0).getAsJsonObject().get("name")
							.getAsString();
					String judgeId = jObject.get("data").getAsJsonObject().get("list").getAsJsonArray().get(i)
							.getAsJsonObject().get("judges").getAsJsonArray().get(0).getAsJsonObject().get("id")
							.getAsString();
					JsonObject jsonObject = new JsonObject();
					JsonArray commentsInfoArray = new JsonArray();
					JsonObject commentsInfoObject = new JsonObject();
					JsonObject judgeObject = new JsonObject();
					JsonArray nodeArray = new JsonArray();
					JsonArray conclusionArray = new JsonArray();
					JsonObject conclusionObject = new JsonObject();
					jsonObject.addProperty("name", orgDestTitle);
					jsonObject.addProperty("type_id", 1);
					jsonObject.addProperty("is_break", 1);
					// ========>>开始生成技战法普通信息<<===================================
					commentsInfoObject.addProperty("id", techId);
					commentsInfoObject.addProperty("technical_id", techId);
					commentsInfoObject.addProperty("judge_id", judgeId);
					commentsInfoObject.addProperty("name", judgeName);
					commentsInfoObject.addProperty("remark", "描述攻击过程以及被攻击后的危害");
					judgeObject.addProperty("id", judgeId);
					judgeObject.addProperty("name", judgeName);
					commentsInfoObject.add("judge", judgeObject);
					commentsInfoArray.add((JsonElement) commentsInfoObject);
					// ========>>结束生成技战法普通信息<<===================================
					// ========>>开始生成危害分析<<========================================
					conclusionObject.addProperty("internation_harm", "internation_harm");
					conclusionObject.addProperty("society_harm", "society_harm");
					conclusionObject.addProperty("country_harm", "country_harm");
					conclusionObject.addProperty("suggest", "suggest");
					conclusionArray.add((JsonElement) conclusionObject);
					// ========>>结束生成危害分析<<========================================
					// ========>>开始生成技战法内容信息<<===================================
					nodeArray = getNodesArray(1);
					jsonObject.add("node", nodeArray);
					jsonObject.add("commentsInfo", commentsInfoArray);
					jsonObject.add("conclusion", conclusionObject);
					jsonStr = jsonObject.toString();
					jsonStr = "type_id:1,x_bound_x,content:" + jsonStr + ",x_bound_x,__crumb__:" + "+__crumb__+}";
					// ========>>结束生成技战法内容信息<<===================================
					String[] uploadString = { contentType, jsonStr };
					response = baseApi.Client.getPutResponse(getUrl(), api + techId, api, getCookie(), uploadString);
				} else {
					continue;
				}
			}
		}
		return response;
	}

	public CloseableHttpResponse overDefenseInfoAttack(String[] args) throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		JsonObject jsonObject = new JsonObject();
		JsonArray imagesArray = new JsonArray();
		JsonObject imagesObject = new JsonObject();
		String apiString = "overDefenseInfoAttack";
		String api = apiMap.get(apiString);
		String contentType = "application/json";
		String filePath = args[0];
		String fileName = args[1];
		String cookieWithoutCrumb = args[2];
		Random random = new Random();
		getMyCrumb(cookieWithoutCrumb);
		// ==================>>开始构建元素<<========================================
		jsonObject.addProperty("type", 2 + random.nextInt(2)); // 2:网址不可用 3：网站首页为静态图片
		jsonObject.addProperty("destination", 13);
		jsonObject.addProperty("description", "description");
		jsonObject.addProperty("name", "name");
		jsonObject.addProperty("url", "https://www.xyac.com/");
		jsonObject.addProperty("__crumb__", get__crumb__());
		imagesObject.addProperty("name", fileName);
		imagesObject.addProperty("url", filePath);
		imagesArray.add((JsonElement) imagesObject);
		jsonObject.add("images", imagesArray);
		// ==================>>结束构建元素<<========================================
		String jsonStr = jsonObject.toString();
		logger.info(jsonStr + "\n");
		String[] uploadString = { contentType, jsonStr };
		response = baseApi.Client.getPostResponse(getUrl() + api, api, api, getCookie(), uploadString);
		return response;
	}

	public String getDefenseId(String[] args) throws ParseException, IOException {
		String apiString = "newReportFilter";
		int needNum = Integer.valueOf(args[0]);
		String cookieWithoutCrumb = args[1];
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString);
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api, api, api, getCookie());
		JsonArray defenseIdArray = new JsonArray();
		StringBuilder returnIdList = new StringBuilder();
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			defenseIdArray = jObject.get("data").getAsJsonObject().get("defenseStatus").getAsJsonArray();
			int defenseNum = defenseIdArray.size();
			List<Integer> randomList = Client.getRandomNum(0, defenseNum, needNum);
			for (int i = 0; i < needNum; i++) {
				int randomidth = randomList.get(i);
				String id = defenseIdArray.get(randomidth).getAsJsonObject().get("id").getAsString();
				String name = defenseIdArray.get(randomidth).getAsJsonObject().get("name").getAsString();
				returnIdList.append(id + ":" + name + ",");
			}
		}
		return returnIdList.toString().substring(0, returnIdList.length() - 1);
	}

	public String getAttackerIp(String args) throws ParseException, IOException {
		String apiString = "adminapiOrgnizatinOutIpsAttacker";
		String cookieWithoutCrumb = args;
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString);
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api, api, api, getCookie());
		JsonArray attackerIpArray = new JsonArray();
		String attackerIP = null;
		Random random = new Random();
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			attackerIpArray = jObject.get("data").getAsJsonArray();
			int attackerIpNum = attackerIpArray.size();
			attackerIP = attackerIpArray.get(random.nextInt(attackerIpNum)).getAsString();
		}
		return attackerIP;
	}

	public CloseableHttpResponse attackerUiBase(String[] argString) {
		String cookieWithoutCrumb = argString[0];
		String api = apiMap.get(argString[1]);
		getMyCrumb(cookieWithoutCrumb);
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api, api, api, getCookie());
		return response;

	}

	/**
	 * 获取当前攻击方没有攻击过的防守方的id
	 * 
	 * @throws IOException
	 * @throws SocketException
	 */
	public List<String> getUnusedDefenseId(String[] args) throws SocketException, IOException {
		String cookieWithoutCrumb = args[0];
		String apiString = "getDefenseId";
		List<String> defenseIdList = new ArrayList<>();
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString);
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api, api, api, getCookie());
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			JsonArray dataArray = jObject.get("data").getAsJsonArray();
			if (dataArray.size() > 0) {
				for (int i = 0; i < dataArray.size(); i++) {
					String used = dataArray.get(i).getAsJsonObject().get("used").getAsString();
					String id = dataArray.get(i).getAsJsonObject().get("id").getAsString();
					if ("false".equals(used)) {
						defenseIdList.add(id);
					}
				}
			} else {
				logger.info("没有可攻击的防守方单位！\n");
			}
		}
		return defenseIdList;
	}

	/**
	 * 执行创建一条空节点成绩
	 * 
	 * @param args
	 * @return response
	 * @throws SocketException
	 * @throws IOException
	 * @throws SQLException
	 */
	public CloseableHttpResponse addAttackerReport(String[] args) throws SocketException, IOException, SQLException {
		String cookieWithoutCrumb = args[0];
		String organization_dst_id = args[1];
		String apiString = "reportAdd";
		String api = apiMap.get(apiString);
		String contentType = "application/json";
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("organization_dst_id", organization_dst_id);
		String jsonStr = jsonObject.toString();
		String[] uploadString = { contentType, jsonStr };
		getMyCrumb(cookieWithoutCrumb);
		CloseableHttpResponse response = baseApi.Client.getPostResponse(getUrl() + api, api, api, getCookie(),
				uploadString);
		return response;
	}

	/**
	 * 执行stage接口提交有节点的成绩树
	 * 
	 * @param args
	 * @return response
	 * @throws SocketException
	 * @throws IOException
	 * @throws SQLException
	 */
	public CloseableHttpResponse createReportTree(String[] args) throws SocketException, IOException, SQLException {
		String cookieWithoutCrumb = args[0];
		String jsonStr = args[1];
		String apiString = "reportStage";
		String api = apiMap.get(apiString);
		String contentType = "application/json";
		String[] uploadString = { contentType, jsonStr };
		getMyCrumb(cookieWithoutCrumb);
		CloseableHttpResponse response = baseApi.Client.getPostResponse(getUrl() + api, api, api, getCookie(),
				uploadString);
		return response;
	}

	public CloseableHttpResponse openNode(String[] args) {
		String apiString = "getOpenNode";
		String cookieWithoutCrumb = args[0];
		String attackerReportid = args[1];
		String nodeUuid = args[2];
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString) + attackerReportid + "/" + nodeUuid + "?id=" + attackerReportid + "&uuid="
				+ nodeUuid + "&";
		logger.info("打开一个节点：");
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api, api, apiMap.get(apiString),
				getCookie());
		return response;
	}

	/**
	 * 执行提交保存成绩节点动作
	 * 
	 * @param args
	 * @return response
	 * @throws SocketException
	 * @throws IOException
	 * @throws SQLException
	 */
	public CloseableHttpResponse editAttackerNode(String[] args) throws SocketException, IOException, SQLException {
		String cookieWithoutCrumb = args[0];
		String jsonStr = args[1];
		String api = apiMap.get(args[3]) + args[2];
		String contentType = "application/json";
		String[] uploadString = { contentType, jsonStr };
		getMyCrumb(cookieWithoutCrumb);
		logger.info("编辑完成一个节点并保存：");
		CloseableHttpResponse response = baseApi.Client.getPostResponse(getUrl() + api, api, apiMap.get(args[3]),
				getCookie(), uploadString);
		return response;
	}

	/**
	 * 执行提交成绩动作
	 * 
	 * @param args
	 * @return response
	 * @throws SocketException
	 * @throws IOException
	 * @throws SQLException
	 */
	public CloseableHttpResponse createReport(String[] args) throws SocketException, IOException, SQLException {
		String cookieWithoutCrumb = args[0];
		String reportId = args[1];
		String org_dst_id = args[2];
		String apiString = "createReport";
		String api = apiMap.get(apiString);
		String contentType = "application/json";
		CloseableHttpResponse viweResponse = getAttackerView(args);
		JsonObject reportObject = new JsonObject();
		if (viweResponse != null) {
			// 获取树形图相关参数
			JsonObject jObject = Client.getResponseDataJson(viweResponse);
			String front_tpl = jObject.get("data").getAsJsonObject().get("node_map").getAsString();
			JsonArray nodeList = jObject.get("data").getAsJsonObject().get("nodeList").getAsJsonArray();
			String front_tpl_hash = jObject.get("data").getAsJsonObject().get("front_tpl_hash").getAsString();
			String attackerIp = getAttackerIp(cookieWithoutCrumb);

			// 组装成绩
			JsonObject treeObject = new JsonObject();
			treeObject.addProperty("front_tpl_hash", front_tpl_hash);
			treeObject.addProperty("front_tpl", front_tpl);
			treeObject.add("nodeList", nodeList);
			reportObject.addProperty("id", reportId);
			reportObject.addProperty("uuid", reportId);
			reportObject.addProperty("attacker_ip", attackerIp);
			reportObject.addProperty("has_target", 1);
			reportObject.addProperty("path_desc", "path_desc");
			reportObject.addProperty("problem", "problem");
			reportObject.addProperty("suggestion", "suggestion");
			reportObject.addProperty("organization_dst_id", org_dst_id);
			reportObject.add("graphData", (JsonElement) treeObject);

		}
		String jsonStr = reportObject.toString();
		String[] uploadString = { contentType, jsonStr };
		getMyCrumb(cookieWithoutCrumb);
		logger.info("提交审核此条成绩：");
		CloseableHttpResponse response = baseApi.Client.getPostResponse(getUrl() + api, api, api, getCookie(),
				uploadString);
		return response;
	}

	/**
	 * 返回成绩节点的jsonObject
	 * 
	 * @param args
	 * @return nodeObject
	 * @throws IOException
	 * @throws SocketException
	 */
	public JsonObject getReportNode(String cookieWithoutCrumb, String[] args) throws SocketException, IOException {
		String reportId = args[0];
		String nodeUuid = args[1];
		String nodePid = args[2];
		String[] openNodeArgs = { cookieWithoutCrumb, reportId, nodeUuid };
		CloseableHttpResponse openNodeResponse = openNode(openNodeArgs);
		ReportNode reportNode = new ReportNode(reportId, nodeUuid, nodePid, openNodeResponse);
		JsonObject nodeObject = reportNode.getNode();
		return nodeObject;
	}

	/**
	 * 执行锁定一条成绩动作
	 * 
	 * @return
	 * @throws IOException
	 * @throws SocketException
	 * @throws SQLException
	 */
	public CloseableHttpResponse reportLock(String[] args) throws SocketException, IOException, SQLException {
		String cookieWithoutCrumb = args[0];
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("uuid", args[1]);
		jsonObject.addProperty("last_update_ts", args[2]);
		String jsonStr = jsonObject.toString();
		String apiString = "reportLock";
		String api = apiMap.get(apiString);
		String contentType = "application/json";
		String[] uploadString = { contentType, jsonStr };
		getMyCrumb(cookieWithoutCrumb);
		logger.info("锁定此条成绩：");
		CloseableHttpResponse response = baseApi.Client.getPostResponse(getUrl() + api, api, api, getCookie(),
				uploadString);
		return response;
	}

	/**
	 * 执行解锁定一条成绩动作
	 * 
	 * @return
	 * @throws IOException
	 * @throws SocketException
	 * @throws SQLException
	 */
	public CloseableHttpResponse reportUnlock(String[] args) throws SocketException, IOException, SQLException {
		String cookieWithoutCrumb = args[0];
		String jsonStr = args[1];
		String apiString = "reportUnlock";
		String api = apiMap.get(apiString);
		String contentType = "application/json";
		String[] uploadString = { contentType, jsonStr };
		getMyCrumb(cookieWithoutCrumb);
		logger.info("解锁此条成绩：");
		CloseableHttpResponse response = baseApi.Client.getPostResponse(getUrl() + api, api, api, getCookie(),
				uploadString);
		return response;
	}

	/**
	 * 获取所有可以修改的攻击方成绩的id列表，状态为0，5，6
	 * 
	 * @param args
	 * @return attackerReportList
	 * @throws ParseException
	 * @throws IOException
	 */
	public List<String> getAttackerReportId(String[] args) throws ParseException, IOException {
		String apiString = "reportAttacker";
		String cookieWithoutCrumb = args[0];
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString);
		List<String> attackerReportList = new ArrayList<String>();
		logger.info("返回所有可编辑成绩的id：");
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api + "page=1&", api + "page=1&", api,
				getCookie());
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			Double reportNum = jObject.get("data").getAsJsonObject().get("total").getAsDouble();
			int pagesNum = (int) Math.ceil(reportNum / 10);
			for (int i = 0; i < pagesNum; i++) {
				String apiPages = api + "page=" + (i + 1) + "&";
				CloseableHttpResponse pagesResponse = Client.getGetResponse(getUrl() + apiPages, apiPages, api,
						getCookie());
				JsonObject pagesObject = Client.getResponseDataJson(pagesResponse);
				JsonArray ReportArray = pagesObject.get("data").getAsJsonObject().get("list").getAsJsonArray();
				int singlePageReportNum = ReportArray.size();
				for (int j = 0; j < singlePageReportNum; j++) {
					String status = ReportArray.get(j).getAsJsonObject().get("status").getAsString();
					if ("0".equals(status) || "5".equals(status) || "6".equals(status)) {
						String id = ReportArray.get(j).getAsJsonObject().get("id").getAsString();
						String update_ts = ReportArray.get(j).getAsJsonObject().get("update_ts").getAsString();
						attackerReportList.add(id + "==>" + update_ts);
					}

				}
			}
		}
		// logger.info("attackerReportIdList: "+attackerReportList);
		return attackerReportList;
	}

	public CloseableHttpResponse getAttackerView(String[] args) {
		String apiString = "attackerView";
		String cookieWithoutCrumb = args[0];
		String attackerReportId = args[1];
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString);
		logger.info("打开一次该成绩树：");
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api + attackerReportId + "?cache=0&",
				api + attackerReportId + "?cache=0&", api, getCookie());
		return response;
	}

	/**
	 * check stage api result
	 * 
	 * @param args
	 * @return
	 * @throws IOException
	 * @throws SocketException
	 */
	public String reportStashTask(String[] args) throws SocketException, IOException {
		String apiString = "reportStashTask";
		String cookieWithoutCrumb = args[0];
		String taskId = args[1];
		String uuidString = "";
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString);
		logger.info("检查一次创建成绩树任务的执行结果：");
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api + taskId + "?",
				api + taskId + "?", api, getCookie());
		if (response != null) {
			JsonObject jsonObject = Client.getResponseDataJson(response);
			logger.info(jsonObject + "\n");
			uuidString = jsonObject.get("data").getAsString();
		}
		return uuidString;
	}

	/**
	 * check stage api result
	 * 
	 * @param args
	 * @return
	 * @throws IOException
	 * @throws SocketException
	 */
	public String nodeSaveTask(String[] args) throws SocketException, IOException {
		String apiString = "reportNodeTask";
		String cookieWithoutCrumb = args[0];
		String taskId = args[1];
		String result = "";
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString);
		logger.info("检查一次保存成绩节点任务的执行结果：");
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api + taskId + "?",
				api + taskId + "?", api, getCookie());
		if (response != null) {
			JsonObject jsonObject = Client.getResponseDataJson(response);
			logger.info(jsonObject);
			if (jsonObject.get("data").isJsonObject()) {
				result = jsonObject.get("data").getAsJsonObject().get("res").getAsString();
				if ("true".equals(result)) {
					logger.info("节点保存成功，校验通过");
				} else if ("false".equals(result)) {
					logger.info("节点保存成功，校验失败");
					result = "节点保存成功，校验失败";
				}
			} else {
				result = jsonObject.get("data").getAsString();
			}
		}
		return result;
	}

	/**
	 * check create report api result
	 * 
	 * @param args
	 * @return
	 * @throws IOException
	 * @throws SocketException
	 */
	public String reportCreateTask(String[] args) throws SocketException, IOException {
		String apiString = "reportCreateTask";
		String cookieWithoutCrumb = args[0];
		String taskId = args[1];
		String result = "";
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString);
		logger.info("检查一次提交成绩任务的执行结果：");
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api + taskId + "?",
				api + taskId + "?", api, getCookie());
		if (response != null) {
			JsonObject jsonObject = Client.getResponseDataJson(response);
			logger.info(jsonObject + "\n");
			result = jsonObject.get("data").getAsString();
		}
		return result;
	}

	public String getAttackerReportDestId(String[] args) throws SocketException, IOException {
		String apiString = "getReportAttacker";
		String cookieWithoutCrumb = args[0];
		String attackerReportId = args[1];
		String lastUpdateTs = java.net.URLEncoder.encode(args[2], "UTF-8");
		lastUpdateTs = lastUpdateTs.replace("+", "%20");
		String orgDstId = "";
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString) + attackerReportId + "?" + "last_update_ts=" + lastUpdateTs + "&";
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api, api, apiMap.get(apiString),
				getCookie());
		if (response != null) {
			JsonObject jsonObject = Client.getResponseDataJson(response);
			logger.info(jsonObject.toString());
			orgDstId = jsonObject.get("data").getAsJsonObject().get("org_dst_id").getAsString();
		}
		return orgDstId;
	}

	public void reportCheck(String[] args) {
		String apiString = "reportCheck";
		String cookieWithoutCrumb = args[0];
		String attackerReportUuid = args[1];
		String orgDestId = args[2];
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString) + "uuid=" + attackerReportUuid + "&organization_dst_id=" + orgDestId + "&";
		baseApi.Client.getGetResponse(getUrl() + api, api, apiMap.get(apiString), getCookie());
	}

	public CloseableHttpResponse getReportNodePostil(String[] args) {
		String apiString = "getReportNodePostil";
		String cookieWithoutCrumb = args[0];
		String nodeUuid = args[1];
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString) + "?uuid=" + nodeUuid + "&";
		logger.info("检查此成绩节点的uuid:");
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api, api, apiMap.get(apiString),
				getCookie());
		return response;
	}

	/**
	 * 获取每个状态为0\2\6的节点的uuid,pid
	 * 
	 * @param args
	 * @return attackerReportInfoList
	 * @throws SocketException
	 * @throws IOException
	 */
	public List<String> getAttackerReportInfo(String[] args) throws SocketException, IOException {
		String attackerReportId = args[1];
		List<String> attackerReportInfoList = new ArrayList<String>();
		JsonObject jsonObject = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		logger.info("返回一条成绩可编辑节点的id,uuid,pid");
		CloseableHttpResponse response = getAttackerView(args);
		if (response != null) {
			jsonObject = Client.getResponseDataJson(response);
			// String front_tpl_hash =
			// jsonObject.get("data").getAsJsonObject().get("front_tpl_hash").getAsString();
			// attackerReportInfoList.add(front_tpl_hash);
			jsonArray = jsonObject.get("data").getAsJsonObject().get("nodeList").getAsJsonArray();
			for (int i = 0; i < jsonArray.size(); i++) {
				String nodeStatus = jsonArray.get(i).getAsJsonObject().get("status").getAsString();
				if ("0".equals(nodeStatus) || "2".equals(nodeStatus) || "6".equals(nodeStatus)) {
					StringBuilder nodeBuilder = new StringBuilder();
					nodeBuilder.append(attackerReportId + ",");
					nodeBuilder.append(jsonArray.get(i).getAsJsonObject().get("uuid").getAsString() + ",");
					nodeBuilder.append(jsonArray.get(i).getAsJsonObject().get("pid").getAsString());
					attackerReportInfoList.add(nodeBuilder.toString());
				}

			}
			// String node_map =
			// jsonObject.get("data").getAsJsonObject().get("node_map").getAsString();
			// attackerReportInfoList.add(node_map);
		}
		return attackerReportInfoList;
	}

	// 生成node函数
	public JsonObject htmlNode(String[] args) {
		/**
		 * 参数1：此节点的i 参数2：此节点的tag 参数3：此节点的level 参数4：此节点的x position 参数5：此节点的y position
		 * 参数6：可选，此节点的父节点的i 参数7：可选，此节点的父节点的tag
		 */
		int i = Integer.parseInt(args[0]);
		String tag = args[1];
		int level = Integer.parseInt(args[2]);
		double x = Double.parseDouble(args[3]);
		double y = Double.parseDouble(args[4]);

		JsonObject htmlNode = new JsonObject();
		JsonObject pObject = new JsonObject();
		JsonObject aObject = new JsonObject();
		JsonObject positionObject = new JsonObject();
		htmlNode.addProperty("c", "ht.HtmlNode");
		htmlNode.addProperty("i", i);
		positionObject.addProperty("x", x);
		positionObject.addProperty("y", y);
		pObject.addProperty("tag", tag);
		pObject.add("image", null);

		// 区分是否是根节点
		if (args.length > 5) { // 非根节点需要7个参数
			int parent = Integer.parseInt(args[5]);
			String parent_id = args[6];
			// 生成sObject
			JsonObject sObject = new JsonObject();
			sObject.addProperty("select.padding", 0);

			// 生成parentObject
			JsonObject parentObject = new JsonObject();
			parentObject.addProperty("__i", parent);

			// 生成非根节点的pObject
			pObject.add("parent", parentObject);
			pObject.addProperty("width", 174);
			pObject.addProperty("height", 71);

			// 非根节点的aObject多一个parent_id
			aObject.addProperty("parent_id", parent_id);

			// 非根节点多一个sObject
			htmlNode.add("s", sObject);
		} else { // 根节点需要4个参数
			// 生成根节点的pObject
			pObject.addProperty("width", 126);
			pObject.addProperty("height", 42);

			// 根节点的aObject多一个is_root
			aObject.addProperty("isRoot", true);
		}
		pObject.add("position", positionObject);
		aObject.addProperty("collapse", true);
		aObject.addProperty("level", level);
		htmlNode.add("p", pObject);
		htmlNode.add("a", aObject);

		return htmlNode;
	}

	public JsonObject htEdge(String[] args) {

		/**
		 * 参数1：sourceI，前面第二个node的id 参数2：targetI，前面第一个node的id 参数3：x0，前面第二个node的position
		 * 参数4：y0，前面第二个node的position 参数5：x1，前面第一个node的position 参数6：y1，前面第一个node的position
		 */
		int sourceI = Integer.parseInt(args[0]);
		int targetI = Integer.parseInt(args[1]);
		double x0 = Double.parseDouble(args[2]);
		double y0 = Double.parseDouble(args[3]);
		double x1 = Double.parseDouble(args[4]);
		double y1 = Double.parseDouble(args[5]);

		JsonObject htEdgeJsonObject = new JsonObject();
		JsonObject pObject = new JsonObject();
		JsonObject sObject = new JsonObject();
		JsonObject sourceObject = new JsonObject();
		JsonObject targetObject = new JsonObject();
		JsonObject edgePointsObject = new JsonObject();
		JsonObject edge0Object = new JsonObject();
		JsonObject edge1Object = new JsonObject();
		JsonArray Aarray = new JsonArray();
		JsonObject iconsObject = new JsonObject();
		JsonObject toArrowObject = new JsonObject();
		JsonObject namesObject = new JsonObject();

		sourceObject.addProperty("__i", sourceI);
		targetObject.addProperty("__i", targetI);
		pObject.add("source", sourceObject);
		pObject.add("target", targetObject);
		edge0Object.addProperty("x", x0);
		edge0Object.addProperty("y", y0);
		edge1Object.addProperty("x", x1);
		edge1Object.addProperty("y", y1);
		Aarray.add(edge0Object);
		Aarray.add(edge1Object);
		edgePointsObject.add("__a", Aarray);
		namesObject.addProperty("names", "toArrow");
		toArrowObject.addProperty("position", 19);
		toArrowObject.addProperty("offsetX", 5);
		toArrowObject.addProperty("keepOrien", true);
		toArrowObject.addProperty("width", 40);
		toArrowObject.addProperty("height", 20);
		toArrowObject.add("names", namesObject);
		iconsObject.add("toArrow", toArrowObject);
		sObject.addProperty("edge.color", "#ABC3FF");
		sObject.addProperty("edge.gap", 0);
		sObject.addProperty("edge.source.offset.x", 2);
		sObject.addProperty("edge.source.anchor.x", 1);
		sObject.addProperty("edge.source.anchor.y", 0.5);
		sObject.addProperty("edge.target.anchor.x", 0);
		sObject.addProperty("edge.type", "points");
		sObject.add("edge.points", edgePointsObject);
		sObject.addProperty("edge.target.offset.x", -2);
		sObject.addProperty("edge.corner.radius", 5);
		sObject.add("icons", iconsObject);

		htEdgeJsonObject.addProperty("c", "ht.Edge");
		htEdgeJsonObject.addProperty("i", targetI + 1);
		htEdgeJsonObject.add("p", pObject);
		htEdgeJsonObject.add("s", sObject);
		return htEdgeJsonObject;
	}

	public JsonObject getGraphData(int tagId, int row, int colum) {

		JsonObject graphData = new JsonObject();
		JsonArray nodeListArray = new JsonArray();
		JsonObject frontTplObject = new JsonObject();
		JsonObject pObject = new JsonObject();
		JsonArray dArray = new JsonArray();

		// 拼接front_tpl的pObject
		pObject.addProperty("autoAdjustIndex", true);
		pObject.addProperty("hierarchicalRendering", false);

		// 拼接front_tpl的dArray
		frontTplObject.addProperty("v", "6.2.3");
		frontTplObject.add("p", pObject);

		/**
		 * Node create
		 */
		if (row % 2 == 1) {
			String tagFather = "";
			int tagIdFather = tagId;
			int i = 0;
			int nodeId = 1;
			// 生成根节点
			/**
			 * 参数1：此节点的i 参数2：此节点的tag 参数3：此节点的level 参数4：此节点的x position 参数5：此节点的y position
			 */
			String[] argsRoot = { tagId + "", "path_node_root", "0", "138", ((row - 1) / 2 * 141 + 110.5) + "" };
			JsonObject rootNode = htmlNode(argsRoot);
			dArray.add(rootNode);
			tagIdFather = tagId;
			tagFather = "path_node_root";
			String souceId = "";
			String targetId = "";
			i++;
			JsonArray dArrayEdgeArray = new JsonArray();
			for (int j = 0; j < row; j++) {
				for (int k = 0; k < colum; k++) {
					{
						// 生成非根节点
						/**
						 * 参数1：此节点的i 参数2：此节点的tag 参数3：此节点的level 参数4：此节点的x position 参数5：此节点的y position
						 * 参数6：可选，此节点的父节点的i 参数7：可选，此节点的父节点的tag
						 */
						if (k == 0) {
							tagIdFather = tagId;
							tagFather = "path_node_root";
						}
						String tag = PublicRandomUtil.getRandomString(16);
						String[] args = { (tagId + i) + "", tag, (k + 1) + "", (264 * k + 378) + "",
								(141 * j + 110.5) + "", tagIdFather + "", tagFather };
						JsonObject node = htmlNode(args);
						dArray.add(node);
						JsonObject nodeObject = new JsonObject();
						nodeObject.addProperty("level", 1);
						nodeObject.addProperty("name", "成果" + nodeId);
						nodeObject.addProperty("node_sort", nodeId);
						nodeObject.addProperty("parent_id", tagFather);
						nodeObject.addProperty("uuid", tag);
						nodeListArray.add(nodeObject);

						tagIdFather = tagId + i;
						tagFather = tag;
						i++;
						nodeId++;
					}

					{
						// 生成edge节点
						/*
						 * 参数1：sourceI，前面第二个node的id 参数2：targetI，前面第一个node的id 参数3：x0，前面第二个node的position
						 * 参数4：y0，前面第二个node的position 参数5：x1，前面第一个node的position 参数6：y1，前面第一个node的position
						 */
						double x0 = 0;
						double y0 = 0;
						double x1 = 0;
						double y1 = 0;

						if (k == 0) {
							souceId = tagId + "";
							targetId = (tagId + i - 1) + "";
							x0 = 248;
							y0 = 251.5;
							x1 = 248;
							y1 = 110.5 + (141 * j);
						} else {
							x0 = 248 + (264 * k);
							y0 = 110.5 + (141 * j);
							x1 = x0;
							y1 = y0;
							souceId = (tagId + i - 3) + "";
							targetId = (tagId + i - 1) + "";
						}
						String[] argsEdge = { souceId, targetId, x0 + "", y0 + "", x1 + "", y1 + "" };
						JsonObject edge = htEdge(argsEdge);
						dArrayEdgeArray.add(edge);
						i++;
					}
				} // end for k
			} // end for j
			dArray.addAll(dArrayEdgeArray);
		} // end if

		frontTplObject.add("d", dArray);

		graphData.addProperty("front_tpl", frontTplObject.toString());
		graphData.add("front_tpl_hash", null);
		graphData.add("nodeList", nodeListArray);
		return graphData;
	}

	public CloseableHttpResponse zeroDayAttackerCreate(String[] args) throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		JsonObject jsonObject = new JsonObject();
		JsonArray dataArray = new JsonArray();
		JsonArray affectArray = new JsonArray();
		JsonObject dataObject = new JsonObject();
		String apiString = "zeroDayAttackerCreate";
		String api = apiMap.get(apiString);
		String contentType = "application/json";
		String filePath = args[0];
		String fileName = args[1];
		String cookieWithoutCrumb = args[2];
		getMyCrumb(cookieWithoutCrumb);
		Random random = new Random();
		// ==================>>开始构建元素<<========================================
		jsonObject.addProperty("vulner_type", 1 + random.nextInt(5));
		jsonObject.addProperty("affect_software", "请填写受影响的软件/硬件的厂商、具体产品名称。\r\n" + "示例：腾讯QQ电脑客户端软件；天融信NGFW防火墙");
		jsonObject.addProperty("status", 2);
		jsonObject.addProperty("name", "关于" + random.nextInt(1000) + "的零day漏洞");
		jsonObject.addProperty("remark", "可在此详细描述该漏洞与互联网内已公开的该产品其他漏洞的显著区别，以及该漏洞的实际影响力描述。");
		jsonObject.addProperty("__crumb__", get__crumb__());
		dataObject.addProperty("affect_version", "Windows版 V7 ～ V10");
		dataObject.addProperty("affect_module", "请给出具体的漏洞所在功能模块。\r\n" + "示例：查看好友动态模块");
		dataObject.addProperty("vulner_process", "描述触发漏洞的条件、输入数据要求和操作过程，达到能够复现的详细程度。");
		dataObject.addProperty("file_name", fileName);
		dataObject.addProperty("file_url", filePath);
		affectArray.add(String.valueOf(1 + random.nextInt(3)));
		dataObject.add("affect", affectArray);
		dataArray.add((JsonElement) dataObject);
		jsonObject.add("data", dataArray);
		// ==================>>结束构建元素<<========================================
		String jsonStr = jsonObject.toString();
		logger.info(jsonStr);
		String[] uploadString = { contentType, jsonStr };
		response = baseApi.Client.getPostResponse(getUrl() + api, api, api, getCookie(), uploadString);
		return response;
	}
}

class ReportNode extends HwAttackerBase {
	// 定义若干列表的元素个数
	private int threatsNum = 3;// 25;
	private int tagsNum = 3;// 18;
	private int informationsNum = 3;// 43;
	private int exist_questionNum = 3;// 6;
	private int exposuresNum = 3;// 9;

	private CloseableHttpResponse openNodeResponse;
	private String id = "";
	private String uuid = "";
	private String parent_id = "";
	private int asset_type = 3;
	private String name = "";
	private String url = "";
	private String ip = "";
	private String process_description = "";
	private String asset_category_id = "1";
	private int status = 0;
	private JsonArray report_tag = new JsonArray();
	private JsonArray threats = Threats.getThreats(threatsNum);
	private JsonArray attacks = Attacks.getTagsArray(tagsNum);
	private JsonArray information = Information.getInformationsArray(informationsNum);
	private JsonArray questions = Questions.getQuestionArray(exist_questionNum);
	private JsonArray exposures = Exposures.getExposures(exposuresNum);
	private JsonArray images = Images.getImages();

	// 初始化变量

	public JsonObject getNode() {
		// 初始化若干变量
		Random random = new Random();
		ip = PublicRandomUtil.getRandomIp();
		url = "http://" + ip + "/abc.com/" + random.nextInt(100);
		name = "网络节点" + ip;
		String randomString = PublicRandomUtil.getRandomCharacter(5000);
		String imageString = "";
		for (int i = 0; i < images.size(); i++) {
			imageString += "<img src=" + images.get(i) + ">";
		}
		process_description = "<div><div>" + randomString + "</div></div><div>" + imageString + "<br></div><div></div>";

		// 打开一个节点并获取节点的部分内容
		String update_ts = "";
		String node_sort = "";
		JsonObject postil = new JsonObject();
		if (openNodeResponse != null) {
			JsonObject openNodeObject = new JsonObject();
			try {
				openNodeObject = Client.getResponseDataJson(openNodeResponse);
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			update_ts = openNodeObject.get("data").getAsJsonObject().get("update_ts").getAsString();
			node_sort = openNodeObject.get("data").getAsJsonObject().get("node_sort").getAsString();
			postil = openNodeObject.get("data").getAsJsonObject().get("postil").getAsJsonObject();
		}
		// String[] getReportNodePostilArgs = {cookieWithoutCrumb,nodeUuid};
		// //没有接口getReportNodePostil了
		// getReportNodePostil(getReportNodePostilArgs); //没有接口getReportNodePostil了

		// 拼接节点
		JsonObject nodeObject = new JsonObject();
		nodeObject.addProperty("id", id);
		nodeObject.addProperty("uuid", uuid);
		nodeObject.addProperty("parent_id", parent_id);
		nodeObject.addProperty("node_sort", node_sort);
		nodeObject.addProperty("asset_type", asset_type);
		nodeObject.addProperty("name", name);
		nodeObject.addProperty("url", url);
		nodeObject.addProperty("ip", ip);
		nodeObject.addProperty("status", status);
		nodeObject.addProperty("is_view_node", 0);
		nodeObject.addProperty("process_description", process_description);
		nodeObject.addProperty("asset_category_id", asset_category_id);
		nodeObject.addProperty("update_ts", update_ts);
		nodeObject.add("postil", postil);

		nodeObject.add("report_tag", report_tag);
		nodeObject.add("threats", threats);
		nodeObject.add("attacks", attacks);
		nodeObject.add("information", information);
		nodeObject.add("questions", questions);
		nodeObject.add("exposures", exposures);
		nodeObject.add("images", images);
		// logger.info(nodeObject.toString());
		return nodeObject;
	}

	// 添加两个constructor
	public ReportNode() {
		super();
	}

	public ReportNode(String id, String uuid, String parent_id, CloseableHttpResponse openNodeResponse) {
		super();
		this.id = id;
		this.uuid = uuid;
		this.parent_id = parent_id;
		this.openNodeResponse = openNodeResponse;
	}

}

/**
 * threats的组装：从所有可能的id列表中，随机获取threatsNum个id，并组装成threatsObject
 * 
 * @author litao
 *
 */
class Threats {
	public static JsonArray getThreats(int threatsNum) {
		JsonArray threats = new JsonArray();
		// 添加生成threats方法
		String threatsString = "{\"1\":[73,74],\"2\":[78,79],\"3\":[80,81],"
				+ "\"82\":[83,86,87,88,89],\"7\":[8,9,94],\"13\":[84,85],"
				+ "\"14\":[15,97,98,99],\"10\":[12,11],\"16\":[100,101]," + "\"17\":[17],\"18\":[19,20],"
				+ "\"21\":[76,77,103,104,105,106,107,108,109,110,111,112,113,114,115,116],"
				+ "\"117\":[117],\"24\":[25,118],\"29\":[30,31,119,120,121,122,123,124],"
				+ "\"4\":[5,6,125,126,127,128],\"33\":[33],\"34\":[34],"
				+ "\"75\":[75],\"36\":[36],\"37\":[37],\"46\":[46]," + "\"47\":[47],\"49\":[49],\"50\":[50]}"; // 25
																												// Threats
		JsonObject ramsStringObject = new JsonParser().parse(threatsString).getAsJsonObject();
		String[] ramsParentId = new String[25]; // rams
		int i = 0;
		for (Entry<String, JsonElement> entry : ramsStringObject.entrySet()) {
			ramsParentId[i] = entry.getKey();
			i += 1;
		}
		List<Integer> ramsPidList = PublicRandomUtil.getRandomNum(0, i, threatsNum);

		for (int j = 0; j < threatsNum; j++) {
			String parentId = ramsParentId[ramsPidList.get(j)].toString();
			int order = ramsPidList.get(j) + 1;
			int childrenLength = ramsStringObject.get(parentId).getAsJsonArray().size();
			int[] childrenId = new int[childrenLength];
			for (int k = 0; k < childrenLength; k++) {
				childrenId[k] = (int) ramsStringObject.get(parentId).getAsJsonArray().get(k).getAsInt();
			}
			threats.add((JsonElement) getThreatsObject(parentId, childrenId, order));
		}
		return threats;
	}

	public static JsonElement getThreatsObject(String parentId, int[] value, int order) {
		Random random = new Random();
		JsonObject threatsElement = new JsonObject();
		JsonArray childrenArray = new JsonArray();
		threatsElement.addProperty("conflict_prompt", "");
		threatsElement.addProperty("item_score", 0);
		threatsElement.addProperty("order", order);
		threatsElement.addProperty("parent_id", parentId);
		threatsElement.addProperty("prompt", "");
		threatsElement.addProperty("suggest_score", 0);
		for (int i = 0; i < value.length; i++) {
			JsonObject childrenObject = new JsonObject();
			switch (value[i]) {
			case 73:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("ip_description", "http://www.11.com");
				childrenObject.addProperty("name", "一级域名");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 74:
				childrenObject.addProperty("ip_description", "http://www.12.com/1");
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("name", "二级域名");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 78:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("ip_description", "1.1.1.2,1.1.1.3");
				childrenObject.addProperty("name", "PC终端");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("ip_count", 2);
				childrenObject.addProperty("value", 2);
				break;
			case 79:
				childrenObject.addProperty("ip_description", "1.1.1.4,1.1.1.5");
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("name", "移动终端");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("ip_count", 2);
				childrenObject.addProperty("value", 2);
				break;
			case 80:
				childrenObject.addProperty("ip_description", "10.1.1.1");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", "1");
				break;
			case 81:
				childrenObject.addProperty("ip_description", "10.1.1.2");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", "1");
				break;
			case 83:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("ip_description", "10.1.1.3");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "OA系统");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", "1");
				break;
			case 86:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "10.1.1.4");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "即时通讯系统");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", "1");
				break;
			case 87:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "10.1.1.5");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "项目管理系统");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", "1");
				break;
			case 88:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "10.1.1.6");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "财务系统");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", "1");
				break;
			case 89:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "10.1.1.7");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "其他");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", "1");
				break;
			case 8:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("ip_description", "2.2.2.2");
				childrenObject.addProperty("name", "系统管理权限");
				childrenObject.addProperty("pname", "SSO");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 9:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "2.2.2.3");
				childrenObject.addProperty("name", "能登入的系统");
				childrenObject.addProperty("pname", "SSO");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 94:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "2.2.2.4");
				childrenObject.addProperty("name", "系统管理权限");
				childrenObject.addProperty("pname", "4A");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 95:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "2.2.2.5");
				childrenObject.addProperty("name", "能登入的系统");
				childrenObject.addProperty("pname", "4A");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 84:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("ip_description", "4.4.4.5");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", "1");
				break;
			case 85:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "4.4.4.6");
				childrenObject.addProperty("name", "域内可控服务器");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", "1");
				break;
			case 15:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("ip_description", "5.5.5.5");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "堡垒机");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", "1");
				break;
			case 97:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "5.5.5.6");
				childrenObject.addProperty("name", "托管的服务器");
				childrenObject.addProperty("pname", "堡垒机");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", "1");
				break;
			case 98:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "5.5.5.7");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "运维机");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", "1");
				break;
			case 99:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "5.5.5.8");
				childrenObject.addProperty("name", "托管的服务器");
				childrenObject.addProperty("pname", "运维机");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", "1");
				break;
			case 11:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "15.5.5.8");
				childrenObject.addProperty("name", "普通用户权限");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 12:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("ip_description", "15.5.5.9");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 100:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("ip_description", "6.6.6.6");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", "1");
				break;
			case 101:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "6.6.6.7");
				childrenObject.addProperty("name", "云上主机");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", "1");
				break;
			case 17:
				childrenObject.addProperty("description", "8txt");
				childrenObject.addProperty("ip_description", "10.10.1.1");
				childrenObject.addProperty("ip_count", 1);
				break;
			case 19:
				childrenObject.addProperty("ip_description", "10.10.1.2");
				childrenObject.addProperty("value", "1");
				childrenObject.addProperty("ip_count", 1);
				break;
			case 20:
				childrenObject.addProperty("ip_description", "10.10.1.3");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", "1");
				break;
			case 76:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("ip_description", "7.7.7.7");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "防火墙");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 77:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "7.7.7.8");
				childrenObject.addProperty("name", "普通用户权限");
				childrenObject.addProperty("pname", "防火墙");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 103:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "7.7.7.9");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "路由器");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 104:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "7.7.7.10");
				childrenObject.addProperty("name", "普通用户权限");
				childrenObject.addProperty("pname", "路由器");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 105:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "7.7.7.11");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "交换机");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 106:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "7.7.7.12");
				childrenObject.addProperty("name", "普通用户权限");
				childrenObject.addProperty("pname", "交换机");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 107:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "7.7.7.13");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "网闸");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 108:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "7.7.7.14");
				childrenObject.addProperty("name", "普通用户权限");
				childrenObject.addProperty("pname", "网闸");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 109:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "7.7.7.15");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "光闸");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 110:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "7.7.7.16");
				childrenObject.addProperty("name", "普通用户权限");
				childrenObject.addProperty("pname", "光闸");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 111:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "7.7.7.17");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "摆渡机");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 112:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "7.7.7.18");
				childrenObject.addProperty("name", "普通用户权限");
				childrenObject.addProperty("pname", "摆渡机");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 113:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "7.7.7.19");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "VPN");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 114:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "7.7.7.20");
				childrenObject.addProperty("name", "普通用户权限");
				childrenObject.addProperty("pname", "VPN");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 115:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "7.7.7.21");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "其他");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 116:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "7.7.7.22");
				childrenObject.addProperty("name", "普通用户权限");
				childrenObject.addProperty("pname", "其他");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 117:
				childrenObject.addProperty("description", "11txt");
				break;
			case 25:
				childrenObject.addProperty("value", 3 + random.nextInt(5));
				break;
			case 118:
				childrenObject.addProperty("value", 3 + random.nextInt(5));
				break;
			case 30:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("ip_description", "8.8.8.1");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "IDS");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 31:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "8.8.8.2");
				childrenObject.addProperty("name", "普通用户权限");
				childrenObject.addProperty("pname", "IDS");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 119:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "8.8.8.3");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "审计设备");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 120:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "8.8.8.4");
				childrenObject.addProperty("name", "普通用户权限");
				childrenObject.addProperty("pname", "审计设备");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 121:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "8.8.8.5");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "WAF");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 122:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "8.8.8.6");
				childrenObject.addProperty("name", "普通用户权限");
				childrenObject.addProperty("pname", "WAF");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 123:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "8.8.8.7");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "其他");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 124:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "8.8.8.8");
				childrenObject.addProperty("name", "普通用户权限");
				childrenObject.addProperty("pname", "其他");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 5:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "9.9.9.1");
				childrenObject.addProperty("name", "普通用户权限");
				childrenObject.addProperty("pname", "Web应用");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 6:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("ip_description", "9.9.9.2");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "Web应用");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 125:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "9.9.9.3");
				childrenObject.addProperty("name", "普通用户权限");
				childrenObject.addProperty("pname", "FTP");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 126:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "9.9.9.4");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "FTP");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 127:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "9.9.9.5");
				childrenObject.addProperty("name", "普通用户权限");
				childrenObject.addProperty("pname", "其他");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 128:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("ip_description", "9.9.9.6");
				childrenObject.addProperty("name", "管理员权限");
				childrenObject.addProperty("pname", "其他");
				childrenObject.addProperty("ip_count", 1);
				childrenObject.addProperty("value", 1);
				break;
			case 33:
				childrenObject.addProperty("description", "14txt");
				childrenObject.addProperty("ip_description", "10.10.2.1");
				childrenObject.addProperty("ip_count", 1);
				break;
			case 34:
				childrenObject.addProperty("description", "15txt");
				break;
			case 75:
				childrenObject.addProperty("description", "16txt");
				break;
			case 36:
				childrenObject.addProperty("description", "17txt");
				break;
			case 37:
				childrenObject.addProperty("description", "18txt");
				break;
			case 46:
				childrenObject.addProperty("description", "19txt");
				break;
			case 47:
				childrenObject.addProperty("description", "20txt");
				break;
			case 49:
				childrenObject.addProperty("description", "21txt");
				break;
			case 50:
				childrenObject.addProperty("description", "22txt");
				break;
			default:
				break;
			}
			childrenObject.addProperty("id", String.valueOf(value[i]));
			childrenArray.add((JsonElement) childrenObject);
		}
		threatsElement.add("children", (JsonElement) childrenArray);
		return threatsElement;
	}
}

/**
 * attacks的组装，从所有可能的attacks种类中随机获取tagsNum个attack类型，并组装成jsonObject返回
 * 
 * @author litao
 *
 */
class Attacks {
	public static JsonArray getTagsArray(int tagsNum) {
		JsonArray tagsArray = new JsonArray(); // tags
		String tagsString = "{\"56\":[1],\"57\":[1],\"58\":[],\"59\":[],\"60\":[],\"61\":[],\"62\":[],"
				+ "\"63\":[],\"64\":[],\"65\":[],\"66\":[],\"67\":[],\"68\":[],"
				+ "\"84\":[],\"85\":[],\"88\":[],\"89\":[]," + "\"90\":[]}"; // 18 attacks
		JsonObject tagsStringObject = new JsonParser().parse(tagsString).getAsJsonObject();
		String[] tagsId = new String[25]; // rams
		int i = 0;
		for (Entry<String, JsonElement> entry : tagsStringObject.entrySet()) {
			tagsId[i] = entry.getKey();
			i += 1;
		}
		List<Integer> tagsidList = PublicRandomUtil.getRandomNum(0, i, tagsNum);
		for (int j = 0; j < tagsNum; j++) {
			String id = tagsId[tagsidList.get(j)].toString();
			if (id.equals("56") || id.equals("57")) {
				JsonArray vulnerability0Array = new JsonArray(); // tags
				JsonObject tags0 = new JsonObject(); // tagsid=56
				JsonObject vulnerability0 = new JsonObject(); //
				if (id.equals("56")) {
					vulnerability0.addProperty("type", "");
					tags0.addProperty("id", "56"); // tags0id56
				} else if (id.equals("57")) {
					vulnerability0.addProperty("type", "");
					tags0.addProperty("id", "57"); // tags0id57
				}
				vulnerability0.addProperty("vulnerability_name", "vulnerability_name001");
				vulnerability0.addProperty("vulnerability_publish", "1");
				vulnerability0.addProperty("vulnerability_version", "223");
				vulnerability0.addProperty("vulnerability_code", "asdfasdf");
				vulnerability0.addProperty("report_node_tag_id", "91");
				vulnerability0.addProperty("vulnerability_description", "vulnerability_description11");
				vulnerability0.addProperty("zero_day_tags", "");
				JsonArray vulnerability0_systemArray = new JsonArray(); //
				vulnerability0_systemArray.add("windows");
				vulnerability0_systemArray.add("linux");
				vulnerability0.add("vulnerability_system", vulnerability0_systemArray);
				vulnerability0Array.add((JsonElement) vulnerability0);
				tags0.add("vulnerability", vulnerability0Array); // tags0
				tagsArray.add((JsonElement) tags0);
			} else {
				JsonArray vulnerability1Array = new JsonArray(); // tags
				JsonObject tags1 = new JsonObject(); // tags
				tags1.addProperty("id", id);
				tags1.add("vulnerability", vulnerability1Array);
				tagsArray.add((JsonElement) tags1);
			}
		}
		return tagsArray;
	}
}

/**
 * Information的组装，从所有可能的Information种类中随机获取informationsNum个Information类型，并组装成jsonObject返回
 * 
 * @author litao
 *
 */
class Information {
	public static JsonArray getInformationsArray(int informationsNum) {
		JsonArray informationsArray = new JsonArray(); // informations
		String[] informationsId = { "2", "3", "5", "7", "8", "9", "10", "11", "12", "14", "15", "16", "17", "18", "19",
				"20", "22", "23", "24", "25", "27", "28", "29", "30", "32", "33", "34", "35", "36", "37", "70", "71",
				"72", "73", "74", "76", "78", "79", "80", "81", "82", "83", "103" }; // 43 informations
		List<Integer> informationsList = PublicRandomUtil.getRandomNum(0, informationsId.length, informationsNum);
		for (int j = 0; j < informationsNum; j++) {
			String id = informationsId[informationsList.get(j)].toString();
			informationsArray.add(id);
		}
		return informationsArray;
	}
}

/**
 * Questions的组装，从所有可能的Questions种类中随机获取exist_questionNum个Questions类型，并组装成jsonObject返回
 * 
 * @author litao
 *
 */
class Questions {
	public static JsonArray getQuestionArray(int exist_questionNum) {
		JsonArray exist_questionArray = new JsonArray();
		/*
		 * String[] exist_questionId = {"38","39","40","41","42","43","44",
		 * "45","46","47","48","49","50","51", "52","53","54","55"}; //18 exist_question
		 */
		// String[] exist_questionId = { "118", "145", "125", "157", "134", "139" }; //
		// 6 exist_question
		String[] exist_questionId = { "119", "123", "120", "124", "121", "122", "126", "130", "127", "131", "128",
				"132", "129", "133", "135", "136", "137", "138", "140", "144", "141", "142", "143", "146", "150", "154",
				"147", "151", "155", "148", "152", "156", "149", "153", "158" }; // 35 exist_question
		List<Integer> exist_questionList = PublicRandomUtil.getRandomNum(0, exist_questionId.length, exist_questionNum);
		for (int j = 0; j < exist_questionNum; j++) {
			String id = exist_questionId[exist_questionList.get(j)].toString();
			exist_questionArray.add(id);
		}
		return exist_questionArray;
	}
}

/**
 * Exposures的组装，从所有可能的Exposures种类中随机获取exposuresNum个Exposures类型，并组装成jsonObject返回
 * 
 * @author litao
 *
 */
class Exposures {
	public static JsonArray getExposures(int exposuresNum) {
		JsonArray exposuresArray = new JsonArray(); // exposures
		String[] exposuresArrayId = { "108", "109", "110", "111", "112", "113", "114", "115", "116" }; // 9 exposures
		List<Integer> exposuresArrayList = PublicRandomUtil.getRandomNum(0, exposuresArrayId.length, exposuresNum);
		for (int j = 0; j < exposuresNum; j++) {
			String id = exposuresArrayId[exposuresArrayList.get(j)].toString();
			exposuresArray.add(id);
		}
		return exposuresArray;
	}
}

class Images {
	public static JsonArray getImages() {
		// 添加生成images方法，图片来源：/huwang/server/src/web/runtime/uploads/report
		// 将测试机种上面路径下的图片名称替换到下面的列表中即可。
		JsonArray imagesArray = new JsonArray();
		imagesArray.add("/report/09361209b0649ffc6c0c5a4e95c60f66.png");
		imagesArray.add("/report/257385ccf9a733f944ea51723df1540b.png");
		imagesArray.add("/report/3f904fc110ebce399ed5aa27bfd397d2.png");
		imagesArray.add("/report/4f9728610e63ca38f00fe7fa22697eec.jpg");
		imagesArray.add("/report/517602fa42f99e4da86ff0c265c48985.jpg");
		imagesArray.add("/report/69fe6444f73274d3d7ac4618a1175689.png");
		imagesArray.add("/report/6bf53e8ab18ceaad0e1fe642675b75ed.jpg");
		imagesArray.add("/report/8b4d9a4ebcc461b4022ffaa0718f3ecf.png");
		imagesArray.add("/report/94a74411521a415ce81c254954a5b296.png");
		imagesArray.add("/report/99d7a271cc007dea1f5296f9de15832d.png");
		imagesArray.add("/report/9c509ef2eb990b769a3a9b62ace58960.jpg");
		imagesArray.add("/report/b0536a3586ae9263dd741054da8889fd.png");
		imagesArray.add("/report/b695a4685979fb65c38e32c2e75e66fb.jpg");
		imagesArray.add("/report/bb62725d3904e124b7b0b453e3e5f7a3.jpg");
		imagesArray.add("/report/c8dc363d2d6f5c500880bf1fecd74507.png");
		imagesArray.add("/report/e21cb9211465a4c33d21e81e96bcef06.jpg");
		imagesArray.add("/report/e8366db6718fc3d8d6fc32199101928e.png");
		imagesArray.add("/report/ea0a0ed8e7564a133688fc7f5c66504d.jpg");
		imagesArray.add("/report/ec6a889493219fa1d0cf2f3b4f33b60b.jpg");
		imagesArray.add("/report/ed8f33308eeb2dcc27019874e3c6aaa5.jpg");
		imagesArray.add("/report/f7239d41807dd249d53e03944e4571f2.png");
		return imagesArray;
	}
}

class PublicRandomUtil {
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

	public static List<Integer> getRandomNum(int requMin, int requMax, int targetLength) {
		if (requMax - requMin < 1) {
			System.out.print("Arguments fault,requMax must bigger than requMin");
			return null;
		} else if (requMax - requMin < targetLength) {
			System.out.print("Arguments fault,targetLength must bigger than requMax-requMin");
			return null;
		}
		List<Integer> list = new ArrayList<>();
		List<Integer> requList = new ArrayList<>();
		for (int i = requMin; i < requMax; i++) {
			requList.add(i);
		}
		for (int i = 0; i < targetLength; i++) {
			int r = (int) (Math.random() * requList.size());
			list.add(requList.get(r));
			requList.remove(r);
		}
		return list;
	}

	public static String getRandomString(int length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(62);
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}

	// 返回固定长度的中文字符串
	public static String getRandomCharacter(int length) {
		String str = "在苍茫的大海上，狂风卷集着乌云。在乌云和大海之间，海燕像黑色的闪电，" + "在高傲地飞翔。一会儿翅膀碰着波浪，一会儿箭一般地直冲向乌云，它叫喊着，"
				+ "──就在这鸟儿勇敢的叫喊声里，乌云听出了欢乐。在这叫喊声里──充满着对暴风雨的渴望！" + "在这叫喊声里，乌云听出了愤怒的力量、热情的火焰和胜利的信心。"
				+ "海鸥在暴风雨来临之前呻吟着，──呻吟着，它们在大海上飞窜，想把自己对暴风雨的恐惧，" + "掩藏到大海深处。海鸭也在呻吟着，──它们这些海鸭啊，享受不了生活的战斗的欢乐：轰隆隆的雷声就把它们吓坏了。"
				+ "蠢笨的企鹅，胆怯地把肥胖的身体躲藏到悬崖底下……只有那高傲的海燕，勇敢地，自由自在地，在泛起白沫的大海上飞翔！"
				+ "乌云越来越暗，越来越低，向海面直压下来，而波浪一边歌唱，一边冲向高空，去迎接那雷声。" + "雷声轰响。波浪在愤怒的飞沫中呼叫，跟狂风争鸣。看吧，狂风紧紧抱起一层层巨浪，"
				+ "恶狠狠地把它们甩到悬崖上，把这些大块的翡翠摔成尘雾和碎末。" + "海燕叫喊着，飞翔着，像黑色的闪电，箭一般地穿过乌云，翅膀掠起波浪的飞沫。"
				+ "看吧，它飞舞着，像个精灵，──高傲的、黑色的暴风雨的精灵，" + "——它在大笑，它又在号叫……它笑那些乌云，它因为欢乐而号叫！这个敏感的精灵，——它从雷声的震怒里，"
				+ "早就听出了困乏，它深信，乌云遮不住太阳，──是的，遮不住的！狂风吼叫……雷声轰响……一堆堆乌云，" + "像青色的火焰，在无底的大海上燃烧。大海抓住闪电的箭光，把它们熄灭在自己的深渊里。"
				+ "这些闪电的影子，活像一条条火蛇，在大海里蜿蜒游动，一晃就消失了。" + "——暴风雨！暴风雨就要来啦！这是勇敢的海燕，在怒吼的大海上，在闪电中间，高傲地飞翔；"
				+ "这是胜利的预言家在叫喊：——让暴风雨来得更猛烈些吧！";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(701);
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}
}
