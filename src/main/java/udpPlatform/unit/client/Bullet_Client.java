package udpPlatform.unit.client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.util.Duration;
import udpPlatform.client.particle.Emitter;
import udpPlatform.client.particle.Particle_Test;
import udpPlatform.client.resources.ResourceBundle;

public class Bullet_Client extends Obj_Client{

	//XXX
	double damage;

	public Bullet_Client(int id, int x, int y, double speed, double direction, int team, double damage) {
		super(id, x, y, speed, direction, team);
		sprite = resources.getSprites().get("bullet.png");
		this.damage = damage;
		//XXX oncreate만들어넣자
		try {
			MediaPlayer mediaPlayer = ResourceBundle.getInstance().getSounds().get("shot1.mp3");
			mediaPlayer.seek(new Duration(100));
			mediaPlayer.play();
		} catch (Exception e) {
		}
	}

	@Override
	public void step() {
		setX(x + xSpeed);
		setY(y+ySpeed);
	}

	@Override
	protected void onDelete() {
		//XXX
		Emitter emitter = new Emitter(getCenterX(), getCenterY(), ps);
		emitter.burst(new Particle_Test(), 30);
	}

	@Override
	public void draw(GraphicsContext gc) {
		draw_sprite(gc, sprite, -1, 1, getX(), getY(), 1, 1, 0, 1, true);
	}
}
