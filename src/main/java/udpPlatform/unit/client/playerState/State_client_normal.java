package udpPlatform.unit.client.playerState;

import javafx.scene.canvas.GraphicsContext;
import udpPlatform.unit.client.Player_Client;

public class State_client_normal extends State_client{
	public State_client_normal(Player_Client player) {
		super(player);
		sprite = resourceBundle.getSprites().get("player_stand.png");
	}

	@Override
	public void draw(GraphicsContext gc) {
		player.draw_sprite(gc, sprite, -1, 1, player.getX(), player.getY(), 1, 1, 0, 1, true);
	}

}
