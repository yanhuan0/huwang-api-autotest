package huwang2020_testCase;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;

import baseApi.Client;
import huwang2021_base.Hw2021JudgeBase;

public class HwJudgeTestCases extends Hw2021JudgeBase {
	private Logger logger = Logger.getLogger(HwJudgeTestCases.class);
	private String currentPath = getClass().getResource("../").getPath().toString().replace("%20", " ").substring(1);
	private String projectPath = currentPath.split("target")[0];
	private String userFile = projectPath + "config/judge.txt";
	private String apiFile = projectPath + "config/judgeapi.txt";

	private String cookieWithoutCrumb = "";
	public final String InitTest = "InitTest";
	public final String JudgeAttacker = "JudgeAttacker";
	public final String JudgeVictim = "JudgeVictim";
	public final String JudgeUi = "JudgeUi";

	@BeforeSuite(groups = InitTest)
	public void initUser() throws SQLException {
		initConfig(userFile, apiFile);
		cookieWithoutCrumb = userLogin();
	}

	@AfterSuite(groups = InitTest)
	public void userLogout() {
		userLogout(cookieWithoutCrumb);
	}

	@Test(groups = JudgeAttacker)
	private void judgeAttacker() throws ParseException, IOException, SQLException {
		String role = "attacker";
		int attackerReportNum = 0;
		String[] argString = { role, cookieWithoutCrumb };
		List<String> attackerReportList = getReportId(argString);
		attackerReportNum = attackerReportList.size();
		logger.info(attackerReportNum);
		for (int i = 0; i < attackerReportNum; i++) {
			String reportId = attackerReportList.get(i);
			logger.info(reportId);
			String[] args = { reportId, cookieWithoutCrumb };
			JsonObject jsonObject = getReportNode(args);
			String[] argsJudgeReport = { reportId, jsonObject.toString(), cookieWithoutCrumb };
			CloseableHttpResponse judgeResponse = JudgeReport(argsJudgeReport);
			if (judgeResponse != null) {
				JsonObject judgeObject = Client.getResponseDataJson(judgeResponse);
				logger.info(judgeObject);
			}
		}
	}

	@Test(groups = JudgeVictim)
	private void judgeVictim() throws ParseException, IOException, SQLException {
		String role = "victim";
		int judgeReportNum = 0;
		String[] argString = { role, cookieWithoutCrumb };
		List<String> judgeReportList = getReportId(argString);
		judgeReportNum = judgeReportList.size();
		logger.info(judgeReportNum);
		for (int i = 0; i < judgeReportNum; i++) {
			String reportId = judgeReportList.get(i);
			logger.info(reportId);
			String[] args = { reportId, cookieWithoutCrumb };
			JsonObject jsonObject = getReportNode(args);
			String[] argsJudgeReport = { reportId, jsonObject.toString(), cookieWithoutCrumb };
			CloseableHttpResponse judgeResponse = JudgeReport(argsJudgeReport);
			if (judgeResponse != null) {
				JsonObject judgeObject = Client.getResponseDataJson(judgeResponse);
				logger.info(judgeObject);
			}
		}
	}

	@Test(groups = { JudgeUi }, priority = 1)
	private void adminapiAllJudge() throws ParseException, IOException {
		String apiString = "adminapiAllJudge";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			// logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 2)
	private void adminapiThreatType() throws ParseException, IOException {
		String apiString = "adminapiThreatType";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 3)
	private void adminapiNew() throws ParseException, IOException {
		String apiString = "adminapiNew";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 4)
	private void adminapiBulletin() throws ParseException, IOException {
		String apiString = "adminapiBulletin";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 5)
	private void adminapiUnreadList() throws ParseException, IOException {
		String apiString = "adminapiUnreadList";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 6)
	private void adminapiAssetCategoryHeader() throws ParseException, IOException {
		String apiString = "adminapiAssetCategoryHeader";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 7)
	private void adminapiTagCustomize() throws ParseException, IOException {
		String apiString = "adminapiTagCustomize";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 8)
	private void adminapiFormConfigType1() throws ParseException, IOException {
		String apiString = "adminapiFormConfigType1";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 9)
	private void adminapiReportMarkType() throws ParseException, IOException {
		String apiString = "adminapiReportMarkType";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 10)
	private void adminapiThreatTypeSearchType1() throws ParseException, IOException {
		String apiString = "adminapiThreatTypeSearchType1";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 11)
	private void adminapiThreatTypeSearchType2() throws ParseException, IOException {
		String apiString = "adminapiThreatTypeSearchType2";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 12)
	private void adminapiNewReportFilter() throws ParseException, IOException {
		String apiString = "adminapiNewReportFilter";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 13)
	private void adminapiReportLogType() throws ParseException, IOException {
		String apiString = "adminapiReportLogType";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 14)
	private void adminapiHoneyReportRecheckJudgeGetAll() throws ParseException, IOException {
		String apiString = "adminapiHoneyReportRecheckJudgeGetAll";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 15)
	private void adminapiGetInfo() throws ParseException, IOException {
		String apiString = "adminapiGetInfo";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 16)
	private void adminapiGetGlobalConfig() throws ParseException, IOException {
		String apiString = "adminapiGetGlobalConfig";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 17)
	private void adminapiDepartmentList() throws ParseException, IOException {
		String apiString = "adminapiDepartmentList";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 18)
	private void adminapiConfigGetLocationSetting() throws ParseException, IOException {
		String apiString = "adminapiConfigGetLocationSetting";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 19)
	private void adminapiNetWebTime() throws ParseException, IOException {
		String apiString = "adminapiNetWebTime";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}

	@Test(groups = { JudgeUi }, priority = 20)
	private void adminapiAttackerReportJudgeStatus2() throws ParseException, IOException {
		String apiString = "adminapiAttackerReportJudgeStatus2";
		String[] argString = { cookieWithoutCrumb, apiString };
		CloseableHttpResponse response = judgeUiBase(argString);
		String message = "";
		if (response != null) {
			JsonObject jObject = Client.getResponseDataJson(response);
			logger.info(jObject.toString());
			message = jObject.get("message").getAsString();
		}
		assertEquals(message, "操作成功", "adminapiReportMarkType Successfully.");
	}
}
