package udpPlatform.unit.client.playerState;

import javafx.scene.canvas.GraphicsContext;
import udpPlatform.unit.client.Player_Client;

public class State_client_jump extends State_client{
	public State_client_jump(Player_Client player) {
		super(player);
		sprite = resourceBundle.getSprites().get("player_jump.png");
	}

	@Override
	public void draw(GraphicsContext gc) {
		int xScale = player.isRight() ? 1 : -1;
		player.draw_sprite(gc, sprite, -1, 1, player.getX(), player.getY(), xScale, 1, 0, 1, false);
	}

}
