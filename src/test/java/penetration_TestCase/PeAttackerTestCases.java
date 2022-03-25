package penetration_TestCase;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;

import baseApi.Client;
import baseApi.RelpaceLine;

import penetration_baseApi.PeAttackerBase;

public class PeAttackerTestCases {
	private String currentPath = getClass().getResource("../").getPath().toString().replace("%20", " ").substring(1);
	private String projectPath = currentPath.split("target")[0];
	private String userFile = projectPath+"penetrationConfig/attacker.txt";
	private String apiFile = projectPath+"penetrationConfig/attackerapi.txt";
	private static final String InitTest = "InitTest";
	private String LogoFilePath = projectPath+"penetrationConfig/image/";
	private String multiUserFile = projectPath+"penetrationConfig/attacker_user.txt";
	private static final String MultiAttackerCreateReports = "MultiAttackerCreateReports";
	private static final String GetProjectId = "GetProjectId";
	private static final String Retest = "Retest";
	private static final String MultiRetest = "MultiRetest";
	
	private static String cookieWithoutCrumb = "";
	
	@BeforeSuite (groups = InitTest)
	public void initUser() {
		PeAttackerBase.initConfig(userFile,apiFile);
	}

	@BeforeTest (groups = InitTest)
	public void attackerLogin() {
		cookieWithoutCrumb = PeAttackerBase.userLogin();
	}
	
	@AfterSuite (groups = InitTest)
	public void attackerLogout() {
		PeAttackerBase.userLogout(cookieWithoutCrumb);
	}
	
	@Test (groups = {MultiAttackerCreateReports},priority = 1)
	public void createReports() throws IOException {
		FileReader multiUserFileReader = new FileReader(multiUserFile);
		@SuppressWarnings("resource")
		BufferedReader multiUserInfo = new BufferedReader(multiUserFileReader);
		String userString = "";
		while ((userString = multiUserInfo.readLine()) != null) {
			String[] argStrings = {userFile,userString};
			RelpaceLine.Relpace(argStrings);
			attackerLogout();
			initUser();
			attackerLogin();
			int ReportsNum = 10;
			Random random = new Random();
			String LogoPath = LogoFilePath+random.nextInt(10)+".jpg";
			System.out.println("Login user successfully.");
			for (int i = 0; i < ReportsNum; i++) {
				String[] argsStrings = {LogoPath,cookieWithoutCrumb};
				CloseableHttpResponse response = PeAttackerBase.attackerReport(argsStrings);
				String message = "";
				System.out.println(response);

		        if (response != null) {
		        	JsonObject jObject = Client.getResponseDataJson(response);
		        	System.out.println(jObject.toString());
		        	message = jObject.get("message").getAsString();
		        }
				assertEquals(message, "操作成功","attackerReportJournals Successfully.");

			}
		}
	}
	
	@Test (groups = {GetProjectId},priority = 100)
	private void getUserList() throws ParseException, IOException {

		String args[] = {cookieWithoutCrumb,"active-project"};

		String projectLiString = PeAttackerBase.getActiveProject(args);

		System.out.println("projectID: "+projectLiString);

	}
	
	@Test (groups = Retest)
	private void retestAttacker() throws ParseException, IOException {
		String apiRetestIndexString = "retest_index";
		String apiRetestString = "retest";
		int attackerReportNum = 0;
		String[] argString = {apiRetestIndexString,cookieWithoutCrumb};
		List<String> attackerReportList = PeAttackerBase.getReportId(argString);
		attackerReportNum = attackerReportList.size();
		System.out.println(attackerReportNum);
		for (int i = 0; i < attackerReportNum; i++) {
			String reportId = attackerReportList.get(i);
			System.out.println(reportId);
			String[] args = {apiRetestString,cookieWithoutCrumb,reportId};
			CloseableHttpResponse retestResponse = PeAttackerBase.retest(args);
			String message = "";
			if (retestResponse != null) {
				JsonObject retestObject = Client.getResponseDataJson(retestResponse);
				message = retestObject.get("message").getAsString();
			}
			assertEquals(message, "操作成功","attackerReportJournals Successfully.");
		}
	}
	
	@Test (groups = {MultiRetest},priority = 1)
	public void multiRetest() throws IOException {
		FileReader multiUserFileReader = new FileReader(multiUserFile);
		@SuppressWarnings("resource")
		BufferedReader multiUserInfo = new BufferedReader(multiUserFileReader);
		String userString = "";
		while ((userString = multiUserInfo.readLine()) != null) {
			String[] argStrings = {userFile,userString};
			RelpaceLine.Relpace(argStrings);
			attackerLogout();
			initUser();
			attackerLogin();
			retestAttacker();
		}
	}
}
