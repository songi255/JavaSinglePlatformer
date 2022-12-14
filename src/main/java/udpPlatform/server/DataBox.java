package udpPlatform.server;

import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListSet;

import udpPlatform.unit.server.Obj_Server;
import udpPlatform.unit.server.Obj_Solid_Server;

public class DataBox {
	//서버용. 엔진, 서버, 오브젝트들 간 공유되는 정보들. 싱글턴으로 전역변수처럼 사용.
	private static DataBox dataBox;
	private Server server;
	private Set<Obj_Server> objects;
	private Set<Obj_Solid_Server> solids;
	private List<Client> clients;
	private ServerEngine engine;

	public ServerEngine getEngine() {
		return engine;
	}

	public void setEngine(ServerEngine engine) {
		this.engine = engine;
	}

	private DataBox() {
		this.objects = new ConcurrentSkipListSet<Obj_Server>();
		this.solids = new ConcurrentSkipListSet<Obj_Solid_Server>();
	}

	public void setClients(Vector<Client> clients) {
		this.clients = clients;
	}

	public List<Client> getClients() {
		return this.clients;
	}

	//서버 등록 및 클라이언트 벡터 등록
	public void setServer(Server server) {
		this.server = server;
		this.clients = server.getClients();
	}

	public Server getServer() {
		return this.server;
	}

	public Set<Obj_Server> getObjects() {
		return this.objects;
	}

	public Set<Obj_Solid_Server> getSolids(){
		return this.solids;
	}

	public static DataBox getInstance() {
		if (dataBox == null) {
			dataBox = new DataBox();
		}
		return dataBox;
	}

	public static void close() {
		dataBox = null;
	}
}
