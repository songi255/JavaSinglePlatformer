package udpPlatform.unit.client.playerState;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import udpPlatform.client.resources.ResourceBundle;
import udpPlatform.unit.client.Player_Client;

public abstract class State_client{
	//리턴값으로 transfer한다.
	protected Player_Client player;
	protected ResourceBundle resourceBundle = ResourceBundle.getInstance();
	protected Image sprite;

	public State_client(Player_Client player) {
		this.player = player;
	}

	public void draw(GraphicsContext gc) {

	}

	public void onTransfer() {

	}
}
