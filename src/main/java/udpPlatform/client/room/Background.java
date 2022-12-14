package udpPlatform.client.room;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

public class Background {
	//자신의 이미지
	Image background;
	//캔버스에서의 x, y
	double drawX;
	double drawY;
	double drawWidth;
	double drawHeight;

	//이미지에서의 x,y
	double imageX;
	double imageY;
	double xSpeed;
	double ySpeed;
	GraphicsContext gc;

	//XXX
	boolean hloop;
	double fitWidth = 400;
	double fitHeight = 720;

	public Background(Image background, GraphicsContext gc) {
		this.background = background;
		this.gc = gc;
		//this.drawWidth = gc.getCanvas().getWidth();
		//this.drawHeight = background.getHeight();
		//XXX
		this.drawWidth = 1280;
		this.drawHeight = 720;
		this.drawX = 0;
		//바닥에 붙이는것
		//this.drawY = gc.getCanvas().getHeight() - background.getHeight();
		//XXX
		//this.drawY = 0 ;
		this.drawY = 0;
	}

	//계산
	public void step() {
		this.imageX += this.xSpeed;
		this.imageY += this.ySpeed;
	}

	//XXX 뭔지모르겠
	public void setDrawOption(boolean hloop, double fitWidth, double fitHeight) {
		this.hloop = hloop;
		this.fitWidth =  fitWidth;
		this.fitHeight = fitHeight;
	}

	//그릴 위치 조정
	public void setDrawRect(double drawX, double drawY, double drawWidth, double drawHeight) {
		this.drawX = drawX;
		this.drawY = drawY;
		this.drawWidth = drawWidth;
		this.drawHeight = drawHeight;
	}

	//이미지내 위치조절
	public void setLocation(double x, double y) {
		this.imageX = x;
		this.imageY = y;
	}

	public void setSpeed(double xSpeed, double ySpeed) {
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
	}

	//백그라운드 그리기.
	public void draw() {
		ImagePattern pattern = null;
		//XXX adaptable하게 수정필요
		pattern = new ImagePattern(background, imageX, imageY, fitWidth, fitHeight ,false);
		gc.setFill(pattern);
		gc.fillRect(drawX, drawY, drawWidth, drawHeight);
	}
}
