package udpPlatform.client.resources;

import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

public class SpriteMap {
	private Map<String, Image> spriteMap = new HashMap<String, Image>();

	public SpriteMap() {
		try {
			Path path = Paths.get(getClass().getResource("sprites").toURI());
			DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
			for (Path filePath : directoryStream) {
				Image image = new Image(new URL(filePath.toUri().toString()).toString());
				String fileName = filePath.getFileName().toString();
				spriteMap.put(fileName, image);
			}
		} catch (Exception e) {
			System.out.println("로딩 에러!!");
			e.printStackTrace();
		}
		System.out.println("sprite 로딩 성공" + spriteMap.size());
	}

	public Map<String, Image> getSprites(){
		return this.spriteMap;
	}
}
