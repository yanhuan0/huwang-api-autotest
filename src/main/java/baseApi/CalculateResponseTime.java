package baseApi;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;

import sshBase.HwMysqlConn;

public class CalculateResponseTime {
	public static String tableName;
	public static Statement stmtStatement;
	static {
		String DBINFO = "127.0.0.1:13307/mydb";
		String USER = "root";
		String PASSWORD = "asdf2asdf";
		String[] argStrings = { DBINFO, USER, PASSWORD };
		try {
			stmtStatement = sshBase.MysqlStatement.mysqlstatement(argStrings);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	};

	/**
	 * 实现统计一个任务的运行时间的方法
	 * 
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static void main(String[] args) throws SQLException {
		// connHwMysql(5);
		// connUbuntuMysql(7);
		createTable("APITEST");
		// insertTime("AAAAA", "300");
//		SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS(毫秒)");
//		String format = date.format(new Date(System.currentTimeMillis()));
//		System.out.println("当前时间：" + format);
	}

	public static CloseableHttpResponse executePost(CloseableHttpClient httpClient, HttpPost httpPost, String api)
			throws ClientProtocolException, IOException, SQLException {
		long startTime = System.currentTimeMillis();
		CloseableHttpResponse response = httpClient.execute(httpPost);
		String status = response.getStatusLine().toString().split(" ")[1];
		long endTime = System.currentTimeMillis();
		long runTime = endTime - startTime;
		SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS");
		String format = date.format(endTime);
		String URI = httpPost.getURI().toString();
		insertTime("POST", status, URI, api, String.valueOf(runTime), format);
		return response;
	}

	public static CloseableHttpResponse executePut(CloseableHttpClient httpClient, HttpPut httpPut, String api)
			throws ClientProtocolException, IOException, SQLException {
		long startTime = System.currentTimeMillis();
		CloseableHttpResponse response = httpClient.execute(httpPut);
		String status = response.getStatusLine().toString().split(" ")[1];
		long endTime = System.currentTimeMillis();
		long runTime = endTime - startTime;
		String URI = httpPut.getURI().toString();
		SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS");
		String format = date.format(endTime);
		insertTime("PUT", status, URI, api, String.valueOf(runTime), format);
		return response;
	}

	public static CloseableHttpResponse executeGet(CloseableHttpClient httpClient, HttpGet httpGet, String api)
			throws ClientProtocolException, IOException, SQLException {
		long startTime = System.currentTimeMillis();
		CloseableHttpResponse response = httpClient.execute(httpGet);
		String status = response.getStatusLine().toString().split(" ")[1];
		long endTime = System.currentTimeMillis();
		long runTime = endTime - startTime;
		String URI = httpGet.getURI().toString();
		SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS");
		String format = date.format(endTime);
		insertTime("GET", status, URI, api, String.valueOf(runTime), format);

		return response;
	}

	public static CloseableHttpResponse executeDelete(CloseableHttpClient httpClient, HttpDeleteWithBody httpDelete,
			String api) throws ClientProtocolException, IOException, SQLException {
		long startTime = System.currentTimeMillis();
		CloseableHttpResponse response = httpClient.execute(httpDelete);
		String status = response.getStatusLine().toString().split(" ")[1];
		long endTime = System.currentTimeMillis();
		long runTime = endTime - startTime;
		String URI = httpDelete.getURI().toString();
		SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS");
		String format = date.format(endTime);
		insertTime("DELETE", status, URI, api, String.valueOf(runTime), format);

		return response;
	}

	public static void connHwMysql(int count) throws SQLException {
		String DBINFO = "127.0.0.1:13306/virtual-range";
		String USER = "root";
		String PASSWORD = "96r]N~KW@r^x";
		String SQL = "SELECT vm_task_id,op_task_id,task_name,req_data,rsp_data,create_time,feedback_time FROM "
				+ "vm_task order by vm_task_id desc limit " + count;
		String[] argStrings = { DBINFO, USER, PASSWORD, SQL };
		List<String> list = new ArrayList<String>();
		list = HwMysqlConn.getDataList(argStrings);
		for (String string : list) {
			System.out.println(string);
		}
	}

	public static void createTable(String tableName) throws SQLException {
		String DROPSQL = "DROP TABLE IF EXISTS " + tableName;
		String SQL = "CREATE TABLE IF NOT EXISTS " + tableName
				+ "(id INT PRIMARY KEY AUTO_INCREMENT,method VARCHAR(8),status VARCHAR(8),uri VARCHAR(256),api VARCHAR(256),"
				+ "currenttime VARCHAR(32),currentminute VARCHAR(32),runtime INT)AUTO_INCREMENT = 1";
		// System.out.println(SQL);
		int dropResult = stmtStatement.executeUpdate(DROPSQL);
		int createResult = stmtStatement.executeUpdate(SQL);
		if (dropResult == 0) {
			System.out.println("删除数据表成功： " + tableName);
		} else {
			System.out.println("删除数据表失败： " + tableName);
		}
		if (createResult == 0) {
			System.out.println("创建数据表成功： " + tableName);
		} else {
			System.out.println("创建数据表失败： " + tableName);
		}

	}

	public static void insertTime(String method, String status, String uri, String api, String runTime,
			String currentTime) throws SQLException {
		String currentMinute = currentTime.substring(0, 16);
		String SQL = "INSERT INTO " + tableName + " VALUES (null,\'" + method + "\',\'" + status + "\',\'" + uri
				+ "\',\'" + api + "\',\'" + currentTime + "\',\'" + currentMinute + "\',\'" + runTime + "\' )";
		// System.out.println(SQL);
		stmtStatement.executeUpdate(SQL);

	}
}
