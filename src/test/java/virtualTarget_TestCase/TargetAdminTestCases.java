package virtualTarget_TestCase;

import static org.testng.Assert.assertNotEquals;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;
import java.util.Random;

import javax.sound.midi.SysexMessage;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import virtualTargetBase.TargetAdminBase;
import virtualTargetBase.TargetBaseClass;

public class TargetAdminTestCases {
	private String currentPath = getClass().getResource("../").getPath().toString().replace("%20", " ").substring(1);
	private String projectPath = currentPath.split("target")[0];
	private String userFile = projectPath+"targetConfig/admin.txt";
	private String apiFile = projectPath+"targetConfig/adminapi.txt";
	private String LogoFilePath = projectPath+"targetConfig/image/";
	private static String cookieWithoutCrumb = "";
	private static final String InitTest = "InitTest";
	private static final String CreateRole = "CreateRole";
	private static final String CreateTeam = "CreateTeam";
	private static final String CreateUser = "CreateUser";
	private static final String UserEnable = "UserEnable";
	private static final String TeamEnable = "TeamEnable";
	private static final String RoleEnable = "RoleEnable";
	private static final String RoleDisable = "RoleDisable";
	private static final String DeleteTeam = "DeleteTeam";
	private static final String DeleteTeamBatch = "DeleteTeamBatch";
	private static final String DeleteUser = "DeleteUser";
	private static final String TestLogin = "TestLogin";
	private static final String GET = "GET";
	private static final String DisableRole = "DisableRole";
	private static final String DeleteRole = "DeleteRole";
	private static final String DeleteRoleBatch = "DeleteRoleBatch";
	private static final String DeleteUserBatch = "DeleteUserBatch";
	private static final String DeleteScenePreview = "DeleteScenePreview";
	
	@BeforeSuite (groups = InitTest)
	public void initUser() {
		TargetAdminBase.initConfig(userFile,apiFile);
	}

	@BeforeMethod (groups = InitTest)
	public void Login() {
		cookieWithoutCrumb = TargetAdminBase.adminLogin();
	}
	
	@AfterMethod (groups = InitTest)
	public void Logout() {
		TargetAdminBase.adminLogout(cookieWithoutCrumb);
	}
	
	@Test (groups = {TestLogin},priority = 100)
	private void testLoginAndLogout() throws ParseException, IOException {
		String apiString = "/simulation/virtual-node/dict";
		String[] argString = {cookieWithoutCrumb,apiString};
		TargetAdminBase.adminUiBase(argString);
	}
	
	@Test (groups = {GET},priority = 101)
	private void getPermission() throws SocketException, IOException{
		String apiString = "permissions";
		String[] argString = {cookieWithoutCrumb,apiString};
		List<String> permiStrings = TargetAdminBase.getPermission(argString);
		if (permiStrings != null) {
			System.out.println(permiStrings);
		}
	}
	
	@Test (groups = {GET},priority = 102)
	private void teamDict() throws SocketException, IOException{
		String apiString = "/publicity/common/team-dict";
		String[] argString = {cookieWithoutCrumb,apiString};
		CloseableHttpResponse response = TargetAdminBase.adminUiBase(argString);
		int code = -1;
		if (response != null) {
			JsonObject jObject = TargetBaseClass.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code,-1,"Get team dict successfully.");
	}
	
	@Test (groups = {GET},priority = 103)
	private void teamRoleDict() throws SocketException, IOException{
		String apiString = "/publicity/common/team-role-dict";
		String[] argString = {cookieWithoutCrumb,apiString};
		CloseableHttpResponse response = TargetAdminBase.adminUiBase(argString);
		int code = -1;
		if (response != null) {
			JsonObject jObject = TargetBaseClass.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code,-1,"Get team role dict successfully.");
	}
	
