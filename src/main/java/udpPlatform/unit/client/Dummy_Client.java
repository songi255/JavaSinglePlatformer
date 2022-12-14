package udpPlatform.unit.client;

import java.nio.ByteBuffer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Dummy_Client extends Obj_Client{

	double hp = 500;

	public Dummy_Client(int id, int x, int y, double speed, double direction, int team) {
		super(id, x, y, speed, direction, team);
	}

	@Override
	public void deserialize(ByteBuffer packet) {
		hp = packet.getInt();
	}

	//XXX
	@Override
	public void draw(GraphicsContext gc) {
		gc.setFill(Color.PURPLE);
		gc.fillRect(x, y, 50, 50);
		gc.strokeRect(x, y-15, 50, 8);
		if (hp>0) {
			gc.setFill(Color.DARKGRAY);
			gc.fillRect(x, y-15, 50*(hp/500), 8);
		}
		gc.setFill(Color.BLACK);
		gc.setFont(new Font(4));
		gc.fillText(hp + "/500", x+10, y-10);
	}
}