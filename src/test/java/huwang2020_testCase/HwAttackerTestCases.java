package huwang2020_testCase;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
import huwang2020_base.HwAttackerBase;

public class HwAttackerTestCases extends HwAttackerBase {
	private static Logger logger = Logger.getLogger(HwAttackerTestCases.class);
	private static String TableName = "HwAttackerTestCases";

	private String currentPath = getClass().getResource("../").getPath().toString().replace("%20", " ").substring(1);
	private String projectPath = currentPath.split("target")[0];
	private String userFile = projectPath + "config/attacker.txt";
	private String apiFile = projectPath + "config/attackerapi.txt";
	private String LogoFilePath = projectPath + "config/image/";
	private String multiUserFile = projectPath + "config/attacker_user.txt";
	private String ZipFilePath = projectPath + "config/gly_litao.zip";
	private String wordFilePath = projectPath + "config/测试.docx";
	private String cookieWithoutCrumb = "";

	public static final String AttackerApi = "AttackerApi";
	public static final String AttackerCreateReports = "AttackerCreateReports";
	public static final String MultiAttackerCreateReports = "MultiAttackerCreateReports";
	public static final String AttackerCreateApplication = "AttackerCreateApplication";
	public static final String ZeroDayAttackerUpload = "ZeroDayAttackerUpload";
	public static final String ZeroDayAttackerCreate = "ZeroDayAttackerCreate";
	public static final String TechAttackCreate = "TechAttackCreate";
	public static final String OverDefenseInfoAttack = "OverDefenseInfoAttack";
	public static final String AttackerUi = "AttackerUi";
	public static final String InitTest = "InitTest";
	public static final String wordUpload = "wordUpload";
	public static final String EditPassword = "EditPassword";

	@BeforeSuite(groups = InitTest)
	public void initUser() throws SQLException {
		initConfig(userFile, apiFile);
		cookieWithoutCrumb = userLogin();
	}

	@AfterSuite(groups = InitTest)
	private void attackerLogout() {
		userLogout(cookieWithoutCrumb);
	}

	static {
		CalculateResponseTime.tableName = TableName;
		try {
			CalculateResponseTime.createTable(TableName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};

	@Test(groups = { AttackerApi, AttackerCreateReports }, priority = 1)
	public void attackerReportsCreate() throws ParseException, IOException, SQLException {
		int ReportsNum = 1;
		Random random = new Random();
		String LogoPath = LogoFilePath + random.nextInt(10) + ".jpg";
		for (int i = 0; i < ReportsNum; i++) {
			int LinkNodesNum = 1 + random.nextInt(5);
			String[] argsStrings = { String.valueOf(LinkNodesNum), LogoPath, cookieWithoutCrumb };
			CloseableHttpResponse response = attackerReport(argsStrings);
			String message = "";
			logger.info(response);
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				logger.info(jObject.toString());
				message = jObject.get("message").getAsString();
			}
			assertEquals(message, "操作成功", "attackerReportJournals Successfully.");
		}
	}

	@Test(groups = { MultiAttackerCreateReports }, priority = 2)
	public void createReports() throws IOException, ParseException, SQLException {
		FileReader multiUserFileReader = new FileReader(multiUserFile);
		@SuppressWarnings("resource")
		BufferedReader multiUserInfo = new BufferedReader(multiUserFileReader);
		String userString = "";
		while ((userString = multiUserInfo.readLine()) != null) {
			String[] argStrings = { userFile, userString };
			Utils.Relpace(argStrings);
			attackerLogout();
			initUser();
			int ReportsNum = 5;
			Random random = new Random();
			String LogoPath = LogoFilePath + random.nextInt(10) + ".jpg";
			for (int i = 0; i < ReportsNum; i++) {
				int LinkNodesNum = 1 + random.nextInt(5);
				String[] argsStrings = { String.valueOf(LinkNodesNum), LogoPath, cookieWithoutCrumb };
				CloseableHttpResponse response = attackerReport(argsStrings);
				String message = "";
				logger.info(response);
				if (response != null) {
					JsonObject jObject = Client.getResponseDataJson(response);
					logger.info(jObject.toString());
					message = jObject.get("message").getAsString();
				}
				assertEquals(message, "操作成功", "attackerReportJournals Successfully.");
			}
		}
	}

	@Test(groups = { AttackerApi, AttackerCreateApplication }, priority = 3)
	private void attackerApplicationCreate() throws ParseException, IOException, SQLException {
		int ApplicationNum = 2;
		Random random = new Random();
		String LogoPath = LogoFilePath + random.nextInt(10) + ".jpg";
		for (int i = 0; i < ApplicationNum; i++) {
			String[] argsStrings = { LogoPath, cookieWithoutCrumb };
			CloseableHttpResponse response = applicationCreate(argsStrings);
			String message = "";
			logger.info(response);
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				logger.info(jObject.toString());
				message = jObject.get("message").getAsString();
			}
			assertEquals(message, "操作成功", "attackerReportJournals Successfully.");
		}
	}