	@Test (groups = {GET},priority = 104)
	private void roleDict() throws SocketException, IOException{
		String apiString = "/publicity/common/role-dict";
		String[] argString = {cookieWithoutCrumb,apiString};
		CloseableHttpResponse response = TargetAdminBase.adminUiBase(argString);
		int code = -1;
		if (response != null) {
			JsonObject jObject = TargetBaseClass.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code,-1,"Get team role dict successfully.");
	}
	
	@Test (groups = {GET},priority = 105)
	private void statusDict() throws SocketException, IOException{
		String apiString = "/publicity/common/status-dict";
		String[] argString = {cookieWithoutCrumb,apiString};
		CloseableHttpResponse response = TargetAdminBase.adminUiBase(argString);
		int code = -1;
		if (response != null) {
			JsonObject jObject = TargetBaseClass.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code,-1,"Get team role dict successfully.");
	}
	
	@Test (groups = {GET},priority = 106)
	private void roleStatusDict() throws SocketException, IOException{
		String apiString = "/publicity/common/role-status-dict";
		String[] argString = {cookieWithoutCrumb,apiString};
		CloseableHttpResponse response = TargetAdminBase.adminUiBase(argString);
		int code = -1;
		if (response != null) {
			JsonObject jObject = TargetBaseClass.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code,-1,"Get team role dict successfully.");
	}
	
	@Test (groups = {GET},priority = 107)
	private void getTeamId() throws SocketException, IOException{
		String apiString = "teamDict";
		String[] argString = {cookieWithoutCrumb,apiString};
		String IdList = TargetAdminBase.getIdListFromApi(argString);
		System.out.println(IdList);
	}
	
	@Test (groups = {GET},priority = 108)
	private void getRoleId() throws SocketException, IOException{
		String apiString = "roleDict";
		String[] argString = {cookieWithoutCrumb,apiString};
		String IdList = TargetAdminBase.getIdListFromApi(argString);
		System.out.println(IdList);
	}
	
	@Test (groups = {GET},priority = 109)
	private void getTeamRoleId() throws SocketException, IOException{
		String apiString = "teamRoleDict";
		String[] argString = {cookieWithoutCrumb,apiString};
		String IdList = TargetAdminBase.getIdListFromApi(argString);
		System.out.println(IdList);
	}
	
	@Test (groups = {GET},priority = 110)
	private void getStatusId() throws SocketException, IOException{
		String apiString = "statusDict";
		String[] argString = {cookieWithoutCrumb,apiString};
		String IdList = TargetAdminBase.getIdListFromApi(argString);
		System.out.println(IdList);
	}
	
	@Test (groups = {GET},priority = 111)
	private void getRoleStatusId() throws SocketException, IOException{
		String apiString = "roleStatusDict";
		String[] argString = {cookieWithoutCrumb,apiString};
		String IdList = TargetAdminBase.getIdListFromApi(argString);
		System.out.println(IdList);
	}
	
	@Test (groups = {GET},priority = 112)
	private void getRole() throws SocketException, IOException{
		String apiString = "getRole";
		String[] argString = {cookieWithoutCrumb,apiString};
		String IdList = TargetAdminBase.getIdListFromUI(argString);
		System.out.println(IdList);
	}
	
	@Test (groups = {GET},priority = 113)
	private void getTeam() throws SocketException, IOException{
		String apiString = "getTeam";
		String[] argString = {cookieWithoutCrumb,apiString};
		String IdList = TargetAdminBase.getIdListFromUI(argString);
		System.out.println(IdList);
	}
	
	@Test (groups = {GET},priority = 114)
	private void getUser() throws SocketException, IOException{
		String apiString = "getUser";
		String[] argString = {cookieWithoutCrumb,apiString};
		String IdList = TargetAdminBase.getIdListFromUI(argString);
		System.out.println(IdList);
	}
	
	@Test (groups = {GET},priority = 115)
	private void getUserDetail() throws SocketException, IOException{
		String apiString = "getUserDetail_2";
		String messageStr = "message";
		String[] argString = {cookieWithoutCrumb,apiString,messageStr};
		String message = TargetAdminBase.getMessage(argString);
		System.out.println("response message: "+message);
		assertNotEquals(message,"\"success\"","Get user detail successfully.");
	}
	
