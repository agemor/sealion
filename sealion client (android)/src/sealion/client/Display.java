package sealion.client;

public class Display {
	public static int width;
	public static int height;

	public static float scaleWidth;
	public static float scaleHeight;
	public static float scale;

	public static void update(int width, int height) {
		Display.width = width;
		Display.height = height;

		scaleWidth = 800f / width;
		scaleHeight = 480f / height;
		scale = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;

	}
}
