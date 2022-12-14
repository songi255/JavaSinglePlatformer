package lobbyServer.request;

import lobbyServer.Room;
import lobbyServer.response.Response;
import lobbyServer.response.Response_Create_Room;

public class Request_Create_Room extends Request {

	private String title;
	private String password;
	private boolean isPublic;

	public Request_Create_Room(String title, String password, boolean isPublic) {
		super();
		this.title = title;
		this.password = password;
		this.isPublic = isPublic;
	}

	public String getTitle() {
		return title;
	}

	public String getPassword() {
		return password;
	}

	public boolean isPublic() {
		return isPublic;
	}

	@Override
	public Response doRequest() {
		Response response = null;
		if(getTitle().equals("") || getPassword().equals("")) {
			response = new Response_Create_Room(false, "모든정보를 입력해주세요!", null);
		}
		else {
			Room room = new Room(getTitle(), getPassword(), isPublic());
			lobbyServer.addRoom(room);
			room.enterRoom(client);
			response = new Response_Create_Room(true, "새로운 방이 등록되었습니다!", room);
		}
		return response;
	}

}
