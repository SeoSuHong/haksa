package application;

public class Administrator {
	
	int no, position_no;
	String id, password, name;

	public Administrator() {}

	public Administrator(String id, String password, String name) {
		this.id = id;
		this.password = password;
		this.name = name;
	}

	public int getNo() {
		return no;
	}
 
	public void setNo(int no) {
		this.no = no;
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
}