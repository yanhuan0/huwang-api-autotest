package huwang2020_testCase;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Random;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;

import baseApi.CalculateResponseTime;
import baseApi.Client;
import baseApi.Utils;
import huwang2020_base.HwAdminBase;

public class HwAdminTestCases extends HwAdminBase {
	private static Logger logger = Logger.getLogger(HwAdminTestCases.class);
	private static String TableName = "HwAdminTestCases";
	private String currentPath = getClass().getResource("../").getPath().toString().replace("%20", " ").substring(1);
	private String projectPath = currentPath.split("target")[0];
	private String userFile = projectPath + "config/admin.txt";
	private String apiFile = projectPath + "config/adminapi.txt";
	private String LogoFilePath = projectPath + "config/image/";
	public static String cookieWithoutCrumb = "";
	public static final String InitTest = "InitTest";
	public static final String UiLevel1_2 = "UiLevel1_2";
	public static final String EditUser = "EditUser";
	public static final String CreateTeam = "CreateTeam";
	public static final String CreateGroup = "CreateGroup";
	public static final String CreateVicGroupMulti = "CreateVicGroupMulti";
	public static final String CreateAttGroupMulti = "CreateAttGroupMulti";
	public static final String CreateDepartment = "CreateDepartment";
	public static final String CreateAsset = "CreateAsset";
	public static final String CreatePolicy = "CreatePolicy";
	public static final String CreateUser = "CreateUser";
	public final static String DeleteGroup = "DeleteGroup";
	public final static String DeleteTeam = "DeleteTeam";
	public final static String DeleteUser = "DeleteUser";
	public static final String AddOutIp = "AddOutIp";
	public static final String ConfigOutIp = "ConfigOutIp";
	public static final String ImportInfo = "ImportInfo";
	public static final String ImportUser = "ImportUser";
	public static final String EditPassword = "EditPassword";

	private static int ATTACKERSIZE = 10;
	private static String ATTGROUPIMPORTFILE;
	private static String VICGROUPIMPORTFILE;
	private static String USERIMPORTFILE;

	static {
		CalculateResponseTime.tableName = TableName;

		if (ATTACKERSIZE == 10) {
			ATTGROUPIMPORTFILE = "gjdw-10-hw2020or2021.xlsx";
			VICGROUPIMPORTFILE = "fsdw-10-hw2020or2021.xlsx";
			USERIMPORTFILE = "yhjs-10-hw2020.xlsx";
		} else if (ATTACKERSIZE == 240) {
			ATTGROUPIMPORTFILE = "gjdw-240-hw2020or2021.xlsx";
			VICGROUPIMPORTFILE = "fsdw-400-hw2020or2021.xlsx";
			USERIMPORTFILE = "yhjsall-640-hw2020.xlsx";
		} else {
			System.out.println("IMPORT FILE MISSING");
			System.exit(0);
		}
		try {
			CalculateResponseTime.createTable(TableName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	};

	@BeforeSuite(groups = InitTest)
	public void initUser() throws SQLException {
		initConfig(userFile, apiFile);
		cookieWithoutCrumb = userLogin();
	}

	@AfterSuite(groups = InitTest)
	public void adminLogout() {
		userLogout(cookieWithoutCrumb);
	}

	@Test(groups = EditUser)
	private void editUserList() throws ParseException, IOException, SQLException {
		String[] roleString = { "attacker", "victim", "judge", "expert", "verifier", "audience", "watchdog" };
		int[] roleNum = new int[roleString.length];
		for (int i = 0; i < roleString.length; i++) {
			String[] argStrings = { roleString[i], cookieWithoutCrumb };
			roleNum[i] = editUserList(argStrings);
		}
		for (int i = 0; i < roleNum.length; i++) {
			logger.info("Edited " + roleString[i] + " : " + roleNum[i]);
		}
	}

	@Test(groups = EditPassword)
	private void editItself() throws ParseException, IOException, SQLException {
		String apiString = "edit-self";
		String newPassword = "Aa..111111";
		String args[] = { cookieWithoutCrumb, apiString, newPassword };
		CloseableHttpResponse response = editUserself(args);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject);
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功");
	}

	@Test(groups = CreateDepartment)
	private void departmentWithLogo() throws ParseException, IOException, SQLException {

		// 提供name和filepath两个参数，即可创建一个带本地图片的行业
		String name = "departmentJava1";
		Random random = new Random();
		String LogoPath = LogoFilePath + random.nextInt(10) + ".jpg";

		// 以下代码不需要修改
		String args[] = { cookieWithoutCrumb, name, LogoPath };
		CloseableHttpResponse response = departmentBase(args);
		logger.info(response);

		// 下面代码是判断条件，可根据需要修改
		int code = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code, -1);
	}

