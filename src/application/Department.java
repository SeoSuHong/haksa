package application;

public class Department {
	int no;
	String name;
	
	Department(){};
	
	Department(int no, String name){
		super();
		this.no = no;
		this.name = name;
	}
	
	public int getNo() {
		return this.no;
	}
	
	public void setNo(int no) {
		this.no = no;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}