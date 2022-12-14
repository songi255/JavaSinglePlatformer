package udpPlatform.client.resources;

import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundMap {
	private Map<String, MediaPlayer> soundMap = new HashMap<>();

	public SoundMap() {

		try {
			//배포파일을 만들때 get에서 예외가 터진다......nio관련예외이고, FileSystemNotExist였나 그거다..
			//XXX 더 알아볼 필요가 있을듯하다.
//	        URI uri = getClass().getResource("sounds").toURI();
//	        System.out.println(uri);
//			Map<String, String> env = new HashMap<>();
//	        env.put("create", "true");
//	        FileSystems.newFileSystem(uri, env);

			Path path = Paths.get(getClass().getResource("sounds").toURI());
			DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
			for (Path filePath : directoryStream) {
				Media media = new Media(filePath.toUri().toString());
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				String fileName = filePath.getFileName().toString();
				soundMap.put(fileName, mediaPlayer);
			}
			System.out.println("sound 로딩 성공" + soundMap.size());
		} catch (Exception e) {
			System.out.println("로딩 에러!!");
			e.printStackTrace();
		}
	}

	public Map<String, MediaPlayer> getSounds(){
		return this.soundMap;
	}
}
