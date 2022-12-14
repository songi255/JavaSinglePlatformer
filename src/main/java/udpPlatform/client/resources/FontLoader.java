package udpPlatform.client.resources;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.text.Font;

public class FontLoader{
	public FontLoader() {
		try {
			Path path = Paths.get(getClass().getResource("fonts").toURI());
			DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
			for (Path filePath : directoryStream) {
				Font font = Font.loadFont(filePath.toUri().toString(), 10);
				System.out.println("로딩된 폰트!!! : " +  font);
			}
			System.out.println("font 로딩 성공");
		} catch (Exception e) {
			System.out.println("로딩 에러!!");
			e.printStackTrace();
		}
	}
}
