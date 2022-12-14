package udpPlatform.unit.server;

public class Block_Server extends Obj_Solid_Server{
	//임시
	{
		this.height = 32;
		this.width = 32;
	}

	//생성자
	public Block_Server(int x, int y, int id, int width, int height) {
		super(x, y, id, width, height);
	}

	public Block_Server(int x, int y, int id) {
		//TODO 인스턴스마다 말고, static으로 한번에 할 수 있었으면 좋겠다..
		super(x, y, id, 32, 32);
	}
}
