package huwang2021_testCase;

import java.io.IOException;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import baseApi.CalculateResponseTime;

public class Hw2021AttackerParallelTest extends Hw2021AttackerTestCases {
	private String TableName = "Hw2021AttackerParallelTest";
	private static Logger logger = Logger.getLogger(Hw2021AttackerParallelTest.class);

	private String currentPath = getClass().getResource("../").getPath().toString().replace("%20", " ").substring(1);
	private String projectPath = currentPath.split("target")[0];
	private String userFile = projectPath + "configHw2021/attacker.txt";
	private String apiFile = projectPath + "configHw2021/attackerapi.txt";
	private String cookieWithoutCrumb = "";

	public Hw2021AttackerParallelTest(String file) {
		userFile = file;
	}

	public void initUser() {
		initConfig(userFile, apiFile);
		this.cookieWithoutCrumb = userLogin();
	}

	private void attackerLogout() {
		userLogout(cookieWithoutCrumb);
	}

	private void autoRun() throws SocketException, IOException, SQLException {
		createAttackerReportNode(cookieWithoutCrumb);
		createAttackerReport(cookieWithoutCrumb);
		attackerLogout();
	}

	@Test(groups = "ParallelReportAttackerTest")
	private void runUser() throws SocketException, IOException, SQLException {
		initUser();
		long id = Thread.currentThread().getId();
		logger.debug("username: " + getUsername() + "\tthreadId: " + id);
		autoRun();
	}

	@Factory
	public Object[] create() throws SQLException {
		int count = 30;
		CalculateResponseTime.tableName = TableName;
		CalculateResponseTime.createTable(TableName);
		List<Hw2021AttackerParallelTest> list = new ArrayList<Hw2021AttackerParallelTest>();
		for (int i = 0; i < count; i++) {
			String fileString = projectPath + "configHw2021/attackerUser/user" + (i + 1) + ".txt";
			list.add(new Hw2021AttackerParallelTest(fileString));
		}
		return list.toArray();
	}

}
