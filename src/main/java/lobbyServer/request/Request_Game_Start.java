package lobbyServer.request;

import java.net.InetSocketAddress;

import lobbyServer.Room;
import lobbyServer.response.Response;
import lobbyServer.response.Response_Game_Start;

public class Request_Game_Start extends Request{
	@Override
	public Response doRequest() {
		Room room = client.getRoom();
		boolean isReady = room.getClients().stream()
				.map(c->c.getAccount().getSelection().isReady())
				.reduce((c1,c2)->c1&&c2)
				.get();

		Response response = null;
		if (isReady) {
			InetSocketAddress inetAddress = client.getAddress();
			String address = inetAddress.getAddress().getHostAddress();
			response = new Response_Game_Start(true,address, room.getClients().size());
			room.setOnGame(true);
		}
		else {
			response = new Response_Game_Start(false,null, -1);
		}
		return response;
	}
}
