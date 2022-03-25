package penetration_TestCase;

import static org.testng.Assert.assertNotEquals;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;

import baseApi.GetIp;
import penetration_baseApi.PeAdminBase;
import penetration_baseApi.PeBaseClass;


public class PeAdminTestCases {
	private String currentPath = getClass().getResource("../").getPath().toString().replace("%20", " ").substring(1);
	private String projectPath = currentPath.split("target")[0];
	private String userFile = projectPath+"penetrationConfig/admin.txt";
	private String apiFile = projectPath+"penetrationConfig/adminapi.txt";
	private static String cookieWithoutCrumb = "";
	private static final String InitTest = "InitTest";
	private static final String GetAttackerList = "GetAttackerList";
	private static final String CreateAttacker = "CreateAttacker";
	private static final String UpdateAttacker = "UpdateAttacker";
	private static final String CreateTeam = "CreateTeam";
	private static final String UpdateTeam = "UpdateTeam";
	private static final String CreateOrganization = "CreateOrganization";
	private static final String UpdateOrganization = "UpdateOrganization";
	private static final String CreateAsset = "CreateAsset";
	private static final String CreateVirtualIp = "CreateVirtualIp";
	private static final String CreateVm = "CreateVm";
	private static final String UpdateVirtualSettings = "UpdateVirtualSettings";
	private static final String CreateProject = "CreateProject";
	private static final String UpdateProjectTime = "UpdateProjectTime";
	private static final String CreateLoginPolicy = "CreateLoginPolicy";
	private static final String AssetRuleCreate = "AssetRuleCreate";
	private static final String FirstLogin = "FirstLogin";
	
	@BeforeSuite (groups = InitTest)
	public void initUser() {
		PeAdminBase.initConfig(userFile,apiFile);
	}

	@BeforeClass (groups = InitTest)
	public void adminLogin() {
		cookieWithoutCrumb = PeAdminBase.adminLogin();
	}
	
	@AfterClass (groups = InitTest)
	public void adminLogout() {
		PeAdminBase.adminLogout(cookieWithoutCrumb);
	}
	
	@Test (groups = {GetAttackerList},priority = 100)
	private void getUserList() throws ParseException, IOException {
		String args[] = {cookieWithoutCrumb,"team_list"};
		String args1[] = {cookieWithoutCrumb,"index-attacker"};
		String args2[] = {cookieWithoutCrumb,"organization_list"};
		String args3[] = {cookieWithoutCrumb,"project_list"};
		String args4[] = {cookieWithoutCrumb,"asset_list"};
		String userLiString = PeAdminBase.getIdList(args1);
		String teamLiString = PeAdminBase.getIdList(args);
		String organizationLiString = PeAdminBase.getIdList(args2);
		String projectLiString = PeAdminBase.getIdList(args3);
		String assetLiString = PeAdminBase.getIdList(args4);
		System.out.println("userID: "+userLiString);
		System.out.println("teamID: "+teamLiString);
		System.out.println("organizationID: "+organizationLiString);
		System.out.println("projectID: "+projectLiString);
		System.out.println("assetID: "+assetLiString);
	}
	
	@Test (groups = {FirstLogin},priority = 1)
	private void firstLogin() throws ParseException, IOException {
		String apiString = "first-login";
		String[] argString = {cookieWithoutCrumb,apiString};
		PeAdminBase.firstLogin(argString);
	}
	
	@Test (groups = {CreateTeam},priority = 2)
	private void createTeam() throws ParseException, IOException {
		String apiString = "team";
		int teamNum = 10;
		for (int i = 0; i < teamNum; i++) {
			JsonObject jsonObject = new JsonObject();
			// 填写下面各个属性的值创建相应的队伍
			jsonObject.addProperty("队伍名称", "蓝天"+(100+i));
			jsonObject.addProperty("队伍简称", "蓝天简称"+(100+i));
			jsonObject.addProperty("所属公司", "蓝天公司"+(100+i));

			//下面代码不需要变化
			String jsonString = jsonObject.toString();
			String[] args = {cookieWithoutCrumb,apiString,jsonString};
			CloseableHttpResponse response = PeAdminBase.Authcontrol(args);
			System.out.println(response);
			
			//下面代码是判断条件，可根据需要修改
			int code = -1;
			if (response != null) {
				JsonObject jObject = PeBaseClass.getResponseDataJson(response);
				code = jObject.get("code").getAsInt();
			}
			assertNotEquals(code,-1,"Create team successfully.");
		}

	}
	
