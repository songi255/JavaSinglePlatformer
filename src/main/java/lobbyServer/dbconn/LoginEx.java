package lobbyServer.dbconn;

public class LoginEx {

	public static void main(String[] args) {

		Dao dao = Dao.getInstance();

		Account login = dao.select_account("erp");
		System.out.println("찾은 ID/PW : " + login.getId() + "/" + login.getPw());


	}

}
