package lobbyServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lobbyServer.LobbyServer.Client;
import lobbyServer.dbconn.Account;
import lobbyServer.dbconn.Dao;
import lobbyServer.request.Request;
import lobbyServer.request.Request_Update_Selection;
import lobbyServer.response.Response;
import lobbyServer.response.Response_Error;
import lobbyServer.response.Response_Update_Selection;

public class LobbyServer {
	Room room;
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	private List<Client> connections = new Vector<Client>();
	private List<Room> roomList = new LinkedList<Room>();
	private Dao dao;
	private static final int BUFFER_SIZE = 5000;
	private static final int SERVER_PORT = 5001;

	public List<Room> getRoomList() {
		return roomList;
	}

	public void addRoom(Room room) {
		roomList.add(room);
	}

	public void deleteRoom(Room room) {
		if (roomList.contains(room)) {
			roomList.remove(room);
		}
	}

	public List<Client> getClients() {
		return connections;
	}

	public LobbyServer() {
		//아래는 좋지 못한 코드.
		Request.setServer(this);
	}

	public void startServer() {
		try {
			this.dao = Dao.getInstance();
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.bind(new InetSocketAddress(SERVER_PORT));
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("lobbyserver opened! : " + serverSocketChannel.getLocalAddress());
		} catch (Exception e) {
			if(serverSocketChannel != null && serverSocketChannel.isOpen()) { stopServer(); }
			return;
		}

		//selector 돌리는 스레드
		Thread thread = new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						int keyCount = selector.select();
						if(keyCount == 0) { continue; }
						Set<SelectionKey> selectedKeys = selector.selectedKeys();
						Iterator<SelectionKey> iterator = selectedKeys.iterator();
						while (iterator.hasNext()) {
							SelectionKey selectionKey = iterator.next();
							if (selectionKey.isAcceptable()) {
								accept(selectionKey);
							} else if (selectionKey.isReadable()) {
								Client client = (Client)selectionKey.attachment();
								client.receive(selectionKey);
							} else if (selectionKey.isWritable()) {
								Client client = (Client)selectionKey.attachment();
								client.send(selectionKey);
							}
							iterator.remove();
						}
					} catch (Exception e) {
						if(serverSocketChannel.isOpen()) { stopServer(); }
						break;
					}
				}
			}
		};
		thread.start();
		System.out.println("LobbyServer start!");
	}

	public void stopServer() {
		try {
			Iterator<Client> iterator = connections.iterator();
			while(iterator.hasNext()) {
				Client client = iterator.next();
				client.socketChannel.close();
				iterator.remove();
			}
			if(serverSocketChannel!=null && serverSocketChannel.isOpen()) {
				serverSocketChannel.close();
			}
			if(selector!=null && selector.isOpen()) {
				selector.close();
			}
		} catch (Exception e) {}
		System.out.println("lobbyServer stopped!");
	}

	private void accept(SelectionKey selectionKey) {
		try {
			//FIXME selector라고 하여 만능은 아님. 접속자가 많아지면 결국 작업이 너무 쌓여 처리속도가 느려질것임.. 여기서 connections의 크기를 체크하며
			//일정인원까지만 받아야함.
			ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
			SocketChannel socketChannel = serverSocketChannel.accept();

			Client client = new Client(socketChannel);
			connections.add(client);
			System.out.println("접속 요청 수락 : " + socketChannel.getLocalAddress() + ", 총 " + connections.size() + "명");
		} catch(Exception e) {
			if(serverSocketChannel.isOpen()) { stopServer(); }
		}
	}


	public class Client {
		private SocketChannel socketChannel;
		private String sendData;
		private Room room;
		private Room prevRoom;
		private Account account;

		Client(SocketChannel socketChannel) throws IOException {
			this.socketChannel = socketChannel;
			this.account = new Account();
			socketChannel.configureBlocking(false);
			SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
			selectionKey.attach(this);
			System.out.println("client 생성!");
		}

		void receive(SelectionKey selectionKey) {
			try {
				ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);

				//상대방이 비정상 종료를 했을 경우 자동 IOException 발생
				int byteCount = socketChannel.read(byteBuffer);

				//상대방이 SocketChannel의 close() 메소드를 호출할 경우
				if(byteCount == -1) {
					throw new IOException();
				}

				byteBuffer.flip();
				Charset charset = Charset.forName("UTF-8");
				String data = charset.decode(byteBuffer).toString();
				System.out.println("클라이언트로부터 받음 : " + data);

				JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
				String className = jsonObject.get("className").getAsString();

				Gson gson = new GsonBuilder().create();
				Request request = (Request)gson.fromJson(jsonObject, Class.forName(className));
				request.setClient(this);

				Response response = request.doRequest();

				String serialized = gson.toJson(response);
				if (response.isBroadcast()) {
					List<Client> clients = null;
					if (this.room == null) {
						clients = new Vector<>();
						clients.addAll(this.prevRoom.getClients());
						clients.add(this);
					}
					else {
						clients = this.room.getClients();
					}
					for (Client client : clients) {
						client.sendData = serialized;
						SelectionKey key = client.socketChannel.keyFor(selector);
						key.interestOps(SelectionKey.OP_WRITE);
						selector.wakeup();
					}
				}
				else {
					sendData = serialized;
					SelectionKey key = socketChannel.keyFor(selector);
					key.interestOps(SelectionKey.OP_WRITE);
					selector.wakeup();
				}
			} catch(Exception e) {
				try {
					System.out.println("exception 발생!!!!!!!!!!!!!!!");
					connections.remove(this);
					socketChannel.close();

					//방안에 있었을 때 나오면
					if (this.room != null) {
						Room room = this.room;
						room.exitRoom(this);
						if (room.isEmpty()) {
							deleteRoom(room);
							System.out.println("방삭제!");
						}
						List<Account> accounts = room.getClients().stream().map(Client::getAccount).collect(Collectors.toList());

						Response response = new Response_Update_Selection(true, accounts);
						Gson gson = new GsonBuilder().create();
						String serialized = gson.toJson(response);

						List<Client> clients = room.getClients();
						for (Client client : clients) {
							client.sendData = serialized;
							SelectionKey key = client.socketChannel.keyFor(selector);
							key.interestOps(SelectionKey.OP_WRITE);
							selector.wakeup();
						}
					}
				} catch (Exception e2) {e2.printStackTrace();}
			}
			System.out.println("client로 부터 request받음!!");
		}

		private void send(SelectionKey selectionKey) {
			try {
				Charset charset = Charset.forName("UTF-8");
				ByteBuffer byteBuffer = charset.encode(sendData);
				socketChannel.write(byteBuffer);
				System.out.println("클라이언트에게 보냄 : " + sendData);
				selectionKey.interestOps(SelectionKey.OP_READ);
				selector.wakeup();
			} catch(Exception e) {
				try {
					connections.remove(this);
					socketChannel.close();
				} catch (IOException e2) {}
			}
		}

		public void setRoom(Room room) {
			this.prevRoom = this.room;
			this.room = room;
		}

		public Room getRoom() {
			return this.room;
		}

		public Room getPrevRoom() {
			return this.prevRoom;
		}

		public Account getAccount() {
			return account;
		}

		public void setAccount(Account account) {
			this.account = account;
		}

		//XXX
		public InetSocketAddress getAddress() {
			try {
				return (InetSocketAddress) socketChannel.getRemoteAddress();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Client) {
				Client client = (Client) obj;
				if (client.account.getNum() == this.account.getNum()) {
					return true;
				}
			}
			return false;
		}
	}
}