	@Test (groups = {UpdateTeam},priority = 3)
	private void UpdateTeam() throws ParseException, IOException {
		JsonObject jsonObject = new JsonObject();
		String apiString = "team";
		String args[] = {cookieWithoutCrumb,"蓝天101"};
		String team_id = PeAdminBase.getIdList(args);
		// 填写下面各个属性的值修改相应的队伍
		jsonObject.addProperty("队伍名称", "蓝天101");
		jsonObject.addProperty("队伍简称", "蓝天简称201");
		jsonObject.addProperty("所属公司", "蓝天公司201");

		//下面代码不需要变化
		String jsonString = jsonObject.toString();
		String[] argsStrings = {cookieWithoutCrumb,apiString,jsonString,team_id};
		CloseableHttpResponse response = PeAdminBase.Authcontrol(argsStrings);
		System.out.println(response);
		
		//下面代码是判断条件，可根据需要修改
		int code = -1;
		if (response != null) {
			JsonObject jObject = PeBaseClass.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code,-1,"Update team successfully.");
	}
	
	@Test (groups = {CreateAttacker},priority = 4)
	private void createAttacker() throws ParseException, IOException {
		JsonObject jsonObject = new JsonObject();
		String apiString = "create-attacker";
		String args[] = {cookieWithoutCrumb,"team_list"};
		String teamIds = PeAdminBase.getIdList(args);
		String teamId[] = teamIds.split(",");
		int attackerNum = 20;
		for (int i = 0; i < attackerNum; i++) {
			Random random = new Random();
			int orgIdNum = random.nextInt(teamId.length); 
			// 填写下面各个属性的值创建相应的用户
			jsonObject.addProperty("用户名", "att_litao"+(100+i));
			jsonObject.addProperty("手机号", "18210001"+(100+i));
			jsonObject.addProperty("姓名", "李涛"+(100+i));
			jsonObject.addProperty("密码", "96e79218965eb72c92a549dd5a330112");
			jsonObject.addProperty("确认密码", "96e79218965eb72c92a549dd5a330112");
			jsonObject.addProperty("队伍", teamId[orgIdNum]);

			//下面代码不需要变化
			String jsonString = jsonObject.toString();
			String[] argStrings = {cookieWithoutCrumb,apiString,jsonString};
			CloseableHttpResponse response = PeAdminBase.Authcontrol(argStrings);
			System.out.println(response);
			
			//下面代码是判断条件，可根据需要修改
			int code = -1;
			if (response != null) {
				JsonObject jObject = PeBaseClass.getResponseDataJson(response);
				code = jObject.get("code").getAsInt();
			}
			assertNotEquals(code,-1,"Create Attacker successfully.");
		}

	}
	
	@Test (groups = {UpdateAttacker},priority = 5)
	private void updateAttacker() throws ParseException, IOException {
		JsonObject jsonObject = new JsonObject();
		String apiString = "update-attacker";
		String args[] = {cookieWithoutCrumb,"att_litao101"};
		String attacker_id = PeAdminBase.getIdList(args);
		String argsTeam[] = {cookieWithoutCrumb,"蓝天101"};
		String team_id = PeAdminBase.getIdList(argsTeam);
		// 填写下面各个属性的值修改相应的用户
		jsonObject.addProperty("用户名", "att_litao201");
		jsonObject.addProperty("手机号", "18220001001");
		jsonObject.addProperty("姓名", "att_litao201");
		jsonObject.addProperty("密码", "96e79218965eb72c92a549dd5a330112");
		jsonObject.addProperty("确认密码", "96e79218965eb72c92a549dd5a330112");
		jsonObject.addProperty("队伍", team_id);

		//下面代码不需要变化
		String jsonString = jsonObject.toString();
		String[] argString = {cookieWithoutCrumb,apiString,jsonString,attacker_id};
		CloseableHttpResponse response = PeAdminBase.Authcontrol(argString);
		System.out.println(response);
		
		//下面代码是判断条件，可根据需要修改
		int code = -1;
		if (response != null) {
			JsonObject jObject = PeBaseClass.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code,-1,"Update Attacker successfully.");
	}
	
	@Test (groups = {CreateOrganization},priority = 6)
	private void CreateOrganization() throws ParseException, IOException {
		JsonObject jsonObject = new JsonObject();
		String apiString = "organization";
		/* 组织部门名称集合 */
		 String[] strings = {"市场部", "人事部", "研发部", "测试部", "IT部", "销售部"};
		 List<String> stringList = java.util.Arrays.asList(strings);
		 /* 开始新建组织部门 */
		 int i = 1000;
		 for (Iterator<String> itr = stringList.iterator(); itr.hasNext();) {
			String orgName = itr.next();
			i++;
			System.out.println(orgName);
			// 填写下面各个属性的值创建相应的部门
			jsonObject.addProperty("组织部门名称", orgName);
			jsonObject.addProperty("简称", orgName+"简称");
			jsonObject.addProperty("负责人", "小马"+i);
			jsonObject.addProperty("负责人手机号", "1831000"+i);

			//下面代码不需要变化
			String jsonString = jsonObject.toString();
			String[] args = {cookieWithoutCrumb,apiString,jsonString};
			CloseableHttpResponse response = PeAdminBase.Authcontrol(args);
			System.out.println(response);
			
			//下面代码是判断条件，可根据需要修改
			int code = -1;
			if (response != null) {
				JsonObject jObject = PeBaseClass.getResponseDataJson(response);
				code = jObject.get("code").getAsInt();
			}
			assertNotEquals(code,-1,"Create Organization successfully.");
		 }

	}
	
