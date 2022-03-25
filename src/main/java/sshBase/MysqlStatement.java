package sshBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlStatement {

	public static Statement mysqlstatement(String[] argStrings) throws SQLException {
		String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
		String DB_URL = "jdbc:mysql://" + argStrings[0]
				+ "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
		String USER = argStrings[1];
		String PASSWORD = argStrings[2];

		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		return stmt;
	}
}
