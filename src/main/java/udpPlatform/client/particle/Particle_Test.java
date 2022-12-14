package udpPlatform.client.particle;

import javafx.scene.paint.Color;

public class Particle_Test extends Particle{
	public Particle_Test() {
		alpha1 = 1;
		alpha2 = 0.6;
		alpha3 = 0.3;

		color1 = Color.RED;
		color2 = Color.BLACK;
		color3 = Color.BLUE;

		direction_max = 360;
		direction_min = 0;

		lifespan_max = 10;
		lifespan_min = 5;

		shape = "line";
		size_max = 20;
		size_min = 5;
		//size_wiggle = 4;

		speed_max = 10;
		speed_min = 5;
		speed_increase = -0.5;
	}


}
