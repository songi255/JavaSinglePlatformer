package udpPlatform.unit.server;

import java.nio.ByteBuffer;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import udpPlatform.server.DataBox;
import udpPlatform.server.Server;
import udpPlatform.server.ServerEngine;
import udpPlatform.unit.Obj;

public abstract class Obj_Server extends Obj{
	/* 설명 : 서버에서 사용할 정보, 메서드만 모음, 게임로직관련 계산
	 * 버전 : 1.0
	 * 상태 : 미완성
	 * 작성자 : 신효재
	 * 마지막 작성일 : 20/05/18
	 * TODO
	 * */
	//Databox 만들기
	static DataBox dataBox = DataBox.getInstance();
	static ServerEngine engine = dataBox.getEngine();

	//알람 남은 시간들을 의미한다.
	int[] alarm = new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};

	//생성자
	public Obj_Server(int x, int y, int id, double speed, double direction, int team) {
		//XXX
		this(x, y, id, speed, direction, 16,32, team);
	}

	//생성자 2
	public Obj_Server(int x, int y, int id, double speed, double direction, int width, int height, int team) {
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
		this.prevX = x;
		this.prevY = y;
		this.xSpeed = speed * Math.cos(direction/180*Math.PI);
		this.ySpeed = speed * Math.sin(direction/180*Math.PI);
		this.direction = direction;
		this.id = id;
		this.bounds = new Rectangle(x,y,width,height);
		this.team = team;
		onCreate();
	}

	private Rectangle bounds;

	public Shape getBounds() {
		return this.bounds;
	}

	//한 라운드의 실행을 정의.
	public void round() {
		runAlarm();
		beforeStep();
		step();
		endStep();
		CollisionCheck();
		otherEvent();
		update();
	}

	//매 라운드 시작전에 실행되는 것. 속력 업데이트 TODO 살짝 바꿔야 할 듯 하다.
	public void beforeStep() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.y += this.ySpeed;
		this.x += this.xSpeed;
		this.ySpeed += this.gravity;
		//bound설정
		this.bounds.setX(x);
		this.bounds.setY(y);
		this.bounds.setRotate(direction);
	}

	//매 라운드마다 실행되는 것.
	protected void step() {
	}

	//한 라운드의 끝에 실행될 메서드
	protected void endStep() {
	}

	//직렬화
	protected ByteBuffer serialize() {
		return null;
	}

	//충돌 판정 메서드
	public boolean collisionCheck(Shape bounds) {
		return this.bounds.intersects(bounds.getBoundsInLocal());
	}

	//업데이트하도록 보낸다.
	public void update() {
		ByteBuffer buffer = serialize();
		if (buffer != null) {
			dataBox.getServer().pushPacket(buffer);
		}
	}

	//생성시
	public void onCreate() {
		//create 패킷 보낸다.
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
		server.pushPacket(buffer);
	}

	//삭제시
	public void onDelete() {
		System.out.println(ObjKind.name() + "삭제");
		dataBox.getObjects().remove(this);

		Server server = dataBox.getServer();

		ByteBuffer buffer = ByteBuffer.allocate(50);

		buffer.put((byte)Server.Command.DELETE.ordinal());
		buffer.putInt((int)id);

		server.pushPacket(buffer);
	}

	//충돌확인 및 실행
	public void CollisionCheck() {

		onCollision_Solid();
		onCollision_object();
	}

	//솔리드와 충돌시
	protected void onCollision_Solid() {

	}

	//오브젝트와 충돌시
	protected void onCollision_object() {

	}

	//기타 이벤트들. 필요하다면 여기에 추가하자.
	protected void otherEvent() {

	}

	//밖으로 나갔을 시
	protected void roomOut() {

	}

	//삭제
	public void deleteObj(Obj_Server obj) {
		engine.obj_delete(obj);
	}

	//생성
	public void createObj(ObjList objKind, int x, int y, double speed, double degree, int team) {
		engine.obj_create(objKind, x, y, speed, degree, team);
	}

	private void runAlarm() {
		for (int i = 0; i < alarm.length; i++) {
			if (alarm[i] != -1) {
				alarm[i]--;
				if (alarm[i] == 0) {
					//성능을 위해 일부러 하드코딩함.
					switch (i) {
						case 0:
							alarm0();
							break;
						case 1:
							alarm1();
							break;
						case 2:
							alarm2();
							break;
						case 3:
							alarm3();
							break;
						case 4:
							alarm4();
							break;
						case 5:
							alarm5();
							break;
						case 6:
							alarm6();
							break;
						case 7:
							alarm7();
							break;
						case 8:
							alarm8();
							break;
						case 9:
							alarm9();
							break;
						default:
							break;
					}
				}
			}
		}
	}

	public void setAlarm(int alarmNum, int time) {
		if (alarmNum > 9 || alarmNum < 0 || time < 1)
			return;
		alarm[alarmNum] = time;
		//알람이 실행되기전에 재호출되면 덮어써짐을 주의하자
	}

	//알람 필요하면 정의해서 쓰자.
	protected void alarm0() {

	}
	protected void alarm1() {

	}
	protected void alarm2() {

	}
	protected void alarm3() {

	}
	protected void alarm4() {

	}
	protected void alarm5() {

	}
	protected void alarm6() {

	}
	protected void alarm7() {

	}
	protected void alarm8() {

	}
	protected void alarm9() {

	}

}