	@Test (groups = {UpdateOrganization},priority = 7)
	private void UpdateOrganization() throws ParseException, IOException {
		JsonObject jsonObject = new JsonObject();
		String apiString = "organization";
		String args[] = {cookieWithoutCrumb,"organization_list","市场部"};
		String orgId = PeAdminBase.getIdList(args);
		// 填写下面各个属性的值修改相应的部门
		jsonObject.addProperty("组织部门名称", "市场部");
		jsonObject.addProperty("简称", "市场部简称2");
		jsonObject.addProperty("负责人", "小马");
		jsonObject.addProperty("负责人手机号", "18310001003");

		//下面代码不需要变化
		String jsonString = jsonObject.toString();
		String[] argStrings = {cookieWithoutCrumb,apiString,jsonString,orgId};
		CloseableHttpResponse response = PeAdminBase.Authcontrol(argStrings);
		System.out.println(response);
		
		//下面代码是判断条件，可根据需要修改
		int code = -1;
		if (response != null) {
			JsonObject jObject = PeBaseClass.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code,-1,"Update Organization successfully.");
	}
	
	@Test (groups = {CreateAsset},priority = 8)
	private void CreateAsset() throws ParseException, IOException {
		JsonObject jsonObject = new JsonObject();
		String apiString = "asset";
		String args[] = {cookieWithoutCrumb,"organization_list"};
		int AssetNum = 200;
		Random random = new Random();
		for (int i = 0; i < AssetNum; i++) {
			String orgIds = PeAdminBase.getIdList(args);
			String orgId[] = orgIds.split(",");
			int orgIdNum = random.nextInt(orgId.length); 
			int netLevel = 1 + random.nextInt(4);
			String randomIp = GetIp.getRandomIp();
			// 填写下面各个属性的值创建相应的资产
			jsonObject.addProperty("资产名称", "XXX-Server"+i);
			jsonObject.addProperty("网络层级", netLevel+"");
			jsonObject.addProperty("IP", randomIp);
			jsonObject.addProperty("URL", "https://www.OA-Server"+i+".com");
			jsonObject.addProperty("资产归属", orgId[orgIdNum]);

			//下面代码不需要变化
			String jsonString = jsonObject.toString();
			String[] argStrings = {cookieWithoutCrumb,apiString,jsonString};
			CloseableHttpResponse response = PeAdminBase.Authcontrol(argStrings);
			System.out.println(response);
			
			//下面代码是判断条件，可根据需要修改
			int code = -1;
			if (response != null) {
				JsonObject jObject = PeBaseClass.getResponseDataJson(response);
				code = jObject.get("code").getAsInt();
			}
			assertNotEquals(code,-1,"Create Asset successfully.");
		}

	}
	
	@Test (groups = {CreateVirtualIp},priority = 9)
	private void createVirtualIp() throws ParseException, IOException {
		JsonObject jsonObject = new JsonObject();
		String apiString = "virtual-ip_create";

		// 填写下面各个属性的值创建相应的IP
		jsonObject.addProperty("批量新增IP地址", "192.168.10.141-149");

		//下面代码不需要变化
		String jsonString = jsonObject.toString();
		String[] argStrings = {cookieWithoutCrumb,apiString,jsonString};
		CloseableHttpResponse response = PeAdminBase.Authcontrol(argStrings);
		System.out.println(response);
		
		//下面代码是判断条件，可根据需要修改
		int code = -1;
		if (response != null) {
			JsonObject jObject = PeBaseClass.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code,-1,"Create VirtualIp successfully.");
	}
	
	@Test (groups = {UpdateVirtualSettings},priority = 10)
	private void updateVirtualSettings() throws ParseException, IOException {
		JsonObject jsonObject = new JsonObject();
		String apiString = "virtual-settings_update";
		
		// 填写下面各个属性的值创建相应用户的虚拟机
		jsonObject.addProperty("CPU核数", "2");
		jsonObject.addProperty("内存大小", "2");
		jsonObject.addProperty("磁盘大小", "50");

		//下面代码不需要变化
		String jsonString = jsonObject.toString();
		String[] argString = {cookieWithoutCrumb,apiString,jsonString};
		CloseableHttpResponse response = PeAdminBase.Authcontrol(argString);
		System.out.println(response);
		
		//下面代码是判断条件，可根据需要修改
		int code = -1;
		if (response != null) {
			JsonObject jObject = PeBaseClass.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code,-1,"Update VirtualSettings successfully.");
	}
	
