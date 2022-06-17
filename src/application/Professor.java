package application;

public class Professor {
	
	int no, department_no, position_no;
	String id, password, name, address, phoneNumber, department_name;

	public Professor() {}

	public Professor(String id, String password, String name, int department_no, String address, String phoneNumber){
		super();
		this.id = id;
		this.password = password;
		this.name = name;
		this.department_no = department_no;
		this.address = address;
		this.phoneNumber = phoneNumber;
	}
	
	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
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