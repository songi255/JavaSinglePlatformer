package lobbyServer.request;

import lobbyServer.LobbyServer;
import lobbyServer.LobbyServer.Client;
import lobbyServer.dbconn.Dao;
import lobbyServer.response.Response;

public abstract class Request {
	//모든 request의 조상이 되는 클래스

	//아래 필드가 자신의 클래스이름을 json에 추가합니다.
	protected String className;
	//dao랑 서버는 어차피 모든 request가 같을거니까..
	protected static LobbyServer lobbyServer;
	protected static Client client;

	//생성자입니다. 여기서 이렇게 리플렉션해주면, 자식들이 모두 실행하여 각자 자신의 클래스이름을 필드에 저장합니다.
	public Request() {
		this.className = getClass().getName();
		System.out.println(className + "생성!");
	}

	//좋지 않은 코드. 임시방편이다.
	public static void setServer(LobbyServer server) {
		lobbyServer = server;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	//request를 실행하는 메서드.
	public abstract Response doRequest();



}
