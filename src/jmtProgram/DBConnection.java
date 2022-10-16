package jmtProgram;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBConnection {
	private Connection connection = null;

	/** connection */
	public void connect() {

		Properties properties = new Properties();

		try {
			FileInputStream fis = new FileInputStream("C:\\java_test\\jmtProgram\\src\\jmtProgram\\db.properties");
			properties.load(fis);
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException" + e.getMessage());
		} catch (IOException e) {
			System.out.println("Fproperties.load error" + e.getMessage());
		}

		try {
			Class.forName(properties.getProperty("driver"));
			connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("userid"),
													 properties.getProperty("password"));
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException" + e.getMessage());
		} catch (SQLException e) {
			System.out.println("connerction error" + e.getMessage());
		}

	}

	/** id,password check */
	public boolean login(String id, String password) {
		Statement st = null;
		ResultSet rs = null;
		
		String selectSearchQuery = "select userid from users where userid = '"+id+"'";
		
		try {
			if (password == null) { // 아이디 중복검사
				st = connection.createStatement();
				rs = st.executeQuery(selectSearchQuery);
				
				if(rs.next()) {
					if (id.equals(rs.getString("userid"))) {
						System.out.println("해당 아이디가 존재합니다.");
						return true;
					} 
				}
				return false;
			}
			
			selectSearchQuery = "select password from users where userid = '"+id+"'";
			st = connection.createStatement();
			rs = st.executeQuery(selectSearchQuery);
			
			if(rs.next()) {
				if (password.equals(rs.getString("password"))) {
					System.out.println("로그인 완료");
					return true;
				} else {
					System.out.println("비밀번호가 일치하지 않습니다.");
					return false;
				}
			}
		} catch (Exception e) {
			System.out.println("select error" + e.getMessage());
		} finally {
			try {
				if (st != null) st.close();
			} catch (SQLException e) {
				System.out.println("Statement close error" + e.getMessage());
			}
		}

		System.out.println("해당 아이디의 정보가 없습니다.");
		
		return false;
	}

	/** user signUp */
	public int insert(User user) {
		PreparedStatement ps = null;
		String signUpQuery = null;
		int insertReturnValue = -1;
		signUpQuery = "call signup(?,?,?,?);";
		
		try {
			ps = connection.prepareStatement(signUpQuery);
			ps.setString(1, user.getUserid());
			ps.setString(2, user.getPassword());
			ps.setString(3, user.getName());
			ps.setString(4, user.getPhone());
			insertReturnValue = ps.executeUpdate();
			} catch (SQLException e) {
				System.out.println("SQLException error" + e.getMessage());
			} catch (Exception e) {
				System.out.println("Exception error" + e.getMessage());
			} finally {
				try {
					if (ps != null) ps.close();
				} catch (SQLException e) {
					System.out.println("PreparedStatement close error" + e.getMessage());
				}
			}
			
		return insertReturnValue;
	}

	/** users select */
	public List<User> select(String useId) {
		List<User> list = new ArrayList<User>();
		Statement st = null;
		ResultSet rs = null;
		String selectQuery = "call userinfo('"+useId+"')";
		
		try {
			st = connection.createStatement();
			rs = st.executeQuery(selectQuery);

			if (!(rs != null || rs.isBeforeFirst())) return list;
	
			while (rs.next()) {
				String userid = rs.getString("userid");
				String password = rs.getString("password");
				String name = rs.getString("name");
				String phone = rs.getString("phone");
				int wallet = rs.getInt("wallet");
				int amount = rs.getInt("amount");
				String grade = rs.getString("grade");
				list.add(new User(userid, password, name, phone, wallet, amount, grade ));
			}

		} catch (Exception e) {
			System.out.println("Exception error" + e.getMessage());
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				System.out.println("Statement close error" + e.getMessage());
			}
		}
		return list;
	}

	/** users update */
	public int update(String password, String phone, String useId) {
		Statement st = null;

		int updateReturnValue = -1;
		String updateQuery = "call userupdate('"+password+"','"+phone+"','"+useId+"')";
		
		try {
			st = connection.createStatement();
			updateReturnValue = st.executeUpdate(updateQuery);
			
		} catch (Exception e) {
			System.out.println("Exception error" + e.getMessage());
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				System.out.println("Statement close error" + e.getMessage());
			}
		}
		
		return updateReturnValue;
	}

	/** stores select */
	public List<Store> selectStore(String Data, int select) {
		List<Store> list = new ArrayList<Store>();
		
		PreparedStatement ps = null;
		ResultSet rs = null;

		String selectQuery = "call select_store_products ('%%','%%')";
		
		try {
			if (select == 1) {
				selectQuery = "call select_store_products (?,'%%');";
			}
			if (select == 2) {
				selectQuery = "call select_store_products ('%%',?);";
			}
			
			
			ps = connection.prepareStatement(selectQuery);
			if (select == 1 || select == 2) {
				ps.setString(1, "%"+Data+"%");						
			}
			
			rs = ps.executeQuery();
			
			if (!(rs != null || rs.isBeforeFirst())) return list;
			
			while (rs.next()) {
				String storeName = rs.getString("storeName");
				String storePhone = rs.getString("storePhone");
				int no = rs.getInt("no");
				String productName = rs.getString("productName");			
				int price = rs.getInt("price");
				int stock = rs.getInt("stock");
				
				list.add(new Store(no, storeName, storePhone, productName, price, stock));
			}		
		} catch (Exception e) {
			System.out.println("Exception error" + e.getMessage());
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				System.out.println("PreparedStatement close error" + e.getMessage());
			}
		}
		
		return list;
	}

	/** order */
	public int order(String useId, Store st, int orderAmount) {
		PreparedStatement ps = null;
		String orderQuery = null;
		int insertReturnValue = -1;
		
		orderQuery = "call insert_orders (?,?,?,?)";
		
		try {
			ps = connection.prepareStatement(orderQuery);
			ps.setString(1, useId);
			ps.setString(2, st.getStoreName());
			ps.setString(3, st.getProductName());
			ps.setInt(4, orderAmount);
			insertReturnValue = ps.executeUpdate();
			} catch (SQLException e) {
				System.out.println("잔액이 부족합니다.");
			} catch (Exception e) {
				System.out.println("Exception error" + e.getMessage());
			} finally {
				try {
					if (ps != null) ps.close();
				} catch (SQLException e) {
					System.out.println("PreparedStatement close error" + e.getMessage());
				}
			}
			
		return insertReturnValue;
	}

	/** insert cs */
	public int insertCs(String useId, String text) {
		PreparedStatement ps = null;
		String insertQuery = null;
		int insertReturnValue = -1;
		
		insertQuery = "call insert_csService(?,?)";
		
		try {
			ps = connection.prepareStatement(insertQuery);
			ps.setString(1, useId);
			ps.setString(2, text);

			insertReturnValue = ps.executeUpdate();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} catch (Exception e) {
				System.out.println("Exception error" + e.getMessage());
			} finally {
				try {
					if (ps != null) ps.close();
				} catch (SQLException e) {
					System.out.println("PreparedStatement close error" + e.getMessage());
				}
			}
		
		
		return insertReturnValue;
	}

	/** select cs */
	public List<CsService> selectCsService(String useId) {
		PreparedStatement ps = null;
		String insertQuery = null;
		ResultSet rs = null;
		List<CsService> list = new ArrayList<CsService>();
		
		insertQuery = "call select_csService(?);";
		
		try {
			ps = connection.prepareStatement(insertQuery);
			ps.setString(1, useId);
			rs = ps.executeQuery();
			
			if (!(rs != null || rs.isBeforeFirst())) return list;
			
			
			while (rs.next()) {
				int no = rs.getInt("csNo");
				String userid = rs.getString("userid");
				String question = rs.getString("question");

				list.add(new CsService(no, userid, question));
			}

			} catch (SQLException e) {
				System.out.println("동일한 이름이 존재합니다.");
			} catch (Exception e) {
				System.out.println("Exception error" + e.getMessage());
			} finally {
				try {
					if (ps != null) ps.close();
				} catch (SQLException e) {
					System.out.println("PreparedStatement close error" + e.getMessage());
				}
			}
		
		return list;
	}

	/** getfroductFunc*/
	public int getfroductFunc() {
		Statement st = null;
		ResultSet rs = null;
		int count = 0;
		String getproductQuery = "SELECT getproductFunc()";
		try {
			st = connection.createStatement();
			rs = st.executeQuery(getproductQuery);
			if(!(rs != null || rs.isBeforeFirst())) {
				return count;
			}
			if(rs.next()) {
				count = rs.getInt("getproductFunc()");
			}
		} catch (Exception e) {
			System.out.println("DBConnection getproductFunc Error" + e.getMessage());
		}finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				System.out.println("Statement Close Error" + e.getMessage());
			}
		}
		return count;
	}

	/** getOrderFunc */
	public int getOrderFunc(String useId) {
		Statement st = null;
		ResultSet rs = null;
		int count = 0;
		String getOrderQuery = "SELECT getOrderFunc('"+useId+"')";
		try {
			st = connection.prepareStatement(getOrderQuery);
			rs = st.executeQuery(getOrderQuery);
			if(!(rs != null || rs.isBeforeFirst())) {
				return count;
			}
			if(rs.next()) {
				count = rs.getInt("getOrderFunc('"+useId+"')");
			}
		} catch (Exception e) {
			System.out.println("DBConnection getOrderFunc Error" + e.getMessage());
		}finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				System.out.println("Statement Close Error" + e.getMessage());
			}
		}
		return count;
	}
	
	/** Connection close */
	public void close() {
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			System.out.println("Connection close error" + e.getMessage());
		}
	}
}
