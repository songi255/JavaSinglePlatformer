package udpPlatform.server;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import udpPlatform.unit.Obj.ObjList;
import udpPlatform.unit.server.Block_Server;
import udpPlatform.unit.server.Bullet_Server;
import udpPlatform.unit.server.Dummy_Server;
import udpPlatform.unit.server.Obj_Server;
import udpPlatform.unit.server.Obj_Solid_Server;
import udpPlatform.unit.server.Player_Server;
import udpPlatform.unit.server.System_Server;

public class ServerEngine {
	//로직계산만 수행하는 클래스이다. 이것저것 생각할 필요 없음.
	private static final int INTERVAL = 33;
	private Set<Obj_Server> objects;
	private Set<Obj_Solid_Server> solids;
	private Timer executor;
	private DataBox dataBox = DataBox.getInstance();
	private ObjFactory objFactory = new ObjFactory();
	//추가, 삭제될 오브젝트들이 일괄처리된다.
	private Queue<CreateRequest> createQueue = new LinkedBlockingQueue<>();
	private Queue<Obj_Server> deleteQueue = new LinkedBlockingQueue<Obj_Server>();

	//생성자
	public ServerEngine() {
		this.objects = dataBox.getObjects();
		this.solids = dataBox.getSolids();
		dataBox.setEngine(this);
	}

	//맵을 로드하는 메서드.
	public void loadMap(String mapName) {
		//TODO 맵로드 제대로 수정하도록 하자.
		System.out.println("loadMap");
		//XXX 임시 stub
		List<Client> clients = dataBox.getClients();
		for (Client client : clients) {
			objFactory.createPlayer(50, 50, client);
		}
		for (int i = 0; i * 32 < 1280; i++ ) {
			objFactory.createSolid(ObjList.Block, i * 32, 688);
		}

		for (int i = 5; i * 32 < 30 * 32; i++ ) {
			objFactory.createSolid(ObjList.Block, i * 32, 622);
		}

		for (int i = 5; i * 32 < 30 * 32; i++ ) {
			objFactory.createSolid(ObjList.Block, i * 32, 520);
		}

		for (int i = 5; i * 32 < 30 * 32; i++ ) {
			objFactory.createSolid(ObjList.Block, i * 32, 450);
		}

		objFactory.createObj(ObjList.Dummy, 200, 400, 0, 0, -1);
		objFactory.createObj(ObjList.System, 0, 0, 0, 0, -1);
	}

	//초기 맵, 오브젝트등을 생성하게 하는 코드
	public void initialize() {
		Vector<Client> clients = (Vector<Client>) dataBox.getClients();
		//연결이 덜 되었으면 기다린다.
		if (clients.size() < clients.capacity()) {
			synchronized (clients) {
				try {
					clients.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		//다 되었다면 해당되는 player를 생성한다.
		loadMap("");
	}

	public void startEngine() {
		initialize();

		executor = new Timer("ServerEngineTimer");
		//TODO 매 라운드 실행될 루프이다.
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				//create작업
				while(!createQueue.isEmpty()) {
					CreateRequest request = createQueue.poll();
					objFactory.createObj(request.getObjKind(),request.getX(),request.getY(),request.getSpeed(),request.getDegree(), request.getTeam());
				}
				//delete작업
				while(!deleteQueue.isEmpty()) {
					Obj_Server obj = deleteQueue.poll();
					obj.onDelete();
					objects.remove(obj);
				}
				for (Obj_Server object : objects) {
					object.round();
				}
			}
		};
		executor.schedule(task, 0, INTERVAL);
	}

	public void stopEngine() {
		System.out.println("stopServerEngine");
		if (this.executor != null) {
			this.executor.cancel();
		}
		if (dataBox != null) {
			dataBox.close();
		}
	}

	//create큐에 넣을 요청객체
	private class CreateRequest{
		ObjList objKind;
		int x;
		int y;
		double speed;
		double degree;
		int team;

		public CreateRequest(ObjList objKind, int x, int y, double speed, double degree, int team) {
			super();
			this.objKind = objKind;
			this.x = x;
			this.y = y;
			this.speed = speed;
			this.degree = degree;
			this.team = team;
		}

		public int getTeam() {
			return this.team;
		}

		public ObjList getObjKind() {
			return objKind;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public double getSpeed() {
			return speed;
		}

		public double getDegree() {
			return degree;
		}
	}

	//움직이는 오브젝트 생성(큐에 넣고 다음라운드에 실행)
	public void obj_create(ObjList objKind, int x, int y, double speed, double degree, int team) {
		createQueue.offer(new CreateRequest(objKind, x, y, speed, degree, team));
	}

	public void obj_delete(Obj_Server obj) {
		deleteQueue.offer(obj);
	}

	private class ObjFactory{
		//시작을 10으로 한 이유는, player을 생성함에 있어서 client의 id를 적용해야 하므로... 충돌을 피하기 위해..
		private int id = 10;

		private int getId() {
			return id++;
		}

		public void createPlayer(int x, int y, Client client) {
			Player_Server player = new Player_Server(x, y, client.getId(), client.getTeam());
			player.setClient(client);
			objects.add(player);
		}

		public void createSolid(ObjList objKind, int x, int y) {
			switch (objKind) {
				case Block:
					Block_Server block = new Block_Server(x, y, getId());
					solids.add(block);
					break;
				default:
					break;
			}
		}

		public void createObj(ObjList objKind, int x, int y, double speed, double degree, int team) {
			switch (objKind) {
				case Bullet:
					Bullet_Server bullet = new Bullet_Server(x, y, getId(), speed, degree, team);
					objects.add(bullet);
					break;
				case Dummy:
					Dummy_Server dummy = new Dummy_Server(x, y, getId(), speed, degree, team);
					objects.add(dummy);
					break;
				case System:
					System_Server system = new System_Server(x, y, getId(), speed, degree, team);
					objects.add(system);
					break;
				default:
					break;
			}
		}
	}
}
