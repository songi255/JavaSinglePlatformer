package udpPlatform.unit.server;

import java.nio.ByteBuffer;

import udpPlatform.server.Server;

public class Bullet_Server extends Obj_Server{
	double damage;

	@Override
	public void onCreate() {
		System.out.println(ObjKind.name() + "생성");
		dataBox.getObjects().add(this);

		Server server = dataBox.getServer();

		ByteBuffer buffer = ByteBuffer.allocate(50);

		buffer.put((byte)Server.Command.CREATE.ordinal());
		buffer.put((byte)ObjKind.ordinal());
		buffer.putInt((int)id);
		buffer.putInt((int)x);
		buffer.putInt((int)y);
		buffer.putFloat((float)Math.sqrt(Math.pow(xSpeed, 2) + Math.pow(ySpeed, 2)));
		buffer.putFloat((float)direction);
		buffer.put((byte)team);
		buffer.putDouble(damage);
		server.pushPacket(buffer);
	}

	public Bullet_Server(int x, int y, int id, double speed, double direction, int team) {
		super(x, y, id, speed, direction, 10, 10, team);
		this.width = 10;
		this.height = 10;
		this.damage = Math.random() * 2 * 10;
	}

	@Override
	protected void otherEvent() {
		super.otherEvent();
		roomOut();
	}


	@Override
	protected void roomOut() {
		if (getX() < 0 || getX() > 1280 || getY() > 720 || getY() < 0) {
			deleteObj(this);
		}
	}

	@Override
	public void step() {
		super.step();
	}
}