	@Test(groups = CreateDepartment)
	private void departmentWithoutLogo() throws ParseException, IOException, SQLException {

		// 提供name，即可创建一个不图片的行业
		String name = "departmentJava2";

		// 以下代码不需要修改
		String args[] = { cookieWithoutCrumb, name };
		CloseableHttpResponse response = departmentBase(args);
		logger.info(response);

		// 下面代码是判断条件，可根据需要修改
		int code = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code, -1);
	}

	@Test(groups = ImportInfo, priority = 1)
	private void teamAttackerImport() throws ParseException, IOException, SQLException {
		String LogoPath = LogoFilePath + ATTGROUPIMPORTFILE;
		String[] args = { cookieWithoutCrumb, LogoPath, "orgnizationAttackersImport" };
		CloseableHttpResponse response = teamOrUserImport(args);
		logger.info(response);
		try {
			logger.info("Wait 60 seconds for import attacker team.");
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test(groups = ImportInfo, priority = 2)
	private void teamVictimImport() throws ParseException, IOException, SQLException {
		String LogoPath = LogoFilePath + VICGROUPIMPORTFILE;
		String[] args = { cookieWithoutCrumb, LogoPath, "orgnizationVictimsImport" };
		CloseableHttpResponse response = teamOrUserImport(args);
		logger.info(response);
		try {
			logger.info("Wait 60 seconds for import victim team.");
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test(groups = ImportUser, priority = 3)
	private void usersImport() throws ParseException, IOException, SQLException {
		String LogoPath = LogoFilePath + USERIMPORTFILE;
		String[] args = { cookieWithoutCrumb, LogoPath, "userImport" };
		CloseableHttpResponse response = teamOrUserImport(args);
		logger.info(response);
		try {
			logger.info("Wait 60 seconds for import user.");
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test(groups = CreateTeam, priority = 4)
	private void orgnizationAttackerWithLogo() throws ParseException, IOException, SQLException {
		JsonObject jsonObject = new JsonObject();

		// 填写下面各个属性的值创建相应的攻击队伍
		jsonObject.addProperty("队伍名称", "attTeamJava1");
		jsonObject.addProperty("队伍简称", "Java1");
		jsonObject.addProperty("所属公司", "360");
		jsonObject.addProperty("队伍性质", "3");
		jsonObject.addProperty("所在地", "110101");
		jsonObject.addProperty("详细地址", "北京市东城区某某街道");
		jsonObject.addProperty("纬度", "39.9109245473");
		jsonObject.addProperty("经度", "116.41338369712");
		jsonObject.addProperty("队伍LOGO", "(binary)");
		Random random = new Random();
		String LogoPath = LogoFilePath + random.nextInt(10) + ".jpg";

		// 下面代码不需要变化
		String jsonString = jsonObject.toString();
		String[] args = { cookieWithoutCrumb, jsonString, LogoPath };
		CloseableHttpResponse response = orgnizationTeam(args);
		logger.info(response);

		// 下面代码是判断条件，可根据需要修改
		int code = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code, -1, "Create department successfully.");
	}

	@Test(groups = CreateTeam, priority = 5)
	private void orgnizationAttackerWithoutLogo() throws ParseException, IOException, SQLException {
		JsonObject jsonObject = new JsonObject();

		// 填写下面各个属性的值创建相应的不带log的攻击队伍
		jsonObject.addProperty("队伍名称", "attTeamjava2");
		jsonObject.addProperty("队伍简称", "Java2");
		jsonObject.addProperty("所属公司", "360");
		jsonObject.addProperty("队伍性质", "3");
		jsonObject.addProperty("所在地", "110101");
		jsonObject.addProperty("详细地址", "北京市东城区某某街道");
		jsonObject.addProperty("纬度", "39.9109245473");
		jsonObject.addProperty("经度", "116.41338369712");
		// jsonObject.addProperty("队伍LOGO", "(binary)");

		// 下面代码不需要变化
		String jsonString = jsonObject.toString();
		String[] args = { cookieWithoutCrumb, jsonString };
		CloseableHttpResponse response = orgnizationTeam(args);
		logger.info(response);

		// 下面代码是判断条件，可根据需要修改
		int code = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code, -1, "Create department successfully.");
	}

	@Test(groups = CreateTeam, priority = 6)
	private void orgnizationVictimWithoutLogo1() throws ParseException, IOException, SQLException {
		JsonObject jsonObject = new JsonObject();

		// 填写下面各个属性的值创建相应的不带log的防守队伍
		jsonObject.addProperty("队伍名称", "vicTeamJava1");
		jsonObject.addProperty("队伍简称", "Java1");
		jsonObject.addProperty("队伍性质", "4"); // 4-7
		jsonObject.addProperty("行业", "1"); // 1-36
		jsonObject.addProperty("所在地", "110101");
		jsonObject.addProperty("详细地址", "北京市东城区某某街道");
		jsonObject.addProperty("纬度", "39.9109245473");
		jsonObject.addProperty("经度", "116.41338369712");
		// jsonObject.addProperty("队伍LOGO", "(binary)");

		// 下面代码不需要变化
		String jsonString = jsonObject.toString();
		String[] args = { cookieWithoutCrumb, jsonString };
		CloseableHttpResponse response = orgnizationTeam(args);
		logger.info(response);

		// 下面代码是判断条件，可根据需要修改
		int code = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code, -1, "Create department successfully.");
	}

	@Test(groups = CreateTeam, priority = 7)
	private void orgnizationVictimWithLogo1() throws ParseException, IOException, SQLException {
		JsonObject jsonObject = new JsonObject();

		// 填写下面各个属性的值创建相应的带log的防守队伍
		jsonObject.addProperty("队伍名称", "vicTeamJava2");
		jsonObject.addProperty("队伍简称", "Java2");
		jsonObject.addProperty("队伍性质", "7"); // 4-7
		jsonObject.addProperty("行业", "36"); // 1-36
		jsonObject.addProperty("所在地", "110101");
		jsonObject.addProperty("详细地址", "北京市东城区某某街道");
		jsonObject.addProperty("纬度", "39.9109245473");
		jsonObject.addProperty("经度", "116.41338369712");
		jsonObject.addProperty("队伍LOGO", "(binary)");
		Random random = new Random();
		String LogoPath = LogoFilePath + random.nextInt(10) + ".jpg";

		// 下面代码不需要变化
		String jsonString = jsonObject.toString();
		String[] args = { cookieWithoutCrumb, jsonString, LogoPath };
		CloseableHttpResponse response = orgnizationTeam(args);
		logger.info(response);

		// 下面代码是判断条件，可根据需要修改
		int code = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			code = jObject.get("code").getAsInt();
		}
		assertNotEquals(code, -1, "Create department successfully.");
	}

	@Test(groups = "CreateGroupAtt", priority = 8)
	private void attackerGroup() throws ParseException, IOException, SQLException {
		String apiString = "attackerGroup";
		JsonObject jsonObject = new JsonObject();

		// 填写下面各个属性的值创建相应的攻击组
		jsonObject.addProperty("名称", "attGroupAutoTest");
		jsonObject.addProperty("描述", "This is a test attacker group from javatest");

		String attackerTeamNameString = ""; // 字符串为空时过滤所有存在的攻击队名称
		String attApiString = "orgnizationListType1";
		String argsAtt[] = { cookieWithoutCrumb, attApiString, attackerTeamNameString };
		String attackerTeamId = teamNameToid(argsAtt);

		String jsonString = jsonObject.toString();
		String args[] = { cookieWithoutCrumb, apiString, jsonString, attackerTeamId };
		CloseableHttpResponse response = orgnizationGroup(args);
		logger.info(response);

		// 下面代码是判断条件，可根据需要修改
		int status = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			status = jObject.get("data").getAsJsonObject().get("status").getAsInt();
		}
		assertEquals(status, 1);
	}

	@Test(groups = CreateAttGroupMulti, priority = 8)
	private void attackerGroupMulti() throws ParseException, IOException, SQLException {
		String apiString = "attackerGroup";
		int teamsPerGroup = 10;
		String attackerTeamNameString = ""; // 字符串为空时过滤所有存在的攻击队名称
		String attApiString = "orgnizationListType1";
		String argsAtt[] = { cookieWithoutCrumb, attApiString, attackerTeamNameString };
		String attackerTeamId = teamNameToid(argsAtt);
		String[] attTeamArray = attackerTeamId.split(",");
		int groups = (int) Math.ceil((double) (attTeamArray.length / teamsPerGroup));
		for (int i = 0; i < groups; i++) {
			String attTeamIdLittle = "";
			for (int j = 0; j < teamsPerGroup; j++) {
				attTeamIdLittle += attTeamArray[i * teamsPerGroup + j] + ",";
			}
			logger.info(attTeamIdLittle);
			logger.info(attTeamIdLittle.split(",").length);
			// 填写下面各个属性的值创建相应的攻击组
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("名称", "attGroupAutoTest" + i);
			jsonObject.addProperty("描述", "This is a test attacker group from javatest");
			String jsonString = jsonObject.toString();
			String args[] = { cookieWithoutCrumb, apiString, jsonString, attTeamIdLittle };
			CloseableHttpResponse response = orgnizationGroup(args);
			logger.info(response);

			// 下面代码是判断条件，可根据需要修改
			int status = -1;
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				status = jObject.get("data").getAsJsonObject().get("status").getAsInt();
			}
			assertEquals(status, 1);
		}

	}

	@Test(groups = "CreateGroupVic", priority = 9)
	private void victimGroup() throws ParseException, IOException, SQLException {
		String apiString = "victimGroup";
		JsonObject jsonObject = new JsonObject();

		// 填写下面各个属性的值创建相应的防守组
		jsonObject.addProperty("名称", "vicGroupAutoTest");
		jsonObject.addProperty("描述", "This is a test victim group from javatest");

		String victimTeamNameString = ""; // 字符串为空时过滤所有存在的攻击队名称
		String vicApiString = "orgnizationListType2";
		String argsVic[] = { cookieWithoutCrumb, vicApiString, victimTeamNameString };
		String victimTeamId = teamNameToid(argsVic);

		String jsonString = jsonObject.toString();
		String args[] = { cookieWithoutCrumb, apiString, jsonString, victimTeamId };
		CloseableHttpResponse response = orgnizationGroup(args);
		logger.info(response);

		// 下面代码是判断条件，可根据需要修改
		int status = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			status = jObject.get("data").getAsJsonObject().get("status").getAsInt();
		}
		assertEquals(status, 1);
	}

	@Test(groups = CreateVicGroupMulti, priority = 9)
	private void victimGroupMulti() throws ParseException, IOException, SQLException {
		String apiString = "victimGroup";
		int teamsPerGroup = 10;

		String victimTeamNameString = ""; // 字符串为空时过滤所有存在的攻击队名称
		String vicApiString = "orgnizationListType2";
		String argsVic[] = { cookieWithoutCrumb, vicApiString, victimTeamNameString };
		String victimTeamId = teamNameToid(argsVic);
		String[] victimTeamArray = victimTeamId.split(",");

		int groups = (int) Math.ceil((double) (victimTeamArray.length / teamsPerGroup));
		for (int i = 0; i < groups; i++) {
			String victimTeamIdLittle = "";
			for (int j = 0; j < teamsPerGroup; j++) {
				victimTeamIdLittle += victimTeamArray[i * teamsPerGroup + j] + ",";
			}
			logger.info(victimTeamIdLittle);
			logger.info(victimTeamIdLittle.split(",").length);
			JsonObject jsonObject = new JsonObject();
			// 填写下面各个属性的值创建相应的防守组
			jsonObject.addProperty("名称", "vicGroupAutoTest" + i);
			jsonObject.addProperty("描述", "This is a test victim group from javatest");

			String jsonString = jsonObject.toString();
			String args[] = { cookieWithoutCrumb, apiString, jsonString, victimTeamIdLittle };
			CloseableHttpResponse response = orgnizationGroup(args);

			// 下面代码是判断条件，可根据需要修改
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				logger.info(jObject.get("data"));
			}
		}

	}

	@Test(groups = CreateGroup, priority = 10)
	private void judgeGroup() throws ParseException, IOException, SQLException {
		String apiString = "judgeGroup";
		JsonObject jsonObject = new JsonObject();

		// 填写下面各个属性的值创建相应的防守组
		jsonObject.addProperty("名称", "judgeGroupAutoTest");
		jsonObject.addProperty("描述", "This is a test judge group from javatest");

		String userNameString = ""; // 字符串为空时过滤所有存在的裁判名称
		String userApiString = "judgeUserList";
		String argsUser[] = { cookieWithoutCrumb, userApiString, userNameString };
		String userId = userNameToid(argsUser);

		String jsonString = jsonObject.toString();
		String args[] = { cookieWithoutCrumb, apiString, jsonString, userId };
		CloseableHttpResponse response = orgnizationGroup(args);
		logger.info(response);

		// 下面代码是判断条件，可根据需要修改
		int status = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			status = jObject.get("data").getAsJsonObject().get("status").getAsInt();
		}
		assertEquals(status, 1);
	}

	@Test(groups = CreateAsset, priority = 11)
	private void assetWithLogo() throws ParseException, IOException, SQLException {
		// 提供name和filepath两个参数，即可创建一个靶标信息
		Random random = new Random();
		String LogoPath = LogoFilePath + random.nextInt(10) + ".jpg";
		int assetNum = 10;
		// 以下代码不需要修改
		for (int i = 0; i < assetNum; i++) {
			String randomIp = Utils.getRandomIp();
			String argsForDefenseId[] = { "1", cookieWithoutCrumb };
			String orgDstId = adminGetDefenseId(argsForDefenseId).split(",")[0].split(":")[0]; // 获取防守单位ID
			String category_parent = String.valueOf(random.nextInt(4) + 1); // 1-4
			String assetIp = randomIp;
			String assetUrl = "https://" + randomIp + "/test/" + i;
			String assetPort = String.valueOf(random.nextInt(100) + 8000);
			String category_logo_type = String.valueOf(random.nextInt(2) + 1);
			String monitor_switch = String.valueOf(random.nextInt(2)); // 0-1
			String display = String.valueOf(random.nextInt(2)); // 0-1
			String categoryName = "asset_" + assetIp;
			String args[] = { cookieWithoutCrumb, orgDstId, category_parent, assetIp, assetUrl, assetPort,
					category_logo_type, monitor_switch, display, categoryName, LogoPath };
			CloseableHttpResponse response = assetBase(args);
			logger.info(response);

			// 下面代码是判断条件，可根据需要修改
			int code = -1;
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				code = jObject.get("code").getAsInt();
			}
			assertNotEquals(code, -1);
		}
	}

	@Test(groups = CreatePolicy, priority = 12)
	private void attackerAssignRuleCreate() throws SocketException, ParseException, IOException, SQLException {
		// 填写下面各个属性的值创建相应的攻击队授权策略
		String policyNameString = "AttackAssignRuleAutoTest";
		String policyDscriptionString = "AttackAssignRuleAutoTest的描述";
		String attackerGroupNameString = "";
		String victimGroupNameString = "";
		// 下面代码不需要变化
		String attApiString = "attackerGroup";
		String vicApiString = "victimGroup";
		String argsAtt[] = { cookieWithoutCrumb, attApiString, attackerGroupNameString };
		String argsVic[] = { cookieWithoutCrumb, vicApiString, victimGroupNameString };
		String attackerGroupId = groupNameToid(argsAtt);
		String victimGroupId = groupNameToid(argsVic);
		int attGroupLength = attackerGroupId.split(",").length;
		int vicGroupLength = victimGroupId.split(",").length;
		logger.info(attackerGroupId);
		logger.info(attGroupLength);
		logger.info(victimGroupId);
		logger.info(vicGroupLength);
		for (int i = 0; i < vicGroupLength; i++) {
			JsonObject jsonObject = new JsonObject();
			CloseableHttpResponse response = null;
			jsonObject.addProperty("策略名称", policyNameString + i);
			jsonObject.addProperty("策略描述", policyDscriptionString);
			if (i <= attGroupLength - 1) {
				jsonObject.addProperty("选择攻击组", attackerGroupId.split(",")[i]);
			} else {
				jsonObject.addProperty("选择攻击组", attackerGroupId.split(",")[attGroupLength - 1]);
			}
			jsonObject.addProperty("选择防守组", victimGroupId.split(",")[i]);
			String jsonString = jsonObject.toString();
			String args[] = { cookieWithoutCrumb, "attackerAssignRuleCreate", jsonString };
			try {
				response = createPolicy(args);
			} catch (SocketException e) {
				logger.info("已经关闭连接");
				e.printStackTrace();
			}
			String message = "";
			if (response != null) {
				JsonObject jObject = null;
				try {
					jObject = Client.getResponseDataJson(response);
					logger.info(jObject.get("data"));
				} catch (SocketException e) {
					logger.info("IOException");
				} catch (IOException e) {
					logger.info("SocketException");
				}
				message = jObject.get("message").getAsString();
			}
			assertEquals(message, "操作成功");
		}

	}

	@Test(groups = CreatePolicy, priority = 13)
	private void judgeAssignRuleCreate() throws SocketException, ParseException, IOException, SQLException {
		// 填写下面各个属性的值创建相应的裁判审核策略
		String policyNameString = "JudgeAssignRuleAutoTest"; // name 策略名称
		String policyDscriptionString = "JudgeAssignRuleAutoTest的描述"; // description 描述
		String policyTypeString = "防守单位"; // type=30 策略类型 固定
		String policyRuleString = "正向策略"; // excepted 策略规则
		String victimTeamNameString = "所有防守单位"; // param 选择防守单位
		String judgeGroup = "judgeGroupAutoTest"; // judge_group_id 选择裁判组
		String assignNum = "1"; // score_num 分配裁判数量
		String judgeNum = "1"; // judge_num 评审裁判数量
		String priority = "80"; // priority 优先级

		// 下面代码不需要变化
		String argsVicTeam[] = { cookieWithoutCrumb, "orgnizationListType2", victimTeamNameString };
		String argsJudgeGroup[] = { cookieWithoutCrumb, "judgeGroupPage0", judgeGroup };
		String victimGroupId = "";
		String type = "";
		String excepted = "";
		String judge_group_id = "";
		if (victimTeamNameString.equals("所有防守单位")) {
			victimGroupId = "0";
		} else {
			victimGroupId = teamNameToid(argsVicTeam);
		}
		if (policyTypeString.equals("防守单位")) {
			type = "30";
		}
		if (policyRuleString.equals("正向策略")) {
			excepted = "0";
		} else if (policyRuleString.equals("反向策略")) {
			excepted = "1";
		}
		if (judgeGroup.equals("全体裁判")) {
			judge_group_id = "1";
		} else {
			judge_group_id = groupNameToid(argsJudgeGroup);
		}
		JsonObject jsonObject = new JsonObject();
		CloseableHttpResponse response = null;
		jsonObject.addProperty("策略名称", policyNameString);
		jsonObject.addProperty("描述", policyDscriptionString);
		jsonObject.addProperty("策略类型", type);
		jsonObject.addProperty("策略规则", excepted);
		jsonObject.addProperty("选择防守单位", victimGroupId);
		jsonObject.addProperty("选择裁判组", judge_group_id);
		jsonObject.addProperty("分配裁判数量", assignNum);
		jsonObject.addProperty("评审裁判数量", judgeNum);
		jsonObject.addProperty("优先级", priority);

		String jsonString = jsonObject.toString();
		String args[] = { cookieWithoutCrumb, "judgeAssignRuleCreate", jsonString };
		try {
			response = createPolicy(args);
		} catch (SocketException e) {
			logger.info("已经关闭连接");
			e.printStackTrace();
		}
		logger.info(response);
		// 下面代码是判断条件，可根据需要修改
		String message = "";
		if (response != null) {
			JsonObject jObject = null;
			try {
				jObject = Client.getResponseDataJson(response);
			} catch (SocketException e) {
				logger.info("IOException");
			} catch (IOException e) {
				logger.info("SocketException");
			}
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功");
	}

	@Test(groups = CreatePolicy, priority = 14)
	private void setTimeConfig() throws SocketException, ParseException, IOException, SQLException {
		// 填写下面各个属性的值配置演习时间
		String startTime = "2021-01-01 00:00:20";
		String endTime = "2022-01-01 00:00:20";

		// 下面代码不需要变化
		JsonObject jsonObject = new JsonObject();
		CloseableHttpResponse response = null;
		jsonObject.addProperty("开始时间", startTime);
		jsonObject.addProperty("结束时间", endTime);

		String jsonString = jsonObject.toString();
		String args[] = { cookieWithoutCrumb, "setTimeConfig", jsonString };
		try {
			response = createPolicy(args);
		} catch (SocketException e) {
			logger.info("已经关闭连接");
			e.printStackTrace();
		}
		logger.info(response);
		// 下面代码是判断条件，可根据需要修改
		String message = "";
		if (response != null) {
			JsonObject jObject = null;
			try {
				jObject = Client.getResponseDataJson(response);
			} catch (SocketException e) {
				logger.info("IOException");
			} catch (IOException e) {
				logger.info("SocketException");
			}
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功");
	}

	@Test(groups = CreatePolicy, priority = 15)
	private void updateRoleModule() throws SocketException, ParseException, IOException, SQLException {
		// 填写下面各个属性配置角色授权策略
		// String attackerGroup = "攻击成绩,攻击申请,提交技战法,非正常防守,资源配置,提交总结,成果复测,0-Day提交";
		// //护网2020
		String attackerGroup = "攻击成绩,攻击申请,提交技战法,发现演习前攻击事件,资源配置,提交总结,成果复测,0-Day提交"; // 护网2021
		String victimGroup = "防守成绩,资产确认,蜜罐信息,提交总结";
		// 下面代码不需要变化
		int attackerModuleNum = attackerGroup.split(",").length;
		int victimModuleNum = victimGroup.split(",").length;
		StringBuilder attackerModules = new StringBuilder();
		StringBuilder victimModules = new StringBuilder();
		for (int i = 0; i < attackerModuleNum; i++) {
			if (attackerGroup.split(",")[i].equals("攻击成绩")) {
				attackerModules.append("MODULE_ATTACKER_REPORT,");
			} else if (attackerGroup.split(",")[i].equals("攻击申请")) {
				attackerModules.append("MODULE_ATTACKER_APPLY,");
			} else if (attackerGroup.split(",")[i].equals("提交技战法")) {
				attackerModules.append("MODULE_ATTACKER_TACTICS,");
			} else if (attackerGroup.split(",")[i].equals("非正常防守")) {
				attackerModules.append("MODULE_ATTACKER_OVER_DEFENSE,");
			} else if (attackerGroup.split(",")[i].equals("资源配置")) {
				attackerModules.append("MODULE_ATTACKER_RESOURCE,");
			} else if (attackerGroup.split(",")[i].equals("提交总结")) {
				attackerModules.append("MODULE_ATTACKER_COMFIRM,");
			} else if (attackerGroup.split(",")[i].equals("成果复测")) {
				attackerModules.append("MODULE_ATTACKER_RETEST,");
			} else if (attackerGroup.split(",")[i].equals("0-Day提交")) {
				attackerModules.append("MODULE_ATTACKER_ZERO_DAY,");
			} else if (attackerGroup.split(",")[i].equals("发现演习前攻击事件")) {
				attackerModules.append("MODULE_ATTACKER_ATT_EVENT,");
			}
		}
		for (int i = 0; i < victimModuleNum; i++) {
			if (victimGroup.split(",")[i].equals("防守成绩")) {
				victimModules.append("MODULE_VICTIM_REPORT,");
			} else if (victimGroup.split(",")[i].equals("资产确认")) {
				victimModules.append("MODULE_VICTIM_GOAL,");
			} else if (victimGroup.split(",")[i].equals("提交总结")) {
				victimModules.append("MODULE_VICTIM_COMFIRM,");
			} else if (victimGroup.split(",")[i].equals("蜜罐信息")) {
				victimModules.append("MODULE_HONEYPOT_INFO,");
			}
		}

		CloseableHttpResponse response = null;
		String args[] = { cookieWithoutCrumb, "updateRoleModule,attacker", attackerModules.toString() };
		try {
			response = createPolicy(args);
		} catch (SocketException e) {
			logger.info("已经关闭连接");
			e.printStackTrace();
		}
		logger.info(response);
		// 下面代码是判断条件，可根据需要修改
		String message = "";
		if (response != null) {
			JsonObject jObject = null;
			try {
				jObject = Client.getResponseDataJson(response);
			} catch (SocketException e) {
				logger.info("IOException");
			} catch (IOException e) {
				logger.info("SocketException");
			}
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功");

		String args1[] = { cookieWithoutCrumb, "updateRoleModule,victim", victimModules.toString() };
		try {
			response = createPolicy(args1);
		} catch (SocketException e) {
			logger.info("已经关闭连接");
			e.printStackTrace();
		}
		logger.info(response);
		// 下面代码是判断条件，可根据需要修改
		if (response != null) {
			JsonObject jObject = null;
			try {
				jObject = Client.getResponseDataJson(response);
			} catch (SocketException e) {
				logger.info("IOException");
			} catch (IOException e) {
				logger.info("SocketException");
			}
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功");
	}

	@Test(groups = CreatePolicy, priority = 16)
	private void accessRuleCreateIn() throws SocketException, ParseException, IOException, SQLException {
		// 填写下面各个属性的值创建相应的准入策略
		String argString = "title:accessRuleAutoTest,obj:0,type:0,priority:4,date:1,net_type:0,start_time:00:00,stop_time:23:59,role[]:0";

		// 下面代码不需要变化
		CloseableHttpResponse response = null;
		String args[] = { cookieWithoutCrumb, "accessRuleCreate", argString };
		try {
			response = createPolicy(args);
		} catch (SocketException e) {
			logger.info("已经关闭连接");
			e.printStackTrace();
		}
		logger.info(response);
		// 下面代码是判断条件，可根据需要修改
		String message = "";
		if (response != null) {
			JsonObject jObject = null;
			try {
				jObject = Client.getResponseDataJson(response);
			} catch (SocketException e) {
				logger.info("IOException");
			} catch (IOException e) {
				logger.info("SocketException");
			}
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功");
	}

	@Test(groups = CreatePolicy, priority = 17)
	private void accessRuleCreateAtt() throws SocketException, ParseException, IOException, SQLException {
		// 填写下面各个属性的值创建相应的准入策略
		String argString = "title:attRuleAutoTest,obj:1,type:0,priority:0,date:1,net_type:0,role[]:1,start_time:00:00,stop_time:23:59,attack_type:2,attack_params[]:1";

		// 下面代码不需要变化
		CloseableHttpResponse response = null;
		String args[] = { cookieWithoutCrumb, "accessRuleCreate", argString };
		try {
			response = createPolicy(args);
		} catch (SocketException e) {
			logger.info("已经关闭连接");
			e.printStackTrace();
		}
		logger.info(response);
		// 下面代码是判断条件，可根据需要修改
		String message = "";
		if (response != null) {
			JsonObject jObject = null;
			try {
				jObject = Client.getResponseDataJson(response);
			} catch (SocketException e) {
				logger.info("IOException");
			} catch (IOException e) {
				logger.info("SocketException");
			}
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功");
	}

	@Test(groups = CreateUser, priority = 18)
	private void createAttackerUser() throws ParseException, IOException, SQLException {
		JsonObject jsonObject = new JsonObject();

		// 填写下面各个属性的值创建相应的攻击方用户
		jsonObject.addProperty("用户名", "attUserJava1");
		jsonObject.addProperty("手机号", "18210003001");
		jsonObject.addProperty("姓名", "attJava1");
		jsonObject.addProperty("密码", "111111");
		jsonObject.addProperty("确认密码", "111111");
		jsonObject.addProperty("角色", "攻击方");
		jsonObject.addProperty("身份证号", "110222199201012345");
		jsonObject.addProperty("攻击队伍", "641");
		jsonObject.addProperty("队伍数据共享", "是");
		jsonObject.addProperty("负责人", "否");
		jsonObject.addProperty("接入方式", "vpn");

		// 下面代码不需要变化
		String jsonString = jsonObject.toString();
		String args[] = { cookieWithoutCrumb, jsonString };
		CloseableHttpResponse response = createUser(args);
		logger.info(response);

		// 下面代码是判断条件，可根据需要修改
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功");
	}

	@Test(groups = CreateUser, priority = 19)
	private void createVictimUser() throws ParseException, IOException, SQLException {
		JsonObject jsonObject = new JsonObject();

		// 填写下面各个属性的值创建相应的防守方用户
		jsonObject.addProperty("用户名", "vicUserJava1");
		jsonObject.addProperty("手机号", "18310003001");
		jsonObject.addProperty("姓名", "vicJava1");
		jsonObject.addProperty("密码", "111111");
		jsonObject.addProperty("确认密码", "111111");
		jsonObject.addProperty("角色", "防守方");
		jsonObject.addProperty("防守队伍", "441");
		jsonObject.addProperty("队伍数据共享", "是");
		jsonObject.addProperty("负责人", "否");

		// 下面代码不需要变化
		String jsonString = jsonObject.toString();
		String args[] = { cookieWithoutCrumb, jsonString };
		CloseableHttpResponse response = createUser(args);
		logger.info(response);

		// 下面代码是判断条件，可根据需要修改
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功");
	}

	@Test(groups = CreateUser, priority = 20)
	private void createExpertUser() throws ParseException, IOException, SQLException {
		JsonObject jsonObject = new JsonObject();

		// 填写下面各个属性的值创建相应的专家用户
		jsonObject.addProperty("用户名", "expertUserJava1");
		jsonObject.addProperty("手机号", "18410003001");
		jsonObject.addProperty("姓名", "expertJava1");
		jsonObject.addProperty("密码", "111111");
		jsonObject.addProperty("确认密码", "111111");
		jsonObject.addProperty("角色", "专家");
		jsonObject.addProperty("接入方式", "vpn");

		// 下面代码不需要变化
		String jsonString = jsonObject.toString();
		String args[] = { cookieWithoutCrumb, jsonString };
		CloseableHttpResponse response = createUser(args);
		logger.info(response);

		// 下面代码是判断条件，可根据需要修改
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功");
	}

	@Test(groups = CreateUser, priority = 21)
	private void createJudgeUser() throws ParseException, IOException, SQLException {
		JsonObject jsonObject = new JsonObject();

		// 填写下面各个属性的值创建相应的裁判用户
		jsonObject.addProperty("用户名", "judgeUserJava1");
		jsonObject.addProperty("手机号", "18510003001");
		jsonObject.addProperty("姓名", "judgeJava1");
		jsonObject.addProperty("密码", "111111");
		jsonObject.addProperty("确认密码", "111111");
		jsonObject.addProperty("角色", "裁判");
		jsonObject.addProperty("裁判组", "3");
		jsonObject.addProperty("接入方式", "vpn");
		jsonObject.addProperty("负责人", "否");

		// 下面代码不需要变化
		String jsonString = jsonObject.toString();
		String args[] = { cookieWithoutCrumb, jsonString };
		CloseableHttpResponse response = createUser(args);
		logger.info(response);

		// 下面代码是判断条件，可根据需要修改
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功");
	}

	@Test(groups = CreateUser, priority = 22)
	private void createAudienceUser() throws ParseException, IOException, SQLException {
		JsonObject jsonObject = new JsonObject();

		// 填写下面各个属性的值创建相应的裁判用户
		jsonObject.addProperty("用户名", "audienceUserJava1");
		jsonObject.addProperty("手机号", "18610003001");
		jsonObject.addProperty("姓名", "audienceJava1");
		jsonObject.addProperty("密码", "111111");
		jsonObject.addProperty("确认密码", "111111");
		jsonObject.addProperty("角色", "主办方");
		jsonObject.addProperty("负责人", "否");

		// 下面代码不需要变化
		String jsonString = jsonObject.toString();
		String args[] = { cookieWithoutCrumb, jsonString };
		CloseableHttpResponse response = createUser(args);
		logger.info(response);

		// 下面代码是判断条件，可根据需要修改
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功");
	}

	@Test(groups = CreateUser, priority = 23)
	private void createVerifierUser() throws ParseException, IOException, SQLException {
		JsonObject jsonObject = new JsonObject();

		// 填写下面各个属性的值创建相应的裁判用户
		jsonObject.addProperty("用户名", "verifierUserJava1");
		jsonObject.addProperty("手机号", "18710003001");
		jsonObject.addProperty("姓名", "verifierJava1");
		jsonObject.addProperty("密码", "111111");
		jsonObject.addProperty("确认密码", "111111");
		jsonObject.addProperty("角色", "武器验证员");
		jsonObject.addProperty("负责人", "否");

		// 下面代码不需要变化
		String jsonString = jsonObject.toString();
		String args[] = { cookieWithoutCrumb, jsonString };
		CloseableHttpResponse response = createUser(args);
		logger.info(response);

		// 下面代码是判断条件，可根据需要修改
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功");
	}

	@Test(groups = CreateUser, priority = 24)
	private void createWatchdogUser() throws SocketException, ParseException, IOException, SQLException {
		JsonObject jsonObject = new JsonObject();
		CloseableHttpResponse response = null;
		// 填写下面各个属性的值创建相应的裁判用户
		jsonObject.addProperty("用户名", "watchdogUserJava1");
		jsonObject.addProperty("手机号", "18810003001");
		jsonObject.addProperty("姓名", "watchdogJava1");
		jsonObject.addProperty("密码", "111111");
		jsonObject.addProperty("确认密码", "111111");
		jsonObject.addProperty("角色", "非正常防守验证员");
		jsonObject.addProperty("负责人", "否");

		// 下面代码不需要变化
		String jsonString = jsonObject.toString();
		String args[] = { cookieWithoutCrumb, jsonString };
		try {
			response = createUser(args);
		} catch (SocketException e) {
			logger.info("已经关闭连接");
			e.printStackTrace();
		}

		logger.info(response);

		// 下面代码是判断条件，可根据需要修改
		String message = "";
		if (response != null) {
			JsonObject jObject = null;
			try {
				jObject = Client.getResponseDataJson(response);
			} catch (SocketException e) {
				logger.info("IOException");
			} catch (IOException e) {
				logger.info("SocketException");
			}
			message = jObject.get("message").getAsString();
			// logger.info(message);
			// String type = jObject.get("type").getAsString();
			// logger.info(type);

		}
		assertEquals(message, "操作成功");
	}

	@Test(groups = DeleteGroup, priority = 25)
	private void deleteJudgeGroup() throws ParseException, IOException, SQLException {
		// 给定要删除组的名称列表
		String nameString = "aaa,bbb";

		// 获取给定的组名所对应的ID
		String apiString = "judgeGroup";
		String args1[] = { cookieWithoutCrumb, apiString, nameString };
		String idString = groupNameToid(args1);
		if (idString.length() != 0) {
			String args[] = { cookieWithoutCrumb, apiString, idString };
			// 执行删除动作
			CloseableHttpResponse response = deleteGroupOrUserOrOgnization(args);
			logger.info(response);

			// 判断用例执行是否成功
			int status = -1;
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				status = jObject.get("data").getAsJsonObject().get("status").getAsInt();
			}
			assertEquals(status, 1);
		} else {
			logger.info("Cases:No groups like " + nameString);
			assertEquals(1, 1);
		}

	}

	@Test(groups = DeleteGroup, priority = 26)
	private void deleteAttGroup() throws ParseException, IOException, SQLException {
		// 给定要删除组的名称列表
		String nameString = "attaaa,attbbb,attccc";

		// 获取给定的组名所对应的ID
		String apiString = "attackerGroup";
		String args1[] = { cookieWithoutCrumb, apiString, nameString };
		String idString = groupNameToid(args1);
		if (idString.length() != 0) {
			String args[] = { cookieWithoutCrumb, apiString, idString };
			// 执行删除动作
			CloseableHttpResponse response = deleteGroupOrUserOrOgnization(args);
			logger.info(response);

			// 判断用例执行是否成功
			int status = -1;
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				status = jObject.get("data").getAsJsonObject().get("status").getAsInt();
			}
			assertEquals(status, 1);
		} else {
			logger.info("Cases:No groups like " + nameString);
			assertEquals(1, 1);
		}
	}

	@Test(groups = DeleteGroup, priority = 27)
	private void deleteVicGroup() throws ParseException, IOException, SQLException {
		// 给定要删除组的名称列表
		String nameString = "vicaaa,vicbbb,vicxxx";

		// 获取给定的组名所对应的ID
		String apiString = "victimGroup";
		String args1[] = { cookieWithoutCrumb, apiString, nameString };
		String idString = groupNameToid(args1);
		if (idString.length() != 0) {
			String args[] = { cookieWithoutCrumb, apiString, idString };
			// 执行删除动作
			CloseableHttpResponse response = deleteGroupOrUserOrOgnization(args);
			logger.info(response);

			// 判断用例执行是否成功
			int status = -1;
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				status = jObject.get("data").getAsJsonObject().get("status").getAsInt();
			}
			assertEquals(status, 1);
		} else {
			logger.info("Cases:No groups like " + nameString);
			assertEquals(1, 1);
		}
	}

	@Test(groups = DeleteTeam, priority = 28)
	private void deleteVicTeam() throws ParseException, IOException, SQLException {
		// 给定要删除组的名称列表
		String nameString = "aaa,bbb,xxx";

		// 获取给定的组名所对应的ID
		String getApiString = "orgnizationListType2";
		String deleteApiString = "orgnization";
		String args1[] = { cookieWithoutCrumb, getApiString, nameString };
		String idString = teamNameToid(args1);
		if (idString.length() != 0) {
			String args[] = { cookieWithoutCrumb, deleteApiString, idString };
			// 执行删除动作
			CloseableHttpResponse response = deleteGroupOrUserOrOgnization(args);

			// 判断用例执行是否成功
			int status = -1;
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				status = jObject.get("data").getAsJsonObject().get("status").getAsInt();
			}
			assertEquals(status, 1);
		} else {
			logger.info("Cases:No team like " + nameString);
			assertEquals(1, 1);
		}
	}

	@Test(groups = DeleteTeam, priority = 29)
	private void deleteAttTeam() throws ParseException, IOException, SQLException {
		// 给定要删除组的名称列表
		String nameString = "aaa,bbb,xxx";

		// 获取给定的组名所对应的ID
		String getApiString = "orgnizationListType1";
		String deleteApiString = "orgnization";
		String args1[] = { cookieWithoutCrumb, getApiString, nameString };
		String idString = teamNameToid(args1);
		if (idString.length() != 0) {
			String args[] = { cookieWithoutCrumb, deleteApiString, idString };
			// 执行删除动作
			CloseableHttpResponse response = deleteGroupOrUserOrOgnization(args);

			// 判断用例执行是否成功
			int status = -1;
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				status = jObject.get("data").getAsJsonObject().get("status").getAsInt();
			}
			assertEquals(status, 1);
		} else {
			logger.info("Cases:No team like " + nameString);
			assertEquals(1, 1);
		}
	}

	@Test(groups = DeleteUser, priority = 30)
	private void deleteUser() throws ParseException, IOException, SQLException {
		// 给定要删除组的名称列表
		String nameString = "audienceUserJava1,expertUserJava1,judgeUserJava1,verifierUserJava1,vicUserJava1,watchdogUserJava1,attUserJava1";

		// 获取给定的组名所对应的ID
		String getApiString = "userList";
		String deleteApiString = "deleteUsers";
		String args1[] = { cookieWithoutCrumb, getApiString, nameString };
		String idString = userNameToid(args1);

		if (idString.length() != 0) {
			String args[] = { cookieWithoutCrumb, deleteApiString, idString };
			// 执行删除动作
			CloseableHttpResponse response = deleteGroupOrUserOrOgnization(args);

			// 判断用例执行是否成功
			String message = "";
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				message = jObject.get("message").getAsString();
			}
			assertEquals(message, "操作成功");
		} else {
			logger.info("Cases:No team like " + nameString);
			assertEquals(1, 1);
		}
	}

	@Test(groups = AddOutIp, priority = 31)
	private void outIpAdd() throws SocketException, ParseException, IOException, SQLException {
		// 填写下面各个属性的值创建相应的准入策略
		StringBuilder ipBuilder = new StringBuilder();
		for (int i = 0; i < 300; i++) {
			String ip = Utils.getRandomIp();
			ipBuilder.append("ips[]:" + ip + ",");
		}
		String argString = ipBuilder.toString();
		argString = argString.substring(0, argString.length() - 1);
		// 下面代码不需要变化
		CloseableHttpResponse response = null;
		String args[] = { cookieWithoutCrumb, "outIpAdd", argString };
		try {
			response = outIp(args);
		} catch (SocketException e) {
			logger.info("已经关闭连接");
			e.printStackTrace();
		}
		logger.info(response);
		// 下面代码是判断条件，可根据需要修改
		String message = "";
		if (response != null) {
			JsonObject jObject = null;
			try {
				jObject = Client.getResponseDataJson(response);
			} catch (SocketException e) {
				logger.info("IOException");
			} catch (IOException e) {
				logger.info("SocketException");
			}
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功");
	}

	@Test(groups = ConfigOutIp, priority = 32)
	private void outIpConfig() throws SocketException, ParseException, IOException, SQLException {
		// 填写下面三个属性的值给攻击队分配ip数量
		String nameString = "";
		String num1 = "1";
		String num2 = "2";

		// 下面代码不需要变化
		// 获取给定的组名所对应的ID
		String getApiString = "orgnizationListType1";
		String args1[] = { cookieWithoutCrumb, getApiString, nameString };
		String idString = teamNameToid(args1);
		int idsLength = idString.split(",").length;
		int addCounts = 1;
		if (idsLength > 10) {
			addCounts = (int) Math.ceil((double) (idsLength / 10));
		}
		for (int j = 0; j < addCounts; j++) {
			StringBuilder argString = new StringBuilder();
			for (int i = 0; i < 10; i++) {
				if (i + (j * 10) <= idsLength) {
					argString.append("org_ids[]:" + idString.split(",")[i + (j * 10)] + ",");
				}
			}
			argString.append("number:" + num1 + ",max_apply_cnt:" + num2);
			CloseableHttpResponse response = null;
			String args[] = { cookieWithoutCrumb, "outIpOrgnizationConfig", argString.toString() };
			try {
				response = outIp(args);
			} catch (SocketException e) {
				logger.info("已经关闭连接");
				e.printStackTrace();
			}
			// 下面代码是判断条件，可根据需要修改
			String message = "";
			if (response != null) {
				JsonObject jObject = null;
				try {
					jObject = Client.getResponseDataJson(response);
				} catch (SocketException e) {
					logger.info("IOException");
				} catch (IOException e) {
					logger.info("SocketException");
				}
				message = jObject.get("message").getAsString();
			}
			assertEquals(message, "操作成功");
		}
	}

	@Test(groups = UiLevel1_2, priority = 200)
	private void configGetConsolePage() throws ParseException, IOException {
		String apiString = "configGetConsolePage";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "configGetConsolePage Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 201)
	private void attackerOrgnization() throws ParseException, IOException {
		String apiString = "attackerOrgnization";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int attackerOrgnizationNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			attackerOrgnizationNum = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(attackerOrgnizationNum, -1, "attackerOrgnization Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 202)
	private void victimOrgnization() throws ParseException, IOException {
		String apiString = "victimOrgnization";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int victimOrgnizationNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			victimOrgnizationNum = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(victimOrgnizationNum, -1, "victimOrgnization Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 203)
	private void attackerGroupGet() throws ParseException, IOException {
		String apiString = "attackerGroupGet";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int attackerGroupNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			attackerGroupNum = jObject.get("data").getAsJsonObject().get("total").getAsInt();
		}
		assertNotEquals(attackerGroupNum, -1, "attackerGroupGet Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 204)
	private void orgnizationAssetsOrgType1() throws ParseException, IOException {
		String apiString = "orgnizationAssetsOrgType1";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int orgnizationAssetsOrgType1Num = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			orgnizationAssetsOrgType1Num = jObject.get("data").getAsJsonObject().get("total_num").getAsInt();
		}
		assertNotEquals(orgnizationAssetsOrgType1Num, -1, "orgnizationAssetsOrgType1 Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 205)
	private void assetOrgType2() throws ParseException, IOException {
		String apiString = "assetOrgType2";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int assetOrgType2Num = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			assetOrgType2Num = jObject.get("data").getAsJsonObject().get("total_num").getAsInt();
		}
		assertNotEquals(assetOrgType2Num, -1, "assetOrgType2 Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 206)
	private void templateAdminAttackerAssets() throws ParseException, IOException {
		String apiString = "templateAdminAttackerAssets";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String templateAdminAttackerAssetsList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			templateAdminAttackerAssetsList = jObject.get("data").toString();
		}
		assertNotNull(templateAdminAttackerAssetsList, "templateAdminAttackerAssets Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 207)
	private void templateAdminVictimsAssets() throws ParseException, IOException {
		String apiString = "templateAdminVictimsAssets";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String templateAdminVictimsAssetsList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			templateAdminVictimsAssetsList = jObject.get("data").toString();
		}
		assertNotNull(templateAdminVictimsAssetsList, "templateAdminVictimsAssets Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 208)
	private void templateVictims() throws ParseException, IOException {
		String apiString = "templateVictims";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String templateVictimsList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			templateVictimsList = jObject.get("data").toString();
		}
		assertNotNull(templateVictimsList, "templateVictims Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 209)
	private void templateAttackers() throws ParseException, IOException {
		String apiString = "templateAttackers";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String templateAttackersList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			templateAttackersList = jObject.get("data").toString();
		}
		assertNotNull(templateAttackersList, "templateAttackers Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 210)
	private void victimOgnizationList() throws ParseException, IOException {
		String apiString = "victimOgnizationList";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int vicOrgnizationNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			vicOrgnizationNum = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(vicOrgnizationNum, -1, "victimOgnizationList Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 211)
	private void orgnizationPropertyType2() throws ParseException, IOException {
		String apiString = "orgnizationPropertyType2";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int orgnizationPropertyType2Num = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			orgnizationPropertyType2Num = jObject.get("data").getAsJsonObject().get("property").getAsJsonArray().size();
		}
		assertNotEquals(orgnizationPropertyType2Num, -1, "orgnizationPropertyType2 Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 212)
	private void orgnizationPropertyType1() throws ParseException, IOException {
		String apiString = "orgnizationPropertyType1";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int orgnizationPropertyType1Num = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			orgnizationPropertyType1Num = jObject.get("data").getAsJsonObject().get("property").getAsJsonArray().size();
		}
		assertNotEquals(orgnizationPropertyType1Num, -1, "orgnizationPropertyType1 Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 213)
	private void orgnizationListType1() throws ParseException, IOException {
		String apiString = "orgnizationListType1";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int orgnizationListType1Num = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			orgnizationListType1Num = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(orgnizationListType1Num, -1, "orgnizationListType1 Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 214)
	private void orgnizationListType2() throws ParseException, IOException {
		String apiString = "orgnizationListType2";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int orgnizationListType2Num = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			orgnizationListType2Num = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(orgnizationListType2Num, -1, "orgnizationListType2 Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 215)
	private void assetCategory() throws ParseException, IOException {
		String apiString = "assetCategory";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int assetCategoryNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			assetCategoryNum = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(assetCategoryNum, -1, "assetCategory Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 216)
	private void assetCategoryResource() throws ParseException, IOException {
		String apiString = "assetCategoryResource";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int assetCategoryResourceNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			assetCategoryResourceNum = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(assetCategoryResourceNum, -1, "assetCategoryResource Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 217)
	private void victimGroupGet() throws ParseException, IOException {
		String apiString = "victimGroupGet";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int victimGroupNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			victimGroupNum = jObject.get("data").getAsJsonObject().get("total").getAsInt();
		}
		assertNotEquals(victimGroupNum, -1, "victimGroupGet Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 218)
	private void victimGroupList() throws ParseException, IOException {
		String apiString = "victimGroupList";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int victimGroupListNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			victimGroupListNum = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(victimGroupListNum, -1, "victimGroupList Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 219)
	private void outIpList() throws ParseException, IOException {
		String apiString = "outIpList";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "outIpList Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 220)
	private void attackerGroupList() throws ParseException, IOException {
		String apiString = "attackerGroupList";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int attackerGroupListNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			attackerGroupListNum = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(attackerGroupListNum, -1, "attackerGroupList Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 221)
	private void departmentResource() throws ParseException, IOException {
		String apiString = "departmentResource";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int departmentResourceNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			departmentResourceNum = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(departmentResourceNum, -1, "departmentResource Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 222)
	private void departmentList() throws ParseException, IOException {
		String apiString = "departmentList";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int departmentListNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			departmentListNum = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(departmentListNum, -1, "departmentList Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 223)
	private void judgeGroupPage1() throws ParseException, IOException {
		String apiString = "judgeGroupPage1";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int judgeGroupNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			judgeGroupNum = jObject.get("data").getAsJsonObject().get("list").getAsJsonArray().size();
		}
		assertNotEquals(judgeGroupNum, -1, "judgeGroupPage1 Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 224)
	private void judgeGroupPage0() throws ParseException, IOException {
		String apiString = "judgeGroupPage0";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int judgeGroupNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			judgeGroupNum = jObject.get("data").getAsJsonObject().get("list").getAsJsonArray().size();
		}
		assertNotEquals(judgeGroupNum, -1, "judgeGroupPage0 Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 225)
	private void allJudge() throws ParseException, IOException {
		String apiString = "allJudge";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String judgeGroupList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			judgeGroupList = jObject.get("message").toString();
		}
		assertNotEquals(judgeGroupList, "操作成功", "allJudge Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 226)
	private void userList() throws ParseException, IOException {
		String apiString = "userList";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int userListNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			userListNum = jObject.get("data").getAsJsonObject().get("total_num").getAsInt();
		}
		assertNotEquals(userListNum, -1, "userList Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 227)
	private void assinRuleTypes() throws ParseException, IOException {
		String apiString = "assinRuleTypes";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int assinRuleTypesNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			assinRuleTypesNum = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(assinRuleTypesNum, -1, "assinRuleTypes Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 228)
	private void judgeAssinRule() throws ParseException, IOException {
		String apiString = "judgeAssinRule";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int judgeAssinRuleNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			judgeAssinRuleNum = jObject.get("data").getAsJsonObject().get("total").getAsInt();
		}
		assertNotEquals(judgeAssinRuleNum, -1, "judgeAssinRule Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 229)
	private void attackAssinRule() throws ParseException, IOException {
		String apiString = "attackAssinRule";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int attackAssinRuleNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			attackAssinRuleNum = jObject.get("data").getAsJsonObject().get("total").getAsInt();
		}
		assertNotEquals(attackAssinRuleNum, -1, "attackAssinRule Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 230)
	private void getRoleModuleAttacker() throws ParseException, IOException {
		String apiString = "getRoleModuleAttacker";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int getRoleModuleAttackerNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			getRoleModuleAttackerNum = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(getRoleModuleAttackerNum, -1, "getRoleModuleAttacker Successfully.");
	}

	@Test(groups = { UiLevel1_2 }, priority = 231)
	private void getRoleModuleVictim() throws ParseException, IOException {
		String apiString = "getRoleModuleVictim";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int getRoleModuleVictimNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			getRoleModuleVictimNum = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(getRoleModuleVictimNum, -1, "getRoleModuleVictim Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 232)
	private void accessRules() throws ParseException, IOException {
		String apiString = "accessRules";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int accessrulenum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			accessrulenum = jObject.get("data").getAsJsonObject().get("total").getAsInt();
		}
		assertNotEquals(accessrulenum, -1, "accessRules Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 233)
	private void outIpAssigned() throws ParseException, IOException {
		String apiString = "outIpAssigned";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int outIpAssignedNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			outIpAssignedNum = jObject.get("data").getAsJsonObject().get("total_num").getAsInt();
		}
		assertNotEquals(outIpAssignedNum, -1, "outIpAssigned Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 234)
	private void outIpNotAssigned() throws ParseException, IOException {
		String apiString = "outIpNotAssigned";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int outIpNotAssignedNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			outIpNotAssignedNum = jObject.get("data").getAsJsonObject().get("total_num").getAsInt();
		}
		assertNotEquals(outIpNotAssignedNum, -1, "outIpNotAssigned Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 235)
	private void outIpOrgnizationConfigGet() throws ParseException, IOException {
		String apiString = "outIpOrgnizationConfigGet";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			message = jObject.get("message").getAsString();
		}
		assertNotEquals(message, "\"操作成功\"", "outIpOrgnizationConfigGet Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 236)
	private void outIp() throws ParseException, IOException {
		String apiString = "outIp";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int outIpnum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			outIpnum = jObject.get("data").getAsJsonObject().get("total_num").getAsInt();
		}
		assertNotEquals(outIpnum, -1, "outIp Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 237)
	private void vpsOrgList() throws ParseException, IOException {
		String apiString = "vpsOrgList";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int vpsOrgNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			vpsOrgNum = jObject.get("data").getAsJsonObject().get("count").getAsInt();
		}
		assertNotEquals(vpsOrgNum, -1, "vpsOrgList Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 238)
	private void vpsForAdmin() throws ParseException, IOException {
		String apiString = "vpsForAdmin";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int vpsForAdmin = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			vpsForAdmin = jObject.get("data").getAsJsonObject().get("count").getAsInt();
		}
		assertNotEquals(vpsForAdmin, -1, "vpsForAdmin Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 239)
	private void springBoard() throws ParseException, IOException {
		String apiString = "springBoard";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int springBoardNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			springBoardNum = jObject.get("data").getAsJsonObject().get("total_num").getAsInt();
		}
		assertNotEquals(springBoardNum, -1, "springBoard Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 240)
	private void springBoardConfig() throws ParseException, IOException {
		String apiString = "springBoardConfig";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int springBoardConfigNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			springBoardConfigNum = jObject.get("data").getAsJsonObject().get("total_num").getAsInt();
		}
		assertNotEquals(springBoardConfigNum, -1, "springBoardConfig Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 241)
	private void springBoardIp() throws ParseException, IOException {
		String apiString = "springBoardIp";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int springBoardIpNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			springBoardIpNum = jObject.get("data").getAsJsonObject().get("total_num").getAsInt();
		}
		assertNotEquals(springBoardIpNum, -1, "springBoardIp Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 242)
	private void threatManagement() throws ParseException, IOException {
		String apiString = "threatManagement";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int threatManagementNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			threatManagementNum = jObject.get("data").getAsJsonObject().get("total").getAsInt();
		}
		assertNotEquals(threatManagementNum, -1, "threatManagement Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 243)
	private void threatManagementFilter() throws ParseException, IOException {
		String apiString = "threatManagementFilter";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int threatManagementFilterNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			threatManagementFilterNum = jObject.get("data").getAsJsonObject().get("attack_type").getAsJsonArray()
					.size();
		}
		assertNotEquals(threatManagementFilterNum, -1, "threatManagementFilter Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 244)
	private void threatManagementUpload() throws ParseException, IOException {
		String apiString = "threatManagementUpload";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int threatManagementUploadNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			threatManagementUploadNum = jObject.get("data").getAsJsonObject().get("total").getAsInt();
		}
		assertNotEquals(threatManagementUploadNum, -1, "threatManagementUpload Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 245)
	private void attackType() throws ParseException, IOException {
		String apiString = "attackType";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String threatManagementUploadList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			threatManagementUploadList = jObject.get("data").toString();
		}
		assertNotNull(threatManagementUploadList, "attackType Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 246)
	private void attackReport() throws ParseException, IOException {
		String apiString = "attackReport";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int attackReportNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			attackReportNum = jObject.get("data").getAsJsonObject().get("total_num").getAsInt();
		}
		assertNotEquals(attackReportNum, -1, "attackReport Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 247)
	private void victimReport() throws ParseException, IOException {
		String apiString = "victimReport";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int victimReportNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			victimReportNum = jObject.get("data").getAsJsonObject().get("total_num").getAsInt();
		}
		assertNotEquals(victimReportNum, -1, "victimReport Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 248)
	private void retestReport() throws ParseException, IOException {
		String apiString = "retestReport";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int retestReportNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			retestReportNum = jObject.get("data").getAsJsonObject().get("total").getAsInt();
		}
		assertNotEquals(retestReportNum, -1, "retestReport Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 249)
	private void statementTemplateType1() throws ParseException, IOException {
		String apiString = "statementTemplateType1";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int statementTemplateType1Num = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			statementTemplateType1Num = jObject.get("data").getAsJsonObject().get("count").getAsInt();
		}
		assertNotEquals(statementTemplateType1Num, -1, "statementTemplateType1 Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 250)
	private void statementBaseSupportType1() throws ParseException, IOException {
		String apiString = "statementBaseSupportType1";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String statementBaseSupportType1List = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			statementBaseSupportType1List = jObject.get("data").toString();
		}
		assertNotNull(statementBaseSupportType1List, "statementBaseSupportType1 Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 251)
	private void exportReportType1() throws ParseException, IOException {
		String apiString = "exportReportType1";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int exportReportType1Num = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			exportReportType1Num = jObject.get("data").getAsJsonObject().get("count").getAsInt();
		}
		assertNotEquals(exportReportType1Num, -1, "exportReportType1 Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 252)
	private void statementType1() throws ParseException, IOException {
		String apiString = "statementType1";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int statementType1Num = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			statementType1Num = jObject.get("data").getAsJsonObject().get("count").getAsInt();
		}
		assertNotEquals(statementType1Num, -1, "statementType1 Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 253)
	private void statementTemplateType2() throws ParseException, IOException {
		String apiString = "statementTemplateType2";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int statementTemplateType2Num = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			statementTemplateType2Num = jObject.get("data").getAsJsonObject().get("count").getAsInt();
		}
		assertNotEquals(statementTemplateType2Num, -1, "statementTemplateType2 Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 254)
	private void statementBaseSupportType2() throws ParseException, IOException {
		String apiString = "statementBaseSupportType2";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String statementBaseSupportType2List = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			statementBaseSupportType2List = jObject.get("data").toString();
		}
		assertNotNull(statementBaseSupportType2List, "statementBaseSupportType2 Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 255)
	private void exportReportType2() throws ParseException, IOException {
		String apiString = "exportReportType2";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int exportReportType2Num = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			exportReportType2Num = jObject.get("data").getAsJsonObject().get("count").getAsInt();
		}
		assertNotEquals(exportReportType2Num, -1, "exportReportType2 Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 256)
	private void statementType2() throws ParseException, IOException {
		String apiString = "statementType2";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int statementType2Num = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			statementType2Num = jObject.get("data").getAsJsonObject().get("count").getAsInt();
		}
		assertNotEquals(statementType2Num, -1, "statementType2 Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 257)
	private void screenConfig() throws ParseException, IOException {
		String apiString = "screenConfig";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String screenConfigList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			screenConfigList = jObject.get("data").toString();
		}
		assertNotNull(screenConfigList, "screenConfig Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 258)
	private void screenPage() throws ParseException, IOException {
		String apiString = "screenPage";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String screenPageList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			screenPageList = jObject.get("data").toString();
		}
		assertNotNull(screenPageList, "screenPage Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 259)
	private void portal() throws ParseException, IOException {
		String apiString = "portal";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String portalList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			portalList = jObject.get("data").toString();
		}
		assertNotNull(portalList, "portal Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 260)
	private void exerciseScaleConfig() throws ParseException, IOException {
		String apiString = "exerciseScaleConfig";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String exerciseScaleConfigList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			exerciseScaleConfigList = jObject.get("data").toString();
		}
		assertNotNull(exerciseScaleConfigList, "exerciseScaleConfig Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 261)
	private void commandConfig() throws ParseException, IOException {
		String apiString = "commandConfig";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String commandConfigList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			commandConfigList = jObject.get("data").toString();
		}
		assertNotNull(commandConfigList, "commandConfig Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 262)
	private void techAdmin() throws ParseException, IOException {
		String apiString = "techAdmin";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int techAdminNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			techAdminNum = jObject.get("data").getAsJsonObject().get("total").getAsInt();
		}
		assertNotEquals(techAdminNum, -1, "techAdmin Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 263)
	private void attackEventV2() throws ParseException, IOException {
		String apiString = "attackEventV2";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int attackEventV2Num = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			attackEventV2Num = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(attackEventV2Num, -1, "attackEventV2 Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 264)
	private void battleOverviewAttention() throws ParseException, IOException {
		String apiString = "battleOverviewAttention";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int battleOverviewAttentionNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			battleOverviewAttentionNum = jObject.get("data").getAsJsonObject().get("config").getAsInt();
		}
		assertNotEquals(battleOverviewAttentionNum, -1, "battleOverviewAttention Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 265)
	private void orgAttackerAudit() throws ParseException, IOException {
		String apiString = "orgAttackerAudit";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int orgAttackerAuditNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			orgAttackerAuditNum = jObject.get("data").getAsJsonObject().get("total").getAsInt();
		}
		assertNotEquals(orgAttackerAuditNum, -1, "orgAttackerAudit Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 266)
	private void orgDefenderAudit() throws ParseException, IOException {
		String apiString = "orgDefenderAudit";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int orgDefenderAuditNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			orgDefenderAuditNum = jObject.get("data").getAsJsonObject().get("total").getAsInt();
		}
		assertNotEquals(orgDefenderAuditNum, -1, "orgDefenderAudit Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 267)
	private void orgAttackerAuditTypes() throws ParseException, IOException {
		String apiString = "orgAttackerAuditTypes";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int orgAttackerAuditTypesNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			orgAttackerAuditTypesNum = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(orgAttackerAuditTypesNum, -1, "orgAttackerAuditTypes Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 268)
	private void orgAttackerAuditSources() throws ParseException, IOException {
		String apiString = "orgAttackerAuditSources";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String orgAttackerAuditSourcesList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			orgAttackerAuditSourcesList = jObject.get("data").toString();
		}
		assertNotNull(orgAttackerAuditSourcesList, "orgAttackerAuditSources Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 269)
	private void orgAttackerAuditDisposes() throws ParseException, IOException {
		String apiString = "orgAttackerAuditDisposes";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String orgAttackerAuditDisposesList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			orgAttackerAuditDisposesList = jObject.get("data").toString();
		}
		assertNotNull(orgAttackerAuditDisposesList, "orgAttackerAuditDisposes Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 270)
	private void orgAttackerAuditLevels() throws ParseException, IOException {
		String apiString = "orgAttackerAuditLevels";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String orgAttackerAuditLevelsList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			orgAttackerAuditLevelsList = jObject.get("data").toString();
		}
		assertNotNull(orgAttackerAuditLevelsList, "orgAttackerAuditLevels Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 271)
	private void orgDefenseInfoAdmin() throws ParseException, IOException {
		String apiString = "orgDefenseInfoAdmin";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String orgDefenseInfoAdminList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			orgDefenseInfoAdminList = jObject.get("data").toString();
		}
		assertNotNull(orgDefenseInfoAdminList, "orgDefenseInfoAdmin Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 272)
	private void orgDefenseInfoAdminReview() throws ParseException, IOException {
		String apiString = "orgDefenseInfoAdminReview";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int orgDefenseInfoAdminReviewNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			orgDefenseInfoAdminReviewNum = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(orgDefenseInfoAdminReviewNum, -1, "orgDefenseInfoAdminReview Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 273)
	private void logsMethodListType1() throws ParseException, IOException {
		String apiString = "logsMethodListType1";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String logsMethodListType1List = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logsMethodListType1List = jObject.get("data").toString();
		}
		assertNotNull(logsMethodListType1List, "logsMethodListType1 Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 274)
	private void logsMethodListType2() throws ParseException, IOException {
		String apiString = "logsMethodListType2";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String logsMethodListType2List = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logsMethodListType2List = jObject.get("data").toString();
		}
		assertNotNull(logsMethodListType2List, "logsMethodListType2 Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 275)
	private void logsUser() throws ParseException, IOException {
		String apiString = "logsUser";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int logsUserNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logsUserNum = jObject.get("data").getAsJsonObject().get("total").getAsInt();
		}
		assertNotEquals(logsUserNum, -1, "logsUser Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 276)
	private void logsSys() throws ParseException, IOException {
		String apiString = "logsSys";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int logsSysNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logsSysNum = jObject.get("data").getAsJsonObject().get("total").getAsInt();
		}
		assertNotEquals(logsSysNum, -1, "logsSys Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 277)
	private void getBaseConfig() throws ParseException, IOException {
		String apiString = "getBaseConfig";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String getBaseConfigList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			getBaseConfigList = jObject.get("data").toString();
		}
		assertNotNull(getBaseConfigList, "getBaseConfig Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 278)
	private void getIps() throws ParseException, IOException {
		String apiString = "getIps";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int getIpsNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			getIpsNum = jObject.get("data").getAsJsonObject().get("total_num").getAsInt();
		}
		assertNotEquals(getIpsNum, -1, "getIps Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 279)
	private void getUserConfig() throws ParseException, IOException {
		String apiString = "getUserConfig";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String getUserConfigList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			getUserConfigList = jObject.get("data").toString();
		}
		assertNotNull(getUserConfigList, "getUserConfig Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 280)
	private void getTimeConfig() throws ParseException, IOException {
		String apiString = "getTimeConfig";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String getTimeConfigList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			getTimeConfigList = jObject.get("data").toString();
		}
		assertNotNull(getTimeConfigList, "getTimeConfig Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 281)
	private void getLocationSetting() throws ParseException, IOException {
		String apiString = "getLocationSetting";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String getLocationSettingList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			getLocationSettingList = jObject.get("message").toString();
		}
		assertEquals(getLocationSettingList, "\"操作成功\"", "getTimeConfig Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 282)
	private void monitor() throws ParseException, IOException {
		String apiString = "monitor";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int monitorNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			monitorNum = jObject.get("data").getAsJsonObject().get("total_num").getAsInt();
		}
		assertNotEquals(monitorNum, -1, "monitor Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 283)
	private void tag() throws ParseException, IOException {
		String apiString = "tag";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String tagList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			tagList = jObject.get("data").toString();
		}
		assertNotNull(tagList, "tag Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 284)
	private void monitoring() throws ParseException, IOException {
		String apiString = "monitoring";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		String monitoringList = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			monitoringList = jObject.get("data").toString();
		}
		assertNotNull(monitoringList, "monitoring Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 285)
	private void bulletin() throws ParseException, IOException {
		String apiString = "bulletin";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int bulletinNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			bulletinNum = jObject.get("data").getAsJsonArray().size();
		}
		assertNotEquals(bulletinNum, -1, "bulletin Successfully.");
	}

	@Test(groups = UiLevel1_2, priority = 286)
	private void message() throws ParseException, IOException {
		String apiString = "message";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = adminUiBase(argString);
		int messageNum = -1;
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			messageNum = jObject.get("data").getAsJsonObject().get("total").getAsInt();
		}
		assertNotEquals(messageNum, -1, "message Successfully.");
	}
}
