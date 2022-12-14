package udpPlatform.unit.server;

public class System_Server extends Obj_Server{

	public System_Server(int x, int y, int id, double speed, double direction, int team) {
		super(x, y, id, speed, direction, team);
	}

	@Override
	public void onCreate() {
		System.out.println(ObjKind.name() + "생성");
		dataBox.getObjects().add(this);
	}

	@Override
	public void beforeStep() {
		int playerCnt = 0;
		int dummyCnt = 0;
		for (Obj_Server obj : dataBox.getObjects()) {
			if (obj instanceof Player_Server) {
				playerCnt++;
			}
			else if(obj instanceof Dummy_Server) {
				dummyCnt++;
			}
		}
		if (playerCnt <= 1 && dummyCnt == 0) {
			//XXX
			dataBox.getServer().stopServer();
			engine.stopEngine();
		}
	}


}
