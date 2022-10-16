package jmtProgram;

public class User {

	private String userid;
	private String password;
	private String name;
	private String phone;
	private int wallet;
	private int amount;
	private String grade;
	
	public User(String userid, String password, String name, String phone, int wallet, int amount, String grade) {
		this.userid = userid;
		this.password = password;
		this.name = name;
		this.phone = phone;
		this.wallet = wallet;
		this.amount = amount;
		this.grade = grade;
	}

	public String getUserid() {return userid;}
	public void setUserid(String userid) {this.userid = userid;}

	public String getPassword() {return password;}
	public void setPassword(String password) {this.password = password;}

	public String getName() {return name;}
	public void setName(String name) {this.name = name;}

	public String getPhone() {return phone;}
	public void setPhone(String phone) {this.phone = phone;}
	
	public int getWallet() {return wallet;}
	public void setWallet(int wallet) {this.wallet = wallet;}

	public int getAmount() {return amount;}
	public void setAmount(int amount) {this.amount = amount;}

	public String getGrade() {return grade;}
	public void setGrade(String grade) {this.grade = grade;}

	@Override
	public String toString() {
		System.out.println("―".repeat(89)+"\n아이디\t\t비밀번호\t이름\t전화\t\t충전금\t구매액\t등급");
		return userid + "\t\t" + password + "\t" + name + "\t" + phone + "\t" + wallet + "\t" + amount + "\t" + grade + "\n" + "―".repeat(89);
	}	
}