	@Test (groups = {GET},priority = 116)
	private void getTeamDetail() throws SocketException, IOException{
		String apiString = "getTeamDetail_2";
		String messageStr = "message";
		String[] argString = {cookieWithoutCrumb,apiString,messageStr};
		String message = TargetAdminBase.getMessage(argString);
		System.out.println("response message: "+message);
		assertNotEquals(message,"\"success\"","Get team detail successfully.");
	}
	
	@Test (groups = {GET},priority = 117)
	private void getRoleDetail() throws SocketException, IOException{
		String apiString = "getRoleDetail_LargestAuthorization";
		String messageStr = "message";
		String[] argString = {cookieWithoutCrumb,apiString,messageStr};
		String message = TargetAdminBase.getMessage(argString);
		System.out.println("response message: "+message);
		assertNotEquals(message,"\"success\"","Get role detail successfully.");
	}
	
	@Test (groups = {GET},priority = 118)
	private void getTeamUserList() throws SocketException, IOException{
		String apiString = "getTeamUserList_2";
		String messageStr = "message";
		String[] argString = {cookieWithoutCrumb,apiString,messageStr};
		String message = TargetAdminBase.getMessage(argString);
		System.out.println("response message: "+message);
		assertNotEquals(message,"\"success\"","Get team user list successfully.");
	}
	
	@Test (groups = {GET},priority = 119)
	private void getTeamMemberNumDict() throws SocketException, IOException{
		String apiString = "getTeamMemberNumDict_";
		String messageStr = "message";
		String[] argString = {cookieWithoutCrumb,apiString,messageStr};
		String message = TargetAdminBase.getMessage(argString);
		System.out.println("response message: "+message);
		assertNotEquals(message,"\"success\"","Get team memeber num successfully.");
	}
	
	@Test (groups = {GET},priority = 120)
	private void getTeamStatusDict() throws SocketException, IOException{
		String apiString = "getTeamStatusDict_";
		String messageStr = "message";
		String[] argString = {cookieWithoutCrumb,apiString,messageStr};
		String message = TargetAdminBase.getMessage(argString);
		System.out.println("response message: "+message);
		assertNotEquals(message,"\"success\"","Get team status dict successfully.");
	}
	
	@Test (groups = {GET},priority = 121)
	private void getLog() throws SocketException, IOException{
		String apiString = "getLog_";
		String messageStr = "message";
		String[] argString = {cookieWithoutCrumb,apiString,messageStr};
		String message = TargetAdminBase.getMessage(argString);
		System.out.println("response message: "+message);
		assertNotEquals(message,"\"success\"","Get log successfully.");
	}
	
	@Test (groups = {CreateRole},priority = 1)
	private void createRole() throws ParseException, IOException {
		String apiString = "addRole";
		int roleNum = 22;
		String[] argString = {cookieWithoutCrumb,"permissions"};
		List<String> permissionList = TargetAdminBase.getPermission(argString);
		for (int i = 0; i < roleNum; i++) {
			JsonObject jsonObject = new JsonObject();
			// ��д����������Ե�ֵ������Ӧ�Ľ�ɫ
			JsonArray permissArray = new JsonArray();
			for (int j = 0; j < permissionList.size(); j++) {
				permissArray.add(permissionList.get(j).replace("\"", ""));
			}
			jsonObject.addProperty("old_name", "");
			if (i == 0) {
				jsonObject.addProperty("role_name", "LargestAuthorization");
			}else {
				jsonObject.addProperty("role_name", "AutoAddedRole"+(100+i));
			}
			jsonObject.add("permissions", (JsonElement) permissArray);

			//������벻��Ҫ�仯
			String jsonString = jsonObject.toString();
			String[] args = {cookieWithoutCrumb,apiString,jsonString};
			CloseableHttpResponse response = TargetAdminBase.PostJson(args);
			System.out.println( "Status: " +response.getStatusLine().getStatusCode());
			
			//����������ж��������ɸ�����Ҫ�޸�
			int code = -1;
			if (response != null) {
				JsonObject jObject = TargetBaseClass.getResponseDataJson(response);
				code = jObject.get("code").getAsInt();
			}
			assertNotEquals(code,-1,"Create role successfully.");
		}
	}
	
