package lobbyServer.dbconn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import client.Selection;

public class Dao {
	//TODO 서버 시작후 계속 연결되야하니까, 연결을 계속 유지하도록, 클래스는 하나만 만들어지도록 싱글턴으로 만드는게 좋습니다.
	private static final String USER_ID = "root";
	private static final String PASSWORD = "1234";
	//login에만 db를 사용하는 것이 아닐수도 있으니 db명은 수정하는게 좋을 듯 합니다.
	private static final String URL = "jdbc:mysql://localhost:3306/gamedb";
	private static Connection connection;
	private static Dao dao;

	private Dao() {
		try {
			System.out.println("생성자");
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(URL, USER_ID, PASSWORD);
			System.out.println("드라이버 연결 성공");
		} catch (Exception e) {
			System.out.println("드라이버 연결 실패");
			closeConn();
		}
	}

	//연결실패시 예외 발생
	public static Dao getInstance() {
		if (dao == null || connection == null) {
			dao = new Dao();
			try {
				if (connection.isClosed()) {
					throw new RuntimeException();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dao;
	}

	//TODO 매개변수의 이름이 text로 되어있었는데, 변수이름만보고 무엇인지 알 수 있도록 하면 좋습니다.
	public Account select_account(String id) {
		String sql = "select * from login where id = ?;";
		Account account = new Account();
		try {
			PreparedStatement prepstat = connection.prepareStatement(sql);
			prepstat.setString(1, id);
			ResultSet rs = prepstat.executeQuery();

			if(rs.next()) {
				account.setNum(rs.getInt(1));
				account.setId(rs.getString(2));
				account.setPw(rs.getString(3));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return account;
	}

	public void closeConn() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
