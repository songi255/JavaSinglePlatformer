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

public class Response_Login extends Response{
	private String errorMessage;
	private Account account;

	public Response_Login(boolean isValid, String errorMessage, Account account) {
		super(isValid);
		this.errorMessage = errorMessage;
		this.account = account;
	}

	@Override
	protected void onAccepted() {
		//TODO 화면전환코드
		try {
			Client client = Client.getInstance();
			client.setAccount(account);
			Parent root = FXMLLoader.load(getClass().getResource("../../client/scenes/scnMain.fxml"));
			Scene scene = primaryStage.getScene();
			Platform.runLater(()->scene.setRoot(root));
			System.out.println("loginResponse 실행!! : " + scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDenied() {
		//TODO 알림 메세지
		Platform.runLater(() -> {
			Stage dialog = new Stage(StageStyle.DECORATED);
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.initOwner(primaryStage);

			try {
				Parent root = FXMLLoader.load(getClass().getResource("../../client/scenes/dialogWarning.fxml"));
				Scene scene = new Scene(root);
				dialog.setScene(scene);
				dialog.setTitle("알림창");
				dialog.show();

				Label label = (Label) root.lookup("#e_msg");
				label.setText(errorMessage);

				Button button = (Button) root.lookup("#btnChk");
				button.setOnAction(e -> dialog.close());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});


		//System.out.println(errorMessage);
	}
}
