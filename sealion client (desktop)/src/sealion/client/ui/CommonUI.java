package sealion.client.ui;

import sealion.client.Display;
import sealion.client.asset.Fonts;
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
	public static Sprite alertDialog;
	public static Sprite noticeDialog;

	public static BitmapFont text;

	/**
	 * 상태
	 */
	public static boolean frozen = false;
	public static boolean alert = false;
	public static boolean notice = false;
	public static boolean areYouSureToExit = false;

	private static float elapsedTime = 0;
	private static String message;

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

		alertDialog = new Sprite(Graphics.sheets.get("ALERT_BACKGROUND"));
		alertDialog.setPosition((Display.width * Display.scale - 500) / 2, (Display.height * Display.scale - 64) / 2);

		noticeDialog = new Sprite(Graphics.sheets.get("ALERT_BACKGROUND"));
		noticeDialog.setPosition((Display.width * Display.scale - 500) / 2, (Display.height * Display.scale - 64) / 2);

		text = Fonts.get(Fonts.Font.CLEAR_GOTHIC);
		text.setColor(Color.WHITE);
	}

	public static void areYouSureToExit() {
		notice = false;
		alert = false;
		areYouSureToExit = true;

		CommonUI.message = "한 번 더 누르면 종료합니다.";
	}

	/**
	 * 경고창을 띄운다.
	 * 
	 * @param message
	 */
	public static void alert(String message) {
		notice = false;
		alert = true;

		CommonUI.message = message;
	}

	/**
	 * 알림창을 띄운다.
	 * 
	 * @param message
	 */
	public static void notice(String message) {
		notice = true;
		alert = false;

		CommonUI.message = message;
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

	public static boolean isBusy() {
		return frozen || notice || alert;
	}

	public static void closeAll() {
		notice = false;
		alert = false;
	}

	public static void draw(float delta) {

		spriteBatch.begin();

		if (frozen) {
			spriteBatch.draw(disabledBackground, 0, 0);
			freeloading.draw(spriteBatch);
			freeloading.rotate(5);
		}

		if (notice) {
			spriteBatch.draw(disabledBackground, 0, 0);
			noticeDialog.draw(spriteBatch);
			text.setColor(Color.WHITE);
			text.draw(spriteBatch, message, noticeDialog.getX(), noticeDialog.getY() + 50);
		}

		else if (alert) {
			spriteBatch.draw(disabledBackground, 0, 0);
			alertDialog.draw(spriteBatch);
			text.setColor(Color.WHITE);
			text.draw(spriteBatch, message, noticeDialog.getX(), noticeDialog.getY() + 50);

		} else if (areYouSureToExit) {
			alertDialog.draw(spriteBatch);
			text.setColor(Color.WHITE);
			text.draw(spriteBatch, message, noticeDialog.getX(), noticeDialog.getY() + 50);
		}

		spriteBatch.end();

		if (areYouSureToExit) {
			if (elapsedTime > 2) {
				areYouSureToExit = false;
				elapsedTime = 0;
			} else
				elapsedTime += delta;
		}
	}
}
