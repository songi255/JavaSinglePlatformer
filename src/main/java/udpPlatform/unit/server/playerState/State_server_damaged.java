package udpPlatform.unit.server.playerState;

import udpPlatform.unit.server.Player_Server;
import udpPlatform.unit.server.playerState.StateMachine_server.StateList;

public class State_server_damaged extends State_server{
	double friction = 0.1;

	public State_server_damaged(Player_Server player) {
		super(player);
	}

	@Override
	public StateList beforeStep() {
		super.beforeStep();
		double xSpeed = player.getxSpeed();
		if (xSpeed > 0) {
			player.setXspeed(xSpeed - friction);
		}
		else {
			player.setXspeed(xSpeed + friction);
		}
		if (Math.abs(player.getxSpeed()) < 0.5) {
			return StateList.normal;
		}
		return null;
	}

	@Override
	public StateList moveLeft() {
		return null;
	}

	@Override
	public StateList moveRight() {
		return null;
	}

	@Override
	public StateList jump() {
		return null;
	}

	@Override
	public StateList attack() {
		return null;
	}

	@Override
	public StateList onTransfer() {
		if (player.isRight()) {
			player.setXspeed(-10);
		}
		else {
			player.setXspeed(10);
		}
		return null;
	}

}
