package sealion.client.ui;

import sealion.client.Display;
import sealion.client.asset.Graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CommonUI {

	private static SpriteBatch spriteBatch;

	/**
	 * 그래픽
	 */
	public static Texture disabledBackground;
	public static Sprite freeloading;

	public static BitmapFont text;

	/**
	 * 상태
	 */
	public static boolean frozen = false;

	public static void initialize() {

		spriteBatch = new SpriteBatch();

		freeloading = new Sprite(Graphics.sheets.get("LOADING"));
		freeloading.setPosition((Display.width - freeloading.getWidth()) / 2,
				(Display.height - freeloading.getHeight()) / 2);
		freeloading.scale(Display.scale);

		Pixmap pixmap = new Pixmap(Display.width, Display.height, Pixmap.Format.RGBA4444);
		pixmap.setColor(new Color(0, 0, 0, 0.5f));
		pixmap.fill();
		disabledBackground = new Texture(pixmap);

	}

	/**
	 * 화면을 얼린다.
	 */
	public static void freeze() {
		frozen = true;
	}

	/**
	 * 화면을 다시 녹인다.
	 */
	public static void melt() {
		frozen = false;
	}

	public static void draw(float delta) {

		spriteBatch.begin();

		if (frozen) {
			spriteBatch.draw(disabledBackground, 0, 0);
			freeloading.draw(spriteBatch);
			freeloading.rotate(5);
		}

		spriteBatch.end();

	}
}
