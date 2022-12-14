package udpPlatform.client.room;

import java.util.List;
import java.util.Vector;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import udpPlatform.client.resources.ResourceBundle;
import udpPlatform.unit.client.Player_Client;

public abstract class Room {
	//상속받은 룸에서 해야할 일
	//백그라운드 등록
	//TODO 여기에 오브젝트들도 넣어야할까?

	//백그라운드 리스트
	protected ResourceBundle resourceBundle = ResourceBundle.getInstance();
	protected List<Background> backgrounds = new Vector<Background>();
	protected View view;
	protected String[] fileNames;
	protected GraphicsContext gc;
	//XXX
	protected int roomWidth = 1280;
	protected int roomHeight = 720;

	//생성자
	public Room(GraphicsContext gc) {
		this.gc = gc;
		//뷰 생성
		view = new View(gc);
		//백그라운드 세팅
		setBackgrounds();
	}

	public void setViewFollow(Player_Client follow) {
		view.setFollow(follow);
	}

	public void setRoomSize(int roomWidth, int roomHeight) {
		this.roomWidth = roomWidth;
		this.roomHeight = roomHeight;
	}

	//오버라이드하여 사용하자.
	protected abstract void setBackgrounds();

	//화면, view 등등 계산
	public void step() {
		for (int i = 0; i < backgrounds.size(); i++) {
			Background background = backgrounds.get(i);
			background.step();
			//XXX 각각 정해주도록 하자.
			if (i > 0) {
				background.setLocation(view.x/(i+0.2), background.imageY);
			}


		}
		view.step();
	}

	//화면 그리기
	public void draw(GraphicsContext gc) {
		view.setDraw();
		for (Background background : backgrounds) {
			background.draw();
		}
	}
}
