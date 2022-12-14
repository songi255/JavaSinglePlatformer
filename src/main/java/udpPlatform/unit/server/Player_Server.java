package udpPlatform.unit.server;

import java.nio.ByteBuffer;
import java.util.ConcurrentModificationException;

import javafx.scene.shape.Line;
import udpPlatform.server.Client;
import udpPlatform.server.Server.Command;
import udpPlatform.unit.server.playerState.StateMachine_server;

public class Player_Server extends Obj_Server{

	private StateMachine_server state;
	private Client client;
	private boolean canJump = true;
	private double hp = 100;
	private boolean canShot = true;
	private boolean isRight = true;
	//XXX
	private double xFriction = 0.1;
	private double yFriction = 0.1;

	//임시d
	public Player_Server(int x, int y, int id, int team) {
		super(x, y, id, 0, 0, 16, 20, team);
		this.width = 16;
		this.height = 20;
		this.gravity = 1;
		this.state = new StateMachine_server(this);
	}

	@Override
	public void beforeStep() {
		super.beforeStep();
		this.state.transferCheck();
		this.state.beforeStep();
	}

	@Override
	public void step() {
		super.step();
		//XXX
		if (hp < 0) {
			deleteObj(this);
		}
		checkKeyInput();
	}

	@Override
	protected void endStep() {
		super.endStep();
	}

	//XXX
	@Override
	public void onCollision_Solid() {
		super.onCollision_Solid();
		Line line = new Line(getPrevX() + width/2,getPrevY() + height,getCenterX(),getY() + height);
		//XXX 조건 더 줘서 좌우로도 이동못하게 해야햠;
		for(Obj_Solid_Server solid : dataBox.getSolids()) {
			if (solid.collisionCheck(line) && ySpeed > 0) {
				System.out.println("collisionCheck!!!!!!!!!!!!!!");
				setY(prevY);	//prevY 원복
				setY(solid.getY() - this.height);
				this.ySpeed = 0;
				this.canJump = true;
				this.state.Collsion_solid();
				break;
			}
		}
	}

	//XXX
	@Override
	public void onCollision_object() {
		super.onCollision_object();
		for (Obj_Server object : dataBox.getObjects()) {
			if (object instanceof Bullet_Server) {
				Bullet_Server bullet = (Bullet_Server) object;
				if (bullet.getTeam() == getTeam()) {
					continue;
				}
				Line line = new Line(bullet.getPrevX(), bullet.getPrevY(), bullet.getX(), bullet.getY());
				if (collisionCheck(line)) {
					System.out.println("총맞음!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					deleteObj(bullet);
					this.hp -= bullet.damage;
					setRight(bullet.getPrevX() >= getX() ? true : false);
					this.state.Collision_Object();
				}
			}
		}
	}

	//직렬화
	@Override
	public ByteBuffer serialize() {
		ByteBuffer buffer = ByteBuffer.allocate(50);
		buffer.put((byte)Command.UPDATE.ordinal());
		buffer.putInt((int)id);
		buffer.put((byte)this.state.getState().ordinal());
		buffer.putInt((int)x);
		buffer.putInt((int)y);
		buffer.putInt((int)hp);
		return buffer;
	}

	//clientSetter
	public void setClient(Client client) {
		this.client = client;
	}

	//클라에서 키입력 가져와서 각 키 실행한다. TODO 키 입력처리하고 해야된다.
	public void checkKeyInput() {
		if (this.client != null) {
			try {
				this.client.getKeys().forEach(this::onKeyPressed);
			} catch (Exception e) {e.printStackTrace();}
		}
	}

	//특정 키가 눌리면 실행될 메서드
	public void onKeyPressed(byte key) {
		switch (key) {
			case 'w':
				state.jump();
				break;
			case 'a':
				state.moveLeft();
				break;
			case 's':
				break;
			case 'd':
				state.moveRight();
				break;
			//XXX
			case '/':
				if (canShot) {
					if (isRight) {
						createObj(ObjList.Bullet, (int)x+8, (int)y+16, 15, 0 + 20-Math.random()*40, getTeam());
					}
					else {
						createObj(ObjList.Bullet, (int)x+8, (int)y+16, 15, 180 + 20-Math.random()*40, getTeam());
					}
					canShot = false;
					setAlarm(0, 3);
				}
				break;
			case 'n':
				state.stop();
				break;
			default:
				break;
		}
	}

	@Override
	protected void otherEvent() {
		super.otherEvent();
		roomOut();
	}

	@Override
	protected void roomOut() {
		super.roomOut();
		if (getX() < 0) {
			//FIXME previous단위로도 조작해야할듯.
			setX(1280);
			setX(1280);
		}
		else if(getX()>1280) {
			setX(0);
			setX(0);
		}
	}

	@Override
	protected void alarm0() {
		canShot = true;
	}

	@Override
	public void setX(double x) {
		super.setX(x);
	}

	@Override
	public void setY(double y) {
		super.setY(y);
	}

	public void setHp(double hp) {
		this.hp = hp;
	}

	public void setRight(boolean isRight) {
		this.isRight = isRight;
	}

	public boolean isRight() {
		return isRight;
	}

	public void setXspeed(double xSpeed) {
		this.xSpeed = xSpeed;
	}
	public void setYspeed(double ySpeed) {
		this.ySpeed = ySpeed;
	}

	public boolean canJump() {
		return this.canJump;
	}

	public void setJump(boolean canJump) {
		this.canJump = canJump;
	}
}
