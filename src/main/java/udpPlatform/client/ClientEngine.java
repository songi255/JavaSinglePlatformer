package udpPlatform.client;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Window;
import udpPlatform.client.particle.ParticleSystem;
import udpPlatform.client.resources.ResourceBundle;
import udpPlatform.client.room.Room;
import udpPlatform.client.room.Room_Test;
import udpPlatform.server.Server.Command;
import udpPlatform.server.Server.Request;
import udpPlatform.unit.Obj.ObjList;
import udpPlatform.unit.client.Block_Client;
import udpPlatform.unit.client.Bullet_Client;
import udpPlatform.unit.client.Dummy_Client;
import udpPlatform.unit.client.Obj_Client;
import udpPlatform.unit.client.Player_Client;

public class ClientEngine {
	// 클라이언트단 엔진. 연산이 필요하지 않다.
	// TODO 클라이언트단에서도 일정 간격마다 클래스의 메서드를 실행시켜주는 루프가 필요하다!!
	// Patch 큐에서 작업 뽑아 적용.
	// 랜더링
	// 서버 열기
	// 키입력
	// 걍 키입력을 클래스로 묶을까...
	private Queue<Patch> commands = new LinkedBlockingQueue<>();
	private Map<Integer, Obj_Client> objects;
	private List<String> keys = new LinkedList<String>();
	private Client client;

	private DataBox dataBox;
	private AnimationTimer render;
	private GraphicsContext gc;
	private static int INTERVAL = 33;
	//XXX
	private Room room;
	private ParticleSystem ps;
	private ThreadGroup threadGroup;
	private Timer timer;
	private Boolean isTerminated = false;

