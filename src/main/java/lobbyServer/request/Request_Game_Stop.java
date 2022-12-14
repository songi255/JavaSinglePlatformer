package lobbyServer.request;

import lobbyServer.Room;
import lobbyServer.response.Response;
import lobbyServer.response.Response_Game_Stop;

public class Request_Game_Stop extends Request{

	@Override
	public Response doRequest() {
		Room room = client.getRoom();
		room.setOnGame(false);
		client.getAccount().getSelection().setReady(false);

		Response response = new Response_Game_Stop(true);
		return response;
	}
}
