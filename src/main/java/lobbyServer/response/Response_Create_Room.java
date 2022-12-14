package lobbyServer.response;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import lobbyServer.Room;
import lobbyServer.request.Request_Update_Selection;

public class Response_Create_Room extends Response{
	private String errorMessage;
	private Room room;

	public Response_Create_Room(boolean isValid, String errorMessage, Room room) {
		super(isValid);
		this.errorMessage = errorMessage;
		this.room = room;
	}

	@Override
	protected void onAccepted() {
		//TODO 화면전환코드
		try {
			Parent root = FXMLLoader.load(getClass().getResource("../../client/scenes/scnWaitRoom.fxml"));
			Scene scene = primaryStage.getScene();
			Platform.runLater(()-> {
				scene.setRoot(root);
				Label no = (Label) root.lookup("#roomNo");
				no.setText(room.getRoomNo()+"");
				Label title = (Label) root.lookup("#roomTitle");
				title.setText(room.getTitle());
				Label pwd = (Label) root.lookup("#roomPwd");
				pwd.setText(room.getPassword());
			});
			System.out.println("RoomResponse 실행!! : " + scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDenied() {
		//TODO 알림 메세지
		System.out.println(errorMessage);
	}

	@Override
	protected void setNextAction() {
		System.out.println("client selection의 값!!!!!!! : " + client.getAccount().getSelection());
		client.request(new Request_Update_Selection(client.getAccount().getSelection()));
	}
}
