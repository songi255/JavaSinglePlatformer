package udpPlatform.unit.client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Block_Client extends Obj_Client{

	public Block_Client(int id, int x, int y, double speed, double direction, int team) {
		super(id, x, y, speed, direction, team);
		width = 32;
		height = 32;
	}

	@Override
	public void draw(GraphicsContext gc) {
		gc.setFill(Color.BLACK);
		gc.fillRect(x, y, 32, 32);
	}
}
