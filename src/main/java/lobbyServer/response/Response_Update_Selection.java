package lobbyServer.response;

import java.util.List;

import client.Selection;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import lobbyServer.dbconn.Account;
import lobbyServer.request.Request_Exit_Room;
import lobbyServer.request.Request_Update_Selection;

public class Response_Update_Selection extends Response{
	private List<Account> accounts;
	//XXX
	private static final int MAX_NUM = 4;

	public Response_Update_Selection(boolean isValid, List<Account> accounts) {
		super(isValid);
		this.accounts = accounts;
	}

	@Override
	protected void onAccepted() {
		Scene scene = primaryStage.getScene();
		Parent root = scene.getRoot();
		//내 정보가 없으면 그냥 return
		if (!accounts.contains(client.getAccount())) {
			System.out.println("내 정보를 못찾음");
			return;
		}

		Platform.runLater(()-> {
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
				Button btnCharRight = (Button) root.lookup("#btnCharRight" + (i+1));
				Button btnCharLeft = (Button) root.lookup("#btnCharLeft" + (i+1));
				Button btnTeamRight = (Button) root.lookup("#btnTeamRight" + (i+1));
				Button btnTeamLeft = (Button) root.lookup("#btnTeamLeft" + (i+1));
				if (account.getNum() != client.getAccount().getNum()) {
					btnReady.setDisable(true);
					btnCharLeft.setDisable(true);
					btnCharRight.setDisable(true);
					btnTeamLeft.setDisable(true);
					btnTeamRight.setDisable(true);
				}
				else {	//자기 자신이면
					btnReady.setDisable(false);
					btnCharLeft.setDisable(false);
					btnCharRight.setDisable(false);
					btnTeamLeft.setDisable(false);
					btnTeamRight.setDisable(false);
					client.setAccount(account);
					//첨에있는놈이 방장임.
					if (i == 0) {
						client.setSuper(true);
					}
					else {
						client.setSuper(false);
					}
				}
			}
			for (int i = MAX_NUM; i > accounts.size(); i--) {
				Label txtPlayer = (Label) root.lookup("#txtPlayer" + i);
				txtPlayer.setText("Player" + i);
				Label txtTeam = (Label) root.lookup("#txtTeam" + i);
				txtTeam.setText("Team1");
				//FIXME 이미지 기본이미지로
				ImageView imageView = (ImageView) root.lookup("#imgCharacter" + i);
				//Image image = new Image(getClass().getResource("경로" + selection.getChampKind().name() + ".png").toString());
				//imageView.setImage(image);
				Button btnReady = (Button) root.lookup("#btnReady" + i);
				btnReady.setText("대기");
				Button btnCharRight = (Button) root.lookup("#btnCharRight" + i);
				Button btnCharLeft = (Button) root.lookup("#btnCharLeft" + i);
				Button btnTeamRight = (Button) root.lookup("#btnTeamRight" + i);
				Button btnTeamLeft = (Button) root.lookup("#btnTeamLeft" + i);
				btnReady.setDisable(true);
				btnCharLeft.setDisable(true);
				btnCharRight.setDisable(true);
				btnTeamLeft.setDisable(true);
				btnTeamRight.setDisable(true);
			}
			Button btnStart = (Button) root.lookup("#btnStart");
			if (client.isSuper()) {
				btnStart.setDisable(false);
			}
			else {
				btnStart.setDisable(true);
			}
		});
		super.onAccepted();
	}

	@Override
	protected void setNextAction() {
		//받은 응답에 내 목록이 없으면
		if (!accounts.contains(client.getAccount())) {
			//초기화 전이 아니면
			Label txtPlayer = (Label) primaryStage.getScene().getRoot().lookup("#txtPlayer1");
			if (txtPlayer != null && !txtPlayer.getText().equals("Player1")) {
				client.request(new Request_Exit_Room());
			}
		}
	}
}
