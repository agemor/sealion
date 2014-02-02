package sealion.client.asset;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * 폰트
 * 
 * @author 현준
 * 
 */
public class Fonts {

	public static Map<String, BitmapFont> fonts;

	static {
		fonts = new HashMap<String, BitmapFont>();
	}

	/**
	 * 폰트를 취득한다.
	 * 
	 * @param font
	 * @return
	 */
	public static BitmapFont get(Font font) {
		return fonts.get(font.toString());
	}

	public static enum Font {
		CLEAR_GOTHIC, NANUM_GOTHIC;
	}

	/**
	 * 비트맵 폰트 로더
	 * 
	 * @author 현준
	 * 
	 */
	public static class Loader {

		/**
		 * 비트맵 폰트를 로드한다.
		 * 
		 * @param assetFilePath
		 * @return
		 */
		public static Map<String, BitmapFont> loadFont(String assetFilePath) {
			try {

				// XML 데이터를 읽는다.
				XmlReader xmlReader = new XmlReader();
				Element entry = xmlReader.parse(Gdx.files.internal(assetFilePath));

				Map<String, BitmapFont> assets = new HashMap<String, BitmapFont>();

				// 모든 속성값을 읽는다.
				for (int i = 0; i < entry.getChildCount(); i++) {
					Element element = entry.getChild(i);

					// 폰트 이름
					String name = element.getAttribute("name");

					// 폰트 경로
					String fontPath = element.getAttribute("fontPath");

					assets.put(name, new BitmapFont(Gdx.files.internal(fontPath), false));

				}

				return assets;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}
}
