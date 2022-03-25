package huwang2020_base;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import baseApi.Client;

/**
 * Huwang api base
 */
public class HwAdminBase extends InitProperty {

	private static Logger logger = Logger.getLogger(HwAdminBase.class);

	public CloseableHttpResponse adminUiBase(String[] argString) {
		String cookieWithoutCrumb = argString[0];
		String api = apiMap.get(argString[1]);
		getMyCrumb(cookieWithoutCrumb);
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api, api, api, getCookie());
		return response;

	}

	public CloseableHttpResponse departmentBase(String[] args) throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		String apiString = apiMap.get("department");
		String contentType = "formData";
		String name = "";
		String cookieWithoutCrumb = args[0];
		getMyCrumb(cookieWithoutCrumb);
		JsonObject jsonObject = new JsonObject();
		if (args.length > 2) {
			name = args[1];
			String filePath = args[2];
			String fileName = filePath.split(":\\\\")[filePath.split(":\\\\").length - 1];
			jsonObject.addProperty("name", name);
			jsonObject.addProperty("background_image", "(binary)");
			jsonObject.addProperty("__crumb__", get__crumb__());
			String uploadStrings[] = { contentType, jsonObject.toString(), "background_image", filePath, "image/jpeg",
					fileName };
			response = Client.getPostResponse(getUrl() + apiString, apiString, apiString, getCookie(), uploadStrings);
		} else {
			name = args[1];
			jsonObject.addProperty("name", name);
			jsonObject.addProperty("__crumb__", get__crumb__());
			String uploadStrings[] = { contentType, jsonObject.toString() };
			response = Client.getPostResponse(getUrl() + apiString, apiString, apiString, getCookie(), uploadStrings);
		}
		return response;
	}

	public CloseableHttpResponse teamOrUserImport(String[] args) throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		String api = args[2];
		String apiString = apiMap.get(api);
		String contentType = "formData";
		String cookieWithoutCrumb = args[0];
		String nameString = "";
		if ("userImport".equals(api)) {
			nameString = "users";
		} else {
			nameString = "organizations[1]";
		}
		getMyCrumb(cookieWithoutCrumb);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(nameString, "(binary)");
		jsonObject.addProperty("__crumb__", get__crumb__());
		String filePath = args[1];
		String fileName = filePath.split(":\\\\")[filePath.split(":\\\\").length - 1];
		String jsonString = jsonObject.toString();
		String uploadStrings[] = { contentType, jsonString, nameString, filePath,
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", fileName };
		response = Client.getPostResponse(getUrl() + apiString, apiString, apiString, getCookie(), uploadStrings);

		return response;
	}

	public CloseableHttpResponse orgnizationTeam(String[] args) throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		String apiString = "";
		String contentType = "formData";
		String cookieWithoutCrumb = args[0];
		getMyCrumb(cookieWithoutCrumb);
		JsonObject jsonObject = new JsonObject();
		if (args.length > 2) {
			String jsonStr = args[1];
			@SuppressWarnings("unchecked")
			HashMap<String, String> formDataMap = new Gson().fromJson(jsonStr, HashMap.class);
			for (String key : formDataMap.keySet()) {
				if (key.equals("队伍名称")) {
					jsonObject.addProperty("title", formDataMap.get(key));
				} else if (key.equals("队伍简称")) {
					jsonObject.addProperty("title_abbreviation", formDataMap.get(key));
				} else if (key.equals("所属公司")) {
					jsonObject.addProperty("unit", formDataMap.get(key));
					apiString = apiMap.get("orgnizationAttacker");
				} else if (key.equals("队伍性质")) {
					jsonObject.addProperty("property_id", formDataMap.get(key));
				} else if (key.equals("所在地")) {
					jsonObject.addProperty("location_id", formDataMap.get(key));
				} else if (key.equals("详细地址")) {
					jsonObject.addProperty("address", formDataMap.get(key));
				} else if (key.equals("经度")) {
					jsonObject.addProperty("longitude", formDataMap.get(key));
				} else if (key.equals("纬度")) {
					jsonObject.addProperty("latitude", formDataMap.get(key));
				} else if (key.equals("分组")) {
					jsonObject.addProperty("team_group[0]", formDataMap.get(key));
				} else if (key.equals("队伍LOGO")) {
					jsonObject.addProperty("logo", formDataMap.get(key));
				} else if (key.equals("行业")) {
					jsonObject.addProperty("department_id", formDataMap.get(key));
					apiString = apiMap.get("orgnizationVictim");
				}
				jsonObject.addProperty("__crumb__", get__crumb__());
				// logger.info(key+": "+formDataMap.get(key));
			}
			String filePath = args[2];
			String fileName = filePath.split(":\\\\")[filePath.split(":\\\\").length - 1];
			String jsonString = jsonObject.toString();
			String uploadStrings[] = { contentType, jsonString, "logo", filePath, "image/jpeg", fileName };
			response = Client.getPostResponse(getUrl() + apiString, apiString, apiString, getCookie(), uploadStrings);

		} else {
			String jsonStr = args[1];
			@SuppressWarnings("unchecked")
			HashMap<String, String> formDataMap = new Gson().fromJson(jsonStr, HashMap.class);
			for (String key : formDataMap.keySet()) {
				if (key.equals("队伍名称")) {
					jsonObject.addProperty("title", formDataMap.get(key));
				} else if (key.equals("队伍简称")) {
					jsonObject.addProperty("title_abbreviation", formDataMap.get(key));
				} else if (key.equals("所属公司")) {
					jsonObject.addProperty("unit", formDataMap.get(key));
					apiString = apiMap.get("orgnizationAttacker");
				} else if (key.equals("队伍性质")) {
					jsonObject.addProperty("property_id", formDataMap.get(key));
				} else if (key.equals("所在地")) {
					jsonObject.addProperty("location_id", formDataMap.get(key));
				} else if (key.equals("详细地址")) {
					jsonObject.addProperty("address", formDataMap.get(key));
				} else if (key.equals("经度")) {
					jsonObject.addProperty("longitude", formDataMap.get(key));
				} else if (key.equals("纬度")) {
					jsonObject.addProperty("latitude", formDataMap.get(key));
				} else if (key.equals("分组")) {
					jsonObject.addProperty("team_group[0]", formDataMap.get(key));
				} else if (key.equals("队伍LOGO")) {
					jsonObject.addProperty("logo", formDataMap.get(key));
				} else if (key.equals("行业")) {
					jsonObject.addProperty("department_id", formDataMap.get(key));
					apiString = apiMap.get("orgnizationVictim");
				}
				jsonObject.addProperty("__crumb__", get__crumb__());
				// logger.info(key+": "+formDataMap.get(key));
			}
			String jsonString = jsonObject.toString();
			String uploadStrings[] = { contentType, jsonString };
			response = Client.getPostResponse(getUrl() + apiString, apiString, apiString, getCookie(), uploadStrings);

		}
		return response;
	}

	public CloseableHttpResponse orgnizationGroup(String[] args) throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		String contentType = "x-www-form-urlencoded";
		String cookieWithoutCrumb = args[0];
		String apiString = "";
		if (args[1].equals("attackerGroup")) {
			apiString = apiMap.get("attackerGroup");
		} else if (args[1].equals("victimGroup")) {
			apiString = apiMap.get("victimGroup");
		} else if (args[1].equals("judgeGroup")) {
			apiString = apiMap.get("judgeGroup");
		}

		getMyCrumb(cookieWithoutCrumb);
		JsonObject jsonObject = new JsonObject();
		String jsonStr = args[2];
		@SuppressWarnings("unchecked")
		HashMap<String, String> formDataMap = new Gson().fromJson(jsonStr, HashMap.class);
		for (String key : formDataMap.keySet()) {
			if (key.equals("名称")) {
				jsonObject.addProperty("name", formDataMap.get(key));
			} else if (key.equals("描述")) {
				jsonObject.addProperty("description", formDataMap.get(key));
			}
		}
		String jsonString = jsonObject.toString();
		String uploadStrings[] = new String[2];
		uploadStrings[0] = contentType;
		if (args.length > 3) {
			String jsonSubString = jsonString.substring(0, jsonString.length() - 1);
			if (!"".equals(args[3])) {
				if (args[1].equals("judgeGroup")) {
					for (String ids : args[3].split(",")) {
						jsonSubString += ",\"judge[]\"" + ":\"" + ids + "\"";
					}
				}
				for (String ids : args[3].split(",")) {
					jsonSubString += ",\"members[]\"" + ":\"" + ids + "\"";
				}
			}
			jsonSubString += ",\"__crumb__\"" + ":\"" + get__crumb__() + "\"}";
			logger.info(jsonSubString);
			uploadStrings[1] = jsonSubString;
		} else {
			uploadStrings[1] = jsonString;
		}

		response = Client.getPostResponse(getUrl() + apiString, apiString, apiString, getCookie(), uploadStrings);
		return response;
	}

	public CloseableHttpResponse createUser(String[] args) throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		String contentType = "x-www-form-urlencoded";
		String cookieWithoutCrumb = args[0];
		String apiString = apiMap.get("createUser");

		getMyCrumb(cookieWithoutCrumb);
		JsonObject jsonObject = new JsonObject();
		String jsonStr = args[1];
		@SuppressWarnings("unchecked")
		HashMap<String, String> formDataMap = new Gson().fromJson(jsonStr, HashMap.class);
		for (String key : formDataMap.keySet()) {
			if (key.equals("用户名")) {
				jsonObject.addProperty("username", formDataMap.get(key));
			} else if (key.equals("手机号")) {
				jsonObject.addProperty("mobile", formDataMap.get(key));
			} else if (key.equals("姓名")) {
				jsonObject.addProperty("name", formDataMap.get(key));
			} else if (key.equals("密码")) {
				jsonObject.addProperty("password", formDataMap.get(key));
			} else if (key.equals("确认密码")) {
				jsonObject.addProperty("checkPass", formDataMap.get(key));
			} else if (key.equals("角色")) {
				if (formDataMap.get(key).equals("攻击方")) {
					jsonObject.addProperty("role", "attacker");
				} else if (formDataMap.get(key).equals("防守方")) {
					jsonObject.addProperty("role", "victim");
				} else if (formDataMap.get(key).equals("专家")) {
					jsonObject.addProperty("role", "expert");
				} else if (formDataMap.get(key).equals("裁判")) {
					jsonObject.addProperty("role", "judge");
				} else if (formDataMap.get(key).equals("主办方")) {
					jsonObject.addProperty("role", "audience");
				} else if (formDataMap.get(key).equals("武器验证员")) {
					jsonObject.addProperty("role", "verifier");
				} else if (formDataMap.get(key).equals("非正常防守验证员")) {
					jsonObject.addProperty("role", "watchdog");
				}
			} else if (key.equals("身份证号")) {
				jsonObject.addProperty("ID_num", formDataMap.get(key));
			} else if (key.equals("攻击队伍")) {
				jsonObject.addProperty("org_id", formDataMap.get(key));
			} else if (key.equals("队伍数据共享")) {
				if (formDataMap.get(key).equals("是")) {
					jsonObject.addProperty("is_org_report_available", "1");
				} else if (formDataMap.get(key).equals("否")) {
					jsonObject.addProperty("is_org_report_available", "0");
				}
			} else if (key.equals("负责人")) {
				if (formDataMap.get(key).equals("是")) {
					jsonObject.addProperty("principal", "1");
				} else if (formDataMap.get(key).equals("否")) {
					jsonObject.addProperty("principal", "0");
				}
			} else if (key.equals("接入方式")) {
				jsonObject.addProperty("access[]", formDataMap.get(key));
			} else if (key.equals("防守队伍")) {
				jsonObject.addProperty("org_id", formDataMap.get(key));
			} else if (key.equals("裁判组")) {
				jsonObject.addProperty("judgeGroups[]", formDataMap.get(key));
			}
		}
		jsonObject.addProperty("__crumb__", get__crumb__());
		String jsonString = jsonObject.toString();
		String uploadStrings[] = { contentType, jsonString };
		try {
			response = Client.getPostResponse(getUrl() + apiString, apiString, apiString, getCookie(), uploadStrings);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	public CloseableHttpResponse deleteGroupOrUserOrOgnization(String[] args)
			throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		String contentType = "x-www-form-urlencoded";
		String cookieWithoutCrumb = args[0];
		String apiString = "";
		if (args[1].equals("orgnization")) {
			apiString = apiMap.get("orgnization");
		} else if (args[1].equals("attackerGroup")) {
			apiString = apiMap.get("attackerGroup");
		} else if (args[1].equals("victimGroup")) {
			apiString = apiMap.get("victimGroup");
		} else if (args[1].equals("judgeGroup")) {
			apiString = apiMap.get("judgeGroup");
		} else if (args[1].equals("deleteUsers")) {
			apiString = apiMap.get("deleteUsers");
		}

		getMyCrumb(cookieWithoutCrumb);
		StringBuilder idString = new StringBuilder();
		String jsonStr = args[2];
		int idLength = jsonStr.split(",").length;
		for (int i = 0; i < idLength; i++) {
			idString.append("ids[]:");
			idString.append(jsonStr.split(",")[i]);
			idString.append(",");
		}
		idString.append("__crumb__:" + get__crumb__());
		// logger.info(idString.toString());
		String uploadStrings[] = { contentType, idString.toString() };
		response = Client.getDeleteResponse(getUrl() + apiString, apiString, apiString, getCookie(), uploadStrings);
		return response;
	}

	public String groupNameToid(String[] args) throws ParseException, IOException {
		String cookieWithoutCrumb = args[0];
		String api = args[1]; // 指定要执行的API
		String nameString = args[2]; // 指定要查询的组名
		String apiString = apiMap.get(api) + "?page=1&page_size=100&";
		getMyCrumb(cookieWithoutCrumb); // 更新COOKIE
		JsonObject nameIds = new JsonObject();
		// 查询所有组信息
		CloseableHttpResponse response = Client.getGetResponse(getUrl() + apiString, apiString, apiString, getCookie());
		// 获取所有组名与ID对应关系的JsonObject
		JsonArray groupList = null;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			groupList = jObject.get("data").getAsJsonObject().get("list").getAsJsonArray();
			if (groupList.size() > 0) {
				for (int i = 0; i < groupList.size(); i++) {
					String strName = groupList.get(i).getAsJsonObject().get("name").toString().replace("\"", "");
					String id = groupList.get(i).getAsJsonObject().get("id").toString().replace("\"", "");
					nameIds.addProperty(strName, id);
				}
			}
		}
		// 获取给定组名的所有ID
		String ids = "";
		if (nameString.length() == 0) {
			@SuppressWarnings("unchecked")
			HashMap<String, String> jsonStrMap = new Gson().fromJson(nameIds, HashMap.class);
			for (String key : jsonStrMap.keySet()) {
				String id = jsonStrMap.get(key);
				ids += "," + id;
				// logger.info(key+": "+id);
			}
		} else {
			int idNum = nameString.split(",").length;
			for (int i = 0; i < idNum; i++) {
				String name = nameString.split(",")[i];
				if (nameIds.has(name)) {
					String id = nameIds.get(name).toString().replace("\"", "");
					ids += "," + id;
				} else {
					logger.info("HwBase:No group " + name);
					continue;
				}
			}
		}
		if (ids.length() != 0) {
			ids = ids.substring(1, ids.length());
		}
		return ids; // 返回给定组名的所有ID
	}

	public String teamNameToid(String[] args) throws ParseException, IOException {
		String cookieWithoutCrumb = args[0];
		String api = args[1]; // 指定要执行的API
		String nameString = args[2]; // 指定要查询的组名
		String apiString = apiMap.get(api);
		getMyCrumb(cookieWithoutCrumb); // 更新COOKIE
		JsonObject nameIds = new JsonObject();
		// 查询所有组信息
		CloseableHttpResponse response = Client.getGetResponse(getUrl() + apiString, apiString, apiString, getCookie());

		// 获取所有组名与ID对应关系的JsonObject
		JsonArray orgnizationList = null;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			orgnizationList = jObject.get("data").getAsJsonArray();
			if (orgnizationList.size() > 0) {
				for (int i = 0; i < orgnizationList.size(); i++) {
					String strName = orgnizationList.get(i).getAsJsonObject().get("title").toString().replace("\"", "");
					String id = orgnizationList.get(i).getAsJsonObject().get("id").toString().replace("\"", "");
					nameIds.addProperty(strName, id);
				}
			}
		}
		// 获取给定组名的所有ID
		String ids = "";
		if (nameString.length() == 0) {
			@SuppressWarnings("unchecked")
			HashMap<String, String> jsonStrMap = new Gson().fromJson(nameIds, HashMap.class);
			for (String key : jsonStrMap.keySet()) {
				String id = jsonStrMap.get(key);
				ids += "," + id;
				// logger.info(key+": "+id);
			}
		} else {
			int idNum = nameString.split(",").length;
			for (int i = 0; i < idNum; i++) {
				String name = nameString.split(",")[i];
				if (nameIds.has(name)) {
					String id = nameIds.get(name).toString().replace("\"", "");
					ids += "," + id;
				} else {
					logger.info("HwBase:No team " + name);
					continue;
				}
			}
		}

		if (ids.length() != 0) {
			ids = ids.substring(1, ids.length());
		}
		return ids; // 返回给定组名的所有ID
	}

	public String userNameToid(String[] args) throws ParseException, IOException {
		String cookieWithoutCrumb = args[0];
		String api = args[1]; // 指定要执行的API
		String nameString = args[2]; // 指定要查询的组名
		String apiString = apiMap.get(api);
		getMyCrumb(cookieWithoutCrumb); // 更新COOKIE
		JsonObject nameIds = new JsonObject();
		// 查询所有组信息
		CloseableHttpResponse response = Client.getGetResponse(getUrl() + apiString, apiString, apiString, getCookie());
		// 获取所有组名与ID对应关系的JsonObject
		JsonArray usersList = null;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			usersList = jObject.get("data").getAsJsonObject().get("list").getAsJsonArray();
			if (usersList.size() > 0) {
				for (int i = 0; i < usersList.size(); i++) {
					String strName = usersList.get(i).getAsJsonObject().get("username").toString().replace("\"", "");
					String id = usersList.get(i).getAsJsonObject().get("id").toString().replace("\"", "");
					nameIds.addProperty(strName, id);
				}
			}
		}
		// 获取给定用户名的所有ID
		String ids = "";
		if (nameString.length() == 0) {
			@SuppressWarnings("unchecked")
			HashMap<String, String> jsonStrMap = new Gson().fromJson(nameIds, HashMap.class);
			for (String key : jsonStrMap.keySet()) {
				String id = jsonStrMap.get(key);
				ids += "," + id;
				// logger.info(key+": "+id);
			}
		} else {
			int idNum = nameString.split(",").length;
			for (int i = 0; i < idNum; i++) {
				String name = nameString.split(",")[i];
				if (nameIds.has(name)) {
					String id = nameIds.get(name).toString().replace("\"", "");
					ids = ids + "," + id;
				} else {
					logger.info("HwBase:No user " + name);
					continue;
				}
			}
		}

		if (ids.length() != 0) {
			ids = ids.substring(1, ids.length());
		}
		return ids; // 返回给定组名的所有ID
	}

	public CloseableHttpResponse createPolicy(String[] args) throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		String contentType = "x-www-form-urlencoded";
		String cookieWithoutCrumb = args[0];
		StringBuilder modulesString = new StringBuilder();
		String apiString = "";
		getMyCrumb(cookieWithoutCrumb);
		String uploadStrings[] = { "", "" };
		if (args[1].contains("AssignRuleCreate") || args[1].contains("setTimeConfig")) {
			// 新增攻击队伍授权策略
			// 新增审核成绩授权策略
			// 更新演习时间策略
			apiString = apiMap.get(args[1]);
			JsonObject jsonObject = new JsonObject();
			String jsonStr = args[2];
			@SuppressWarnings("unchecked")
			HashMap<String, String> formDataMap = new Gson().fromJson(jsonStr, HashMap.class);
			for (String key : formDataMap.keySet()) {
				if (key.equals("策略名称")) {
					jsonObject.addProperty("name", formDataMap.get(key));
				} else if (key.equals("描述")) {
					jsonObject.addProperty("description", formDataMap.get(key));
				} else if (key.equals("策略类型")) {
					jsonObject.addProperty("type", formDataMap.get(key));
				} else if (key.equals("策略规则")) {
					jsonObject.addProperty("excepted", formDataMap.get(key));
				} else if (key.equals("选择防守单位")) {
					jsonObject.addProperty("param", formDataMap.get(key));
				} else if (key.equals("选择裁判组")) {
					jsonObject.addProperty("judge_group_id", formDataMap.get(key));
				} else if (key.equals("分配裁判数量")) {
					jsonObject.addProperty("score_num", formDataMap.get(key));
				} else if (key.equals("评审裁判数量")) {
					jsonObject.addProperty("judge_num", formDataMap.get(key));
				} else if (key.equals("优先级")) {
					jsonObject.addProperty("priority", formDataMap.get(key));
				} else if (key.equals("策略描述")) {
					jsonObject.addProperty("description", formDataMap.get(key));
				} else if (key.equals("选择攻击组")) {
					jsonObject.addProperty("attack_group_id", formDataMap.get(key));
				} else if (key.equals("选择防守组")) {
					jsonObject.addProperty("defense_group_id", formDataMap.get(key));
				} else if (key.equals("开始时间")) {
					jsonObject.addProperty("start_date", formDataMap.get(key));
				} else if (key.equals("结束时间")) {
					jsonObject.addProperty("end_date", formDataMap.get(key));
				}
			}
			jsonObject.addProperty("__crumb__", get__crumb__());
			String jsonString = jsonObject.toString();
			uploadStrings[0] = contentType;
			uploadStrings[1] = jsonString;
		} else if (args[1].equals("accessRuleCreate")) {
			// 处理攻击授权策略
			// 处理准入策略
			apiString = apiMap.get(args[1]);
			String jsonStr = args[2];
			for (int i = 0; i < jsonStr.split(",").length; i++) {
				modulesString.append(jsonStr.split(",")[i]);
				modulesString.append(",");
			}
			modulesString.append("__crumb__:" + get__crumb__());
			uploadStrings[0] = contentType;
			uploadStrings[1] = modulesString.toString();
		} else {
			// 处理功能授权策略
			// 需要分角色攻击方和防守方
			apiString = apiMap.get(args[1].split(",")[0]);
			modulesString.append("role:" + args[1].split(",")[1] + ",");
			String jsonStr = args[2];
			int idLength = jsonStr.split(",").length;
			for (int i = 0; i < idLength; i++) {
				modulesString.append("modules[]:" + jsonStr.split(",")[i] + ",");
			}
			modulesString.append("__crumb__:" + get__crumb__());
			uploadStrings[0] = contentType;
			uploadStrings[1] = modulesString.toString();
		}
		try {
			response = Client.getPostResponse(getUrl() + apiString, apiString, apiString, getCookie(), uploadStrings);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	public CloseableHttpResponse outIp(String[] args) throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		String contentType = "x-www-form-urlencoded";
		String cookieWithoutCrumb = args[0];
		StringBuilder modulesString = new StringBuilder();
		String apiString = "";
		getMyCrumb(cookieWithoutCrumb);
		String uploadStrings[] = { "", "" };
		if (args[1].equals("outIpAdd") || args[1].equals("outIpOrgnizationConfig")) {
			// 处理攻击授权策略
			// 处理准入策略
			apiString = apiMap.get(args[1]);
			String jsonStr = args[2];
			for (int i = 0; i < jsonStr.split(",").length; i++) {
				modulesString.append(jsonStr.split(",")[i]);
				modulesString.append(",");
			}
			modulesString.append("__crumb__:" + get__crumb__());
			uploadStrings[0] = contentType;
			uploadStrings[1] = modulesString.toString();
		}
		try {
			response = Client.getPostResponse(getUrl() + apiString, apiString, apiString, getCookie(), uploadStrings);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	public CloseableHttpResponse editAdminself(String[] args) throws ParseException, IOException, SQLException {
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

	public int editUserList(String[] args) throws ParseException, IOException, SQLException {
		String role = args[0];
		String apiString = "/adminapi/users-list?page=1&page_size=10&role=" + role + "&";
		String editApiString = "/adminapi/edit-user/";
		String cookieWithoutCrumb = args[1];
		getMyCrumb(cookieWithoutCrumb);
		CloseableHttpResponse response = Client.getGetResponse(getUrl() + apiString, apiString, apiString, getCookie());
		double userListNum = -1;
		int editNum = 0;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			userListNum = jObject.get("data").getAsJsonObject().get("total_num").getAsDouble();
			int pagesNum = (int) Math.ceil(userListNum / 10);
			for (int i = 0; i < pagesNum; i++) {
				String apiPages = "/adminapi/users-list?page=" + (i + 1) + "&page_size=10&role=" + role + "&";
				CloseableHttpResponse pagesResponse = Client.getGetResponse(getUrl() + apiPages, apiPages, apiString,
						getCookie());
				JsonObject pagesObject = Client.getResponseDataJson(pagesResponse);
				JsonArray userListArray = pagesObject.get("data").getAsJsonObject().get("list").getAsJsonArray();
				int userArrayNum = userListArray.size();
				for (int j = 0; j < userArrayNum; j++) {
					editNum += 1;
					JsonObject userObject = userListArray.get(j).getAsJsonObject();
					String userIdString = userObject.get("id").getAsString();
					String editApi = editApiString + userIdString;
					String uploadStrings[] = { "application/json", userObject.toString() };
					logger.info("Editing " + editNum + "th " + role + " user...");
					CloseableHttpResponse editResponse = Client.getPostResponse(getUrl() + editApi, editApi,
							editApiString, getCookie(), uploadStrings);
					if (editResponse != null) {
						JsonObject editObject = Client.getResponseDataJson(editResponse);
						logger.info(editObject);
					}
				}
			}
		}
		return editNum;
	}

	public CloseableHttpResponse assetBase(String[] args) throws ParseException, IOException, SQLException {
		CloseableHttpResponse response = null;
		String apiString = apiMap.get("asset");
		String contentType = "formData";
		String cookieWithoutCrumb = args[0];
		String org_id = args[1];
		String category_parent = args[2];
		String asset_ip = args[3];
		String asset_url = args[4];
		String asset_port = args[5];
		String category_logo_type = args[6];
		String monitor_switch = args[7];
		String display = args[8];
		String category_name = args[9];

		getMyCrumb(cookieWithoutCrumb);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("org_id", org_id);
		jsonObject.addProperty("category_parent", category_parent);
		jsonObject.addProperty("ip", asset_ip);
		jsonObject.addProperty("url", asset_url);
		jsonObject.addProperty("port", asset_port);
		jsonObject.addProperty("category_logo_type", category_logo_type);
		jsonObject.addProperty("monitor_switch", monitor_switch);
		jsonObject.addProperty("display", display);
		jsonObject.addProperty("category_name", category_name);
		if (category_logo_type.equals("1")) {
			String filePath = args[10];
			String fileName = filePath.split(":\\\\")[filePath.split(":\\\\").length - 1];
			jsonObject.addProperty("category_logo", "(binary)");
			jsonObject.addProperty("__crumb__", get__crumb__());
			String uploadStrings[] = { contentType, jsonObject.toString(), "category_logo", filePath, "image/jpeg",
					fileName };
			logger.info(jsonObject.toString());
			response = Client.getPostResponse(getUrl() + apiString, apiString, apiString, getCookie(), uploadStrings);
		} else {
			String imgString = "";
			String getResourceApi = apiMap.get("assetCategoryResource");
			CloseableHttpResponse resouceResponse = Client.getGetResponse(getUrl() + getResourceApi, getResourceApi,
					getResourceApi, getCookie());
			if (resouceResponse != null) {
				Random random = new Random();
				JsonObject resouceObject = Client.getResponseDataJson(response);
				imgString = resouceObject.get("data").getAsJsonArray().get(random.nextInt(18)).getAsJsonObject()
						.get("url").getAsString();
			}
			jsonObject.addProperty("category_logo", imgString);
			jsonObject.addProperty("__crumb__", get__crumb__());
			String uploadStrings[] = { contentType, jsonObject.toString() };
			logger.info(jsonObject.toString());
			response = Client.getPostResponse(getUrl() + apiString, apiString, apiString, getCookie(), uploadStrings);
		}
		return response;
	}

	public String adminGetDefenseId(String[] args) throws ParseException, IOException {
		String apiString = "orgnizationListType2";
		int needNum = Integer.valueOf(args[0]);
		String cookieWithoutCrumb = args[1];
		getMyCrumb(cookieWithoutCrumb);
		String api = apiMap.get(apiString);
		CloseableHttpResponse response = baseApi.Client.getGetResponse(getUrl() + api, api, api, getCookie());
		JsonArray defenseIdArray = new JsonArray();
		StringBuilder returnIdList = new StringBuilder();
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			defenseIdArray = jObject.get("data").getAsJsonArray();
			int defenseNum = defenseIdArray.size();
			List<Integer> randomList = Client.getRandomNum(0, defenseNum, needNum);
			for (int i = 0; i < needNum; i++) {
				int randomidth = randomList.get(i);
				String id = defenseIdArray.get(randomidth).getAsJsonObject().get("id").getAsString();
				String title = defenseIdArray.get(randomidth).getAsJsonObject().get("title").getAsString();
				returnIdList.append(id + ":" + title + ",");
			}
		}
		return returnIdList.toString().substring(0, returnIdList.length() - 1);
	}
}