	@Test (groups = {DisableRole},priority = 2)
	private void disableRole() throws ParseException, IOException {
		String apiString = "disableRole";
		int roleNum = 5;
		for (int i = 0; i < roleNum; i++) {
			JsonObject jsonObject = new JsonObject();
			// ��д����������Ե�ֵ������Ӧ�Ľ�ɫ
			jsonObject.addProperty("role", "AutoAddedRole"+(100+i));

			//������벻��Ҫ�仯
			String jsonString = jsonObject.toString();
			String[] args = {cookieWithoutCrumb,apiString,jsonString};
			CloseableHttpResponse response = TargetAdminBase.PostJson(args);
			System.out.println( "Status: " +response.getStatusLine().getStatusCode());
			
			//����������ж��������ɸ�����Ҫ�޸�
			int code = -1;
			if (response != null) {
				JsonObject jObject = TargetBaseClass.getResponseDataJson(response);
				code = jObject.get("code").getAsInt();
			}
			assertNotEquals(code,-1,"Disable role successfully.");
		}
	}
	
	@Test (groups = {DeleteRole},priority = 3)
	private void deleteRole() throws ParseException, IOException {
		String apiString = "deleteRole";
		JsonObject jsonObject = new JsonObject();
		String[] argString = {cookieWithoutCrumb,"roleDict"};
		String roleIdList = TargetAdminBase.getIdListFromApi(argString);
		String roleId = roleIdList.split(",")[0];
		if ((! roleId.equals("LargestAuthorization"))&&(! roleId.equals("����ִ��Ա"))&&(! roleId.equals("�������Ա"))&&(! roleId.equals("��������Ա"))&&(! roleId.equals("ϵͳ����Ա"))) {
			jsonObject.addProperty("role", roleId);
			System.out.println("Deleting role: "+roleId);
			String jsonString = jsonObject.toString();
			String[] args = {cookieWithoutCrumb,apiString,jsonString,"DELETE"};
			CloseableHttpResponse response = TargetAdminBase.PostJson(args);
			System.out.println( "Status: " +response.getStatusLine().getStatusCode());
			//����������ж��������ɸ�����Ҫ�޸�
			int code = -1;
			if (response != null) {
				JsonObject jObject = TargetBaseClass.getResponseDataJson(response);
				code = jObject.get("code").getAsInt();
			}
			assertNotEquals(code,-1,"Delete 1 role successfully.");
			System.out.println("Delete 1 role successfully.");
		}else {
			assertNotEquals(0,-1,"I will not delete defult role or first created role: "+roleId);
			System.out.println("I will not delete defult role or first created role: "+roleId);
		}

	}
	
	@Test (groups = {DeleteRoleBatch},priority = 3)
	private void deleteRoleBatch() throws ParseException, IOException {
		String apiString = "deleteRoleBatch";
		JsonObject jsonObject = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		String[] argString = {cookieWithoutCrumb,"roleDict"};
		String roleIdList = TargetAdminBase.getIdListFromApi(argString);
		for (int i = 0; i < roleIdList.split(",").length; i++) {
			String roleId = roleIdList.split(",")[i];
			if ((! roleId.equals("LargestAuthorization"))&&(! roleId.equals("����ִ��Ա"))&&(! roleId.equals("�������Ա"))&&(! roleId.equals("��������Ա"))&&(! roleId.equals("ϵͳ����Ա"))) {
				jsonArray.add(roleId);
			}else {
				System.out.println("I will not delete defult role or first created role: "+roleId);
			}
		}
		if (jsonArray.size() > 0) {
			jsonObject.add("role",(JsonElement) jsonArray);
			String jsonString = jsonObject.toString();
			System.out.println(jsonString);
			String[] args = {cookieWithoutCrumb,apiString,jsonString,"DELETE"};
			CloseableHttpResponse response = TargetAdminBase.PostJson(args);
			System.out.println( "Status: " +response.getStatusLine().getStatusCode());
			//����������ж��������ɸ�����Ҫ�޸�
			int code = -1;
			if (response != null) {
				JsonObject jObject = TargetBaseClass.getResponseDataJson(response);
				code = jObject.get("code").getAsInt();
			}
			assertNotEquals(code,-1,"Delete all roles successfully.");
			System.out.println("Delete all roles successfully.");
		}else {
			assertNotEquals(0,-1,"Empty role list.");
			System.out.println("Empty role list.");
		}

	}
	
