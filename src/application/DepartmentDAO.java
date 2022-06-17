package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {
	
	static final String DB_URL = "jdbc:mysql://localhost:3306/haksa";
	static final String DB_NAME = "root";
	static final String PASSWORD = "111111";
	
	static Connection conn;
	static Statement stmt;
	static ResultSet rs;
	
	public int departmentNoSelect(String name) {
		String query = "SELECT no FROM department WHERE name = '" + name + "'";
		Department department = new Department();
		int department_no = 0;
		try {
			conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				department.setNo(rs.getInt("no"));
				
				department_no = department.getNo();
			}
			
			stmt.close();
			conn.close();
		} catch(SQLException e) {
			System.out.println("departmentNoSelect ¿À·ù : " + e.getMessage());
		}
		return department_no;
	}
	
	public List<String> departmentSignUpSelect(){
		Department department = new Department();
		String query = "SELECT no, name FROM department ORDER BY name";
		List<String> departments = new ArrayList<String>();
		
		try {
			conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				department.setName(rs.getString("name"));
				departments.add(department.getName());
			}
			
			stmt.close();
			conn.close();
		} catch(SQLException e) {
			System.out.println("departmentLogInSelect ¿À·ù : " + e.getMessage());
		}
		return departments;
	}
}