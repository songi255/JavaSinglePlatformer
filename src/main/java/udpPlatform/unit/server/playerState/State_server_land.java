package udpPlatform.unit.server.playerState;

import udpPlatform.unit.server.Player_Server;
import udpPlatform.unit.server.playerState.StateMachine_server.StateList;

public class State_server_land extends State_server{
	//XXX
	int duration = 11;
	int escapeCount = 0;

	public State_server_land(Player_Server player) {
		super(player);
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
		super.jump();
		return StateList.jump;
	}

	@Override
	public StateList transferCheck() {
		if (escapeCount >= duration) {
			return StateList.normal;
		}
		else {
			escapeCount++;
			return null;
		}
	}

	@Override
	public StateList onTransfer() {
		player.setXspeed(0);
		this.escapeCount = 0;
		return null;
	}
}
