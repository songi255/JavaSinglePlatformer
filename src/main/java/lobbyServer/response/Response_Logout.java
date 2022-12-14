package lobbyServer.response;

import java.io.IOException;

import client.Client;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lobbyServer.dbconn.Account;

public class Response_Logout extends Response{
	private String errorMessage;

	public Response_Logout(boolean isValid, String errorMessage) {
		super(isValid);
		this.errorMessage = errorMessage;
	}

	@Override
	protected void onAccepted() {
		//TODO 화면전환코드
		try {
			Parent root = FXMLLoader.load(getClass().getResource("../../client/scenes/scnLogin.fxml"));
			Scene scene = primaryStage.getScene();
			Platform.runLater(()->scene.setRoot(root));
			System.out.println("logoutResponse 실행!! : " + scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDenied() {
		//TODO 알림 메세지
		System.out.println(errorMessage);
	}
}
