package udpPlatform.unit.server.playerState;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import udpPlatform.unit.server.Player_Server;

public class StateMachine_server {
	//서버단 상태머신

	public enum StateList {
		attack, cast, damaged, dash, fall, jump, land, normal, nuckdown, wait, walk
	}

	// 서버단 player 상태객체.
	protected Player_Server player;
	private State_server curState;
	private Map<StateList, State_server> states = new HashMap<StateList, State_server>();
	private StateList stateKind;

	public StateMachine_server(Player_Server player) {
		try {
			// 매핑 및 초기화
			this.player = player;
			// 최상위클래스일시만 초기화한다.
			StateList[] states = StateList.values();
			for (StateList stateKind : states) {
				Class<? extends State_server> clazz =(Class<? extends State_server>) Class.forName(getClass().getPackage().getName() + ".State_server_" + stateKind.name());
				Constructor<? extends State_server> constructor = clazz.getConstructor(Player_Server.class);
				State_server state = constructor.newInstance(this.player);
				this.states.put(stateKind, state);
			}
			transfer(StateList.wait);
			System.out.println("State 초기화 완료!" + this.states.size());
			System.out.println(getClass().getName() + "생성 완료!!!!!!!!!!");
		} catch (Exception e) {
			System.out.println("state 초기화 실패!!");
			e.printStackTrace();
		}

	}

	public void Collsion_solid() {
		transfer(this.curState.Collision_solid());
	}

	public void Collision_Object() {
		transfer(this.curState.Collision_Object());
	}

	public void transferCheck() {
		StateList stateKind = curState.transferCheck();
		transfer(stateKind);
	}

	public void beforeStep() {
		StateList stateKind = curState.beforeStep();
		transfer(stateKind);
	}

	public void step() {
		StateList stateKind = curState.step();
		transfer(stateKind);
	}

	public void stop() {
		StateList stateKind = curState.stop();
		transfer(stateKind);
	}

	public void moveRight() {
		StateList stateKind = curState.moveRight();
		transfer(stateKind);
	}

	public void moveLeft() {
		StateList stateKind = curState.moveLeft();
		transfer(stateKind);
	}

	public void jump() {
		StateList stateKind = curState.jump();
		transfer(stateKind);
	}

	public void attack() {
		StateList stateKind = curState.attack();
		transfer(stateKind);
	}

	public void dash() {
		StateList stateKind = curState.dash();
		transfer(stateKind);
	}

	public void damaged() {
		StateList stateKind = curState.damaged();
		transfer(stateKind);
	}

	private void transfer(StateList stateKind) {
		if (stateKind != null && !stateKind.equals(this.stateKind)) {
			this.stateKind = stateKind;
			this.curState = this.states.get(stateKind);
			this.curState.onTransfer();
			transfer(this.curState.transferCheck());
		}
	}

	public StateList getState() {
		return stateKind;
	}
}
