package udpPlatform.unit.server;

public abstract class Obj_Solid_Server extends Obj_Server {
	//블럭같이 존재하기만 하고 행동은 없는 오브젝트를 의미한다.

	public Obj_Solid_Server(int x, int y, int id, int width, int height) {
		super(x, y, id, 0, 0, width, height, -1);
	}

	//아무 이벤트가 없다.
	@Override
	public void step() {

	}
}
