package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.*;
import com.google.gson.stream.MalformedJsonException;
import lobbyServer.request.Request;
import lobbyServer.response.Response;
import lobbyServer.dbconn.Account;

public class Client {
	private Selector selector;
	private SocketChannel socketChannel;
	private static final String SERVER_ADDRESS = "121.151.0.175";
	private static final int SERVER_PORT = 5001;
	private static final int BUFFER_SIZE = 5000;
	private boolean isConnected;
	private static Client client;
	private Account account;
	private boolean waiting;
	private boolean isSuper;
	private Timer reqTimeout = new Timer(true);

	public static Client getInstance() {
		if (client == null) {
			client = new Client();
		}
		return client;
	}

	private Client() {
	}

	public void startClient() {
		try {
			selector = Selector.open();
		} catch(Exception e) {
			if(socketChannel.isOpen()) { stopClient(); }
			return;
		}
		try {
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
			socketChannel.connect(new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT));

		} catch(Exception e) {
			if(socketChannel.isOpen()) { stopClient(); }
			return;
		}

		//selector 돌리는 스레드
		Runnable runnable = new Runnable() {
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
							if (selectionKey.isConnectable()) {
								connect(selectionKey);
							} else if(selectionKey.isReadable()) {
								receive(selectionKey);
							} else if(selectionKey.isWritable()) {
								send(selectionKey);
							}
							iterator.remove();
						}
					} catch (Exception e) {
						if(socketChannel.isOpen()) { stopClient(); }
						break;
					}
				}
			}
		};
		new Thread(runnable).start();
		System.out.println("client networking start!");
	}

	public void stopClient() {
		try {
			if(socketChannel!=null && socketChannel.isOpen()) {
				socketChannel.close();
			}
			if (selector != null && selector.isOpen()) {
				selector.close();
			}
		} catch (IOException e) {}
		System.out.println("client networking stop!");
	}

	public void connect(SelectionKey selectionKey) {
		try {
			socketChannel.finishConnect();
			selectionKey.interestOps(SelectionKey.OP_READ);
			isConnected = true;
			System.out.println("서버 연결 성공");
		} catch(Exception e) {
			if(socketChannel.isOpen()) { stopClient(); }
		}
	}



	private void receive(SelectionKey selectionKey) {
		try {
			ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);

			//서버가 비정상 종료를 했을 경우 자동 IOException 발생
			int byteCount = socketChannel.read(byteBuffer);

			//서버가 정상적으로 SocketChannel의 close() 메소드를 호출할 경우
			if(byteCount == -1) {
				throw new IOException();
			}

			//String 형태로 받음
			byteBuffer.flip();
			Charset charset = Charset.forName("UTF-8");
			String data = charset.decode(byteBuffer).toString();

			//response
			JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
			String className = jsonObject.get("className").getAsString();

			Gson gson = new GsonBuilder().create();
			Response response = (Response) gson.fromJson(jsonObject, Class.forName(className));
			System.out.println(Class.forName(className));
			response.execute();

			System.out.println("서버가 보낸 데이터를 받음! received!");
		}catch (JsonSyntaxException | MalformedJsonException e) {
			//버퍼가 밀렸을 경우
			//TODO 좋은 예외처리는 아니고, 후에 따로 생각해봐야할 듯하다.
			e.printStackTrace();
		}
		catch (IllegalStateException e1) {
			e1.printStackTrace();
		}
		catch(Exception e2) {
			e2.printStackTrace();
			stopClient();
		}
	}

	//채널에 write할때 실행됨.
	private void send(SelectionKey selectionKey) {
		try {
			ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();
			socketChannel.write(byteBuffer);
			selectionKey.interestOps(SelectionKey.OP_READ);
		} catch(Exception e) {
			stopClient();
		}
	}

	public void request(Request request) {
		if (!isConnected) {
			System.out.println("서버 연결되지 않았음!");
			return;
		}
		if (waiting) {
			System.out.println("응답 기다리는 중임!");
			setWaiting(true);
			return;
		}
		Charset charset = Charset.forName("UTF-8");

		Gson gson = new GsonBuilder().create();
		String serialized = gson.toJson(request);
		System.out.println("서버로 보냄 : " + serialized);

		ByteBuffer byteBuffer = charset.encode(serialized);
		//System.out.println(socketChannel.isConnected());
		//System.out.println(socketChannel.isRegistered());
		SelectionKey key = socketChannel.keyFor(selector);
		key.attach(byteBuffer);
		key.interestOps(SelectionKey.OP_WRITE);
		selector.wakeup();
		setWaiting(true);
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public boolean isWaiting() {
		return waiting;
	}

	public void setWaiting(boolean waiting) {
		this.waiting = waiting;
		if (waiting) {
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					if (!Client.this.waiting) {
						Client.this.waiting = true;
					}
				}
			};
			reqTimeout = new Timer(true);
			reqTimeout.schedule(task, 5000);
		}
		else {
			try {
				reqTimeout.cancel();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isSuper() {
		return isSuper;
	}

	public void setSuper(boolean isSuper) {
		this.isSuper = isSuper;
	}
}