	@Test(groups = { AttackerApi, ZeroDayAttackerUpload }, priority = 4)
	private void zeroDayAttackerUpload() throws ParseException, IOException, SQLException {
		int zeroDayNum = 1;
		String FilePath = ZipFilePath;
		for (int i = 0; i < zeroDayNum; i++) {
			String[] argsStrings = { FilePath, cookieWithoutCrumb };
			CloseableHttpResponse response = zeroDayAttackerUpload(argsStrings);
			String message = "";
			logger.info(response);
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				logger.info(jObject.toString());
				message = jObject.get("message").getAsString();
			}
			assertEquals(message, "操作成功", "zeroDayAttackerUpload Successfully.");
		}
	}

	@Test(groups = { AttackerApi, ZeroDayAttackerCreate }, priority = 5)
	private void zeroDayAttackerCreate() throws ParseException, IOException, SQLException {
		int zeroDayNum = 3;
		String FilePath = ZipFilePath;
		for (int i = 0; i < zeroDayNum; i++) {
			String[] uploadStrings = { FilePath, cookieWithoutCrumb };
			CloseableHttpResponse uploadResponse = zeroDayAttackerUpload(uploadStrings);
			String path = "";
			String name = "";
			if (uploadResponse != null) {
				JsonObject jUploadObject = Client.getResponseDataJson(uploadResponse);
				path = jUploadObject.get("data").getAsJsonObject().get("path").getAsString();
				name = jUploadObject.get("data").getAsJsonObject().get("name").getAsString();
				String[] createStrings = { path, name, cookieWithoutCrumb };
				CloseableHttpResponse createResponse = zeroDayAttackerCreate(createStrings);
				String message = "";
				if (createResponse != null) {
					JsonObject jObject = Client.getResponseDataJson(createResponse);
					logger.info(jObject.toString());
					message = jObject.get("message").getAsString();
					assertEquals(message, "操作成功", "zeroDayAttackerCreate Successfully.");
				} else {
					assertEquals("0", "1", "zeroDayAttackerCreate Failed,create no response.");
				}
			} else {
				assertEquals("0", "1", "zeroDayAttackerCreate Failed,upload images no response.");
			}
		}
	}

	@Test(groups = { AttackerApi, TechAttackCreate }, priority = 6)
	private void techAttackCreate() throws ParseException, IOException, SQLException {
		String[] argStrings = { cookieWithoutCrumb };
		CloseableHttpResponse response = techAttack(argStrings);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
			assertEquals(message, "操作成功", "zeroDayAttackerUpload Successfully.");
		} else {
			assertEquals("1", "1", "zeroDayAttackerUpload Successfully.");
		}
	}

	@Test(groups = { AttackerApi, OverDefenseInfoAttack }, priority = 7)
	private void overDefenseInfoAttack() throws ParseException, IOException, SQLException {
		int overDfenseNum = 3;
		Random random = new Random();
		String LogoPath = LogoFilePath + random.nextInt(10) + ".jpg";
		for (int i = 0; i < overDfenseNum; i++) {
			String[] uploadStrings = { LogoPath, "over_defense", cookieWithoutCrumb };
			String[] uploadResult = uploadFile(uploadStrings);
			String url = uploadResult[0];
			String name = uploadResult[1];
			String[] createStrings = { url, name, cookieWithoutCrumb };
			CloseableHttpResponse createResponse = overDefenseInfoAttack(createStrings);
			String message = "";
			if (createResponse != null) {
				JsonObject jObject = Client.getResponseDataJson(createResponse);
				logger.info(jObject.toString());
				message = jObject.get("message").getAsString();
				assertEquals(message, "操作成功", "zeroDayAttackerCreate Successfully.");
			} else {
				assertEquals("1", "1", "zeroDayAttackerCreate Successfully.");
			}
		}
	}

	@Test(groups = { AttackerApi, wordUpload }, priority = 8)
	private void wordUpload() throws ParseException, IOException, SQLException {
		int zeroDayNum = 1;
		String FilePath = wordFilePath;
		for (int i = 0; i < zeroDayNum; i++) {
			String[] argsStrings = { FilePath, cookieWithoutCrumb };
			CloseableHttpResponse response = wordUpload(argsStrings);
			String message = "";
			logger.info(response);
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				logger.info(jObject.toString());
				message = jObject.get("message").getAsString();
			}
			assertEquals(message, "操作成功", "word upload Successfully.");
		}
	}

	@Test(groups = "test1", priority = 100)
	private void adminapiReportJournals() throws ParseException, IOException {
		String apiString = "adminapiReportJournals";
		String[] argString = { cookieWithoutCrumb, apiString };
		System.out.println(cookieWithoutCrumb);
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "attackerReportJournals Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 101)
	private void newReportFilter() throws ParseException, IOException {
		String apiString = "newReportFilter";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "newReportFilter Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 102)
	private void adminapiNew() throws ParseException, IOException {
		String apiString = "adminapiNew";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiNew Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 103)
	private void adminapiReportMarkType() throws ParseException, IOException {
		String apiString = "adminapiReportMarkType";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 104)
	private void adminapiUnreadList() throws ParseException, IOException {
		String apiString = "adminapiUnreadList";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiUnreadList Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 105)
	private void adminapiGetinfo() throws ParseException, IOException {
		String apiString = "adminapiGetinfo";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiGetinfo Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 106)
	private void adminapiGetGlobalConfig() throws ParseException, IOException {
		String apiString = "adminapiGetGlobalConfig";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiGetGlobalConfig Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 107)
	private void adminapiDepartmentList() throws ParseException, IOException {
		String apiString = "adminapiDepartmentList";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiDepartmentList Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 108)
	private void adminapiConfigGetLocationSetting() throws ParseException, IOException {
		String apiString = "adminapiConfigGetLocationSetting";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiConfigGetLocationSetting Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 109)
	private void adminapiThreatTypeSearchType1() throws ParseException, IOException {
		String apiString = "adminapiThreatTypeSearchType1";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiThreatTypeSearchType1 Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 110)
	private void adminapiReportTasks() throws ParseException, IOException {
		String apiString = "adminapiReportTasks";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportTasks Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 111)
	private void adminapiReportLogType() throws ParseException, IOException {
		String apiString = "adminapiReportLogType";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportLogType Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 112)
	private void adminapiOrgnizatinOutIpsAttacker() throws ParseException, IOException {
		String apiString = "adminapiOrgnizatinOutIpsAttacker";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiOrgnizatinOutIpsAttacker Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 113)
	private void adminapiNetWebTime() throws ParseException, IOException {
		String apiString = "adminapiNetWebTime";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiNetWebTime Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 114)
	private void adminapiAttackApplication() throws ParseException, IOException {
		String apiString = "adminapiAttackApplication";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiAttackApplication Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 115)
	private void adminapiZeroDayAttackerIndex() throws ParseException, IOException {
		String apiString = "adminapiZeroDayAttackerIndex";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiZeroDayAttackerIndex Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 116)
	private void adminapiTechAttack() throws ParseException, IOException {
		String apiString = "adminapiTechAttack";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiTechAttack Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 117)
	private void adminapiOverDefenseInfoAttack() throws ParseException, IOException {
		String apiString = "adminapiOverDefenseInfoAttack";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiOverDefenseInfoAttack Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 118)
	private void adminapiNetAttackTime() throws ParseException, IOException {
		String apiString = "adminapiNetAttackTime";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiNetAttackTime Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 119)
	private void adminapiLogsAttackerIp() throws ParseException, IOException {
		String apiString = "adminapiLogsAttackerIp";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiLogsAttackerIp Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 120)
	private void adminapiVpsForAttack() throws ParseException, IOException {
		String apiString = "adminapiVpsForAttack";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiVpsForAttack Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 121)
	private void adminapiSpringBoardIp() throws ParseException, IOException {
		String apiString = "adminapiSpringBoardIp";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiSpringBoardIp Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 122)
	private void adminapiSpringBoardList() throws ParseException, IOException {
		String apiString = "adminapiSpringBoardList";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiSpringBoardList Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 123)
	private void adminapiSummaryReportAttack() throws ParseException, IOException {
		String apiString = "adminapiSummaryReportAttack";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiSummaryReportAttack Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 124)
	private void adminapiDefenseAssetHistory() throws ParseException, IOException {
		String apiString = "adminapiDefenseAssetHistory";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiDefenseAssetHistory Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 125)
	private void adminapiDefenseAsset() throws ParseException, IOException {
		String apiString = "adminapiDefenseAsset";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiDefenseAsset Successfully.");
	}

	@Test(groups = { AttackerApi, AttackerUi }, priority = 126)
	private void adminapiRetestList() throws ParseException, IOException {
		String apiString = "adminapiRetestList";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = attackerUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiRetestList Successfully.");
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
}