	@Test (groups = {CreateTeam},priority = 4)
	private void createTeam() throws ParseException, IOException {
		String apiString = "addTeam";
		int teamNum = 21;
		for (int i = 0; i < teamNum; i++) {
			JsonObject jsonObject = new JsonObject();
			JsonArray teamMemberArray = new JsonArray();
			Random random = new Random();
			String LogoPath = LogoFilePath+random.nextInt(10)+".jpg";
			String[] uploadStrings = {LogoPath,cookieWithoutCrumb};
			String uploadResult = TargetAdminBase.logoUpload(uploadStrings);
			// ��д����������Ե�ֵ������Ӧ�Ķ���
			jsonObject.addProperty("team_department", "");
			jsonObject.addProperty("team_id", "");
			jsonObject.addProperty("team_logo_path", uploadResult);
			jsonObject.add("team_members",(JsonElement) teamMemberArray);
			jsonObject.addProperty("team_name", "AutoAddedTeam"+(10+i));
			//������벻��Ҫ�仯
			String jsonString = jsonObject.toString();
			String[] args = {cookieWithoutCrumb,apiString,jsonString};
			CloseableHttpResponse response = TargetAdminBase.PostJson(args);
			System.out.println( "Status: " +response.getStatusLine().getStatusCode());
			
			//����������ж��������ɸ�����Ҫ�޸�
			int code = -1;
			if (response != null) {
				JsonObject jObject = TargetBaseClass.getResponseDataJson(response);
				code = jObject.get("code").getAsInt();
			}
			assertNotEquals(code,-1,"Create teams successfully.");
		}
	}

	@Test (groups = {DeleteTeam},priority = 5)
	private void deleteTeam() throws ParseException, IOException {
		String apiString = "deleteTeam";
		JsonObject jsonObject = new JsonObject();
		String[] argString = {cookieWithoutCrumb,"teamDict"};
		String teamIdList = TargetAdminBase.getIdListFromApi(argString);
		String teamId = teamIdList.split(",")[0];
		if (! teamId.isEmpty()) {
			jsonObject.addProperty("id", teamId);
			String jsonString = jsonObject.toString();
			String[] args = {cookieWithoutCrumb,apiString,jsonString,"DELETE"};
			CloseableHttpResponse response = TargetAdminBase.PostJson(args);

			//����������ж��������ɸ�����Ҫ�޸�
			int code = -1;
			if (response != null) {
				JsonObject jObject = TargetBaseClass.getResponseDataJson(response);
				code = jObject.get("code").getAsInt();
			}
			assertNotEquals(code,-1,"Delete 1 team successfully.");
			System.out.println("Delete 1 team successfully.");
		}else {
			assertNotEquals(0,-1,"Empty team list.");
			System.out.println("Empty team list.");
		}

	}
	
