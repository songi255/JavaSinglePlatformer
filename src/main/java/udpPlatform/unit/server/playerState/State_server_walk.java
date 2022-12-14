package udpPlatform.unit.server.playerState;

import udpPlatform.unit.server.Player_Server;
import udpPlatform.unit.server.playerState.StateMachine_server.StateList;

public class State_server_walk extends State_server{
	public State_server_walk(Player_Server player) {
		super(player);
	}

	@Override
	public StateList jump() {
		super.jump();
		return StateList.jump;
	}

	@Override
	public StateList stop() {
		return StateList.normal;
	}

	@Override
	public StateList Collision_Object() {
		return StateList.damaged;
	}
}
