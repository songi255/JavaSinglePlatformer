package lobbyServer.request;

import lobbyServer.response.Response;
import lobbyServer.response.Response_Exit_Room;

public class Request_Exit_Room extends Request {

	@Override
	public Response doRequest() {
		Response response = new Response_Exit_Room(true, "방에서 나옴!!");
		return response;
	}

}
