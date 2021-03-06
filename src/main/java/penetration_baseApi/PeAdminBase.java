package penetration_baseApi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.testng.Reporter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class PeAdminBase {
	public static String Cookie = "";
	public static String url ="";
	public static String username = "";
	public static String password = "";
	public static String captcha = "111111";
	public static String checked = "1";
	public static String __crumb__ = "111111";
	
	public static final HashMap<String, String> apiMap = new HashMap<String, String>();
	
	public static void initConfig(String ...fileString) {
		String configFileString = "";
		String apiFileString = "";
		try {
			//System.out.println(fileString.length);
			if (fileString.length != 0) {
				configFileString = fileString[0];
				apiFileString = fileString[1];
			}else {
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
		Reporter.log("Before suit,init url,user,passord successfully.",true);
		
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
		Reporter.log("Before suit,init api.",true);
	}
	public static String adminLogin() {
		System.out.println(username);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("username", username);
		jsonObject.addProperty("password", password);
		jsonObject.addProperty("captcha", captcha);
		jsonObject.addProperty("is_agreement", checked);
		jsonObject.addProperty("__crumb__", __crumb__);
		//System.out.println(jsonObject);
		//??????????????????Cookie
		String cookieWithoutCrumb = PeLogin.LoginHw(url, jsonObject.toString());
		//System.out.println("before class: " + cookieWithoutCrumb);
		Reporter.log("Before class,login admin user.",true);
		return cookieWithoutCrumb;
	}
	public static void getMyCrumb(String cookieWithoutCrumb) {
		//??????????????????Cookie
		__crumb__ = PeCrumb.getCrumb(url, cookieWithoutCrumb);
		Cookie = cookieWithoutCrumb + "__crumb__=" + __crumb__; 
		//Reporter.log("Before each method,get crumb.",true);
	}
	
	public static void adminLogout(String cookieWithoutCrumb) {
		getMyCrumb(cookieWithoutCrumb);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("__crumb__", __crumb__);
		//????????????
		PeLogout.Logout(url, Cookie,jsonObject.toString());
		Reporter.log("After class,logout admin user.",true);
	}
	
	public static CloseableHttpResponse adminUiBase(String[] argString) {
		String cookieWithoutCrumb = argString[0];
		String api = argString[1];
		getMyCrumb(cookieWithoutCrumb);
		CloseableHttpResponse response = PeBaseClass.getGetResponse(url,api,Cookie);
		return response;
	}
	
	public static String getIdList(String[] args) throws ParseException, IOException {
		String cookieWithoutCrumb = args[0];
		String keywordString = "";
		if (args.length > 2) {
			keywordString = URLEncoder.encode(args[2], "utf-8");
		}
		String api = apiMap.get(args[1])+keywordString;
		System.out.println(api);
		String[] argString = {cookieWithoutCrumb,api+"&"+"page=1"};
		CloseableHttpResponse response = PeAdminBase.adminUiBase(argString);
		StringBuilder userList = new StringBuilder();
        if (response != null) {
        	JsonObject jObject = PeBaseClass.getResponseDataJson(response);
        	Double listNum = jObject.get("data").getAsJsonObject().get("total_num").getAsDouble();
        	int pagesNum = (int) Math.ceil(listNum/10);
        	for (int i = 0; i < pagesNum; i++) {
        		String apiPages = api+"&"+"page="+(i+1);
				CloseableHttpResponse pagesResponse = PeBaseClass.getGetResponse(url, apiPages, Cookie);
				JsonObject pagesObject = PeBaseClass.getResponseDataJson(pagesResponse);
	        	JsonArray ReportArray = pagesObject.get("data").getAsJsonObject().get("list").getAsJsonArray();
	        	int singlePageReportNum = ReportArray.size(); 
            	for (int j = 0; j < singlePageReportNum; j++) {
        			if (args[1].equals("project_list")) {
						String status = ReportArray.get(j).getAsJsonObject().get("status").getAsString();
						if (status.equals("1")) {
							String id = ReportArray.get(j).getAsJsonObject().get("id").getAsString();
							userList.append(id);
		        			userList.append(",");
						}
        			}else {
						String id = ReportArray.get(j).getAsJsonObject().get("id").getAsString();
						userList.append(id);
	        			userList.append(",");
					}
    			}
			}
	    }
        String users = userList.toString();
        return users.substring(0,users.length()-1);
	}
	
	public static CloseableHttpResponse penetrationResManage(String[] args) throws ParseException, IOException {
		String cookieWithoutCrumb = args[0];
		String keywordString = "";
		if (args.length > 2) {
			keywordString = URLEncoder.encode(args[2], "utf-8");
		}
		String api = apiMap.get(args[1])+keywordString;
		String[] argString = {cookieWithoutCrumb,api};
		CloseableHttpResponse response = PeAdminBase.adminUiBase(argString);
		return response;
	}
	
	public static void firstLogin(String[] args) throws ParseException, IOException {
		CloseableHttpResponse response = null;
		String contentType = "application/json";
		String cookieWithoutCrumb = args[0];
		String apiString = apiMap.get(args[1]);
		String logoutApiString = "get_logout";
		getMyCrumb(cookieWithoutCrumb);    //??????cookie
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("confirm_password", "14e0c17989bbd0930b9a1629db4fff0e");
		jsonObject.addProperty("new_password", "14e0c17989bbd0930b9a1629db4fff0e");
		jsonObject.addProperty("password", "e10adc3949ba59abbe56e057f20f883e");
		jsonObject.addProperty("project_name", "????????????????????????");
		jsonObject.addProperty("username", "admin");
		String jsonString = jsonObject.toString();
		String uploadStrings[] = {contentType,jsonString};
		response = PeBaseClass.getPostResponse(url,apiString,Cookie,uploadStrings);
		System.out.println(response);
		String[] logoutArgString = {cookieWithoutCrumb,logoutApiString};
		CloseableHttpResponse logoutResponse = PeAdminBase.adminUiBase(logoutArgString);
		System.out.println(logoutResponse);

	}
	
	public static CloseableHttpResponse Authcontrol(String[] args) throws ParseException, IOException {
		CloseableHttpResponse response = null;
		String contentType = "application/json";
		String cookieWithoutCrumb = args[0];
		String apiString = apiMap.get(args[1]);
		getMyCrumb(cookieWithoutCrumb);    //??????cookie
		JsonObject jsonObject = new JsonObject();
		String jsonStr = args[2];
		@SuppressWarnings("unchecked")
		HashMap<String,String> formDataMap = new Gson().fromJson(jsonStr, HashMap.class);
		for (String key:formDataMap.keySet()){
			switch (args[1]) {
			case "update-attacker":
			case "create-attacker":
				if (key.equals("?????????")) {              //??????????????????????????????????????????
					jsonObject.addProperty("username", formDataMap.get(key));
				}else if (key.equals("?????????")) {
					jsonObject.addProperty("phone", formDataMap.get(key));
				}else if (key.equals("??????")) {
					jsonObject.addProperty("nickname", formDataMap.get(key));
				}else if (key.equals("??????")) {
					jsonObject.addProperty("password", formDataMap.get(key));
				}else if (key.equals("????????????")) {
					jsonObject.addProperty("confirm_pass", formDataMap.get(key));
				}else if (key.equals("??????")) {
					jsonObject.addProperty("team_id", formDataMap.get(key));
				}
				jsonObject.addProperty("role_name", "pene_attack");
				break;
			case "team":
				if (key.equals("????????????")) { //????????????????????????????????????
					jsonObject.addProperty("team_name", formDataMap.get(key));
				}else if (key.equals("????????????")) {
					jsonObject.addProperty("team_title", formDataMap.get(key));
				}else if (key.equals("????????????")) {
					jsonObject.addProperty("team_company", formDataMap.get(key));
				}
				break;
			case "organization":
				if (key.equals("??????????????????")) {     //??????????????????????????????????????????
					jsonObject.addProperty("name", formDataMap.get(key));
				}else if (key.equals("??????")) {
					jsonObject.addProperty("abbreviate", formDataMap.get(key));
				}else if (key.equals("?????????")) {
					jsonObject.addProperty("manager_name", formDataMap.get(key));
				}else if (key.equals("??????????????????")) {
					jsonObject.addProperty("manager_phone", formDataMap.get(key));
				}
				break;
			case "asset":
				if (key.equals("????????????")) {         //??????????????????????????????????????????
					jsonObject.addProperty("name", formDataMap.get(key));
				}else if (key.equals("????????????")) {
					jsonObject.addProperty("category", formDataMap.get(key));
				}else if (key.equals("IP")) {
					jsonObject.addProperty("ip", formDataMap.get(key));
				}else if (key.equals("URL")) {
					jsonObject.addProperty("url", formDataMap.get(key));
				}else if (key.equals("????????????")) {
					jsonObject.addProperty("organization_id", formDataMap.get(key));
				}
				break;
			case "virtual-ip_create":
				if (key.equals("????????????IP??????")) {    //??????IP??????
					jsonObject.addProperty("ips", formDataMap.get(key));
				}
				break;
			case "vm_create":
				if (key.equals("??????????????????")) {      //?????????????????????????????????????????????
					jsonObject.addProperty("vm_name", formDataMap.get(key));
				}else if (key.equals("????????????")) {
					jsonObject.addProperty("user_id", formDataMap.get(key));
				}
				break;
			case "virtual-settings_update":
				if (key.equals("CPU??????")) {        //???????????????????????????????????????
					jsonObject.addProperty("vcpus", formDataMap.get(key));
				}else if (key.equals("????????????")) {      
					jsonObject.addProperty("ram", formDataMap.get(key));
				}else if (key.equals("????????????")) {
					jsonObject.addProperty("disk", formDataMap.get(key));
				}
				break;
			case "project":
				if (key.equals("????????????")) {         //????????????
					jsonObject.addProperty("name", formDataMap.get(key));
				}
				break;
			case "project-time":
				if (key.equals("??????????????????")) {       //??????????????????
					jsonObject.addProperty("start_time", formDataMap.get(key));
				}else if (key.equals("??????????????????")) {
					jsonObject.addProperty("end_time", formDataMap.get(key));
				}else if (key.equals("??????ID")) {
					jsonObject.addProperty("project_id", formDataMap.get(key));
				}
				break;
			default:
				break;
			}
		}
		String jsonString = jsonObject.toString();
		System.out.println(jsonString);
		String uploadStrings[] = {contentType,jsonString};
		if (args.length > 3) {   //????????????????????????????????????????????????PUT
			if (args[1].equals("team") || args[1].equals("organization")) {  //??????????????????????????????
				apiString = apiString + "/" + args[3];
			}else if (args[1].equals("update-attacker")) {   //???????????????
				apiString = apiString + "?id=" + args[3];
			}else if (args[1].equals("project-time")) {   //???????????????
				apiString = apiString + args[3];
			}
			response = PeBaseClass.getPutResponse(url,apiString,Cookie,uploadStrings);
		}else {
			if (args[1].equals("virtual-settings_update")) {
				response = PeBaseClass.getPutResponse(url,apiString,Cookie,uploadStrings);
			}else {   //??????????????????POST
				response = PeBaseClass.getPostResponse(url,apiString,Cookie,uploadStrings);
			}
		}
		return response;
	}

	public static CloseableHttpResponse createPolicy(String[] args) throws ParseException, IOException {
		CloseableHttpResponse response = null;
		String contentType = "application/json";
		String cookieWithoutCrumb = args[0];
		String apiString = apiMap.get(args[1])+args[2];
		String teamArgs[] = {cookieWithoutCrumb,"team_list"};
		String userArgs[] = {cookieWithoutCrumb,"index-attacker"};
		getMyCrumb(cookieWithoutCrumb);    //??????cookie
		JsonObject jsonObject = new JsonObject();
		JsonArray teamsArray = new JsonArray();
		JsonArray userArray = new JsonArray();
		List<String> teamList = java.util.Arrays.asList(getIdList(teamArgs).split(","));
		List<String> userList = java.util.Arrays.asList(getIdList(userArgs).split(","));
		for(String s:teamList){
			 teamsArray.add(s);
		}
		for(String s:userList){
			 userArray.add(s);
		}
		// =============>>> ?????????????????????????????????  <<<=============
		jsonObject.addProperty("title", "AllowPolicy");
		jsonObject.addProperty("date", "1");
		jsonObject.addProperty("priority", 0);
		jsonObject.addProperty("project_id", args[2]);
		jsonObject.addProperty("start_time", "00:00");
		jsonObject.addProperty("stop_time", "23:59");
		jsonObject.addProperty("type", "0");
		jsonObject.add("team_ids", teamsArray);
		jsonObject.add("user_ids", userArray);
		// =============>>> ?????????????????????????????????  <<<=============
		
		String jsonString = jsonObject.toString();
		String uploadStrings[] = {contentType,jsonString};
		response = PeBaseClass.getPostResponse(url,apiString,Cookie,uploadStrings);
		return response;
	}
	public static CloseableHttpResponse createAssetRule(String[] args) throws ParseException, IOException {
		CloseableHttpResponse response = null;
		String contentType = "application/json";
		String cookieWithoutCrumb = args[0];
		String apiString = apiMap.get(args[1])+args[2];
		String assetArgs[] = {cookieWithoutCrumb,"asset_list"};
		getMyCrumb(cookieWithoutCrumb);    //??????cookie
		JsonObject jsonObject = new JsonObject();
		JsonArray assetArray = new JsonArray();
		List<String> assetList = java.util.Arrays.asList(getIdList(assetArgs).split(","));
		for(String s:assetList){
			 assetArray.add(s);
		}
		// =============>>> ?????????????????????????????????  <<<=============
		jsonObject.addProperty("project_id", args[2]);
		jsonObject.add("asset_ids", assetArray);
		// =============>>> ?????????????????????????????????  <<<=============
		
		String jsonString = jsonObject.toString();
		System.out.println(jsonString);
		String uploadStrings[] = {contentType,jsonString};
		response = PeBaseClass.getPostResponse(url,apiString,Cookie,uploadStrings);
		return response;
	}
}
