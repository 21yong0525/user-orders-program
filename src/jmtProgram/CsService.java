package jmtProgram;

public class CsService {
	
	private int no;
	private String userid;
	private String text;
	public static int non = 5;
	public static final int non2 = 5;
	
	public CsService(int no, String userid, String text) {
		this.no = no;
		this.userid = userid;
		this.text = text;
	}


	@Override
	public String toString() {
		System.out.println("―".repeat(89));
		System.out.println("글번호 : " + no);
		System.out.println("작성자 : " + userid);
		System.out.println("내용 : ");
		return text  + "\n" + "―".repeat(89);
	}
}
