package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AdministratorDAO {
	
	static final String DB_URL = "jdbc:mysql://localhost:3306/haksa";
	static final String DB_NAME = "root";
	static final String PASSWORD = "111111";
	
	static Connection conn;
	static Statement stmt;
	static ResultSet rs;
	
	// 관리자 회원가입
	public void administratorSignUpInsert(String id, String password, String name) {
		Administrator administrator = new Administrator(id, password, name);
		String query = "INSERT INTO administrator (id, password, name) "
				+ "VALUES ('" + administrator.id + "', '" + administrator.password + "', '" + administrator.name + "')";
		
		try {
			conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
			
			stmt.close();
			conn.close();
		} catch(SQLException e) {
			System.out.println("administratorSignUpInsert 오류 : " + e.getMessage());
		}
	}
	
	// 관리자 로그인
	public String[] administratorLogInSelect(String id) {
		Administrator administrator = new Administrator();
		String query = "SELECT id, password FROM administrator WHERE id = '" + id + "'";
		String[] logInInfo = null;
		
		try {
			conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				administrator.setId(rs.getString("id"));
				administrator.setPassword(rs.getString("password"));
				logInInfo = new String[] {administrator.getId(), administrator.getPassword()};
			}
			
			stmt.close();
			conn.close();
		} catch(SQLException e) {
			System.out.println("administratorLogInSelect 오류 : " + e.getMessage());
		}
		
		return logInInfo;
	}
	
//-------------------- 학생관리에서 필요한 메소드 ------------------------------
	// 학생관리 - 학생 계정 검색
	public ObservableList<Student> studentInfoSelect(String department_name, int studentId, String name) {
		
		ObservableList<Student> students = FXCollections.observableArrayList();
		String query = "";
		
		// 학생 정보 입력 란에 어떤 정보를 입력했냐에 따라 DB에 보내는 Query문이 다르다.
		if (department_name != null && studentId == 0 && name.equals("")) {  // 학과만 입력 시 같은 학과 학생 모두 검색
			query = "SELECT student_id, student.name, department.name AS department_name, id, password, address, phoneNumber FROM student"
					+ " LEFT JOIN department ON student.department_no = department.no"
					+ " WHERE department.name = '" + department_name + "'";
			
		} else if (department_name == null && studentId != 0 && name.equals("")) {  // 학번만 입력 시 같은 학번 학생만 검색
			query = "SELECT student_id, student.name, department.name AS department_name, id, password, address, phoneNumber FROM student"
					+ " LEFT JOIN department ON student.department_no = department.no"
					+ " WHERE student_id = " + studentId;
			
		} else if (department_name == null && studentId == 0 && !name.equals("")) {  // 이름만 입력 시 같은 이름 학생 모두 검색
			query = "SELECT student_id, student.name, department.name AS department_name, id, password, address, phoneNumber FROM student"
					+ " LEFT JOIN department ON student.department_no = department.no"
					+ " WHERE student.name = '" + name + "'";
			
		} else if (department_name != null && studentId != 0 && name.equals("")) {   // 학과와 학번만 입력 시 같은 학과의 같은 학번 학생만 검색
			query = "SELECT student_id, student.name, department.name AS department_name, id, password, address, phoneNumber FROM student"
					+ " LEFT JOIN department ON student.department_no = department.no"
					+ " WHERE department.name = '" + department_name + "' AND student_id = " + studentId;
			
		} else if (department_name == null && studentId != 0 && !name.equals("")) {  // 학번과 이름만 입력 시 같은 학번의 같은 이름 학생만 검색
			query = "SELECT student_id, student.name, department.name AS department_name, id, password, address, phoneNumber FROM student"
					+ " LEFT JOIN department ON student.department_no = department.no"
					+ " WHERE student_id = " + studentId + " AND student.name = '" + name + "'";
			
		} else if (department_name != null && studentId == 0 && !name.equals("")) {  // 학과와 이름만 입력 시 같은 학과의 같은 이름 학생 모두 검색
			query = "SELECT student_id, student.name, department.name AS department_name, id, password, address, phoneNumber FROM student"
					+ " LEFT JOIN department ON student.department_no = department.no"
					+ " WHERE department.name = '" + department_name + "' AND student.name = '" + name + "'";
			
		} else  // 학과, 학번, 이름이 같은 학생 검색
			query = "SELECT student_id, student.name, department.name AS department_name, id, password, address, phoneNumber FROM student"
					+ " LEFT JOIN department ON student.department_no = department.no"
					+ " WHERE department.name = '" + department_name + "' AND  student_id = " + studentId + " AND student.name = '" + name + "'";
	
		try {
			conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				Student student = new Student();
				student.setStudent_id(rs.getInt("student_id"));
				student.setName(rs.getString("name"));
				student.setDepartment_name(rs.getString("department_name"));
				student.setId(rs.getString("id"));
				student.setPassword(rs.getString("password"));
				student.setAddress(rs.getString("address"));
				student.setPhoneNumber(rs.getString("phoneNumber"));
				students.add(student);
			}
			
			conn.close();
			stmt.close();
		} catch(SQLException e) {
			System.out.println("studentInfoSelect 오류 : " + e.getMessage());
		}
		return students;
	}
	
	// 학생관리 - 학생 정보 수정
	public void studentInfoUpdate(int studentId, String name, int department_no, String id, String password, String address, String phoneNumber) {
		String query = "UPDATE student SET id = '" + id + "', password = '" + password + "', name = '" + name + "', "
				+ "department_no = " + department_no + ", address = '" + address + "', phoneNumber = '" + phoneNumber + "' WHERE student_id = " + studentId;
		
		try {
			conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
			
			stmt.close();
			conn.close();
		} catch(SQLException e) {
			System.out.println("studentInfoUpdate 오류 : " + e.getMessage());
		}
	}
	
	// 학생관리 - 학생 계정 삭제
	public void studentInfoDelete(int studentId) {
		String query = "DELETE FROM student WHERE student_id = " + studentId;
		
		try {
			conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
			
			stmt.close();
			conn.close();
		} catch(SQLException e) {
			System.out.println("studentInfoDelect 오류 : " + e.getMessage());
		}
	}
	
