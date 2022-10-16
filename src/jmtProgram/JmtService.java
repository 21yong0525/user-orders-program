package jmtProgram;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

// 존맛탱 음식 배달 프로그램
public class JmtService {
	
	public static final int LOGINMENU = 1, MAINMENU = 2, ID = 3 , PASSWORD = 4, NAME = 5, PHONE = 6, SELECT = 7, STORE = 8, ORDERS = 9;
	public static final int LOGIN = 1, SIGNUP = 2 , EXIT = 3;
	public static final int USER_INFO = 1, UPDATE = 2, SEARHCH = 3, ORDER = 4, LOGOUT = 5, CS = 6, MAINEXIT = 7;
	public static final int STORE_SEARCH = 1 , PRODUCT_SEARCH = 2, ALL_SEARCH = 3;
	public static final int WRITE = 1 , READ = 2;
	public static String useId = null;
	
	public static Scanner sc = new Scanner(System.in);
	public static void main(String[] args){
		
		boolean mainFlag = false;
		boolean loginFlag = false;
		int select = 0;
		
		program : 
		while (!mainFlag) {
			
			switch (displayMenu(select,LOGINMENU)) {
				case LOGIN : 		// 로그인
					loginFlag = login();
					break;
				case SIGNUP : 		// 회원가입
					signUp();
					break;
				case EXIT : 		// 종료
					System.out.println("프로그램을 종료합니다.");
					break program;
			}
			
			while (loginFlag) {
				switch (displayMenu(select,MAINMENU)) {
					case USER_INFO : 	// 유저정보
						select();
						break;
					case UPDATE :		// 정보수정
						update();
						break;
					case SEARHCH : 		// 매장,상품검색
						search();
						break;
					case ORDER : 		// 주문
						order();
						break;
					case LOGOUT : 		// 로그아웃
						System.out.println("로그아웃");
						loginFlag = false;
						break;
					case CS : 			// 고객센터
						csService();
						break;
					case MAINEXIT: 		// 종료
						System.out.println("프로그램을 종료합니다.");
						break program;
				}
			}
				
		}
	}

	/** 유저 정보 출력 */
	public static void select() { 

		DBConnection dbc = new DBConnection();
		dbc.connect();
		
		int orderCount = dbc.getOrderFunc(useId);
		
		System.out.println("현재까지 "+ orderCount + "번 주문 하였습니다.");
		
		List<User> list = new ArrayList<User>();
		
		list = dbc.select(useId);
		
		for (User user : list) {
			System.out.println(user);
		}
		
		dbc.close();
	}
	
	/** 유저 정보 수정 */
	public static void update() {
		DBConnection dbc = new DBConnection();
		dbc.connect();
		String password = null;
		String phone = null;
		
		try {
			System.out.println("1.수정 || 2.취소");
			int select = sc.nextInt();
			boolean value = checkInputPattern(String.valueOf(select),SELECT);
			if (!value) return;	
			
			if (select == 2) return;
			
			System.out.println("비밀번호를 입력하세요 (숫자 5~10 글자)");
			password = sc.nextLine();
			value = checkInputPattern(password,4);
			if (!value) return;
			
			System.out.println("비밀번호를 한번 더 입력하세요");		
			if (!(password.equals(sc.nextLine()))) {
				System.out.println("비밀번호가 맞지 않습니다.");
				return;
			}
			
			System.out.println("핸드폰 번호를 입력하세요 (010-0000-0000)");
			phone = sc.nextLine();
			
			value = checkInputPattern(phone,6);
			if (!value) return;
		
		} catch (InputMismatchException e) {
			System.out.println("잘못 입력하였습니다." + e.getMessage());
			sc.nextLine();
			return;
		} catch (Exception e) {
			System.out.println("잘못 입력하였습니다." + e.getMessage());
			return;
		}
	
		
		int returnUpdateValue = dbc.update(password, phone, useId);
		if (returnUpdateValue == -1) {
			System.out.println("수정 정보가 없습니다.");
			return;
		}
		System.out.println("정보 수정을 완료하였습니다.");
	}

