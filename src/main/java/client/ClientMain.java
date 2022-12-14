package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lobbyServer.response.Response;

public class ClientMain extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("scenes/scnLogin.fxml"));
		Scene scene = new Scene(root);

		//XXX 안좋은 코드
		Response.setStage(primaryStage);
		//클라이언트 생성 후 stage에 첨부.
		Client.getInstance().startClient();

		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		Client client = Client.getInstance();
		client.stopClient();
	}
}
