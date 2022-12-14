package udpPlatform.unit.client.playerState;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.canvas.GraphicsContext;
import udpPlatform.unit.client.Player_Client;
import udpPlatform.unit.server.playerState.StateMachine_server.StateList;

public class StateMachine_client {
	//클라이언트단 상태머신

	protected Player_Client player;
	private State_client curState;
	private Map<StateList, State_client> states = new HashMap<StateList, State_client>();
	private StateList stateKind;

	public StateMachine_client(Player_Client player) {
		try {
			// 매핑 및 초기화
			this.player = player;
			// 최상위클래스일시만 초기화한다.
			StateList[] states = StateList.values();
			for (StateList stateKind : states) {
				Class<? extends State_client> clazz =(Class<? extends State_client>) Class.forName(getClass().getPackage().getName() + ".State_client_" + stateKind.name());
				Constructor<? extends State_client> constructor = clazz.getConstructor(Player_Client.class);
				State_client state = constructor.newInstance(this.player);
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

	public StateList getState() {
		return stateKind;
	}

	public void draw(GraphicsContext gc) {
		curState.draw(gc);
	}

	public void transfer(StateList stateKind) {
		if (stateKind != null && !stateKind.equals(this.stateKind)) {
			this.stateKind = stateKind;
			this.curState = this.states.get(stateKind);
			this.curState.onTransfer();
		}
	}
}
