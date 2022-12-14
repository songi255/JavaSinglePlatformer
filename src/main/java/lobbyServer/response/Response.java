package lobbyServer.response;

import client.Client;
import javafx.stage.Stage;
import lobbyServer.request.Request;

public abstract class Response {
	protected static Client client = Client.getInstance();
	protected static Stage primaryStage;
	protected String className;
	protected boolean isValid;
	protected transient boolean isBroadcast;

	//XXX 안좋은 코드
	public static void setStage(Stage stage) {
		primaryStage = stage;
	}

	public Response(boolean isValid) {
		this.className = getClass().getName();
		this.isValid = isValid;
		System.out.println(className + "생성!");
	}

	//외부에서 실행하는 메서드.
	public final void execute() {
		if (isValid) {
			onAccepted();
		}
		else {
			onDenied();
		}
		Client client = Client.getInstance();
		client.setWaiting(false);
		setNextAction();
	}

	public final boolean isBroadcast() {
		return this.isBroadcast;
	}

	public final void setBroadcast(boolean isBroadcast) {
		this.isBroadcast = isBroadcast;
	}

	//아래 두 메서드는 오버라이딩하여 사용하는것이다.
	//요청 성공했을 때
	protected void onAccepted() {

	}

	//요청 거부당했을 때
	protected void onDenied() {

	}

	//실행 후 실행할 내용
	protected void setNextAction() {

	}
}
