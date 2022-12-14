package lobbyServer.response;

import java.util.List;

import client.Client;
import client.Selection;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import lobbyServer.Room;
import lobbyServer.dbconn.Account;
import lobbyServer.request.Request;
import lobbyServer.request.Request_Update_Selection;

public class Response_Enter_Room extends Response{
	private String errorMessage;
	private Room room;
	private List<Account> accounts;

	public Response_Enter_Room(boolean isValid, String errorMessage, Room room, List<Account> accounts) {
		super(isValid);
		this.errorMessage = errorMessage;
		this.room = room;
		this.accounts = accounts;
	}

	@Override
	protected void onAccepted() {
		//TODO 알림메세지
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
				for (int i = 0; i < accounts.size(); i++) {
					Account account = accounts.get(i);
					Selection selection = account.getSelection();
					Label txtPlayer = (Label) root.lookup("#txtPlayer" + (i+1));
					//FIXME 닉네임 매칭코드로 수정
					txtPlayer.setText(account.getId());
					Label txtTeam = (Label) root.lookup("#txtTeam" + (i+1));
					txtTeam.setText("Team" + selection.getTeam());
					//FIXME 이미지 세팅
					ImageView imageView = (ImageView) root.lookup("#imgCharacter" + (i+1));
					//Image image = new Image(getClass().getResource("경로" + selection.getChampKind().name() + ".png").toString());
					//imageView.setImage(image);
					Button btnReady = (Button) root.lookup("#btnReady" + (i+1));
					if (selection.isReady()) {
						btnReady.setText("준비완료");
					}
					else {
						btnReady.setText("대기");
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(errorMessage);
	}

	@Override
	protected void onDenied() {
		//TODO 알림 메세지
		System.out.println(errorMessage);
	}

	@Override
	protected void setNextAction() {
		client.request(new Request_Update_Selection(client.getAccount().getSelection()));
	}
}
