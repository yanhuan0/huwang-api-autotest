package huwang2021_base;

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
import huwang2020_base.HwAttackerBase;

public class Hw2021AttackerBase extends HwAttackerBase {

	private Logger logger = Logger.getLogger(Hw2021AttackerBase.class);

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
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl(), api, api, getCookie());
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
				logger.info("没有可攻击的防守方单位！");
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
		// String apiString = "reportNode";
		String api = apiMap.get(args[3]) + args[2];
		String contentType = "application/json";
		String[] uploadString = { contentType, jsonStr };
		getMyCrumb(cookieWithoutCrumb);
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
		logger.info(jsonStr);
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
	 */
	public JsonObject getReportNode(String cookieWithoutCrumb, String[] args) {
		String reportId = args[0];
		String nodeUuid = args[1];
		String nodePid = args[2];
		String[] openNodeArgs = { cookieWithoutCrumb, reportId, nodeUuid };
		String[] getReportNodePostilArgs = { cookieWithoutCrumb, nodeUuid };
		openNode(openNodeArgs);
		getReportNodePostil(getReportNodePostilArgs);
		ReportNode reportNode = new ReportNode(reportId, nodeUuid, nodePid);
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
		logger.info("锁定此条成绩");
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
		logger.info("解锁此条成绩");
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
		logger.info("返回所有可编辑成绩的id");
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl(), api + "page=1&", api, getCookie());
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			Double reportNum = jObject.get("data").getAsJsonObject().get("total").getAsDouble();
			int pagesNum = (int) Math.ceil(reportNum / 10);
			for (int i = 0; i < pagesNum; i++) {
				String apiPages = api + "page=" + (i + 1) + "&";
				CloseableHttpResponse pagesResponse = Client.getGetResponse(getUrl(), apiPages, api, getCookie());
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
		logger.info("attackerReportIdList: " + attackerReportList);
		return attackerReportList;
	}

	public CloseableHttpResponse getAttackerView(String[] args) {
		String apiString = "attackerView";
		String cookieWithoutCrumb = args[0];
		String attackerReportId = args[1];
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString);
		logger.info("打开一次该成绩树");
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl(), api + attackerReportId + "?cache=0&",
				api, getCookie());
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
		logger.info("检查一次创建成绩树任务的执行结果。");
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl(), api + taskId + "?", api, getCookie());
		if (response != null) {
			JsonObject jsonObject = Client.getResponseDataJson(response);
			logger.info(jsonObject);
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
		logger.info("检查一次保存成绩节点任务的执行结果。");
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl(), api + taskId + "?", api, getCookie());
		if (response != null) {
			JsonObject jsonObject = Client.getResponseDataJson(response);
			logger.info(jsonObject);
			result = jsonObject.get("data").getAsJsonObject().get("res").getAsString();
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
		logger.info("检查一次提交成绩任务的执行结果。");
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl(), api + taskId + "?", api, getCookie());
		if (response != null) {
			JsonObject jsonObject = Client.getResponseDataJson(response);
			logger.info(jsonObject);
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
		String api = apiMap.get(apiString);
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl(),
				api + attackerReportId + "?" + "last_update_ts=" + lastUpdateTs + "&", api, getCookie());
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
		baseApi.Client.getGetResponse(getUrl(), api, apiMap.get(apiString), getCookie());
	}

	public CloseableHttpResponse openNode(String[] args) {
		String apiString = "getOpenNode";
		String cookieWithoutCrumb = args[0];
		String attackerReportid = args[1];
		String nodeUuid = args[2];
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString) + attackerReportid + "/" + nodeUuid + "?id=" + attackerReportid + "&uuid="
				+ nodeUuid + "&";
		logger.info("打开此成绩节点");
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl(), api, apiMap.get(apiString),
				getCookie());
		return response;
	}

	public CloseableHttpResponse getReportNodePostil(String[] args) {
		String apiString = "getReportNodePostil";
		String cookieWithoutCrumb = args[0];
		String nodeUuid = args[1];
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString) + "?uuid=" + nodeUuid + "&";
		logger.info("检查此成绩节点的uuid");
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl(), api, apiMap.get(apiString),
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

class ReportNode {

	private Logger logger = Logger.getLogger(ReportNode.class);
	// 定义若干列表的元素个数
	private int threatsNum = 10;// 25;
	private int tagsNum = 6;// 18;
	private int informationsNum = 13;// 43;
	private int exist_questionNum = 3;// 6;
	private int exposuresNum = 3;// 9;

	private String id = "";
	private String uuid = "";
	private String parent_id = "";
	private int node_sort = 3;
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

