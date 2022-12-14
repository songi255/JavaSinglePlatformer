package lobbyServer.request;

import java.util.List;

import lobbyServer.Room;
import lobbyServer.response.Response;
import lobbyServer.response.Response_RoomList;

public class Request_RoomList extends Request {

	public Request_RoomList() {
		super();
	}

	@Override
	public Response doRequest() {
		Response response = null;
		List<Room> roomList = lobbyServer.getRoomList();
		response = new Response_RoomList(true, "방 목록 가져오기", roomList);
		return response;
	}

}