	@Test (groups = {CreateVm},priority = 11)
	private void createVm() throws ParseException, IOException {
		JsonObject jsonObject = new JsonObject();
		String apiString = "vm_create";
		String args[] = {cookieWithoutCrumb,"index-attacker","att_litao101"};
		String attacker_id = PeAdminBase.getIdList(args);

		// 填写下面各个属性的值创建相应用户的虚拟机
		jsonObject.addProperty("虚拟终端名称", "vm-att_litao101");
		jsonObject.addProperty("分配对象", attacker_id);

		//下面代码不需要变化
		String jsonString = jsonObject.toString();
		String[] argString = {cookieWithoutCrumb,apiString,jsonString};
		CloseableHttpResponse response = PeAdminBase.Authcontrol(argString);
		System.out.println(response);
		
		//下面代码是判断条件，可根据需要修改
		int code = -1;
		if (response != null) {
			JsonObject jObject = PeBaseClass.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code,-1,"Create vm successfully.");
	}
	
	@Test (groups = {CreateProject},priority = 12)
	private void createProject() throws ParseException, IOException {
		JsonObject jsonObject = new JsonObject();
		String apiString = "project";
		
		// 填写下面各个属性的值创建实例
		jsonObject.addProperty("实例名称", "2020年第"+new Random().nextInt(100)+"次渗透");

		//下面代码不需要变化
		String jsonString = jsonObject.toString();
		String[] argString = {cookieWithoutCrumb,apiString,jsonString};
		CloseableHttpResponse response = PeAdminBase.Authcontrol(argString);
		System.out.println(response);
		
		//下面代码是判断条件，可根据需要修改
		int code = -1;
		if (response != null) {
			JsonObject jObject = PeBaseClass.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code,-1,"Create project successfully.");
	}
	
	@Test (groups = {UpdateProjectTime},priority = 13)
	private void updateProjectTime() throws ParseException, IOException {
		JsonObject jsonObject = new JsonObject();
		String apiString = "project-time";
		String args[] = {cookieWithoutCrumb,"project_list"};
		String projectId = PeAdminBase.getIdList(args);   //获取进行的项目ID
		
		// 填写下面各个属性的值创建相应用户的虚拟机
		jsonObject.addProperty("项目开始时间", "2020-10-31 23:59:59");
		jsonObject.addProperty("项目结束时间", "2021-10-31 23:59:59");
		jsonObject.addProperty("项目ID", projectId);

		//下面代码不需要变化
		String jsonString = jsonObject.toString();
		String[] argString = {cookieWithoutCrumb,apiString,jsonString,projectId};
		CloseableHttpResponse response = PeAdminBase.Authcontrol(argString);
		System.out.println(response);
		
		//下面代码是判断条件，可根据需要修改
		int code = -1;
		if (response != null) {
			JsonObject jObject = PeBaseClass.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code,-1,"Create vm successfully.");
	}
	
	@Test (groups = {CreateLoginPolicy},priority = 14)
	private void createLoginPolicy() throws ParseException, IOException {
		String apiString = "net-access-rule_create";
		String args[] = {cookieWithoutCrumb,"project_list"};
		String projectId = PeAdminBase.getIdList(args);   //获取进行的项目ID

		//下面代码不需要变化
		String[] argString = {cookieWithoutCrumb,apiString,projectId};
		CloseableHttpResponse response = PeAdminBase.createPolicy(argString);
		System.out.println(response);
		
		//下面代码是判断条件，可根据需要修改
		int code = -1;
		if (response != null) {
			JsonObject jObject = PeBaseClass.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code,-1,"Create vm successfully.");
	}
	
	@Test (groups = {AssetRuleCreate},priority = 14)
	private void assetRuleCreate() throws ParseException, IOException {
		String apiString = "organization-asset-rule_create";
		String args[] = {cookieWithoutCrumb,"project_list"};
		String projectId = PeAdminBase.getIdList(args);   //获取进行的项目ID

		//下面代码不需要变化
		String[] argString = {cookieWithoutCrumb,apiString,projectId};
		CloseableHttpResponse response = PeAdminBase.createAssetRule(argString);
		System.out.println(response);
		
		//下面代码是判断条件，可根据需要修改
		int code = -1;
		if (response != null) {
			JsonObject jObject = PeBaseClass.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code,-1,"Create vm successfully.");
	}
}
