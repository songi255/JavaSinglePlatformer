package udpPlatform.unit.client;

import java.nio.ByteBuffer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import udpPlatform.client.resources.ResourceBundle;
import udpPlatform.unit.client.playerState.StateMachine_client;
import udpPlatform.unit.server.playerState.StateMachine_server.StateList;

public class Player_Client extends Obj_Client{

	//오른쪽이냐 왼쪽이냐?
	private boolean isRight = true;
	//XXX
	double hp = 100;
	//XXX
	StateMachine_client state;

	public Player_Client(int id, int x, int y, double speed, double direction, int team) {
		super(id, x, y, speed, direction, team);
		System.out.println("Player_Client생성");
		this.state = new StateMachine_client(this);

		setX(50);
		setY(50);
		//XXX
		width = 20;
		height = 20;
	}

	public boolean isRight() {
		return this.isRight;
	}

	@Override
	public void beginStep() {
		super.beginStep();
		//XXX
		xSpeed = x-prevX;
		ySpeed = y - prevY;
		if (xSpeed > 0) {
			isRight = true;
		}
		else if(xSpeed < 0) {
			isRight = false;
		}
		else {
		}
	}

	//XXX
	@Override
	public void deserialize(ByteBuffer packet) {
		super.deserialize(packet);
		state.transfer(StateList.values()[packet.get()]);
		setX(packet.getInt());
		setY(packet.getInt());
		this.hp = packet.getInt();
	}

	@Override
	public void draw(GraphicsContext gc) {

		state.draw(gc);

		gc.strokeRect(x, y-10, 24, 8);
		if (hp > 0) {
			if (myTeam == team) {
				gc.setFill(Color.GREEN);
				gc.fillPolygon(new double[]{getCenterX(), getCenterX()-10, getCenterX()+10}
						,new double[]{y-20, y - 30, y - 30}, 3);
			}
			else {
				gc.setFill(Color.RED);
			}
			gc.fillRect(x, y-10, 24 * ((hp)/100), 8);
			gc.setFill(Color.BLACK);
			gc.setFont(new Font(4));
			gc.fillText(hp + "/100", x, y-5);
			gc.setFont(new Font(10));
			gc.setFill(Color.WHITE);
			gc.fillText(state.getState().name(), x, y-25);
		}
		gc.fillText(objects.size()+ "", 50, 40);
	}
}
