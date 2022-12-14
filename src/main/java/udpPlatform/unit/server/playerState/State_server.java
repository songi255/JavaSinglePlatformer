package udpPlatform.unit.server.playerState;

import udpPlatform.unit.server.Player_Server;
import udpPlatform.unit.server.playerState.StateMachine_server.StateList;

public abstract class State_server{
	//리턴값으로 transfer한다.
	Player_Server player;

	public State_server(Player_Server player) {
		this.player = player;
	}

	public StateList beforeStep() {
		return null;
	}

	public StateList step() {
		return null;
	}

	public StateList stop() {
		player.setXspeed(0);
		return null;
	}

	public StateList moveRight() {
		player.setXspeed(10);
		player.setRight(true);
		return null;
	}

	public StateList moveLeft() {
		player.setXspeed(-10);
		player.setRight(false);
		return null;
	}

	public StateList jump() {
		if (player.canJump()) {
			player.setYspeed(-15);
			player.setJump(false);
			return null;
		}
		else
			return null;
	}

	public StateList attack() {
		return null;
	}

	public StateList dash() {
		return null;
	}

	public StateList damaged() {
		return null;
	}

	public StateList Collision_solid() {
		return null;
	}

	public StateList Collision_Object() {
		return null;
	}

	public StateList transferCheck() {
		return null;
	}

	public StateList onTransfer() {
		return null;
	}
}
