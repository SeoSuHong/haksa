package application;

public class Student {
	
	int student_id, department_no, position_no;
	String id, password, name, address, phoneNumber, department_name;


	public Student() {}

	public Student(int student_id, String id, String password, String name, int department_no, String address, String phoneNumber){
		super();
		this.student_id = student_id;
		this.id = id;
		this.password = password;
		this.name = name;
		this.department_no = department_no;
		this.address = address;
		this.phoneNumber = phoneNumber;
	}
//	public Student(int student_id, String name, String department_name, String id, String password, String address, String phoneNumber){
//		super();
//		this.student_id = student_id;
//		this.id = id;
//		this.password = password;
//		this.name = name;
//		this.department_name = department_name;
//		this.address = address;
//		this.phoneNumber = phoneNumber;
//	}
 
	public int getStudent_id() {
		return student_id;
	}

	public void setStudent_id(int student_id) {
		this.student_id = student_id;
	}

	public int getDepartment_no() {
		return department_no;
	}

	public void setDepartment_no(int department_no) {
		this.department_no = department_no;
	}

	public int getPosition_no() {
		return position_no;
	}

	public void setPosition_no(int position_no) {
		this.position_no = position_no;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDepartment_name() {
		return department_name;
	}

	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}
}