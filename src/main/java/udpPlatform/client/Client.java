package udpPlatform.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.NotYetConnectedException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import udpPlatform.server.Server.Command;
import udpPlatform.server.Server.Request;

public class Client {
	//클라이언트단 네트워킹 담당 클래스
	private DatagramChannel channel;
	private int id;
	private final String SERVER_ADDRESS;
	private static final int SERVER_PORT = 5000;
	private static final int CLIENT_PORT = 5003;
	// 서버로부터 받은 패킷 리스트
	private Queue<ByteBuffer> packets = new LinkedBlockingQueue<ByteBuffer>();
	// 서버로 보낼 패킷 리스트
	private Queue<ByteBuffer> keyInputs = new LinkedBlockingQueue<ByteBuffer>();
	private boolean isConnected = false;
	// 받음을 확실히 하는 변수
	private int packetCount = 0;
	private ThreadGroup threadGroup;
	private boolean isOpen;

	// 생성자
	public Client(String serverAddress) {
		System.out.println("Client생성");
		this.SERVER_ADDRESS = serverAddress;
		this.threadGroup = new ThreadGroup("client");
	}

	// 연결요청 메서드 TODO 닉네임도 보내자.
	public void connect() {
		Thread thread = new Thread(threadGroup, () -> {
			try {
				while (!isConnected) {
					ByteBuffer buffer = ByteBuffer.allocate(50);
					buffer.put((byte) Request.REGISTER.ordinal());
					// TODO 닉네임쓰기
					buffer.flip();
					channel.send(buffer, new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT));

					System.out.println(channel.getLocalAddress() + "connect");

					System.out.println("connect시도");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						break;
					}
				}
				if (Thread.interrupted()) {
					return;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		thread.start();
	}

	public void startClient() {
		System.out.println("startClient");
		setOpen(true);
		try {
			this.channel = DatagramChannel.open(StandardProtocolFamily.INET);
			this.channel.bind(new InetSocketAddress(CLIENT_PORT));

			receive();
			connect();
			sendKeys();
		} catch (IOException e) {
			stopClient();
		}
	}

	public void stopClient() {
		System.out.println("stopClient");
		if (this.channel != null && this.channel.isOpen()) {
			try {
				this.channel.close();
			} catch (IOException e) {
			}
		}
		threadGroup.interrupt();
		setOpen(false);
	}

	// send하는 메서드.
	public void send(ByteBuffer buffer) {
		try {
			buffer.flip();
			channel.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NotYetConnectedException e) {
			System.out.println("not connected!");
		}
	}

	// 키입력 보내기 시작하는 메서드
	public void sendKeys() {
		Thread thread = new Thread(threadGroup, () -> {
			while (true) {
				try {
					ByteBuffer buffer = keyInputs.poll();
					if (buffer == null) {
						synchronized (keyInputs) {
							keyInputs.wait();
						}
						continue;
					}
					send(buffer);
					System.out.println("sended");
				}catch (InterruptedException e) {
					break;
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	// 외부에서 키입력 바이트를 넣는 메서드
	public void pushKeyInput(ByteBuffer buffer) {
		synchronized (keyInputs) {
			//키입력이 없으면
			if (buffer.position() == 1) {
				//XXX
				buffer.put((byte)'n');
			}
			keyInputs.offer(buffer);
			keyInputs.notify();
			System.out.println("keypushed");
		}
	}

	// 패킷 수신하여 packets에 넣는 스레드 실행.
	// TODO 여기서 Patch를 넣는게 맞는 것도 같다.
	public void receive() {
		Thread thread = new Thread(threadGroup, () -> {
			while (true) {
				try {
					ByteBuffer buffer = ByteBuffer.allocate(50);
					SocketAddress address = null;


					System.out.println(channel.getLocalAddress() + "recieve");

					// 연결 됬으면 read한다.(read가 더 빠르다)
					System.out.println("현재 내 주소 : " + channel.getLocalAddress());
					if (!isConnected) {
						address = channel.receive(buffer);
						System.out.println(address + "received 호출!!!!!!!!!!!!");
					} else {
						channel.read(buffer);
						System.out.println("read 호출!!!!!!!!!!!!");
					}
					buffer.flip();

					//XXX 도착 확인 코드
					try {
						buffer.rewind();
						System.out.println(buffer);
						while(true) {
							System.out.print(buffer.get());
						}
					} catch (Exception e) {
						System.out.println("라는 패킷 도착!!");
						buffer.rewind();
					}

					Command command = Command.values()[buffer.get()];
					System.out.println(command.name() + " : " + buffer);
					switch (command) {
						case CONFIRM:
							int round = buffer.getInt();
							// 이 라운드에 관해 응답 받았다고 보낸다.
							ByteBuffer response = ByteBuffer.allocate(50);
							response.put((byte) Request.CONFIRM.ordinal());
							response.putInt(round);
							send(response);

							// 제 라운드에 도착 잘 했다면 break하지 않고 조건검사하여 적용한다.
							if (packetCount == round) {
								packetCount++;
								command = Command.values()[buffer.get()];
							}
							else {
								break;
							}
						case CONNECT:
							if (!isConnected) {
								this.id = buffer.get();
								channel.connect(address);
								isConnected = true;
								//XXX
								DataBox.getInstance().setMyTeam(this.id);
							}
							break;
						case CREATE:
						case UPDATE:
						case DELETE:
							synchronized (packets) {
								packets.offer(buffer);
								packets.notifyAll();
							}
							break;
						default:
							break;
					}

				}catch (PortUnreachableException e1) {
					e1.printStackTrace();
					stopClient();
					break;
				}catch (Exception e) {
					e.printStackTrace();
				}
				if (Thread.interrupted()) {
					return;
				}
			}
		});
		thread.start();
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public int getId() {
		return this.id;
	}

	public Queue<ByteBuffer> getPackets() {
		return this.packets;
	}
}
