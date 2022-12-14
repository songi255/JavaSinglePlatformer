package client.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import client.Client;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lobbyServer.Room;
import lobbyServer.request.Request;
import lobbyServer.request.Request_Create_Room;
import lobbyServer.request.Request_Enter_Room;
import lobbyServer.request.Request_Logout;
import lobbyServer.request.Request_RoomList;

public class MainController implements Initializable{
	private static final int INTERVAL = 3000;
	@FXML Button btnCreateRoom;
	@FXML Button btnJoinRoom;
	@FXML Button btnQuickStart;
	@FXML Button btnLog_out;

	Client client;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		client = Client.getInstance();

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Request rqt = new Request_RoomList();
				client.request(rqt);
			}
		};
		timer.schedule(task, 0, INTERVAL);

		btnCreateRoom.sceneProperty().addListener(new ChangeListener<Scene>() {
			@Override
			public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
				newValue.rootProperty().addListener(new ChangeListener<Parent>() {
					@Override
					public void changed(ObservableValue<? extends Parent> observable, Parent oldValue,
										Parent newValue) {
						timer.cancel();
						newValue.getScene().rootProperty().removeListener(this);
					}
				});
				btnCreateRoom.sceneProperty().removeListener(this);
			}
		});

		btnCreateRoom.setOnAction(e -> {
			System.out.println("방만들기 버튼 누름");
			Stage dialog = new Stage(StageStyle.DECORATED);
			dialog.initModality(Modality.WINDOW_MODAL);

			try {
				Parent root = FXMLLoader.load(getClass().getResource("../../client/scenes/dialogCreateRoom.fxml"));
				Scene scene = new Scene(root);
				dialog.setScene(scene);
				dialog.setTitle("알림창");
				dialog.show();

				TextField textTitle = (TextField) root.lookup("#roomTitle");
				TextField textPw = (TextField) root.lookup("#roomPwd");
				ComboBox<String> textPulibc = (ComboBox<String>) root.lookup("#chkPublic");

				Button btnCreate = (Button) root.lookup("#btnCreate");
				btnCreate.setOnAction(evt_create -> {
					boolean isPublic = false;
					String selectedItem = textPulibc.getSelectionModel().getSelectedItem();
					if (selectedItem != null && selectedItem.equals("공개")) {
						isPublic = true;
					}
					Request request = new Request_Create_Room(
							textTitle.getText(), textPw.getText(), isPublic);
					client.request(request);
					dialog.close();
				});
				Button btnCancel = (Button) root.lookup("#btnCancel");
				btnCancel.setOnAction(evt_cancel -> dialog.close());

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		btnLog_out.setOnAction(e->{
			client.request(new Request_Logout());
		});

		//FIXME 이것보단 테이블 더블클릭해서 접속하는게 나을듯.
		btnJoinRoom.setOnAction(e -> {
			Parent root = btnJoinRoom.getScene().getRoot();
			TableView<Room> tv = (TableView<Room>) root.lookup("#tableView_RoomList");
			Room room = tv.getSelectionModel().getSelectedItem();
			if (room == null) {
				return;
			}
			Request request = new Request_Enter_Room(room.getRoomNo());
			client.request(request);
		});

	}
}
