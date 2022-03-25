package huwang2021_testCase;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.http.ParseException;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;

import baseApi.CalculateResponseTime;
import huwang2020_testCase.HwJudgeTestCases;

public class Hw2021JudgeTestCases extends HwJudgeTestCases {
	private static String TableName = "Hw2021JudgeTestCases";
	private static Logger logger = Logger.getLogger(Hw2021JudgeTestCases.class);
	private String currentPath = getClass().getResource("../").getPath().toString().replace("%20", " ").substring(1);
	private String projectPath = currentPath.split("target")[0];
	private String userFile = projectPath + "configHw2021/judge.txt";
	private String apiFile = projectPath + "configHw2021/judgeapi.txt";
	private String cookieWithoutCrumb = "";
	static {
		CalculateResponseTime.tableName = TableName;
		try {
			CalculateResponseTime.createTable(TableName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};

	@BeforeSuite(groups = InitTest)
	public void initUser() throws SQLException {
		initConfig(userFile, apiFile);
		cookieWithoutCrumb = userLogin();
	}

	@AfterSuite(groups = InitTest)
	private void attackerLogout() {
		userLogout(cookieWithoutCrumb);
	}

	@Test(groups = JudgeAttacker)
	private void judgeGetReportList() throws ParseException, IOException {
		String role = "attacker";
		int attackerReportNum = 0;
		String[] argString = { role, cookieWithoutCrumb };
		List<String> attackerReportList = getReportId(argString);
		attackerReportNum = attackerReportList.size();
		for (int i = 0; i < attackerReportNum; i++) {
			System.out.println(attackerReportList.get(i));
		}
	}

	@Test(groups = "test")
	private void judgeGetNodeUuid() throws ParseException, IOException {
		String role = "attacker";
		int attackerReportNum = 0;
		String[] argString = { role, cookieWithoutCrumb };
		List<String> attackerReportList = getReportId(argString);
		attackerReportNum = attackerReportList.size();
		for (int i = 0; i < attackerReportNum; i++) {
			System.out.println(attackerReportList.get(i));
			String[] argsGetNode = { role, cookieWithoutCrumb, attackerReportList.get(i) };
			List<String> nodeList = getNodeUuid(argsGetNode);
			for (int j = 0; j < nodeList.size(); j++) {
				System.out.println(nodeList.get(j));
			}
		}
	}

	@Test(groups = "test")
	private void judgeGetThreatType() throws ParseException, IOException {
		String role = "attacker";
		String[] argString = { role, cookieWithoutCrumb };
		JsonObject threatTypeObject = getThreatType(argString);

		System.out.println(threatTypeObject.toString());
	}

	@Test(groups = JudgeAttacker)
	private void judgeGetNodePostRams() throws ParseException, IOException, SQLException {
		String role = "attacker";
		int attackerReportNum = 0;
		String[] argString = { role, cookieWithoutCrumb };
		List<String> attackerReportList = getReportId(argString); // 获取成绩的ID
		attackerReportNum = attackerReportList.size();
		// 遍历审核处理每一条待审核的成绩
		logger.info("开始遍历审核处理每一条待审核的成绩。。。");
		for (int i = 0; i < attackerReportNum; i++) {
			String reportId = attackerReportList.get(i);
			logger.info("reportId: " + reportId);
			String[] argsGetNode = { role, cookieWithoutCrumb, attackerReportList.get(i) };
			List<String> nodeList = getNodeUuid(argsGetNode); // 获取成绩中某个节点的ID
			String[] argsReportScore = { "", "", "", "" };
			// 审核提交成绩中的每一个节点
			logger.info("开始遍历审核成绩" + reportId + "的每一个节点。。。");
			for (int j = 0; j < nodeList.size(); j++) {
				String nodeId = nodeList.get(j);
				logger.info("nodeId: " + nodeId);
				String[] argsForNodePostRams = { role, cookieWithoutCrumb, reportId, nodeId };
				JsonObject nodeRams = getNodeRams(argsForNodePostRams);
				String[] argsPostNode = { role, cookieWithoutCrumb, nodeId, nodeRams.toString() };
				String judgeNodeResult = postJudgeNode(argsPostNode);
				logger.info("审核节点：" + nodeRams + "，结果是: " + judgeNodeResult);
				argsReportScore = argsForNodePostRams;
			}

			// 审核提交成绩
			String reportScore = getReportScore(argsReportScore);
			String[] argsForGetUpdateTs = { "attacker", cookieWithoutCrumb, reportId };
			String last_update_ts = getReportUpdateTs(argsForGetUpdateTs);
			JsonObject postJudgeReportObject = new JsonObject();
			postJudgeReportObject.addProperty("status", 5);
			postJudgeReportObject.addProperty("id", reportId);
			postJudgeReportObject.addProperty("score", reportScore);
			postJudgeReportObject.addProperty("comments", "审核通过");
			postJudgeReportObject.addProperty("has_technical_tactical", 1);
			postJudgeReportObject.addProperty("technical_tactical_desc", "请描写技战法，攻击过程");
			postJudgeReportObject.addProperty("last_update_ts", last_update_ts);
			String[] argsForJudgeReportPost = { "attacker", cookieWithoutCrumb, reportId,
					postJudgeReportObject.toString() };
			String judgeReportTaskId = postJudgeAttackReport(argsForJudgeReportPost);

			// 检查提交审核成绩的任务是否完成
			String[] argsForScoreTask = { "attacker", cookieWithoutCrumb, judgeReportTaskId };
			String scoreTaskResult = "";
			int scoreTaskWaitTime = 0;
			do {
				try {
					scoreTaskWaitTime++;
					logger.info(getUsername() + "\tthreadId: " + getThreadId() + "\tWait " + scoreTaskWaitTime
							+ " seconds for score report...");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				scoreTaskResult = getScoreTaskResult(argsForScoreTask);
			} while ("false".equals(scoreTaskResult));
		}
	}
}