	// 생성자
	public ClientEngine(GraphicsContext gc, String serverAddress) {
		threadGroup = new ThreadGroup("clientEngine");
		dataBox = DataBox.getInstance();
		objects = dataBox.getObjects();
		this.gc = gc;
		ps = new ParticleSystem(gc);
		dataBox.setParticleSystem(ps);
		double width = gc.getCanvas().getWidth();
		double height = gc.getCanvas().getHeight();

		//XXX
		room = new Room_Test(gc);

		render = new AnimationTimer() {
			@Override
			public void handle(long now) {
				gc.clearRect(0, 0, width, height);
				room.draw(gc);
				objects.values().forEach(o -> o.draw(gc));
				ps.draw();
			}
		};

		// 키입력 야매
		Canvas canvas = gc.getCanvas();

		//XXX
		Thread thread = new Thread(threadGroup, ()->{
			while (true) {
				Scene scene = canvas.getScene();
				if (scene != null) {
					scene.setOnKeyTyped(e -> {
						String key = e.getCharacter();
						if (keys.contains(key)) {
							keys.remove(key);
						}
						keys.add(key);
						System.out.println(keys);
						sendKeys();
					});

					scene.setOnKeyReleased(e -> {
						String key = e.getText();
						if (keys.contains(key)) {
							keys.remove(key);
						}
						System.out.println(keys);
						sendKeys();
					});

					// 화면에서 포커스 떠나면 커맨드 초기화
					scene.windowProperty().addListener(new ChangeListener<Window>() {
						@Override
						public void changed(ObservableValue<? extends Window> observable, Window oldValue,
											Window newWindow) {
							newWindow.focusedProperty().addListener(new ChangeListener<Boolean>() {
								@Override
								public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
													Boolean newValue) {
									if (!client.isOpen()) {
										newWindow.focusedProperty().removeListener(this);
									}
									keys.clear();
									sendKeys();
								}
							});
							if (!client.isOpen()) {
								scene.windowProperty().removeListener(this);
							}
						}
					});
					break;
				}
			}
		});
		thread.start();
	}

	//TODO timer스레드 실행
	public void startEngine() {
		System.out.println("startClientEngine");
		patch();
		render.start();
		//patch와 render는 밖으로 뺸다.. 엄격한 동기화가 안되었기 때문에.. 또한 성능을 포기하더라도 레이턴시 때문에...
		timer = new Timer("clientEngineTimer");
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (!client.isOpen()) {
					stopEngine();
				}
				room.step();
				Collection<Obj_Client> objs = objects.values();
				for (Obj_Client object : objs) {
					object.round();
				}
				ps.process();
			}
		};
		timer.schedule(task, 0, INTERVAL);
	}

	public void stopEngine() {
		System.out.println("stopClientEngine");
		if (this.render != null) {
			render.stop();
		}
		if (this.client != null) {
			client.stopClient();
		}
		Scene scene = gc.getCanvas().getScene();
		scene.setOnKeyPressed(null);
		scene.setOnKeyReleased(null);
		scene.setOnKeyTyped(null);

		timer.cancel();
		threadGroup.interrupt();
		ResourceBundle.getInstance().unload();
		//XXX
		if (dataBox != null) {
			dataBox.close();
		}
		synchronized (isTerminated) {
			isTerminated.notifyAll();
		}
	}

	// 외부에서 command큐에 push하는 메서드
	public void pushCommand(Patch command) {
		this.commands.offer(command);
	}


	public void setClient(Client client) {
		this.client = client;
		//XXX ID로 했는데 팀으로 바꿔주자.
		dataBox.setMyTeam(client.getId());
	}

	public void sendKeys() {
		//클라이언트가 유효하고 연결된 경우
		if (!client.isOpen()) {
			stopEngine();
		}

		if (client != null && client.getId() != -1) {
			System.out.println("sendKeys");
			ByteBuffer buffer = ByteBuffer.allocate(50);
			buffer.put((byte)Request.KEYINPUT.ordinal());
			//buffer.put((byte)client.getId());
			try {
				keys.stream().forEach(k -> buffer.put((byte) k.charAt(0)));
				client.pushKeyInput(buffer);
			} catch (ConcurrentModificationException e) {
			}
		}
	}

	// 커맨드를 실행하는 스레드
	public void patch() {
		System.out.println("startPatch");
		Thread thread = new Thread(threadGroup, () -> {
			while (true) {
				try {
					Queue<ByteBuffer> packets = client.getPackets();
					ByteBuffer buffer = packets.poll();
					if (buffer == null) {
						synchronized (packets) {
							packets.wait();
							continue;
						}
					}

					Patch patch = null;
					buffer.rewind();
					Command command = Command.values()[buffer.get()];
					switch (command) {
						case CREATE:
							patch = new CreatePatch(buffer);
							break;
						case UPDATE:
							patch = new UpdatePatch(buffer);
							break;
						case DELETE:
							patch = new DeletePatch(buffer);
							break;
						default:
							break;
					}
					if (patch != null) {
						patch.execute();
						System.out.println("patched!");
					}
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

	// 커맨드 클래스
	abstract class Patch {
		// 커맨드를 실행하는 메서드
		protected ByteBuffer buffer;

		public Patch(ByteBuffer buffer) {
			this.buffer = buffer;
		}

		public abstract void execute();
	}

	// create
	class CreatePatch extends Patch {
		private ObjList objKind;
		private int id;
		private int x;
		private int y;
		private double speed;
		private double direction;
		private int team;

		public CreatePatch(ByteBuffer buffer) {
			super(buffer);
		}

		@Override
		public void execute() {
			objKind = ObjList.values()[buffer.get()];
			id = buffer.getInt();
			x = buffer.getInt();
			y = buffer.getInt();
			try {
				speed = buffer.getFloat();
				direction = buffer.getFloat();
				team = buffer.get();
			} catch (Exception e) {
			}
			createObj();
		}

		//TODO 리플렉션으로 할까? TODO 마저 완성하자.
		public void createObj() {
			Obj_Client obj = null;
			switch (objKind) {
				case Player:
					obj = new Player_Client(id, x, y, speed, direction, team);
					//XXX
					if (obj.getTeam() == obj.getMyTeam()) {
						room.setViewFollow((Player_Client)obj);
					}
					break;
				case Block:
					obj = new Block_Client(id, x, y, 0, 0, -1);
					break;
				case Bullet:
					//XXX
					obj = new Bullet_Client(id, x, y, speed, direction, team, buffer.getDouble());
					break;
				case Dummy:
					obj = new Dummy_Client(id, x, y, speed, direction, team);
					break;
				default:
					break;
			}
			if (obj != null) {
				objects.put(id, obj);
			}
		}
	}

	// update
	class UpdatePatch extends Patch {
		private int id;

		public UpdatePatch(ByteBuffer buffer) {
			super(buffer);
			id = buffer.getInt();
		}

		//TODO id는 읽은 상태로 넘겨줌
		@Override
		public void execute() {
			Obj_Client obj = objects.get(id);
			if (objects.containsKey(id)) {
				obj.deserialize(buffer);
			}
		}
	}

	// create
	class DeletePatch extends Patch {
		private int id;

		public DeletePatch(ByteBuffer buffer) {
			super(buffer);
			id = buffer.getInt();
		}

		@Override
		public void execute() {
			if (objects.containsKey(id)) {
				objects.get(id).delete();
				//objects.remove(id);
			}
		}
	}

	public void waitTerminate() {
		if (!(isTerminated.booleanValue())) {
			synchronized (isTerminated) {
				try {
					isTerminated.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("waitTerminate Return!");
	}
}
