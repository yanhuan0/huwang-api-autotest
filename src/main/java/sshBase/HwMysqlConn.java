package sshBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class HwMysqlConn {
	public static List<String> getDataList(String[] argStrings) throws SQLException {
		List<String> list = new ArrayList<String>();
		String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
		String DB_URL = "jdbc:mysql://"+argStrings[0]+"?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
		String USER = argStrings[1];
		String PASSWORD = argStrings[2];
		String SQL = argStrings[3];
		
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try{
            Class.forName(JDBC_DRIVER);  
            conn = DriverManager.getConnection(DB_URL,USER,PASSWORD);
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(SQL);
            int collumCount = rs.getMetaData().getColumnCount();
            int rowCount = 0;
            try {
                rs.last();
                rowCount = rs.getRow();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
				rs.beforeFirst();;
			}
            System.out.println("rowCount: "+rowCount);
            while(rs.next()){
            	StringBuilder stringBuilder = new StringBuilder();
            	for (int i = 1; i < collumCount; i++) {
            		stringBuilder.append(rs.getString(i)+",");
				}
            	String line = stringBuilder.toString();
                list.add(line.substring(0,line.length()-1));
            }
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            rs.close();
            stmt.close();
            conn.close();
            try {
				if (rs!=null) {
					rs.close();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            	se2.printStackTrace();
            }
            
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
		return list;
	}
}