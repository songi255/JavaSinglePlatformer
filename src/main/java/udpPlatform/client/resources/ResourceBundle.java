package udpPlatform.client.resources;

import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class ResourceBundle {
	//사용할 리소스들을 가지고 있는 클래스.(메모리에 미리 로드하고 사용용이하게 하기 위함.)
	//싱글턴이지만, open과 close로 로딩을 제어하자.
	private static ResourceBundle resourceBundle;
	private SoundMap soundMap;
	private SpriteMap spriteMap;
	private BackgroundMap backgroundMap;

	private boolean isOpen;

	public static ResourceBundle getInstance() {
		if (resourceBundle == null) {
			resourceBundle = new ResourceBundle();
		}
		if (!resourceBundle.isOpen) {
			resourceBundle.load();
		}
		return resourceBundle;
	}

	//생성자
	private ResourceBundle() {
	}

	public boolean isOpen() {
		return isOpen;
	}

	public Map<String, MediaPlayer> getSounds() {
		return soundMap.getSounds();
	}

	public Map<String , Image> getBackgrounds() {
		return backgroundMap.getBackgrounds();
	}

	public Map<String , Image> getSprites() {
		return spriteMap.getSprites();
	}

	public void load() {
		this.soundMap = new SoundMap();
		this.spriteMap = new SpriteMap();
		this.backgroundMap = new BackgroundMap();
		new FontLoader();
		//XXX 로딩이 다되었는지 확인할 수 있나?
		isOpen = true;
	}

	public void unload() {
		//gc가 수거해가게 한다.
		this.soundMap = null;
		this.spriteMap = null;
		this.backgroundMap = null;
		isOpen = false;
	}
}
