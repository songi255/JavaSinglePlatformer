package udpPlatform.unit.server.playerState;

import udpPlatform.unit.server.Player_Server;
import udpPlatform.unit.server.playerState.StateMachine_server.StateList;

public class State_server_attack extends State_server{
	public State_server_attack(Player_Server player) {
		super(player);
	}

	//XXX
	@Override
	public StateList transferCheck() {
		return StateList.normal;
	}

}
