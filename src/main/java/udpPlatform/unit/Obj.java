package udpPlatform.unit;

public abstract class Obj implements Comparable<Obj>{
	/* 설명 : 게임 상 모든 유닛의 조상클래스
	 * 버전 : 1.0
	 * 상태 : 미완성
	 * 작성자 : 신효재
	 * 마지막 수정 : 20/05/18
	 * TODO
	 * */
	public static enum ObjList{
		Obj,
		Player,
		Block,
		Bullet,
		Dummy,
		System
	}

	//자신의 Obj종류.
	protected ObjList ObjKind;
	//ObjKind 초기화
	{
		//Class clazz = MethodHandles.lookup().lookupClass();
		//TODO 아래 코드는 오브젝트들이 생성될 때마다 만들어지는것. 성능은 좋지 않을 것이다. 나중에 고치도록 하자.
		Class clazz = getClass();
		String name = clazz.getSimpleName();
		name = name.split("_")[0];
		System.out.println(name + "!!!!!!!!!!!!!!!!!!!!!!!!!!");
		ObjKind = ObjList.valueOf(name);
	}

	//좌표, 속도, id
	protected int id;
	protected double x;
	protected double y;
	protected double prevX;
	protected double prevY;
	protected double xSpeed;
	protected double ySpeed;
	protected double gravity;
	protected double direction;	//degree
	protected double width;
	protected double height;
	protected int team;

	public int getTeam() {
		return team;
	}

	public void setX(double x) {
		this.prevX = this.x;
		this.x = x;
	}

	public void setY(double y) {
		this.prevY = this.y;
		this.y = y;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	};

	public double getxSpeed() {
		return xSpeed;
	}

	public double getySpeed() {
		return ySpeed;
	}

	public double getPrevX() {
		return prevX;
	}

	public double getPrevY() {
		return prevY;
	}

	//Set에 넣기위해 구현.
	@Override
	public int compareTo(Obj o) {
		return this.id - o.id;
	}

	public double getCenterX() {
		return this.x + this.width/2;
	}

	public double getCenterY() {
		return this.y + this.height/2;
	}

}
