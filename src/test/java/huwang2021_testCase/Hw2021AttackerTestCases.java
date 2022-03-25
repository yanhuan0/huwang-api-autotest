package huwang2021_testCase;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.List;

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
import huwang2020_testCase.HwAttackerTestCases;

public class Hw2021AttackerTestCases extends HwAttackerTestCases {
	private static String TableName = "Hw2021AttackerTestCases";
	private static Logger logger = Logger.getLogger(Hw2021AttackerTestCases.class);
	private String currentPath = getClass().getResource("../").getPath().toString().replace("%20", " ").substring(1);
	private String projectPath = currentPath.split("target")[0];
	private String userFile = projectPath + "configHw2021/attacker.txt";
	private String apiFile = projectPath + "configHw2021/attackerapi.txt";
	private String multiUserFile = projectPath + "configHw2021/attacker_user.txt";
	private String ZipFilePath = projectPath + "configHw2021/huwang2017_20210406110848.sql.asc";
	private String WordFilePath = projectPath + "configHw2021/测试.docx";
	private String cookieWithoutCrumb = "";
	public static final String ReportAttacker = "ReportAttacker";
	public static final String NodeAttacker = "NodeAttacker";
	public static final String attackAttEvent = "attackAttEvent";
	public static final String multiUserCreateReports = "multiUserCreateReports";

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

	@Test(groups = "multiUserEditPassword", priority = 1)
	public void multiUserEditPassword() throws IOException, SQLException {
		FileReader multiUserFileReader = new FileReader(multiUserFile);
		@SuppressWarnings("resource")
		BufferedReader multiUserInfo = new BufferedReader(multiUserFileReader);
		String userString = "";
		while ((userString = multiUserInfo.readLine()) != null) {
			String[] argStrings = { userFile, userString };
			Utils.Relpace(argStrings);
			attackerLogout();
			initUser();
			editItself();
		}
	}

	@Test(groups = multiUserCreateReports, priority = 1)
	public void multiUserCreateReports() throws IOException, SQLException {
		FileReader multiUserFileReader = new FileReader(multiUserFile);
		@SuppressWarnings("resource")
		BufferedReader multiUserInfo = new BufferedReader(multiUserFileReader);
		String userString = "";
		while ((userString = multiUserInfo.readLine()) != null) {
			String[] argStrings = { userFile, userString };
			Utils.Relpace(argStrings);
			attackerLogout();
			initUser();
			createAttackerReportNode();
			createAttackerReport();
		}
	}

	public void createAttackerReportNode(String cookieWithoutCrumb) throws SocketException, IOException, SQLException {
		this.cookieWithoutCrumb = cookieWithoutCrumb;
		createAttackerReportNode();
	}

