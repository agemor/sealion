package sealion.client.asset;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class Graphics {

	public static Map<String, TextureRegion> sheets;
	public static Map<String, TextureRegion[]> animations;

	static {
		sheets = new HashMap<String, TextureRegion>();
		animations = new HashMap<String, TextureRegion[]>();
	}

	/**
	 * 자원 정보 파일(.asset) 로더
	 * 
	 * @author 현준
	 * 
	 */
	public static class Loader {

		/**
		 * XML 자원 파일에서 애니메이션을 추출한다.
		 * 
		 * @param assetFilePath
		 * @return
		 */
		public static Map<String, TextureRegion[]> loadAnimation(String assetFilePath) {

			// 먼저 시트 형태로 자원을 취득한다.
			Map<String, TextureRegion> sheets = loadSheet(assetFilePath);
			Map<String, TextureRegion[]> assets = new HashMap<String, TextureRegion[]>();

			for (Map.Entry<String, TextureRegion> entry : sheets.entrySet())
				assets.put(entry.getKey(), splitTexture(entry.getValue()));

			return assets;
		}

		/**
		 * XML 자원 파일에서 시트를 추출한다.
		 * 
		 * @param assetFilePath
		 * @return
		 */
		public static Map<String, TextureRegion> loadSheet(String assetFilePath) {
			try {

				// XML 데이터를 읽는다.
				XmlReader xmlReader = new XmlReader();
				Element entry = xmlReader.parse(Gdx.files.internal(assetFilePath));

				// 텍스처를 취득한다.
				String imagePath = entry.getAttribute("imagePath");
				Texture texture = new Texture(imagePath);

				Map<String, TextureRegion> assets = new HashMap<String, TextureRegion>();

				// 모든 속성값을 읽는다.
				for (int i = 0; i < entry.getChildCount(); i++) {
					Element element = entry.getChild(i);

					// 자원 이름
					String name = element.getAttribute("name");

					// 정보
					int width = Integer.parseInt(element.getAttribute("width"));
					int height = Integer.parseInt(element.getAttribute("height"));
					int x = Integer.parseInt(element.getAttribute("x"));
					int y = Integer.parseInt(element.getAttribute("y"));

					assets.put(name, new TextureRegion(texture, x, y, width, height));
				}

				return assets;

			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * 텍스처를 가로로 8등분한다.
		 * 
		 * @param texture
		 * @return
		 */
		public static TextureRegion[] splitTexture(TextureRegion texture) {

			// 가로 길이를 구한다.
			int width = texture.getRegionWidth() / 8;
			TextureRegion[] result = new TextureRegion[8];

			// 여덟 조각으로 자른다.
			for (int i = 0; i < 8; i++)
				result[i] = new TextureRegion(texture, i * width, 0, width, texture.getRegionHeight());

			return result;
		}
	}

}
