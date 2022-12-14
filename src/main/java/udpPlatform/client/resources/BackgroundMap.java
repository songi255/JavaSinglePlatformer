package udpPlatform.client.resources;

import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

public class BackgroundMap{
	private Map<String, Image> backgroundMap = new HashMap<String, Image>();

	public BackgroundMap() {
		try {
			Path path = Paths.get(getClass().getResource("backgrounds").toURI());
			DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
			for (Path filePath : directoryStream) {
				Image image = new Image(new URL(filePath.toUri().toString()).toString());
				String fileName = filePath.getFileName().toString();
				backgroundMap.put(fileName, image);
			}
		} catch (Exception e) {
			System.out.println("로딩 에러!!");
			e.printStackTrace();
		}
		System.out.println("background 로딩 성공" + backgroundMap.size());
	}

	public Map<String, Image> getBackgrounds(){
		return this.backgroundMap;
	}

}
