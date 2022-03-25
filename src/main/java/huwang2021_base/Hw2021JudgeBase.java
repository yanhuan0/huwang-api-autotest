package huwang2021_base;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import baseApi.Client;
import huwang2020_base.HwJudgeBase;

public class Hw2021JudgeBase extends HwJudgeBase {

	private Logger logger = Logger.getLogger(Hw2021JudgeBase.class);

	private static HashMap<String, String> apiMap = new HashMap<String, String>();

	public void initConfig(String userFile, String apiFile) {
		String[] argStrings = { userFile, apiFile };
		apiMap = super.initConfig(argStrings);
	}

	public List<String> getNodeUuid(String[] args) throws ParseException, IOException {
		String apiString = "";
		if (args[0].equals("attacker")) {
			apiString = "adminapiReportJudgeViewCacheGetNode";
		} else {
			apiString = "adminapiVictimReportJudgeStatus2";
		}
		List<String> nodeList = new ArrayList<String>();
		String cookieWithoutCrumb = args[1];
		String reportUuid = args[2];
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString) + reportUuid + "?cache=0&";
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api, api, apiMap.get(apiString),
				getCookie());
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			JsonObject dataObject = jObject.get("data").getAsJsonObject();
			JsonArray nodeListArray = dataObject.get("nodeList").getAsJsonArray();
			for (int i = 0; i < nodeListArray.size(); i++) {
				String nodeUuid = nodeListArray.get(i).getAsJsonObject().get("uuid").getAsString();
				nodeList.add(nodeUuid);
			}
		}
		return nodeList;
	}

	public JsonObject getThreatType(String[] args) throws ParseException, IOException {
		String apiString = "";
		if (args[0].equals("attacker")) {
			apiString = "admiapiThreatType1";
		} else {
			apiString = "";
		}
		JsonObject threatTypeObject = new JsonObject();
		String cookieWithoutCrumb = args[1];
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString);
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api, api, api, getCookie());
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			JsonArray dataArray = jObject.get("data").getAsJsonArray();
			for (int i = 0; i < dataArray.size(); i++) {
				JsonObject TypeObject = dataArray.get(i).getAsJsonObject();
				JsonArray itemsArray = TypeObject.get("items").getAsJsonArray();
				for (int j = 0; j < itemsArray.size(); j++) {
					String id = itemsArray.get(j).getAsJsonObject().get("id").getAsString();
					String order = itemsArray.get(j).getAsJsonObject().get("order").getAsString();
					threatTypeObject.addProperty(id, order);
				}
			}
		}
		return threatTypeObject;
	}

	public JsonObject getNodeRams(String[] args) throws ParseException, IOException {
		String apiString = "";
		if (args[0].equals("attacker")) {
			apiString = "adminapiReportGetNodeRamsAll";
		} else {
			apiString = "";
		}
		String cookieWithoutCrumb = args[1];
		String reportId = args[2];
		String nodeId = args[3];
		Random random = new Random();

		// 获取threatsId
		String role = "attacker";
		String[] argString = { role, cookieWithoutCrumb };
		JsonObject threatTypeObject = getThreatType(argString);

		JsonObject nodeObject = new JsonObject();
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString) + reportId + "/" + nodeId + "?id=" + reportId + "&uuid=" + nodeId + "&";
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api, api, apiMap.get(apiString),
				getCookie());
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			JsonObject dataObject = jObject.get("data").getAsJsonObject();
			int status = 5;
			String uuid = dataObject.get("id").toString().replace("\"", "");
			String asset_category_id = dataObject.get("asset_category_id").toString().replace("\"", "");
			JsonArray exposures = dataObject.get("exposures").getAsJsonArray();
			JsonArray information = dataObject.get("information").getAsJsonArray();
			JsonArray attacks = dataObject.get("attacks").getAsJsonArray();
			JsonArray questions = dataObject.get("questions").getAsJsonArray();
			JsonArray threats = dataObject.get("threats").getAsJsonArray();
			JsonArray report_tag = dataObject.get("report_tag").getAsJsonArray();
			for (int i = 0; i < threats.size(); i++) {
				JsonObject threatObject = threats.get(i).getAsJsonObject();
				String parent_id = threatObject.get("parent_id").getAsString();
				String order = threatTypeObject.get(parent_id).getAsString();
				threatObject.addProperty("item_score", random.nextInt(20));
				threatObject.addProperty("order", Integer.parseInt(order));
			}
			nodeObject.addProperty("status", status);
			nodeObject.addProperty("uuid", uuid);
			nodeObject.addProperty("asset_category_id", asset_category_id);
			nodeObject.add("threats", threats);
			nodeObject.add("exposures", exposures);
			nodeObject.add("information", information);
			nodeObject.add("attacks", attacks);
			nodeObject.add("questions", questions);
			nodeObject.add("reportTag", report_tag);
		}
		return nodeObject;
	}

	public String postJudgeNode(String[] args) throws ParseException, IOException, SQLException {
		String apiString = "";
		String contentType = "application/json";
		if (args[0].equals("attacker")) {
			apiString = "adminapiNodeJudge";
		} else {
			apiString = "";
		}
		String cookieWithoutCrumb = args[1];
		String nodeId = args[2];
		String jsonString = args[3];
		String judgeNodeResult = "";
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString) + nodeId;
		String uploadStrings[] = { contentType, jsonString };

		CloseableHttpResponse response = null;
		try {
			response = Client.getPostResponse(getUrl() + api, api, apiMap.get(apiString), getCookie(), uploadStrings);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject);
			judgeNodeResult = jObject.get("data").getAsString();
		}
		return judgeNodeResult;
	}

	public String getReportScore(String[] args) throws ParseException, IOException {
		String apiString = "";
		if (args[0].equals("attacker")) {
			apiString = "adminapiScoreStatusScore";
		} else {
			apiString = "";
		}
		String score = "";
		String cookieWithoutCrumb = args[1];
		String reportId = args[2];
		String nodeId = args[3];
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString) + reportId + "?node_uuid=" + nodeId + "&";
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api, api, apiMap.get(apiString),
				getCookie());
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject);
			JsonObject dataObject = jObject.get("data").getAsJsonObject();
			score = dataObject.get("score").getAsString();
		}
		return score;
	}

	public String postJudgeAttackReport(String[] args) throws ParseException, IOException, SQLException {
		String apiString = "";
		String contentType = "application/json";
		String cookieWithoutCrumb = args[1];
		String reportId = args[2];
		String jsonString = args[3];
		if (args[0].equals("attacker")) {
			apiString = "adminapiScoreJudge";
		} else {
			apiString = "";
		}
		String postJudgeTask = "";
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString) + reportId;
		String uploadStrings[] = { contentType, jsonString };
		CloseableHttpResponse response = null;
		try {
			response = Client.getPostResponse(getUrl() + api, api, apiMap.get(apiString), getCookie(), uploadStrings);
			logger.info(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject);
			postJudgeTask = jObject.get("data").getAsString();
		}
		return postJudgeTask;
	}

	public String getScoreTaskResult(String[] args) throws ParseException, IOException {
		String apiString = "";
		if (args[0].equals("attacker")) {
			apiString = "adminapiScoreTask";
		} else {
			apiString = "";
		}
		String cookieWithoutCrumb = args[1];
		String taskId = args[2];
		String taskResult = "";
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString) + taskId + "?";
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api, api, apiMap.get(apiString),
				getCookie());
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			taskResult = jObject.get("data").getAsString();
			logger.info(jObject);
		}
		return taskResult;
	}

	public String getReportUpdateTs(String[] args) throws ParseException, IOException {
		String apiString = "";
		if (args[0].equals("attacker")) {
			apiString = "adminapiReportJudgeGetUpdateTs";
		} else {
			apiString = "";
		}
		String cookieWithoutCrumb = args[1];
		String reportId = args[2];
		String lastUpdateTs = "";
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString) + reportId + "?status=2&";
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api, api, apiMap.get(apiString),
				getCookie());
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject);
			lastUpdateTs = jObject.get("data").getAsJsonObject().get("update_ts").getAsString();

		}
		return lastUpdateTs;
	}
}
