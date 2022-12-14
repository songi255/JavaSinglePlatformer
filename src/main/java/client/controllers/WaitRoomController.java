package client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import client.Client;
import client.Selection;
import client.Selection.ChampList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import lobbyServer.request.Request;
import lobbyServer.request.Request_Exit_Room;
import lobbyServer.request.Request_Game_Start;
import lobbyServer.request.Request_Update_Selection;
import lobbyServer.response.Response;

public class WaitRoomController implements Initializable {
	private static final int MAX_TEAM_NO = 4;
	@FXML Button btnBack;
	@FXML Button btnStart;
	@FXML BorderPane root;

	Client client;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		client = Client.getInstance();

		btnBack.setOnAction(e -> {
			System.out.println("나가기 누름");
			client.request(new Request_Update_Selection(null));
		});

		btnStart.setOnAction(e -> {
			client.request(new Request_Game_Start());
		});

		for (int i = 0; i < MAX_TEAM_NO; i++) {
			Button btnReady = (Button) root.lookup("#btnReady" + (i + 1));
			btnReady.setOnAction(e -> {
				Selection selection = client.getAccount().getSelection().copy();
				if (btnReady.getText().equals("대기")) {
					selection.setReady(true);
				} else {
					selection.setReady(false);
				}
				update(selection);
			});
			Button btnCharRight = (Button) root.lookup("#btnCharRight" + (i + 1));
			btnCharRight.setOnAction(e -> {
				Selection selection = client.getAccount().getSelection().copy();
				ChampList[] champLists = ChampList.values();
				int ordinal = selection.getChampKind().ordinal() + 1;
				if (champLists.length <= ordinal) {
					ordinal = 0;
				}
				selection.setChampKind(ChampList.values()[ordinal]);
				update(selection);
			});
			Button btnCharLeft = (Button) root.lookup("#btnCharLeft" + (i + 1));
			btnCharLeft.setOnAction(e -> {
				Selection selection = client.getAccount().getSelection().copy();
				ChampList[] champLists = ChampList.values();
				int ordinal = selection.getChampKind().ordinal() - 1;
				if (ordinal < 0) {
					ordinal = champLists.length - 1;
				}
				selection.setChampKind(ChampList.values()[ordinal]);
				update(selection);
			});
			Button btnTeamRight = (Button) root.lookup("#btnTeamRight" + (i + 1));
			btnTeamRight.setOnAction(e -> {
				Selection selection = client.getAccount().getSelection().copy();
				int team = selection.getTeam() + 1;
				if (team > MAX_TEAM_NO) {
					team = 1;
				}
				selection.setTeam(team);
				update(selection);
			});
			Button btnTeamLeft = (Button) root.lookup("#btnTeamLeft" + (i + 1));
			btnTeamLeft.setOnAction(e -> {
				Selection selection = client.getAccount().getSelection().copy();
				int team = selection.getTeam() - 1;
				if (team < 1) {
					team = 4;
				}
				selection.setTeam(team);
				update(selection);
			});
		}
	}

	public void update(Selection selection) {
		client.request(new Request_Update_Selection(selection));
	}
}
