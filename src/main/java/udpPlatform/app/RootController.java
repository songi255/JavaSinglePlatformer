package udpPlatform.app;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import udpPlatform.client.Client;
import udpPlatform.client.ClientEngine;
import udpPlatform.server.DataBox;
import udpPlatform.server.Server;
import udpPlatform.server.ServerEngine;

public class RootController implements Initializable{
	@FXML Canvas canvas;
	String serverIp;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//엔진 생성(네트워크는 포함되어있음), 임시로 내아이피로 함.
		try {
			//String ip = InetAddress.getLocalHost().getHostName();
			//학원
			serverIp = "121.151.0.175";
			//집
			//serverIp = "220.81.28.104";

			System.out.println(InetAddress.getLocalHost());
			Client client = new Client(serverIp);
			client.startClient();

			ClientEngine engine = new ClientEngine(canvas.getGraphicsContext2D(), serverIp);
			engine.setClient(client);
			engine.startEngine();

			Socket socket = new Socket();
			socket.connect(new InetSocketAddress("google.com", 80));
			String address = socket.getLocalAddress().getHostAddress();

			if (serverIp.equals(address)) {
				openServer(1);
			}

			//if (serverIp.equals(InetAddress.getLocalHost().getHostAddress())) {
			//	openServer(1);
			//지금은 실험용으로 1개로 줬지만, 숫자는 달라질 수 있다.
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//서버엔진, 서버 생성
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





















