package lobbyServer.response;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lobbyServer.request.Request_Update_Selection;

public class Response_Game_Stop extends Response{

	public Response_Game_Stop(boolean isValid) {
		super(isValid);
	}

	@Override
	protected void onAccepted() {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("../../client/scenes/scnWaitRoom.fxml"));
			Scene scene = primaryStage.getScene();
			scene.setRoot(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setNextAction() {
		client.request(new Request_Update_Selection(client.getAccount().getSelection()));
	}
}
