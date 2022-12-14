package client.controllers;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import udpPlatform.client.Client;
import udpPlatform.client.ClientEngine;
import udpPlatform.server.DataBox;
import udpPlatform.server.Server;
import udpPlatform.server.ServerEngine;

public class GameController implements Initializable{
	@FXML Canvas canvas;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//엔진 생성(네트워크는 포함되어있음), 임시로 내아이피로 함.
	}
}
