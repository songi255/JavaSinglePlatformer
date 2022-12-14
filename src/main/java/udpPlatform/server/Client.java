package udpPlatform.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.util.LinkedList;
import java.util.List;

import udpPlatform.server.Server.Command;
import udpPlatform.server.Server.Request;

public class Client {
	//서버단 접속자 관리 클래스
	private DatagramChannel channel;
	private InetSocketAddress address;
	private List<Byte> keys;
	//클라이언트 식별자
	static int lastAddedId;
	private int id;
	private String nickname;
	//TODO Register에서 닉네임 받게하자.
	//confirm하게 send하기 위해 패킷마다 붙이는 id
	private int packetCount = 0;
	private int team;
	private ThreadGroup threadGroup;
	private boolean inturrupted;

	public int getTeam() {
		return this.team;
	}

	public Client(InetSocketAddress address, String nickname, int team) {
		try {
			//채널 열고 연결 설정
			this.threadGroup = new ThreadGroup("Server_client");
			channel = DatagramChannel.open(StandardProtocolFamily.INET);
			this.address = address;
			this.keys = new LinkedList<Byte>();
			this.id = lastAddedId++;
			this.nickname = nickname;
			//XXX 팀설정 제대로하자..
			this.team = id;
			receive();
			connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Client생성 : " + address);
	}

	public void stop() {
		System.out.println("stopServerClient");
		try {
			if (this.channel != null && this.channel.isOpen()) {
				if (this.channel.isConnected()) {
					this.channel.disconnect();
				}
				this.channel.close();
			}
			inturrupted = true;
			threadGroup.interrupt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//client와 쌍방향 connect를 유지하게 하는 것.
	private void connect() {
		try {
			//connect
			//XXX channel.connect(address);

			ByteBuffer buffer = ByteBuffer.allocate(50);
			buffer.put((byte)Command.CONFIRM.ordinal());
			buffer.putInt(packetCount);
			buffer.put((byte)Command.CONNECT.ordinal());
			buffer.put((byte)id);
			//XXX send_confirm(buffer);
			int round = packetCount;
			while(round == packetCount && !inturrupted) {
				try {
					buffer.flip();
					channel.send(buffer, address);

					//XXX 확인코드
					try {
						buffer.rewind();
						System.out.println(buffer);
						while(true) {
							System.out.print(buffer.get());
						}
					} catch (Exception e) {
						System.out.println(address + "로보냄!!!!!");
					}

					//응답 받을 때 까지 일정간격으로 계속 보낸다.
					Thread.sleep(33);
				} catch (Exception e) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void send(ByteBuffer buffer) {
		try {
			buffer.flip();
			channel.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//패킷 받아 분기처리하는 스레드
	public void receive() {
		Thread thread = new Thread(threadGroup, ()-> {
			while(true) {
				try {
					ByteBuffer buffer = ByteBuffer.allocate(50);
					if (channel.isConnected()) {
						channel.read(buffer);
					}
					else {

						channel.receive(buffer);
					}
					buffer.flip();

					//XXX 받았으면 확인
					try {
						while(true) {
							System.out.print(buffer.get());
						}
					} catch (Exception e) {
						System.out.println("!!!!!!!!!!!!!!!!!!!@#!@#!@");
						buffer.flip();
					}

					Request request = Request.values()[buffer.get()];
					switch (request) {
						case REGISTER:
							//등록은 이미 끝났으니 무시한다.(받을일도 없다)
							break;
						case CONFIRM:
							int count = buffer.getInt();
							//현재 수신대기중인 패킷이 들어왔다면(잘 받았다는 응답이 온 것이다.)
							if (count == packetCount) {
								packetCount++;	//confirm함수를 탈출시킴
								//request = Request.values()[buffer.get()];
								//헤더 뒤의 명령어를 받아 놓고 break 안하고 계속 switch검사
							}
							else {
								break;
							}
						case CONNECT:
							//XXX
							if (!channel.isConnected()) {
								System.out.println("connect안됬으니 연결함!!");
								channel.connect(address);
							}
							System.out.println("쌍방향 연결 완료!!");
							break;
						case KEYINPUT:
							System.out.println("key input!@@!@!@!@!@!@!@!@@!");
							setKeys(buffer);
							break;
						default:
							break;
					}
				}catch (ClosedByInterruptException e) {
					e.printStackTrace();
					stop();
					break;
				}catch (IOException e) {
					e.printStackTrace();
					stop();
					break;
				}			}
		});
		thread.start();
	}

	//확실하게 수신 확인하는 메서드 XXX 보내기를 큐를 사용하여 순차적으로 처리하도록 해야한다.....안그러면 스레드 터진다..
	//스레드풀을 쓸 필요도 없다.. 이건 순차적으로 보내야 하니깐....
	public void send_confirm(ByteBuffer buffer) {
//XXX 이부분은 broadcast영역에 넣자.
//		buffer.rewind();
//		//buffer에 confirm, count 헤더 추가
//		buffer.put((byte)Command.CONFIRM.ordinal());
//		buffer.putInt(packetCount);

		//send, 확인 받기
		int round = packetCount;
		while(round == packetCount && !inturrupted) {
			try {
				buffer.flip();
				channel.write(buffer);

				//XXX 확인코드
				try {
					buffer.rewind();
					System.out.println(buffer);
					while(true) {
						System.out.print(buffer.get());
					}
				} catch (Exception e) {
					System.out.println("보냄!!!!!");
				}

				//응답 받을 때 까지 일정간격으로 계속 보낸다.
				Thread.sleep(33);
			} catch (Exception e) {
			}
		}
	}

	public synchronized void setKeys(ByteBuffer buffer) {
		this.keys.clear();
		while(buffer.hasRemaining()) {
			this.keys.add(buffer.get());
		}
	}

	public synchronized List<Byte> getKeys() {
		return this.keys;
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	public int getId() {
		return this.id;
	}

	public String getNickname() {
		return this.nickname;
	}

}
