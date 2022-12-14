package client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import client.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lobbyServer.request.Request_Login;
import lobbyServer.request.Request;

public class LoginController implements Initializable{
	@FXML Button btnLogin;
	@FXML Button btnExit;
	//TODO 컴포넌트 변수명은 이런식으로 많이 줍니다.(변수만 보고도 무슨 컴포넌트인지부터 파악할 수 있게 txt를 앞에 적습니다.)
	@FXML TextField txtId;
	@FXML TextField txtPw;

	Client client;	//클라이언트 접속 객체 생성

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		client = Client.getInstance();
		System.out.println(client);

		btnExit.setOnAction(e->Platform.exit());

		btnLogin.setOnAction(e->{
			//TODO 요청예시
			Request request = new Request_Login(txtId.getText(), txtPw.getText());
			client.request(request);
		});
	}

}