	@Test (groups = {DeleteTeamBatch},priority = 6)
	private void deleteTeamBatch() throws ParseException, IOException {
		String apiString = "deleteTeamBatch";
		JsonObject jsonObject = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		String[] argString = {cookieWithoutCrumb,"teamDict"};
		String teamIdList = TargetAdminBase.getIdListFromApi(argString);
		for (int i = 0; i < teamIdList.split(",").length; i++) {
			String teamId = teamIdList.split(",")[i];
			jsonArray.add(teamId);
		}
		if (jsonArray.size() != 0) {
			jsonObject.add("id", (JsonElement)jsonArray);	
			String jsonString = jsonObject.toString();
			System.out.println(jsonString);
			String[] args = {cookieWithoutCrumb,apiString,jsonString,"DELETE"};
			CloseableHttpResponse response = TargetAdminBase.PostJson(args);

			//����������ж��������ɸ�����Ҫ�޸�
			int code = -1;
			if (response != null) {
				JsonObject jObject = TargetBaseClass.getResponseDataJson(response);
				code = jObject.get("code").getAsInt();
			}
			assertNotEquals(code,-1,"Delete all teams successfully.");
		}else {
			assertNotEquals(0,-1,"Empty team list.");
			System.out.println("Empty team list.");
		}

	}
	
	@Test (groups = {CreateUser},priority = 7)
	private void createUser() throws ParseException, IOException {
		String apiString = "addUser";
		int userNum = 21;
		Random random = new Random();
		String[] argString = {cookieWithoutCrumb,"teamDict"};
		String teamIdList = TargetAdminBase.getIdListFromApi(argString);
		for (int i = 0; i < userNum; i++) {
			JsonObject jsonObject = new JsonObject();
			String teamId = teamIdList.split(",")[random.nextInt(teamIdList.split(",").length)];
			// ��д����������Ե�ֵ������Ӧ���û�
			if (i == 0) {
				jsonObject.addProperty("username", "litao");
				jsonObject.addProperty("password", "Qq..111111");
				jsonObject.addProperty("password_confirm", "Qq..111111");
				jsonObject.addProperty("personnel_name","����");
			}else {
				jsonObject.addProperty("username", "litao"+(100+i));
				jsonObject.addProperty("password_confirm", "Aa..111111");
				jsonObject.addProperty("personnel_name","����"+(100+i));
				jsonObject.addProperty("password", "Aa..111111");
			}
			jsonObject.addProperty("role", "LargestAuthorization");
			jsonObject.addProperty("team_id", Integer.parseInt(teamId));
			jsonObject.addProperty("user_team_role_id", 0);
			jsonObject.addProperty("department", "");

			//������벻��Ҫ�仯
			String jsonString = jsonObject.toString();
			String[] args = {cookieWithoutCrumb,apiString,jsonString};
			CloseableHttpResponse response = TargetAdminBase.PostJson(args);
			System.out.println( "Status: " +response.getStatusLine().getStatusCode());
			
			//����������ж��������ɸ�����Ҫ�޸�
			int code = -1;
			if (response != null) {
				JsonObject jObject = TargetBaseClass.getResponseDataJson(response);
				code = jObject.get("code").getAsInt();
			}
			assertNotEquals(code,-1,"Create User successfully.");
		}
	}
	
	@Test (groups = {DeleteUser},priority = 8)
	private void deleteUser() throws ParseException, IOException {
		String apiString = "deleteUser";
		JsonObject jsonObject = new JsonObject();
		String[] argString = {cookieWithoutCrumb,"getUser"};
		String userIdList = TargetAdminBase.getIdListFromUI(argString);
		String userId = userIdList.split(",")[0];
		if (Integer.parseInt(userId) > 2) {
			jsonObject.addProperty("id", userId);
			String jsonString = jsonObject.toString();
			String[] args = {cookieWithoutCrumb,apiString,jsonString,"DELETE"};
			CloseableHttpResponse response = TargetAdminBase.PostJson(args);
			System.out.println( "Status: " +response.getStatusLine().getStatusCode());
			//����������ж��������ɸ�����Ҫ�޸�
			int code = -1;
			if (response != null) {
				JsonObject jObject = TargetBaseClass.getResponseDataJson(response);
				code = jObject.get("code").getAsInt();
			}
			assertNotEquals(code,-1,"Delete 1 user successfully.");
			System.out.println("Delete all users successfully.");
		}else {
			assertNotEquals(0,-1,"I will not delete defult user or first created user: "+userId);
			System.out.println("I will not delete default user or first created user: "+userId);
		}
	}
	