//-------------------- 교수관리에서 필요한 메소드 ------------------------------
	// 교수관리 - 교수 계정 검색
	public ObservableList<Professor> professorInfoSelect(String department_name, String name){
		ObservableList<Professor> professors = FXCollections.observableArrayList();
		String query = "";
		
		// 교수 정보 입력 란에 어떤 정보를 입력했냐에 따라 DB에 보내는 Query문이 다르다.
		if (department_name != null && name.equals("")) {  // 학과만 입력 시 같은 학과 교수 모두 검색
			query = "SELECT professor.name, department.name AS department_name, id, password, address, phoneNumber FROM professor"
					+ " LEFT JOIN department ON professor.department_no = department.no"
					+ " WHERE department.name = '" + department_name + "'";
			
		} else if (department_name == null && !name.equals("")) {  // 이름만 입력 시 같은 이름 교수 모두 검색
			query = "SELECT professor.name, department.name AS department_name, id, password, address, phoneNumber FROM professor"
					+ " LEFT JOIN department ON professor.department_no = department.no"
					+ " WHERE professor.name = '" + name + "'";
			
		} else  // 학과, 이름이 같은 교수 검색
			query = "SELECT professor.name, department.name AS department_name, id, password, address, phoneNumber FROM professor"
					+ " LEFT JOIN department ON professor.department_no = department.no"
					+ " WHERE department.name = '" + department_name + "' AND professor.name = '" + name + "'";
	
		try {
			conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				Professor professor = new Professor();
				professor.setName(rs.getString("name"));
				professor.setDepartment_name(rs.getString("department_name"));
				professor.setId(rs.getString("id"));
				professor.setPassword(rs.getString("password"));
				professor.setAddress(rs.getString("address"));
				professor.setPhoneNumber(rs.getString("phoneNumber"));
				professors.add(professor);
			}
			
			stmt.close();
			conn.close();
		} catch(SQLException e) {
			System.out.println("ProfessorInfoSelect 오류 : " + e.getMessage());
		}
		return professors;
	}
	
	// 교수관리 - 교수 정보 수정
		public void professorInfoUpdate(String name, int department_no, String id, String password, String address, String phoneNumber) {
			String query = "UPDATE professor SET password = '" + password + "', name = '" + name + "', "
					+ "department_no = " + department_no + ", address = '" + address + "', phoneNumber = '" + phoneNumber + "' WHERE id = '" + id + "'";
			
			try {
				conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
				stmt = conn.createStatement();
				stmt.executeUpdate(query);
				
				stmt.close();
				conn.close();
			} catch(SQLException e) {
				System.out.println("professorInfoUpdate 오류 : " + e.getMessage());
			}
		}
		
		// 교수관리 - 교수 계정 삭제
		public void professorInfoDelete(String id) {
			String query = "DELETE FROM professor WHERE id = '" + id + "'";
			
			try {
				conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
				stmt = conn.createStatement();
				stmt.executeUpdate(query);
				
				stmt.close();
				conn.close();
			} catch(SQLException e) {
				System.out.println("professorInfoDelete 오류 : " + e.getMessage());
			}
		}
