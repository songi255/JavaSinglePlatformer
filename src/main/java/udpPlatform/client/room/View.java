package udpPlatform.client.room;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;
import udpPlatform.unit.client.Player_Client;

public class View {
	//실제 오브젝트 배치상에서 보여줄 구역
	double x;
	double y;
	double width = 640;
	double height = 360;

	//추종자
	Player_Client follow;
	GraphicsContext gc;

	//실제 캔버스의 width, height
	double screenWidth = 1280;
	double screenHeight = 720;

	public View(GraphicsContext gc) {
		this.gc = gc;
		screenWidth = gc.getCanvas().getWidth();
		screenHeight = gc.getCanvas().getHeight();
	}

	public void setFollow(Player_Client follow) {
		this.follow = follow;
	}

	//계산
	public void step() {
		if (follow == null) {
			return;
		}

		//현재위치에서 destination으로 내분하여 서서히 다가가게 하자.
		double ratio = 6;
		double destination = 0;

		if (follow.isRight()) {
			destination = follow.getCenterX() - this.width / 3;
		}
		else {
			destination = follow.getCenterX() - this.width / 3 * 2;
		}
		this.x = (this.x * ratio + destination)/(ratio + 1);

		if (follow.getySpeed() > 0 ) {
			destination = follow.getCenterY() - this.height / 3;
		}
		else {
			destination = follow.getCenterY() - this.height / 3 * 2;
		}

		this.y = (this.y * ratio + destination)/(ratio + 1);

		//XXX
		//Roomwidth, RoomHeight 추가해야 할듯하다
		if ((this.x + width) > 1280) {
			this.x = 1280 - width;
		}
		else if(this.x < 0) {
			this.x = 0;
		}
		if ((this.y + height) > 720) {
			this.y = 720 - height;
		}
		else if(this.y < 0) {
			this.y = 0;
		}
	}

	//XXX 흔들기
	public void shake() {

	}

	public void setDraw() {
		if (follow == null) {
			return;
		}
		Affine affine = new Affine();
		//pivot점이 외분점임
		affine.appendScale(screenWidth/width, screenHeight/height,
				(screenWidth*x)/(screenWidth - width),
				(screenHeight*y)/(screenHeight-height));
		gc.setTransform(affine);
	}
}