	@Test (groups = {DeleteUserBatch},priority = 9)
	private void deleteUserBatch() throws ParseException, IOException {
		String apiString = "deleteUserBatch";
		JsonObject jsonObject = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		String[] argString = {cookieWithoutCrumb,"getUser"};
		String userIdList = TargetAdminBase.getIdListFromUI(argString);
		for (int i = 0; i < userIdList.split(",").length; i++) {
			String userId = userIdList.split(",")[i];
			if (Integer.parseInt(userId) > 2) {
				jsonArray.add(userId);	
			}
		}
		if (jsonArray.size() > 0) {
			jsonObject.add("id", (JsonElement) jsonArray);
			String jsonString = jsonObject.toString();
			String[] args = {cookieWithoutCrumb,apiString,jsonString,"DELETE"};
			CloseableHttpResponse response = TargetAdminBase.PostJson(args);
			System.out.println( "Status: " +response.getStatusLine().getStatusCode());
			//����������ж��������ɸ�����Ҫ�޸�
			int code = -1;
			if (response != null) {
				JsonObject jObject = TargetBaseClass.getResponseDataJson(response);
				code = jObject.get("code").getAsInt();
			}
			assertNotEquals(code,-1,"Delete all users successfully.");
			System.out.println("Delete all users successfully.");
		}else {
			assertNotEquals(0,-1,"I will not delete default user or first created user.");
			System.out.println("I will not delete default user or first created user.");
		}
	}
	
	@Test (groups = {UserEnable},priority = 11)
	private void userEnable() throws ParseException, IOException {
		String apiString = "userEnable";
		String[] argString = {cookieWithoutCrumb,apiString};
		TargetAdminBase.userEnable(argString);
	}
	
	@Test (groups = {UserEnable},priority = 11)
	private void userDisable() throws ParseException, IOException {
		String apiString = "userDisable";
		String[] argString = {cookieWithoutCrumb,apiString};
		TargetAdminBase.userEnable(argString);
	}
	
	@Test (groups = {TeamEnable},priority = 11)
	private void teamEnable() throws ParseException, IOException {
		String apiString = "teamEnable";
		String[] argString = {cookieWithoutCrumb,apiString};
		TargetAdminBase.teamEnable(argString);
	}
	
	@Test (groups = {TeamEnable},priority = 11)
	private void teamDisable() throws ParseException, IOException {
		String apiString = "teamDisable";
		String[] argString = {cookieWithoutCrumb,apiString};
		TargetAdminBase.teamEnable(argString);
	}
	
	@Test (groups = {RoleEnable},priority = 11)
	private void roleEnable() throws ParseException, IOException {
		String apiString = "roleEnable";
		String[] argString = {cookieWithoutCrumb,apiString};
		TargetAdminBase.roleEnable(argString);
	}
	
	@Test (groups = {RoleDisable},priority = 11)
	private void roleDisable() throws ParseException, IOException {
		String apiString = "roleDisable";
		String[] argString = {cookieWithoutCrumb,apiString};
		TargetAdminBase.roleEnable(argString);
	}
	
	@Test (groups = {DeleteScenePreview},priority = 8)
	private void deleteScenePreview() throws ParseException, IOException {
		String apiString = "previewDelete";
		JsonObject jsonObject = new JsonObject();
		String previewId = "8";
		jsonObject.addProperty("id", previewId);
		String jsonString = jsonObject.toString();
		String[] args = { cookieWithoutCrumb, apiString, jsonString, "DELETE" };
		CloseableHttpResponse response = TargetAdminBase.PostJson(args);
		System.out.println("Status: " + response.getStatusLine().getStatusCode());
		// ����������ж��������ɸ�����Ҫ�޸�
		int code = -1;
		if (response != null) {
			JsonObject jObject = TargetBaseClass.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code, -1, "Delete 1 preview successfully.");
		System.out.println("Delete all preview successfully.");
	}
}
