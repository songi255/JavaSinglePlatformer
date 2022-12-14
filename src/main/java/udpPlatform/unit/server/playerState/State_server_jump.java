package udpPlatform.unit.server.playerState;

import udpPlatform.unit.server.Player_Server;
import udpPlatform.unit.server.playerState.StateMachine_server.StateList;

public class State_server_jump extends State_server{
	public State_server_jump(Player_Server player) {
		super(player);
	}

	@Override
	public StateList moveRight() {
		super.moveRight();
		return null;
	}

	@Override
	public StateList moveLeft() {
		super.moveLeft();
		return null;
	}

	@Override
	public StateList transferCheck() {
		if (player.getySpeed() > 0) {
			return StateList.fall;
		}
		return null;
	}

	@Override
	public StateList Collision_solid() {
		return StateList.land;
	}

	@Override
	public StateList Collision_Object() {
		return StateList.attack;
	}
}
