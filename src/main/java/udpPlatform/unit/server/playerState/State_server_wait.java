package udpPlatform.unit.server.playerState;

import udpPlatform.unit.server.Player_Server;
import udpPlatform.unit.server.playerState.StateMachine_server.StateList;

public class State_server_wait extends State_server{

	public State_server_wait(Player_Server player) {
		super(player);
	}

	@Override
	public StateList moveLeft() {
		super.moveLeft();
		return StateList.walk;
	}

	@Override
	public StateList moveRight() {
		super.moveRight();
		return StateList.walk;
	}

	@Override
	public StateList jump() {
		super.jump();
		return StateList.jump;
	}

	@Override
	public StateList transferCheck() {
		if (player.getySpeed() > 0) {
			return StateList.fall;
		}
		return null;
	}
}
