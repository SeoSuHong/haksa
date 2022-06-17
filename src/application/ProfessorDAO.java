package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProfessorDAO {

	static Connection conn;
	static Statement stmt;
	static ResultSet rs;
	
	static final String DB_URL = "jdbc:mysql://localhost:3306/haksa";
	static final String DB_NAME = "root";
	static final String PASSWORD = "111111";
	
//-------------------- 회원가입 시 필요한 메소드 ------------------------------
	// 교수 ID 중복 확인
		public int professorIdCheckSelect(String id) {
			int count = 0;
			String query = "SELECT id, COUNT(id) as cnt FROM professor WHERE id = '" + id + "' GROUP BY id HAVING COUNT(id) > 0";
			
			if(id.equals("")) {
				return -1;
			}
			
			try {
				conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);
				
				while(rs.next()) {
					count += rs.getInt("cnt");
				}
				
				stmt.close();
				conn.close();
			} catch(SQLException e) {
				System.out.println("professorIdCheckSelect 오류 : " + e.getMessage());
			}
			System.out.println(count);
			return count;
		}
		
	// 교수 회원가입 시 정보 저장
	public void professorSignUpInsert(String id, String password, String name, int department_no, String address, String phoneNumber) {
		Professor professor = new Professor(id, password, name, department_no, address, phoneNumber);
		String query = "INSERT INTO professor (id, password, name, department_no, address, phoneNumber) "
				+ "VALUES ('" + professor.id + "', '" + professor.password + "', '" + professor.name + "', '" + professor.department_no + "', '" + professor.address + "', '" + professor.phoneNumber + "')";
		 try {
			 conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
			 stmt = conn.createStatement();
			 stmt.executeUpdate(query);
			 
			 stmt.close();
			 conn.close();
		 } catch(SQLException e) {
			 System.out.println("professorSignUpInsert 오류 : " + e.getMessage());
		 }
	}
//----------------------------------------------------------------------
	
	// 교수 로그인 시 ID, Password 검색
	public String[] professorLogInSelect(String id) {
		String query = "SELECT id, password FROM professor WHERE id = '" + id + "'";
		Professor professor = new Professor();
		String[] logInInfo = null;
		
		try {
			conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				professor.setId(rs.getString("id"));
				professor.setPassword(rs.getString("password"));
				logInInfo = new String[] {professor.getId(), professor.getPassword()};
			}
			
			stmt.close();
			conn.close();
		} catch(SQLException e) {
			System.out.println("professorLogInSelect 오류 : " + e.getMessage());
		}
		
		return logInInfo;
	}
}