//-------------------- 학과관리에서 필요한 메소드 ------------------------------
		// 학과관리 - 전체 학과 데이터 가져오기(TableView)
		public ObservableList<Department> departmentInfoSelect(){
			ObservableList<Department> departments = FXCollections.observableArrayList();
			String query = "SELECT * FROM department";
			
			try {
				conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);
				
				while (rs.next()) {
					Department department = new Department();
					department.setNo(rs.getInt("no"));
					department.setName(rs.getString("name"));
					
					departments.add(department);
				}
				
				stmt.close();
				conn.close();
			} catch(SQLException e) {
				System.out.println("DepartmentInfoSelect 오류 : " + e.getMessage());
			}
			return departments;
		}
		
		// 학과관리 - 학과의 학생, 교수 인원 수
		public int[] departmentCntSelect(int department_no) {
			int[] count = new int[2];
			Statement stmt2;
			ResultSet rs2;
			String stuQuery = "SELECT COUNT(*) AS cnt_student FROM student WHERE department_no = " + department_no;
			String profQuery = "SELECT COUNT(*) AS cnt_professor FROM professor WHERE department_no = " + department_no;
			
			try {
				conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
				stmt = conn.createStatement();
				stmt2 = conn.createStatement();
				rs = stmt.executeQuery(stuQuery);
				rs2 = stmt2.executeQuery(profQuery);
				
				while(rs.next()) {
					count[0] = rs.getInt("cnt_student");	
				}
				
				while(rs2.next()) {
					count[1] = rs2.getInt("cnt_professor");	
				}

				stmt.close();
				stmt2.close();
				conn.close();
			} catch(SQLException e) {
				System.out.println("departmentCntSelect 오류 : " + e.getMessage());
			}
			return count;
		}
		
		// 학과관리 - 과목 추가
		public void departmentInsert(String name) {
			String query = "INSERT INTO department(name) VALUES ('" + name + "')";
			
			try {
				conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
				stmt = conn.createStatement();
				stmt.executeUpdate(query);
				
				stmt.close();
				conn.close();
			} catch(SQLException e) {
				System.out.println("departmentInsert 오류 : " + e.getMessage());
			}
		}
		
		// 학과관리 - 과목명 수정
		public void departmentNameUpdate(int no, String name) {
			String query = "UPDATE department SET name = '" + name + "' WHERE no = " + no;
			
			try {
				conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
				stmt = conn.createStatement();
				stmt.executeUpdate(query);
				
				stmt.close();
				conn.close();
			} catch(SQLException e) {
				System.out.println("departmentNameUpdate 오류 : " + e.getMessage());
			}
		}
		
		// 학과관리 - 과목 삭제
		public void departmentDelete(int no) {
			String query = "DELETE FROM department WHERE no = " + no;
			
			try {
				conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
				stmt = conn.createStatement();
				stmt.executeUpdate(query);
				
				stmt.close();
				conn.close();
			} catch(SQLException e) {
				System.out.println("departmentDelete 오류 : " + e.getMessage());
			}
		}
//-------------------- 내정보관리에서 필요한 메소드 ------------------------------
		public Administrator administratorInfoSelect(String id) {
			Administrator admin = new Administrator();
			String query = "SELECT no, name, id, password FROM administrator WHERE id = '" + id + "'";
			
			try {
				conn = DriverManager.getConnection(DB_URL, DB_NAME, PASSWORD);
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);
				
				while(rs.next()) {
					admin.setNo(rs.getInt("no"));
					admin.setName(rs.getString("name"));
					admin.setId(rs.getString("id"));
					admin.setPassword(rs.getString("password"));
				}
				
				stmt.close();
				conn.close();
			} catch(SQLException e) {
				System.out.println("administratorInfoSelect 오류 : " + e.getMessage());
			}
			return admin;
		}
}