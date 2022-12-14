package udpPlatform.unit.client.playerState;

import javafx.scene.canvas.GraphicsContext;
import udpPlatform.unit.client.Player_Client;

public class State_client_damaged extends State_client{
	int imgIdx;

	public State_client_damaged(Player_Client player) {
		super(player);
		sprite = resourceBundle.getSprites().get("player_damaged.png");
	}

	@Override
	public void onTransfer() {
		super.onTransfer();
		this.imgIdx = (int)Math.random() * (int)(sprite.getWidth()/sprite.getHeight());
	}

	@Override
	public void draw(GraphicsContext gc) {
		int xScale = player.isRight() ? 1 : -1;
		player.draw_sprite(gc, sprite, imgIdx, 1, player.getX(), player.getY(), xScale, 1, 0, 1, false);
	}
}
