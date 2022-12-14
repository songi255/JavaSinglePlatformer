package udpPlatform.unit.server;

import java.nio.ByteBuffer;

import javafx.scene.shape.Line;
import udpPlatform.server.Server.Command;

public class Dummy_Server extends Obj_Server{

	double hp = 500;
	int bulletNum = 10;
	int remainBulletNum = 10;

	public Dummy_Server(int x, int y, int id, double speed, double direction, int team) {
		super(x, y, id, speed, direction, 50, 50, team);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		setAlarm(0, 100);
	}

	@Override
	protected void onCollision_object() {
		for (Obj_Server obj : dataBox.getObjects()) {
			if (obj instanceof Bullet_Server) {
				Bullet_Server bullet = (Bullet_Server) obj;
				Line line = new Line(bullet.getPrevX(),bullet.getPrevY(),bullet.getX(),bullet.getY());
				if (collisionCheck(line)) {
					hp -= bullet.damage;
					deleteObj(bullet);
					update();
				}
			}
		}
	}

	@Override
	protected ByteBuffer serialize() {
		ByteBuffer buffer = ByteBuffer.allocate(50);
		buffer.put((byte)Command.UPDATE.ordinal());
		buffer.putInt((int)id);
		buffer.putInt((int)hp);
		return buffer;
	}

	@Override
	protected void step() {
		super.step();
		if (hp < 0) {
			deleteObj(this);
		}
	}

	@Override
	protected void alarm0() {
		remainBulletNum = bulletNum;
		setAlarm(1, 100);
	}

	@Override
	protected void alarm1() {
		if (remainBulletNum > 0) {
			createObj(ObjList.Bullet, (int)getCenterX() + 50, (int)getCenterY(), 20, 10 - 20*Math.random(), getTeam());
			remainBulletNum--;
			setAlarm(1, 2);
		}
		else {
			setAlarm(0, 1);
		}
	}
}
