package lobbyServer.response;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lobbyServer.request.Request_Exit_Room;
import lobbyServer.request.Request_Game_Stop;
import udpPlatform.client.Client;
import udpPlatform.client.ClientEngine;
import udpPlatform.server.DataBox;
import udpPlatform.server.Server;
import udpPlatform.server.ServerEngine;

public class Response_Game_Start extends Response{
	private transient Parent root;
	private String serverIp;
	private int num;

	public Response_Game_Start(boolean isValid, String serverIp, int num) {
		super(isValid);
		this.serverIp = serverIp;
		this.num = num;
	}

	@Override
	protected void onAccepted() {
		try {
			root = FXMLLoader.load(getClass().getResource("../../client/scenes/scnGame.fxml"));
			Scene scene = primaryStage.getScene();
			scene.setRoot(root);

			try {
				System.out.println(InetAddress.getLocalHost());
				Client client = new Client(serverIp);
				client.startClient();

				Canvas canvas = (Canvas) root.lookup("#canvas");
				ClientEngine engine = new ClientEngine(canvas.getGraphicsContext2D(), serverIp);
				engine.setClient(client);
				engine.startEngine();

				Socket socket = new Socket();
				socket.connect(new InetSocketAddress("google.com", 80));
				String address = socket.getLocalAddress().getHostAddress();

				if (address.equals(this.serverIp)) {
					openServer(num);
				}

				Thread thread = new Thread(()-> {
					engine.waitTerminate();
					System.out.println("대기끝");
					this.client.request(new Request_Game_Stop());
				});
				thread.start();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
		}

	}

	public void openServer(int num) {
		//서버 생성 및 등록
		Server server = new Server(num);
		DataBox dataBox = DataBox.getInstance();
		dataBox.setServer(server);
		server.startServer();

		//서버엔진 생성 및 등록
		ServerEngine engine = new ServerEngine();
		engine.startEngine();
	}
}