	@Test(groups = NodeAttacker, priority = 1)
	public void createAttackerReportNode() throws SocketException, IOException, SQLException {
		int rowNum = 1;
		int columnNum = 2;
		String[] argString = { cookieWithoutCrumb };
		List<String> defenseIdList = getUnusedDefenseId(argString);
		int startId = 2;
		for (int i = 0; i < defenseIdList.size(); i++) {
			JsonObject reportObject = new JsonObject();
			String defenseId = defenseIdList.get(i);
			String uuid = "";
			String[] reportAddArg = { cookieWithoutCrumb, defenseId };
			CloseableHttpResponse response = addAttackerReport(reportAddArg);
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				uuid = jObject.get("data").getAsJsonObject().get("uuid").getAsString();
			}
			// 生成graphData
			// 参数1：节点i开始的值
			// 参数2：生成节点树的行数
			// 参数3：生成节点树的列数
			JsonObject graphData = getGraphData(startId, rowNum, columnNum);
			startId += 600;
			// stage接口提交成绩
			reportObject.add("graphData", graphData);
			reportObject.addProperty("organization_dst_id", defenseId);
			reportObject.addProperty("path_desc", "description...");
			reportObject.addProperty("uuid", uuid);
			String reportString = reportObject.toString();
			String[] argsCheck = { cookieWithoutCrumb, uuid, defenseId };
			String[] reportJsonStrings = { cookieWithoutCrumb, reportString };
			CloseableHttpResponse responseStage = createReportTree(reportJsonStrings);
			if (responseStage != null) {
				// 检查创建成绩树的任务是否完成
				JsonObject stageObject = Client.getResponseDataJson(responseStage);
				String taskId = stageObject.get("data").getAsString();
				String[] argsStash = { cookieWithoutCrumb, taskId };
				String stageResult = "";
				int createTreeWaitTime = 0;
				do {
					try {
						createTreeWaitTime++;
						logger.info(getUsername() + "\tthreadId: " + getThreadId() + "\tWait " + createTreeWaitTime
								+ " seconds for create tree...");
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					stageResult = reportStashTask(argsStash);
				} while ("false".equals(stageResult));
				// 打开一次该成绩树
				// CloseableHttpResponse viweResponse = getAttackerView(argsCheck);
				// JsonObject jObject = Client.getResponseDataJson(viweResponse);
				// logger.info(jObject);
				// 解锁成绩
				reportUnlock(argsCheck);

			}
		}
	}

	public void createAttackerReport(String cookieWithoutCrumb) throws ParseException, IOException, SQLException {
		this.cookieWithoutCrumb = cookieWithoutCrumb;
		createAttackerReport();
	}