	/** 상품 정보 검색 */
	public static void search() {
		DBConnection dbc = new DBConnection();
		dbc.connect();
		String searchData = null;
		boolean value = false;
		List<Store> list = new ArrayList<Store>();
		
		try {
			int productCount = dbc.getfroductFunc();
			
			System.out.println("현재 "+ productCount + "개의 상품이 등록 되어있습니다.");
		
			System.out.println("1.매장검색 || 2.상품검색 || 3.전체보기");
			int select = sc.nextInt();
			
			value = checkInputPattern(String.valueOf(select),MAINMENU);
			if (!value) return;	
			sc.nextLine();
			
			switch (select) {
				case STORE_SEARCH :
					System.out.println("검색할 매장명을 입력하세요");
					searchData = sc.nextLine();
					value = checkInputPattern(String.valueOf(searchData),STORE);
					if (!value) return;
					list = dbc.selectStore(searchData,select);
					
					if (list.size() <= 0) {
						System.out.println("매장 정보가 없습니다.");
						return;
					}
						System.out.println(list.get(0).toStoreString());
					break;
				case PRODUCT_SEARCH :
					System.out.println("검색할 상품명을 입력하세요");
					searchData = sc.nextLine();
					value = checkInputPattern(String.valueOf(searchData),STORE);
					if (!value) return;
				case ALL_SEARCH :
					list = dbc.selectStore(searchData,select);
					
					if (list.size() <= 0) {
						System.out.println("상품 정보가 없습니다.");
						return;
					}
					for (Store store : list) {
						System.out.println(store.toString());
					}
			}
			dbc.close();
			
		} catch (InputMismatchException e) {
			System.out.println("잘못 입력하였습니다." + e.getMessage());
			sc.nextLine();
			return;
		} catch (Exception e) {
			System.out.println("잘못 입력하였습니다." + e.getMessage());
			return;
		}
		
	}
	
	/** 상품 주문 */
	public static void order() {
		DBConnection dbc = new DBConnection();
		dbc.connect();
		boolean value = false;
		List<Store> list = new ArrayList<Store>();
		int insertReturnValue = -1;
		
			try {
				System.out.println("주문할 매장명을 입력하세요");
				String StoreName = sc.nextLine();
				value = checkInputPattern(StoreName,STORE);
				if (!value) return;
				list = dbc.selectStore(StoreName,1);
	
				if (list.size() <= 0) {
					System.out.println("매장 정보가 없습니다.");
					return;
				}
				
				System.out.println("메뉴 정보 입니다.");
				
				for (Store store : list) {
					System.out.println(store.toString());
				}
				
				System.out.println("―".repeat(89)+"\n어떤 상품을 주문할까요?");
				String productName = sc.nextLine();
				value = checkInputPattern(String.valueOf(productName),STORE);
				if (!value) return;
				
				list = dbc.selectStore(productName,2);
				
				if (list.size() <= 0) {
					System.out.println("상품 정보가 없습니다.");
					return;
				}
				
				Store st = list.get(0);
					
				System.out.println("몇개를 주문할까요? (1~5)");
				int orderAmount = sc.nextInt();
				value = checkInputPattern(String.valueOf(orderAmount),ORDERS);
				if (!value) return;
				
				insertReturnValue = dbc.order(useId,st,orderAmount);

				if (insertReturnValue != 1) {
					System.out.println("주문이 불가합니다.");
					return;
				}
				
				System.out.println("주문을 완료하였습니다.");
							
		} catch (InputMismatchException e) {
			System.out.println("잘못 입력하였습니다." + e.getMessage());
			sc.nextLine();
			return;
		} catch (Exception e) {
			System.out.println("잘못 입력하였습니다." + e.getMessage());
			return;
		} finally {
			dbc.close();
		}
			
	}
	
	/** 고객센터 */
	public static void csService() {
		DBConnection dbc = new DBConnection();
		dbc.connect();
		int insertReturnValue = -1;
		List<CsService> list = new ArrayList<CsService>();
		
		
		try {
			System.out.println("1.글남기기 || 2.글보기");
			int select = sc.nextInt();
			boolean value = checkInputPattern(String.valueOf(select),SELECT);
			if (!value) return;	
			
			switch (select) {
				case WRITE :
					System.out.println("남길 글을 입력하세요 (0~50자)");
					String text = sc.nextLine();

					
					insertReturnValue = dbc.insertCs(useId,text);
					
					if (insertReturnValue == -1 || insertReturnValue == 0) {
						System.out.println("등록 실패입니다.");
					} else {
						System.out.println("글을 등록하였습니다.");
					}
					break;
				case READ :
					
					list = dbc.selectCsService(useId);
					
					if (list.size() <= 0) {
						System.out.println("글 정보가 없습니다.");
					}

					for (CsService data  : list) {
						System.out.println(data.toString());
					}
					break;
			}
			
		} catch (InputMismatchException e) {
			System.out.println("정확히 입력하세요");
			sc.nextLine();
			return;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		} finally {
			dbc.close();
		}
	}
	
	/** 로그인 */
	public static boolean login() {
		System.out.println("아이디를 입력하세요");
		String id = sc.nextLine();
		boolean value = checkInputPattern(id,ID);
		if (!value) return false;		
		
		System.out.println("비밀번호를 입력하세요");
		String password = sc.nextLine();
		value = checkInputPattern(password,PASSWORD);
		if (!value) return false;
		
		DBConnection dbc = new DBConnection();
		dbc.connect();
		boolean isId_checkPassword = dbc.login(id,password);
		
		useId = id;
		return isId_checkPassword;
	}

