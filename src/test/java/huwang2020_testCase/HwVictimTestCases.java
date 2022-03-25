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
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;

import baseApi.Client;
import baseApi.Utils;
import huwang2020_base.HwVictimBase;

public class HwVictimTestCases {
	private Logger logger = Logger.getLogger(HwVictimTestCases.class);
	private String currentPath = getClass().getResource("../").getPath().toString().replace("%20", " ").substring(1);
	private String projectPath = currentPath.split("target")[0];
	private String userFile = projectPath + "config/victim.txt";
	private String apiFile = projectPath + "config/victimapi.txt";
	private String LogoFilePath = projectPath + "config/image/";
	private String multiUserFile = projectPath + "config/victim_user.txt";

	private static String cookieWithoutCrumb = "";
	private static final String VictimCreateReports = "VictimCreateReports";
	private static final String MultiVictimCreateReports = "MultiVictimCreateReports";
	private static final String VictimUi = "VictimUi";
	private static final String VictimApi = "VictimApi";
	private static final String InitTest = "InitTest";

	@BeforeSuite(groups = InitTest)
	public void initUser() throws SQLException {
		HwVictimBase.initConfig(userFile, apiFile);
	}

	@BeforeTest(groups = InitTest)
	public void victimLogin() {
		cookieWithoutCrumb = HwVictimBase.userLogin();
	}

	@AfterTest(groups = InitTest)
	public void victimLogout() {
		HwVictimBase.userLogout(cookieWithoutCrumb);
	}

