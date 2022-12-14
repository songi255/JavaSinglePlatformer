package udpPlatform.unit.client;

import java.nio.ByteBuffer;
import java.util.Map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import udpPlatform.client.DataBox;
import udpPlatform.client.particle.ParticleSystem;
import udpPlatform.client.resources.ResourceBundle;
import udpPlatform.unit.Obj;

public abstract class Obj_Client extends Obj{
	/* 설명 : 클라이언트에서 사용할 정보, 메서드만 모음.랜더링
	 * 버전 : 1.0
	 * 상태 : 미완성
	 * 작성자 : 신효재
	 * 마지막 수정 : 20/05/18
	 * TODO
	 * */

	//이미지 XXX 따로 이미지맵에서 한꺼번에 로드하는게 맞아보인다.
	public Image sprite;
	private Image imgForAnimating;
	private int curImgIdx;
	private int delayCount;
	protected static DataBox dataBox = DataBox.getInstance();
	protected static int myTeam = dataBox.getMyTeam();
	protected Map<Integer, Obj_Client> objects = dataBox.getObjects();
	protected ParticleSystem ps = dataBox.getParticleSystem();
	protected ResourceBundle resources = ResourceBundle.getInstance();


	public Obj_Client(int id, int x, int y, double speed, double direction, int team) {
		this.id = id;
		setX(x);
		setY(y);
		prevX = x;
		prevY = y;
		this.direction = direction;
		this.xSpeed = speed * Math.cos(direction/180*Math.PI);
		this.ySpeed = speed * Math.sin(direction/180*Math.PI);
		this.team = team;
	}

	//매 라운드 실행될 메서드
	public void round() {
		beginStep();
		step();
		Endstep();
	}

	public void beginStep() {

	}

	//매 라운드 거치는 단계
	public void step() {

	}

	public void Endstep() {

	}

	public void delete() {
		onDelete();
		objects.remove(id);
	}

	protected void onDelete() {
		System.out.println("총알파괴!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}

	//canvas에 draw할 내용.
	public abstract void draw(GraphicsContext gc);

	//패킷을 받아 역직렬화 후 적용. update를 의미한다.
	public void deserialize(ByteBuffer packet) {

	}

	public int getMyTeam() {
		return myTeam;
	}

	public void draw_sprite(GraphicsContext gc, Image sprite, int subimg, int delay, double x, double y, double xScale, double yScale, double rot, double alpha, boolean loop) {
		//무조건 정사각형일때만 작동한다!
		double width = sprite.getHeight();
		double height = sprite.getHeight();
		int spriteLen = (int) (sprite.getWidth()/height);
		if (subimg == -1) {
			if (sprite.equals(this.imgForAnimating)) {
				if (delayCount >= delay) {
					this.curImgIdx++;
					this.delayCount = 0;
				}
				else {
					this.delayCount++;
				}
			}
			else {
				this.imgForAnimating = sprite;
				this.curImgIdx = 0;
				this.delayCount = 0;
			}
		}
		else {
			this.curImgIdx = subimg;
			this.delayCount = 1;
		}
		if ((this.curImgIdx + 1) >= spriteLen) {
			if (loop) {
				this.curImgIdx = this.curImgIdx - spriteLen + 1;
			}
			else {
				this.curImgIdx = spriteLen - 1;
			}
		}
		gc.save();
		gc.setGlobalAlpha(alpha);
		Affine affine = gc.getTransform();
		affine.appendScale(xScale, yScale, getCenterX(), getCenterY());
		affine.appendRotation(rot, getCenterX(), getCenterY());
		gc.setTransform(affine);
		gc.drawImage(sprite, this.curImgIdx * width,
				0, width, height, x, y, width, height);
		gc.restore();
		//TODO 할 수 있다면 color도 구현하자.
	}
}













