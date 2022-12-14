package udpPlatform.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
	//클라이언트에 보낼 명령어
	public static enum Command{
		CONFIRM,	//confirm_broadcast에 위임할 시 사용.
		CONNECT,	//accept되면 응답하는 것이다.
		CREATE,
		UPDATE,
		DELETE
	}

	//서버에 request할 시 사용하는 명령어
	public enum Request{
		CONFIRM,
		REGISTER,	//맨처음 등록
		CONNECT,	//CONNECT시도에 응답
		KEYINPUT
	}

	private static final int SERVER_PORT = 5000;
	private DatagramChannel serverChannel;
	private List<Client> clients;
	private int maxPlayer;
	//TODO blocking은 성능이 나쁘다는데 고려해보자.
	private Queue<ByteBuffer> sendingQueue = new LinkedBlockingQueue<ByteBuffer>();
	private ThreadGroup threadGroup;

	//생성자
	public Server(int maxPlayer) {
		this.maxPlayer = maxPlayer;
		this.clients = new Vector<Client>(maxPlayer);
		this.threadGroup = new ThreadGroup("server");
	}

	//서버시작
	public void startServer() {
		try {
			System.out.println("startServer");
			this.serverChannel = DatagramChannel.open();
			this.serverChannel.bind(new InetSocketAddress(SERVER_PORT));
			acceptAll();
			startBroadcast();
		} catch (IOException e) {
			stopServer();
		}
	}

	//TODO 제대로 수정해야 함.
	//maxPlayer수까지 연결받아주는 스레드.
	public void acceptAll() {
		Thread thread = new Thread(threadGroup, ()-> {
			int connected = 0;
			Set<String> receivedIp = new LinkedHashSet<>(maxPlayer);
			while(connected < maxPlayer) {
				try {
					if (Thread.interrupted()) {
						break;
					}
					System.out.println("대기");
					ByteBuffer buffer = ByteBuffer.allocate(50);
					SocketAddress address = serverChannel.receive(buffer);
					System.out.println("뭔가 받음");
					buffer.flip();

					//올일은 없지만 이상한게 왔으면 continue.
					Request request = Request.values()[buffer.get()];
					if (request != Request.REGISTER) {
						continue;
					}
					//XXX 닉네임 얻기
					String nickname = "";

					//주소 얻기
					StringTokenizer st = new StringTokenizer(address.toString(), "/:");
					String addr= st.nextToken();
					String port = st.nextToken();

					System.out.println(addr + " : " + port + "에서 요청들어옴!");
					//이미 받았던 주소이면
					if (receivedIp.contains(addr)) {
						System.out.println(port);
						continue;
					}
					else {
						receivedIp.add(addr);
					}

					//클라이언트 생성.//XXX 팀 설정 제대로 하자.
					Client client = new Client(new InetSocketAddress(addr, Integer.parseInt(port)), nickname, -1);
					this.clients.add(client);

					connected++;
					System.out.println("accept : " + connected);
				} catch (Exception e) {
					break;
				}
			}
			System.out.println("accept완료");
			//broadcast스레드를 깨운다. 아울러 serverEngine의 initialize 메소드의 블락을 해제하는 역할도 한다.
			synchronized (clients) {
				clients.notifyAll();
			}
		});
		thread.start();

	}

	//무한루프돌며 패킷 큐에서 계속 패킷 보내는 스레드
	public void startBroadcast() {
		Thread normalBrod = new Thread(threadGroup, ()-> {
			//플레이어가 모두 연결될 때 까지 대기
			if (clients.size() < maxPlayer) {
				try {
					synchronized (clients) {
						System.out.println("blocked");
						clients.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("startBroadcast");
			while(true) {
				try {
					ByteBuffer buffer = this.sendingQueue.poll();
					System.out.println("buffer : " + buffer);
					//가져올 게 없으면
					if (buffer == null) {
						synchronized (sendingQueue) {
							sendingQueue.wait();
						}
						continue;
					}
					broadcast(buffer);
				} catch (InterruptedException e) {
					break;
				}
			}
		});
		normalBrod.start();
	}

	//외부에서 sendingQueue에 push하는 메서드
	public void pushPacket(ByteBuffer buffer) {
		System.out.println("pushPacket : " + sendingQueue.size());
		this.sendingQueue.offer(buffer);
		synchronized (sendingQueue) {
			sendingQueue.notify();
		}
	}

	//서버종료
	public void stopServer() {
		System.out.println("stopServer");
		try {
			if (this.serverChannel != null && this.serverChannel.isOpen()) {
				this.serverChannel.close();
			}
			threadGroup.interrupt();
			this.clients.forEach(Client::stop);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//패킷 전송하는 메서드(신뢰도 x)
	public void broadcast(ByteBuffer buffer) {
		System.out.println("broadcast");
		for (Client client : clients) {
			try {
				client.send(buffer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//패킷 전송하는 메서드(신뢰도 o) TODO 구현해야 함.
	public void broadcast_confirm() {
	}

	public List<Client> getClients() {
		return this.clients;
	}
}



