	@Test(groups = ReportAttacker, priority = 2)
	public void createAttackerReport() throws ParseException, IOException, SQLException {
		int attackerReportNum = 0;
		String[] argString = { cookieWithoutCrumb };
		// 获取所有可以编辑的成绩的节点
		List<String> attackerReportList = getAttackerReportId(argString);
		attackerReportNum = attackerReportList.size();

		// 循环每一条成绩获取这条成绩需要重新编辑保存的节点
		// 填充此节点并保存
		// 最后提交这条成绩
		for (int i = 0; i < attackerReportNum; i++) {
			// String last_update_ts = Client.currentTime();
			String reportString = attackerReportList.get(i);
			String reportId = reportString.split("==>")[0];
			String last_update_ts = reportString.split("==>")[1];
			String[] args = { cookieWithoutCrumb, reportId };

			// 锁定此成绩
			String[] reportLockArgs = { cookieWithoutCrumb, reportId, last_update_ts };
			reportLock(reportLockArgs);

			// 获取此成绩对应的防守方ID
			String orgDstId = getAttackerReportDestId(reportLockArgs);

			// 执行成绩内容的check
			String[] argsCheck = { cookieWithoutCrumb, reportId, orgDstId };
			reportCheck(argsCheck);

			List<String> attackerReportInfoList = getAttackerReportInfo(args);
			if (attackerReportInfoList.size() > 0) {
				// 如果成绩有status是0的节点，开始循环编辑保存节点
				for (int j = 0; j < attackerReportInfoList.size(); j++) {
					JsonObject reportNode = getReportNode(cookieWithoutCrumb, attackerReportInfoList.get(j).split(","));

					// 编辑节点并且保存
					String[] argReportNode = { cookieWithoutCrumb, reportNode.toString(), reportId, "reportNode" };
					CloseableHttpResponse response = editAttackerNode(argReportNode);
					if (response != null) {
						JsonObject saveNodeRspObject = Client.getResponseDataJson(response);
						// logger.info(saveNodeRspObject.toString());
						String taskId = saveNodeRspObject.get("data").getAsString();
						// 检查保存节点的任务是否完成
						String[] argsSaveNode = { cookieWithoutCrumb, taskId };
						String saveNodeTaskResult = "";
						int saveNodeWaitTime = 0;
						do {
							try {
								saveNodeWaitTime++;
								logger.info(getUsername() + "\tthreadId: " + getThreadId() + "\tWait "
										+ saveNodeWaitTime + " seconds for save node...");
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							saveNodeTaskResult = nodeSaveTask(argsSaveNode);
						} while ("false".equals(saveNodeTaskResult));
					}
				}
			} else {
				logger.info("成绩：" + reportId + " 没有需要编辑的成绩节点。");
			}
			// 提交成绩
			CloseableHttpResponse createRsp = createReport(argsCheck);
			JsonObject createRspObject = Client.getResponseDataJson(createRsp);
			// logger.info(createRspObject.toString());
			String taskId = createRspObject.get("data").getAsString();

			// 检查提交成绩的任务是否完成
			String[] argsStash = { cookieWithoutCrumb, taskId };
			String createTaskResult = "";
			int createReportWaitTime = 0;
			do {
				try {
					createReportWaitTime++;
					logger.info(getUsername() + "\tthreadId: " + getThreadId() + "" + "\tWait " + createReportWaitTime
							+ " seconds for create report...");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				createTaskResult = reportCreateTask(argsStash);
			} while ("false".equals(createTaskResult));

			// 解锁成绩
			reportUnlock(args);
		}
	}

	@Test(groups = { AttackerApi, wordUpload }, priority = 3)
	public void wordUpload() throws ParseException, IOException, SQLException {
		int zeroDayNum = 1;
		String FilePath = WordFilePath;
		for (int i = 0; i < zeroDayNum; i++) {
			String[] argsStrings = { FilePath, cookieWithoutCrumb };
			CloseableHttpResponse response = wordUpload(argsStrings);
			String message = "";
			// logger.info(response);
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				logger.info(jObject.toString());
				message = jObject.get("message").getAsString();
			}
			assertEquals(message, "操作成功", "word upload Successfully.");
		}
	}

	@Test(groups = { AttackerApi, ZeroDayAttackerUpload }, priority = 4)
	public void zeroDayAttackerUpload() throws ParseException, IOException, SQLException {
		int zeroDayNum = 1;
		String FilePath = ZipFilePath;
		for (int i = 0; i < zeroDayNum; i++) {
			String[] argsStrings = { FilePath, cookieWithoutCrumb };
			CloseableHttpResponse response = zeroDayAttackerUpload(argsStrings);
			String message = "";
			// logger.info(response);
			if (response != null) {
				JsonObject jObject = Client.getResponseDataJson(response);
				logger.info(jObject.toString());
				message = jObject.get("message").getAsString();
			}
			assertEquals(message, "操作成功", "zeroDayAttackerUpload Successfully.");
		}
	}

	@Test(groups = { AttackerApi, ZeroDayAttackerCreate }, priority = 5)
	public void zeroDayAttackerCreate() throws ParseException, IOException, SQLException {
		int zeroDayNum = 3;
		String FilePath = ZipFilePath;
		for (int i = 0; i < zeroDayNum; i++) {
			String[] uploadStrings = { FilePath, cookieWithoutCrumb };
			CloseableHttpResponse uploadResponse = zeroDayAttackerUpload(uploadStrings);
			String path = "";
			String name = "";
			if (uploadResponse != null) {
				JsonObject jUploadObject = Client.getResponseDataJson(uploadResponse);
				// System.out.println(jUploadObject);
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

	@Test(groups = { AttackerApi, attackAttEvent }, priority = 6)
	public void attackAttEventCommit() throws ParseException, IOException, SQLException {
		int AttEventNum = 3;
		String FilePath = WordFilePath;
		for (int i = 0; i < AttEventNum; i++) {
			String[] uploadStrings = { FilePath, cookieWithoutCrumb };
			CloseableHttpResponse uploadResponse = wordUpload(uploadStrings);
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
					assertEquals(message, "操作成功", "attackAttEventCreate Successfully.");
				} else {
					assertEquals("0", "1", "attackAttEventCreate Failed,create no response.");
				}
			} else {
				assertEquals("0", "1", "attackAttEventCreate Failed,upload word no response.");
			}
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
