package udpPlatform.client;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import udpPlatform.client.particle.Particle;
import udpPlatform.client.particle.ParticleSystem;
import udpPlatform.client.resources.ResourceBundle;
import udpPlatform.unit.client.Obj_Client;

public class DataBox {
	//Client단에서 클래스간 데이터를 주고받을 싱글턴 클래스
	private static DataBox dataBox;
	private Map<Integer, Obj_Client> objects;
	private int myTeam = -1;
	private ParticleSystem ps;
	private ResourceBundle resourceBundle = ResourceBundle.getInstance();

	public void setParticleSystem(ParticleSystem ps) {
		this.ps = ps;
	}

	public ParticleSystem getParticleSystem() {
		return this.ps;
	}

	public void setMyTeam(int myTeam) {
		this.myTeam = myTeam;
	}

	public int getMyTeam() {
		return this.myTeam;
	}

	private DataBox() {
		objects = new ConcurrentHashMap<Integer, Obj_Client>();
	}

	public Map<Integer, Obj_Client> getObjects() {
		return objects;
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