	@Test(groups = { VictimCreateReports }, priority = 1)
	private void victimCreateReports() throws ParseException, IOException, SQLException {
		int ReportsNum = 5;
		Random random = new Random();
		String LogoPath = LogoFilePath + random.nextInt(10) + ".jpg";
		for (int i = 0; i < ReportsNum; i++) {
			int LinkNodesNum = 1 + random.nextInt(5);
			String[] argsStrings = { String.valueOf(LinkNodesNum), LogoPath, cookieWithoutCrumb };
			CloseableHttpResponse response = HwVictimBase.victimReport(argsStrings);
			String message = "";
			String data = "";
			logger.info(response);
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				logger.info(jObject.toString());
				message = jObject.get("message").getAsString();
				assertEquals(message, "操作成功", "attackerReportJournals Successfully.");
				data = jObject.get("data").getAsString();
				String[] argData = { data, cookieWithoutCrumb };
				String taskMessage = HwVictimBase.getReportTask(argData);
				assertEquals(taskMessage, "操作成功", "attackerReportTask存储 Successfully.");
			} else {
				assertEquals("0", "1", "attackerReportJournals Failed,create report no response.");
			}
		}
	}

	@Test(groups = { MultiVictimCreateReports }, priority = 2)
	public void multiVictimCreateReports() throws IOException, ParseException, SQLException {
		FileReader multiUserFileReader = new FileReader(multiUserFile);
		@SuppressWarnings("resource")
		BufferedReader multiUserInfo = new BufferedReader(multiUserFileReader);
		String userString = "";
		while ((userString = multiUserInfo.readLine()) != null) {
			String[] argStrings = { userFile, userString };
			Utils.Relpace(argStrings);
			victimLogout();
			initUser();
			victimLogin();
			int ReportsNum = 5;
			Random random = new Random();
			String LogoPath = LogoFilePath + random.nextInt(10) + ".jpg";
			for (int i = 0; i < ReportsNum; i++) {
				int LinkNodesNum = 1 + random.nextInt(5);
				String[] argsStrings = { String.valueOf(LinkNodesNum), LogoPath, cookieWithoutCrumb };
				CloseableHttpResponse response = HwVictimBase.victimReport(argsStrings);
				String message = "";
				String data = "";
				logger.info(response);
				if (response != null) {
					JsonObject jObject = Client.getResponseDataJson(response);
					logger.info(jObject.toString());
					message = jObject.get("message").getAsString();
					assertEquals(message, "操作成功", "attackerReportJournals Successfully.");
					data = jObject.get("data").getAsString();
					String[] argData = { data, cookieWithoutCrumb };
					String taskMessage = HwVictimBase.getReportTask(argData);
					assertEquals(taskMessage, "操作成功", "attackerReportTask存储 Successfully.");
				} else {
					assertEquals("0", "1", "attackerReportJournals Failed,create report no response.");
				}
			}

		}
	}

	@Test(groups = { VictimUi, VictimApi }, priority = 101)
	private void adminapiReportMark() throws ParseException, IOException {
		String apiString = "adminapiReportMark";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = HwVictimBase.victimUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
			assertEquals(message, "操作成功", "adminapiReportMark Successfully.");
		} else {
			assertEquals("0", "1", "adminapiReportMark Failed,no response.");
		}

	}

	@Test(groups = { VictimUi, VictimApi }, priority = 102)
	private void adminapiNew() throws ParseException, IOException {
		String apiString = "adminapiNew";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = HwVictimBase.victimUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
			assertEquals(message, "操作成功", "adminapiNew Successfully.");
		} else {
			assertEquals("0", "1", "adminapiNew Failed,no response.");
		}
	}

	@Test(groups = { VictimUi, VictimApi }, priority = 103)
	private void adminapiNewReportFilter() throws ParseException, IOException {
		String apiString = "adminapiNewReportFilter";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = HwVictimBase.victimUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
			assertEquals(message, "操作成功", "adminapiNewReportFilter Successfully.");
		} else {
			assertEquals("0", "1", "adminapiNewReportFilter Failed,no response.");
		}

	}

	@Test(groups = { VictimUi, VictimApi }, priority = 104)
	private void adminapiThreatTypeSearchType2() throws ParseException, IOException {
		String apiString = "adminapiThreatTypeSearchType2";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = HwVictimBase.victimUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
			assertEquals(message, "操作成功", "adminapiThreatTypeSearchType2 Successfully.");
		} else {
			assertEquals("0", "1", "adminapiThreatTypeSearchType2 Failed,no response.");
		}
	}

	@Test(groups = { VictimUi, VictimApi }, priority = 105)
	private void adminapiUnreadList() throws ParseException, IOException {
		String apiString = "adminapiUnreadList";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = HwVictimBase.victimUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
			assertEquals(message, "操作成功", "adminapiUnreadList Successfully.");
		} else {
			assertEquals("0", "1", "adminapiUnreadList Failed,no response.");
		}
	}

	@Test(groups = { VictimUi, VictimApi }, priority = 106)
	private void adminapiGetInfo() throws ParseException, IOException {
		String apiString = "adminapiGetInfo";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = HwVictimBase.victimUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
			assertEquals(message, "操作成功", "adminapiGetInfo Successfully.");
		} else {
			assertEquals("0", "1", "adminapiGetInfo Failed,no response.");
		}
	}

	@Test(groups = { VictimUi, VictimApi }, priority = 107)
	private void adminapiGetGlobalConfig() throws ParseException, IOException {
		String apiString = "adminapiGetGlobalConfig";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = HwVictimBase.victimUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
			assertEquals(message, "操作成功", "adminapiGetGlobalConfig Successfully.");
		} else {
			assertEquals("0", "1", "adminapiGetGlobalConfig Failed,no response.");
		}
	}

	@Test(groups = { VictimUi, VictimApi }, priority = 108)
	private void adminapiDepartmentList() throws ParseException, IOException {
		String apiString = "adminapiDepartmentList";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = HwVictimBase.victimUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
			assertEquals(message, "操作成功", "adminapiDepartmentList Successfully.");
		} else {
			assertEquals("0", "1", "adminapiDepartmentList Failed,no response.");
		}

	}

	@Test(groups = { VictimUi, VictimApi }, priority = 109)
	private void adminapiConfigGetLocationSetting() throws ParseException, IOException {
		String apiString = "adminapiConfigGetLocationSetting";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = HwVictimBase.victimUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
			assertEquals(message, "操作成功", "adminapiConfigGetLocationSetting Successfully.");
		} else {
			assertEquals("0", "1", "adminapiConfigGetLocationSetting Failed,no response.");
		}
	}

	@Test(groups = { VictimUi, VictimApi }, priority = 110)
	private void adminapiReportTasks() throws ParseException, IOException {
		String apiString = "adminapiReportTasks";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = HwVictimBase.victimUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
			assertEquals(message, "操作成功", "adminapiReportTasks Successfully.");
		} else {
			assertEquals("0", "1", "adminapiReportTasks Failed,no response.");
		}
	}

	@Test(groups = { VictimUi, VictimApi }, priority = 111)
	private void adminapiReportJournals() throws ParseException, IOException {
		String apiString = "adminapiReportJournals";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = HwVictimBase.victimUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
			assertEquals(message, "操作成功", "adminapiReportJournals Successfully.");
		} else {
			assertEquals("0", "1", "adminapiReportJournals Failed,no response.");
		}
	}

	@Test(groups = { VictimUi, VictimApi }, priority = 112)
	private void adminapiReportLogType() throws ParseException, IOException {
		String apiString = "adminapiReportLogType";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = HwVictimBase.victimUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
			assertEquals(message, "操作成功", "adminapiReportLogType Successfully.");
		} else {
			assertEquals("0", "1", "adminapiReportLogType Failed,no response.");
		}
	}

	@Test(groups = { VictimUi, VictimApi }, priority = 113)
	private void adminapiNetWebTime() throws ParseException, IOException {
		String apiString = "adminapiNetWebTime";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = HwVictimBase.victimUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
			assertEquals(message, "操作成功", "adminapiNetWebTime Successfully.");
		} else {
			assertEquals("0", "1", "adminapiNetWebTime Failed,no response.");
		}
	}

	@Test(groups = { VictimUi, VictimApi }, priority = 114)
	private void adminapiBulletin() throws ParseException, IOException {
		String apiString = "adminapiBulletin";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = HwVictimBase.victimUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
			assertEquals(message, "操作成功", "adminapiBulletin Successfully.");
		} else {
			assertEquals("0", "1", "adminapiBulletin Failed,no response.");
		}
	}

	@Test(groups = { VictimUi, VictimApi }, priority = 115)
	private void adminapiAssetConfirm() throws ParseException, IOException {
		String apiString = "adminapiAssetConfirm";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = HwVictimBase.victimUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
			assertEquals(message, "操作成功", "adminapiAssetConfirm Successfully.");
		} else {
			assertEquals("0", "1", "adminapiAssetConfirm Failed,no response.");
		}
	}

	@Test(groups = { VictimUi, VictimApi }, priority = 116)
	private void adminapiSummaryReport() throws ParseException, IOException {
		String apiString = "adminapiSummaryReport";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = HwVictimBase.victimUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
			assertEquals(message, "操作成功", "adminapiSummaryReport Successfully.");
		} else {
			assertEquals("0", "1", "adminapiSummaryReport Failed,no response.");
		}
	}
}
