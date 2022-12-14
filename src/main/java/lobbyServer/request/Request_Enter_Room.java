package lobbyServer.request;

import java.util.List;
import java.util.stream.Collectors;

import lobbyServer.LobbyServer.Client;
import lobbyServer.Room;
import lobbyServer.dbconn.Account;
import lobbyServer.response.Response;
import lobbyServer.response.Response_Enter_Room;

public class Request_Enter_Room extends Request {
	private int roomNo;

	public Request_Enter_Room(int roomNo) {
		this.roomNo = roomNo;
	}

	@Override
	public Response doRequest() {
		Response response = null;
		List<Room> rooms = lobbyServer.getRoomList();
		Room desRoom = null;
		for (Room room : rooms) {
			if (room.getRoomNo() == this.roomNo) {
				desRoom = room;
				break;
			}
		}

		if (desRoom != null && desRoom.enterRoom(client)) {
			List<Client> clients = desRoom.getClients();
			List<Account> accountList = clients.stream().map(Client::getAccount).collect(Collectors.toList());

			response = new Response_Enter_Room(true, "입장 성공!!!", desRoom, accountList);
		}
		else {
			response = new Response_Enter_Room(false, "입장 실패!?!?!?!?", null, null);
		}
		return response;
	}
}
