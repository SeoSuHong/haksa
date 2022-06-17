package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class StudentDAO {

	static Connection conn;
	static Statement stmt;
	static ResultSet rs;

	static final String DB_URL = "jdbc:mysql://localhost:3306/haksa";
	static final String DB_NAME = "root";
	static final String PASSWORD = "111111";
	
//-------------------- 회원가입 시 필요한 메소드 ------------------------------
	// 학생 ID 중복 확인
	public int studentIdCheckSelect(String id) {
		int count = 0;
		String query = "SELECT id, COUNT(id) as cnt FROM student WHERE id = '" + id + "' GROUP BY id HAVING COUNT(id) > 0";
		Alert alert = new Alert(AlertType.NONE,"" , ButtonType.APPLY);
		
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
			System.out.println("studentIdCheckSelect 오류 : " + e.getMessage());
		}
		System.out.println(count);
		return count;
	}
	
	// 학생 회원가입 시 정보 저장
	public void studentSignUpInsert(int student_id, String id, String password, String name, int department_no, String address, String phoneNumber) {
		
		Student student = new Student(student_id, id, password, name, department_no, address, phoneNumber);
		String query = "INSERT INTO student (student_id, id, password, name, department_no, address, phoneNumber) "
				+ "VALUES ('" + student.student_id + "', '" + student.id + "', '" + student.password + "', '" + student.name + "', '" + student.department_no + "', '" + student.address + "', '" + student.phoneNumber + "')";

		try {
			conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
			stmt = conn.createStatement();
			stmt.executeUpdate(query);

			stmt.close();
			conn.close();
		} catch(SQLException e) {
			System.out.println("studentSignUpInsert 오류 : " + e.getMessage());
		}
	}

//-------------------- 로그인 시 필요한 메소드 ------------------------------
	// 학생 로그인 시 ID, Password 검색
	public String[] studentLogInSelect (String id) {	

		Student student = new Student();
		String query = "SELECT id, password FROM student WHERE id = '" + id + "'";
		String[] logInInfo = null;

		try {
			conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			while(rs.next()) {
				student.setId(rs.getString("id"));
				student.setPassword(rs.getString("password"));
				logInInfo = new String[] {student.getId(), student.getPassword()}; 
			}

			stmt.close();
			conn.close();
		} catch(SQLException e) {
			System.out.println("studentLogInSelect 오류 : " + e.getMessage());
		}

		return logInInfo;
	}
	
}