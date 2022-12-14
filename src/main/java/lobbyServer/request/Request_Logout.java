package lobbyServer.request;

import lobbyServer.response.Response_Logout;
import lobbyServer.dbconn.Account;
import lobbyServer.response.Response;

public class Request_Logout extends Request{

	public Request_Logout() {
		super();
	}

	@Override
	public Response doRequest() {
		client.setAccount(new Account());
		Response response = new Response_Logout(true, "로그아웃 성공");
		return response;
	}
}
