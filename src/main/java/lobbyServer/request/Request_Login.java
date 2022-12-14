package lobbyServer.request;

import lobbyServer.dbconn.Account;
import lobbyServer.dbconn.Dao;
import lobbyServer.response.Response_Login;
import lobbyServer.response.Response;

public class Request_Login extends Request{
	private String id;
	private String pw;

	public Request_Login(String id, String pw) {
		super();
		this.id = id;
		this.pw = pw;
	}

	public String getId() {
		return id;
	}

	public String getPw() {
		return pw;
	}

	@Override
	public Response doRequest() {
		Dao dao = Dao.getInstance();
		Account account = dao.select_account(id);
		//아이디가 없거나, 비밀번호가 일치하지 않으면
		Response response = null;
		if ( account.getId() == null) {
			response = new Response_Login(false, "존재하지 않는 아이디입니다.", null);
		}
		else if(!account.getPw().equals(pw)){
			response = new Response_Login(false, "비밀번호가 일치하지 않습니다.", null);
		}
		else {
			response = new Response_Login(true, null, account);
			client.setAccount(account);
		}
		return response;
	}
}
