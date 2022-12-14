package udpPlatform.client.room;

import java.util.Map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Room_Test extends Room{

	public Room_Test(GraphicsContext gc) {
		super(gc);
	}

	@Override
	public void setBackgrounds() {
		Map<String, Image> backgrounds = resourceBundle.getBackgrounds();
		Background background1 = new Background(backgrounds.get("bg_mountain_background.png"), gc);
		background1.setDrawOption(true, roomWidth, roomHeight);
		background1.setDrawRect(0, 0, roomWidth, roomHeight);
		this.backgrounds.add(background1);
		this.backgrounds.add(new Background(backgrounds.get("bg_mountain_far.png"), gc));
		this.backgrounds.add(new Background(backgrounds.get("bg_mountain_mountains.png"), gc));
		this.backgrounds.add(new Background(backgrounds.get("bg_mountain_trees.png"), gc));
		this.backgrounds.add(new Background(backgrounds.get("bg_mountain_foreground.png"), gc));

//		Background background1 = new Background(backgrounds.get("bg_forest_background.png"), gc); 
//		background1.setDrawOption(true, roomWidth, roomHeight);
//		background1.setDrawRect(0, 0, roomWidth, roomHeight);
//		this.backgrounds.add(background1);
//		this.backgrounds.add(new Background(backgrounds.get("bg_forest_tree1.png"), gc));
//		this.backgrounds.add(new Background(backgrounds.get("bg_forest_tree2.png"), gc));
//		this.backgrounds.add(new Background(backgrounds.get("bg_forest_light.png"), gc));
	}
}
