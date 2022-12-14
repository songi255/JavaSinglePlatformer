package lobbyServer.response;

import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lobbyServer.Room;

public class Response_RoomList extends Response{
	private String errorMessage;
	private List<Room> rooms;

	public Response_RoomList(boolean isValid, String errorMessage, List<Room> rooms) {
		super(isValid);
		this.errorMessage = errorMessage;
		this.rooms = rooms;
	}

	@Override
	protected void onAccepted() {
		//TODO 테이블 정보 저장
		Parent root = primaryStage.getScene().getRoot();
		Platform.runLater(()-> {
			ObservableList<Room> list = FXCollections.observableArrayList(rooms);
			TableView<Room> tvRoom = (TableView<Room>) root.lookup("#tableView_RoomList");
			TableColumn<Room, Integer> tcNo = (TableColumn<Room, Integer>) tvRoom.getColumns().get(0);
			tcNo.setCellValueFactory(item->new ReadOnlyObjectWrapper<>(item.getValue().getRoomNo()));
			TableColumn<Room, String> tcTitle = (TableColumn<Room, String>) tvRoom.getColumns().get(1);
			tcTitle.setCellValueFactory(item->new ReadOnlyObjectWrapper<>(item.getValue().getTitle()));
			TableColumn<Room, String> tcPerson = (TableColumn<Room, String>) tvRoom.getColumns().get(2);
			tcPerson.setCellValueFactory(item->new ReadOnlyObjectWrapper<>(item.getValue().getSpace()));
			TableColumn<Room, String> tcPublic = (TableColumn<Room, String>) tvRoom.getColumns().get(4);
			tcPublic.setCellValueFactory(item->new ReadOnlyObjectWrapper<>(item.getValue().isPublic() ? "공개" : "비공개"));
			tvRoom.setItems(list);
		});
	}

	@Override
	protected void onDenied() {
		//TODO 알림 메세지
		System.out.println(errorMessage);
	}
}