	/** 회원가입 */
	public static void signUp() {
		DBConnection dbc = new DBConnection();
		dbc.connect();
		int insertReturnValue = -1;

		try {
			System.out.println("회원가입을 진행합니다.");
			
			System.out.println("아이디를 입력하세요 (영어 5~10 글자)");
			String id = sc.nextLine();
			boolean value = checkInputPattern(id,ID);
			if (!value) return;		
			
			boolean isId_checkPassword = dbc.login(id,null);
			if (isId_checkPassword == true) return;
			
			System.out.println("비밀번호를 입력하세요 (숫자 5~10 글자)");
			String password = sc.nextLine();
			value = checkInputPattern(password,PASSWORD);
			if (!value) return;

			System.out.println("비밀번호를 한번 더 입력하세요");		
			if (!(password.equals(sc.nextLine()))) {
				System.out.println("비밀번호가 맞지 않습니다.");
				return;
			}
			
			System.out.println("이름을 입력하세요 (한글 1~5자)");
			String name = sc.nextLine();
			value = checkInputPattern(name,NAME);
			if (!value) return;

				
			System.out.println("핸드폰 번호를 입력하세요 (010-0000-0000)");
			String phone = sc.nextLine();
			
			value = checkInputPattern(phone,PHONE);
			if (!value) return;
			
			insertReturnValue = dbc.insert(new User(id, password, name, phone, 0, 0, "D"));
			
			if (insertReturnValue == -1 || insertReturnValue == 0) {
				System.out.println("회원가입 실패입니다.");
			} else {
				System.out.println("회원가입 성공입니다.");
			}
	
			dbc.close();
	
		} catch (InputMismatchException e) {
			System.out.println("정확히 입력하세요");
			sc.nextLine();
			return;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}
	}
	
	/** 메뉴 */
	public static int displayMenu(int select, int type) {
		boolean value = false;
		try {
			
			switch (type) {
				case LOGINMENU:
					System.out.println("〓".repeat(70));
					System.out.println("1.로그인 || 2.회원가입 || 3.종료");
					System.out.println("〓".repeat(70));
					select = sc.nextInt();
							
					value = checkInputPattern(String.valueOf(select),1);
					if (!value) return 0;
					break;
				case MAINMENU:
					System.out.println("〓".repeat(70));
					System.out.println("1.유저정보 || 2.정보수정 || 3.상품검색 || 4.상품주문 || 5.로그아웃 || 6.고객센터 || 7.종료");
					System.out.println("〓".repeat(70));
					select = sc.nextInt();
									
					value = checkInputPattern(String.valueOf(select),2);
					if (!value) return 0;
					break;
			}

		} catch (InputMismatchException e) {
			System.out.println("숫자를 입력하세요");
		} finally {
			sc.nextLine();
		}
		return select;
	}
	
	/** 패턴검색 */
	public static boolean checkInputPattern(String data, int patternType) {
		String pattern = null;
		boolean regex = false;
		String message = null;

		switch (patternType) {
		case LOGINMENU:
			pattern = "^[1-3]$";
			message = "다시 입력하세요 (1~3)";
			break;
		case MAINMENU: 
			pattern = "^[1-7]$";
			message = "다시 입력하세요 (1~7)";
			break;
		case ID: // id
			pattern = "^[a-zA-Z]{5,10}$";
			message = "다시 입력하세요 (영어 5~10자)";
			break;
		case PASSWORD: // password
			pattern = "^[0-9]{5,10}$";
			message = "다시 입력하세요 (숫자 5~10자)";
			break;
		case NAME: // 이름
			pattern = "^[가-힝]{1,5}$";
			message = "다시 입력하세요";
			break;
		case PHONE: // 핸드폰
			pattern = "^010-[0-9]{4}-[0-9]{4}$";
			message = "전화번호를 다시 입력하세요 (010-0000-0000)";
			break;
		case SELECT: // 1,2 선택
			pattern = "^[1-2]$";
			message = "다시 입력하세요 (1~2)";
			sc.nextLine();
			break;
		case STORE: // 매장,상품검색
			pattern = "^[가-힝]{1,10}$";
			message = "다시 입력하세요 (한글 1~10자)";
			break;
		case ORDERS: // 주문수량
			pattern = "^[1-5]$";
			message = "다시 입력하세요 (1~5)";
			break;
		}

		regex = Pattern.matches(pattern, data);

		if (!regex) {
			System.out.println(message);
			return false;
		}
		
		return regex;
	}
}