	// 添加两个constructor
	// 初始化变量

	public JsonObject getNode() {
		// 初始化若干变量
		Random random = new Random();
		ip = PublicRandomUtil.getRandomIp();
		url = "http://" + ip + "/abc.com/" + random.nextInt(100);
		name = "网络节点" + ip;
		String randomString = PublicRandomUtil.getRandomCharacter(5);
		String imageString = "";
		for (int i = 0; i < images.size(); i++) {
			imageString += "<img src=" + images.get(i) + ">";
		}
		process_description = "<div><div>" + randomString + "</div></div><div>" + imageString + "<br></div><div></div>";
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
		nodeObject.add("report_tag", report_tag);
		nodeObject.add("threats", threats);
		nodeObject.add("attacks", attacks);
		nodeObject.add("information", information);
		nodeObject.add("questions", questions);
		nodeObject.add("exposures", exposures);
		nodeObject.add("images", images);
		logger.info("返回一条编辑好的节点内容");
		logger.info(nodeObject.toString());
		return nodeObject;
	}

	public ReportNode() {
		super();
	}

	public ReportNode(String id, String uuid, String parent_id) {
		super();
		this.id = id;
		this.uuid = uuid;
		this.parent_id = parent_id;
	}

	public ReportNode(String uuid, String parent_id, int node_sort, int asset_type, String name, String url, String ip,
			String process_description, String asset_category_id, JsonArray threats, JsonArray attacks,
			JsonArray information, JsonArray questions, JsonArray exposures, JsonArray images) {
		super();
		this.uuid = uuid;
		this.parent_id = parent_id;
		this.node_sort = node_sort;
		this.asset_type = asset_type;
		this.name = name;
		this.url = url;
		this.ip = ip;
		this.process_description = process_description;
		this.asset_category_id = asset_category_id;
		this.threats = threats;
		this.attacks = attacks;
		this.information = information;
		this.questions = questions;
		this.exposures = exposures;
		this.images = images;
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
				+ "\"14\":[15,97,98,99],\"16\":[100,101],\"17\":[17],\"18\":[19,20],"
				+ "\"21\":[76,77,103,104,105,106,107,108,109,110,111,112,113,114,115,116],"
				+ "\"117\":[117],\"24\":[25,118],\"29\":[30,31,119,120,121,122,123,124],"
				+ "\"4\":[5,6,125,126,127,128],\"10\":[11,12],\"33\":[33],\"34\":[34],"
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
				childrenObject.addProperty("description", "http://www.11.com,http://www.2.com");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("value", 2);
				break;
			case 74:
				childrenObject.addProperty("description", "http://www.12.com/1,http://www.2.com/2");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("value", 2);
				break;
			case 78:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("description", "1.1.1.2,1.1.1.3");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("value", 2);
				break;
			case 79:
				childrenObject.addProperty("description", "1.1.1.4,1.1.1.5");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("value", 2);
				break;
			case 80:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("value", 1);
				break;
			case 81:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("value", 2);
				break;
			case 83:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "OA系统");
				childrenObject.addProperty("value", String.valueOf(3 + random.nextInt(5)));
				break;
			case 86:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "即时通讯系统");
				childrenObject.addProperty("value", String.valueOf(3 + random.nextInt(5)));
				break;
			case 87:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "项目管理系统");
				childrenObject.addProperty("value", String.valueOf(3 + random.nextInt(5)));
				break;
			case 88:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "财务系统");
				childrenObject.addProperty("value", String.valueOf(3 + random.nextInt(5)));
				break;
			case 89:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "其他");
				childrenObject.addProperty("value", String.valueOf(3 + random.nextInt(5)));
				break;
			case 8:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("description", "http://www.sso1.com/+2.2.2.2");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "SSO");
				childrenObject.addProperty("value", 1);
				break;
			case 9:
				childrenObject.addProperty("description", "http://www.sso2.com/+2.2.2.3");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "SSO");
				childrenObject.addProperty("value", 1);
				break;
			case 94:
				childrenObject.addProperty("description", "http://www.4a1.com/+2.2.2.4");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "4A");
				childrenObject.addProperty("value", 1);
				break;
			case 95:
				childrenObject.addProperty("description", "http://www.4a2.com/+2.2.2.5");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "4A");
				childrenObject.addProperty("value", 1);
				break;
			case 84:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("description", "4.4.4.5");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("value", 1);
				break;
			case 85:
				childrenObject.addProperty("description", "4.4.4.6");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("value", 1);
				break;
			case 15:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("description", "5.5.5.5");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "堡垒机");
				childrenObject.addProperty("value", 1);
				break;
			case 97:
				childrenObject.addProperty("description", "5.5.5.6");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "堡垒机");
				childrenObject.addProperty("value", 1);
				break;
			case 98:
				childrenObject.addProperty("description", "5.5.5.7");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "运维机");
				childrenObject.addProperty("value", 1);
				break;
			case 99:
				childrenObject.addProperty("description", "5.5.5.8");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "运维机");
				childrenObject.addProperty("value", 1);
				break;
			case 100:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("description", "http://www.6.com+6.6.6.6");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("value", 1);
				break;
			case 101:
				childrenObject.addProperty("description", "http://www.7.com+6.6.6.7");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("value", 1);
				break;
			case 17:
				childrenObject.addProperty("description", "8txt");
				childrenObject.addProperty("value", 0);
				break;
			case 19:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("value", 3 + random.nextInt(5));
				break;
			case 20:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("value", 3 + random.nextInt(5));
				break;
			case 76:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("description", "7.7.7.7");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "防火墙");
				childrenObject.addProperty("value", 1);
				break;
			case 77:
				childrenObject.addProperty("description", "7.7.7.8");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "防火墙");
				childrenObject.addProperty("value", 1);
				break;
			case 103:
				childrenObject.addProperty("description", "7.7.7.9");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "路由器");
				childrenObject.addProperty("value", 1);
				break;
			case 104:
				childrenObject.addProperty("description", "7.7.7.10");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "路由器");
				childrenObject.addProperty("value", 1);
				break;
			case 105:
				childrenObject.addProperty("description", "7.7.7.11");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "交换机");
				childrenObject.addProperty("value", 1);
				break;
			case 106:
				childrenObject.addProperty("description", "7.7.7.12");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "交换机");
				childrenObject.addProperty("value", 1);
				break;
			case 107:
				childrenObject.addProperty("description", "7.7.7.13");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "网闸");
				childrenObject.addProperty("value", 1);
				break;
			case 108:
				childrenObject.addProperty("description", "7.7.7.14");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "网闸");
				childrenObject.addProperty("value", 1);
				break;
			case 109:
				childrenObject.addProperty("description", "7.7.7.15");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "光闸");
				childrenObject.addProperty("value", 1);
				break;
			case 110:
				childrenObject.addProperty("description", "7.7.7.16");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "光闸");
				childrenObject.addProperty("value", 1);
				break;
			case 111:
				childrenObject.addProperty("description", "7.7.7.17");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "摆渡机");
				childrenObject.addProperty("value", 1);
				break;
			case 112:
				childrenObject.addProperty("description", "7.7.7.18");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "摆渡机");
				childrenObject.addProperty("value", 1);
				break;
			case 113:
				childrenObject.addProperty("description", "7.7.7.19");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "VPN");
				childrenObject.addProperty("value", 1);
				break;
			case 114:
				childrenObject.addProperty("description", "7.7.7.20");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "VPN");
				childrenObject.addProperty("value", 1);
				break;
			case 115:
				childrenObject.addProperty("description", "7.7.7.21");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "其他");
				childrenObject.addProperty("value", 1);
				break;
			case 116:
				childrenObject.addProperty("description", "7.7.7.22");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "其他");
				childrenObject.addProperty("value", 1);
				break;
			case 117:
				childrenObject.addProperty("description", "11txt");
				childrenObject.addProperty("value", 0);
				break;
			case 25:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("value", 3 + random.nextInt(5));
				break;
			case 118:
				childrenObject.addProperty("description", "");
				childrenObject.addProperty("value", 3 + random.nextInt(5));
				break;
			case 30:
				// childrenObject.addProperty("need_detail","1"); //2021.3.30检查最新接口没有此条数据
				childrenObject.addProperty("description", "8.8.8.1");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "IDS");
				childrenObject.addProperty("value", 1);
				break;
			case 31:
				childrenObject.addProperty("description", "8.8.8.2");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "IDS");
				childrenObject.addProperty("value", 1);
				break;
			case 119:
				childrenObject.addProperty("description", "8.8.8.3");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "审计设备");
				childrenObject.addProperty("value", 1);
				break;
			case 120:
				childrenObject.addProperty("description", "8.8.8.4");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "审计设备");
				childrenObject.addProperty("value", 1);
				break;
			case 121:
				childrenObject.addProperty("description", "8.8.8.5");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "WAF");
				childrenObject.addProperty("value", 1);
				break;
			case 122:
				childrenObject.addProperty("description", "8.8.8.6");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "WAF");
				childrenObject.addProperty("value", 1);
				break;
			case 123:
				childrenObject.addProperty("description", "8.8.8.7");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "其他");
				childrenObject.addProperty("value", 1);
				break;
			case 124:
				childrenObject.addProperty("description", "8.8.8.8");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "其他");
				childrenObject.addProperty("value", 1);
				break;
			case 5:
				// childrenObject.addProperty("need_detail","1"); //2021.3.30检查最新接口没有此条数据
				childrenObject.addProperty("description", "http://www.91.com+9.9.9.1");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "Web应用");
				childrenObject.addProperty("value", 1);
				break;
			case 6:
				childrenObject.addProperty("description", "http://www.92.com+9.9.9.2");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "Web应用");
				childrenObject.addProperty("value", 1);
				break;
			case 125:
				childrenObject.addProperty("description", "http://www.93.com+9.9.9.3");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "FTP");
				childrenObject.addProperty("value", 1);
				break;
			case 126:
				childrenObject.addProperty("description", "http://www.94.com+9.9.9.4");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "FTP");
				childrenObject.addProperty("value", 1);
				break;
			case 127:
				childrenObject.addProperty("description", "http://www.95.com+9.9.9.5");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "其他");
				childrenObject.addProperty("value", 1);
				break;
			case 128:
				childrenObject.addProperty("description", "http://www.96.com+9.9.9.6");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "其他");
				childrenObject.addProperty("value", 1);
				break;
			case 11:
				childrenObject.addProperty("need_detail", "1");
				childrenObject.addProperty("description", "10.10.10.1");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("value", 1);
				break;
			case 12:
				childrenObject.addProperty("description", "10.10.10.2");
				childrenObject.addProperty("name", "");
				childrenObject.addProperty("pname", "");
				childrenObject.addProperty("value", 1);
				break;
			case 33:
				childrenObject.addProperty("description", "14txt");
				childrenObject.addProperty("value", 0);
				break;
			case 34:
				childrenObject.addProperty("description", "15txt");
				childrenObject.addProperty("value", 0);
				break;
			case 75:
				childrenObject.addProperty("description", "16txt");
				childrenObject.addProperty("value", 0);
				break;
			case 36:
				childrenObject.addProperty("description", "17txt");
				childrenObject.addProperty("value", 0);
				break;
			case 37:
				childrenObject.addProperty("description", "18txt");
				childrenObject.addProperty("value", 0);
				break;
			case 46:
				childrenObject.addProperty("description", "19txt");
				childrenObject.addProperty("value", 0);
				break;
			case 47:
				childrenObject.addProperty("description", "20txt");
				childrenObject.addProperty("value", 0);
				break;
			case 49:
				childrenObject.addProperty("description", "21txt");
				childrenObject.addProperty("value", 0);
				break;
			case 50:
				childrenObject.addProperty("description", "22txt");
				childrenObject.addProperty("value", 0);
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
		String[] exist_questionId = { "118", "145", "125", "157", "134", "139" }; // 6 exist_question
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
		imagesArray.add("/report/00fb31ea4c2034aa43d5845f9c0f4929.jpg");
		imagesArray.add("/report/010e0722ff69ba5cf3bdd6d46eb5dca9.png");
		imagesArray.add("/report/12dfdec99a27bedf1fbc7bc05fd05189.jpg");
		imagesArray.add("/report/2b319641bd0f304208266e5ecf8cd4dc.jpg");
		imagesArray.add("/report/35ff5ef56b8e0311cd595300b8f834a2.png");
		imagesArray.add("/report/4bd77c8e5ca3c418b0345c5005950b27.png");
		imagesArray.add("/report/4d975e1fe65437fd07900b3c02a732e2.jpg");
		imagesArray.add("/report/62eed5f21785f27b69660ba3ffea9e5f.jpg");
		imagesArray.add("/report/643306005353a33335ebd78132d90a25.jpg");
		imagesArray.add("/report/66ed0b1888e1c7ba267c4f9462615105.jpg");
		imagesArray.add("/report/7c8f3219456a4b5b3199907d37d8eea3.png");
		imagesArray.add("/report/8a5f9535b8cedfc8b9cafc3f89252c95.jpg");
		imagesArray.add("/report/a529b4f2290955716cb3c99bbb876118.jpg");
		imagesArray.add("/report/a64067b682e5aabbdcd7e2b4b0864ba2.png");
		imagesArray.add("/report/ae17ed3891d782c420e54f86415de62b.jpg");
		imagesArray.add("/report/ed96fbd51e85257d13b3946549a4a77b.png");
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
