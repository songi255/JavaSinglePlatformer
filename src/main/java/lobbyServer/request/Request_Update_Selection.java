package lobbyServer.request;

import java.util.List;
import java.util.stream.Collectors;

import client.Selection;
import lobbyServer.LobbyServer.Client;
import lobbyServer.Room;
import lobbyServer.dbconn.Account;
import lobbyServer.response.Response;
import lobbyServer.response.Response_Update_Selection;

public class Request_Update_Selection extends Request{
	private Selection selection;

	public Request_Update_Selection(Selection selection) {
		super();
		this.selection = selection;
	}

	@Override
	public Response doRequest() {
		Response response= null;
		Room room = client.getRoom();
		//나갈거라면
		if (selection == null) {
			room.exitRoom(client);
			if (room.isEmpty()) {
				lobbyServer.deleteRoom(room);
				System.out.println("방삭제!");
			}
		}
		else {
			client.getAccount().setSelection(selection);
		}
		List<Account> accounts = room.getClients().stream().map(Client::getAccount).collect(Collectors.toList());

		response = new Response_Update_Selection(true, accounts);
		response.setBroadcast(true);
		return response;
	}

}
