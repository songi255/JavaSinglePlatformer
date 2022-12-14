package lobbyServer;

import java.util.List;
import java.util.Vector;

import lobbyServer.LobbyServer.Client;

public class Room {
	private static int lastId = 1;
	private int roomNo;
	private String title;
	private String password;
	private boolean isPublic;
	private final int MAX_NUM = 4;
	private transient List<Client> clients;
	private int size = 0;
	private boolean isOnGame;

	public Room(String title, String password, boolean isPublic) {
		super();
		clients = new Vector<>(MAX_NUM);
		this.roomNo = lastId++;
		this.title = title;
		this.password = password;
		this.isPublic = isPublic;
	}

	//성공여부 반환
	public boolean enterRoom(Client client) {
		if (this.clients.size() >= MAX_NUM) {
			return false;
		}
		client.setRoom(this);
		clients.add(client);
		updateSize();
		return true;
	}

	public void exitRoom(Client client) {
		client.setRoom(null);
		client.getAccount().clearSelection();
		clients.remove(client);
		updateSize();
	}

	public int getRoomNo() {
		return roomNo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public String getSpace() {
		return this.size + "/" + MAX_NUM;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public List<Client> getClients() {
		return this.clients;
	}

	public boolean isEmpty() {
		return this.clients.size() == 0;
	}

	private void updateSize() {
		this.size = clients.size();
	}

	public int getSize() {
		return this.size;
	}

	public boolean isOnGame() {
		return isOnGame;
	}

	public void setOnGame(boolean isOnGame) {
		this.isOnGame = isOnGame;
	}
}
