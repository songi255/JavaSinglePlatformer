package udpPlatform.unit.server.playerState;

import udpPlatform.unit.server.Player_Server;
import udpPlatform.unit.server.playerState.StateMachine_server.StateList;

public class State_server_fall extends State_server{
	public State_server_fall(Player_Server player) {
		super(player);
	}

	@Override
	public StateList moveLeft() {
		super.moveLeft();
		return null;
	}

	@Override
	public StateList moveRight() {
		super.moveRight();
		return null;
	}

	@Override
	public StateList Collision_solid() {
		return StateList.land;
	}

}
