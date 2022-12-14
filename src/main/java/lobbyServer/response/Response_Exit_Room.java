package lobbyServer.response;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Response_Exit_Room extends Response{
	private String errorMessage;

	public Response_Exit_Room(boolean isValid, String errorMessage) {
		super(isValid);
		this.errorMessage = errorMessage;
	}

	@Override
	protected void onAccepted() {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("../../client/scenes/scnMain.fxml"));
			Scene scene = primaryStage.getScene();
			scene.setRoot(root);
			System.out.println(errorMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDenied() {
		System.out.println(errorMessage);
	}

}
