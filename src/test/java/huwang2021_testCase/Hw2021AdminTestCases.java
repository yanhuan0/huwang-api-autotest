package huwang2021_testCase;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;

import baseApi.CalculateResponseTime;
import baseApi.Client;
import huwang2020_testCase.HwAdminTestCases;

public class Hw2021AdminTestCases extends HwAdminTestCases {

	private static String TableName = "Hw2021AdminTestCases";
	private static Logger logger = Logger.getLogger(Hw2021AdminTestCases.class);
	private String currentPath = getClass().getResource("../").getPath().toString().replace("%20", " ").substring(1);
	private String projectPath = currentPath.split("target")[0];
	private String userFile = projectPath + "configHw2021/admin.txt";
	private String apiFile = projectPath + "configHw2021/adminapi.txt";
	private String LogoFilePath = projectPath + "configHw2021/image/";
	private static int ATTACKERSIZE = 40;
	private static String ATTGROUPIMPORTFILE;
	private static String VICGROUPIMPORTFILE;
	private static String USERIMPORTFILE;
	public static final String ImportUser = "ImportUser";
	static {
		CalculateResponseTime.tableName = TableName;

		if (ATTACKERSIZE == 10) {
			ATTGROUPIMPORTFILE = "gjdw-10-hw2020or2021.xlsx";
			VICGROUPIMPORTFILE = "fsdw-10-hw2020or2021.xlsx";
			USERIMPORTFILE = "yhjs-10-hw2021.xlsx";
		} else if (ATTACKERSIZE == 40) {
			ATTGROUPIMPORTFILE = "gjdw-40-hw2020or2021.xlsx";
			VICGROUPIMPORTFILE = "fsdw-50-hw2020or2021.xlsx";
			USERIMPORTFILE = "yhjs-gjf40-hw2021-with-fsf50.xlsx";
		} else if (ATTACKERSIZE == 240) {
			ATTGROUPIMPORTFILE = "gjdw-240-hw2020or2021.xlsx";
			VICGROUPIMPORTFILE = "fsdw-400-hw2020or2021.xlsx";
			USERIMPORTFILE = "yhjs-gjf240-hw2021-with-fsf400.xlsx";
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

	@Test
	private void editUserList() throws ParseException, IOException, SQLException {
		String[] roleString = { "attacker", "victim", "judge", "expert", "audience" };
		int[] roleNum = new int[roleString.length];
		for (int i = 0; i < roleString.length; i++) {
			String[] argStrings = { roleString[i], cookieWithoutCrumb };
			roleNum[i] = editUserList(argStrings);
		}
		for (int i = 0; i < roleNum.length; i++) {
			logger.info("Edited " + roleString[i] + " : " + roleNum[i]);
		}
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
			Thread.sleep(90000);
		} catch (InterruptedException e) {
			e.printStackTrace();
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
}
