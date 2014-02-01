package sealion.client;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class Launcher {

	public static Resolution display = Resolution.WVGA;

	public static void main(String[] args) {
		start(display);
	}

	public static void start(Resolution resolution) {

		Display.update(resolution.height, resolution.width);

		Main main = new Main();
		Main.self = main;

		new LwjglApplication(Main.self, "Lionse", resolution.height, resolution.width, true);
	}

	/**
	 * 해상도
	 * 
	 * @author 현준
	 * 
	 */
	public static enum Resolution {

		HVGA(240, 320), // Optimus One, iPhone 3GS, Galaxy Ace
		WVGA(480, 800), // Nexus One, Nexus S, Galaxy S2, Vega Racer, Optimus Q
		FWVGA(480, 854), // Experia X10, Experia Arc, Motoroi,
		nHD(540, 960), // Take Janus, Evo 4G, Atrix
		qHD(640, 960), // iPhone 4S
		HD(720, 1280), // Optimus LTE2, Galaxy S3, Vega Racer2
		FullHD(1080, 1920); // Optimus G Pro, Vega No. 6, Galaxy S4

		public int width;
		public int height;

		public float scaleWidth;
		public float scaleHeight;
		public float scale;

		Resolution(int width, int height) {
			this.width = width;
			this.height = height;

			scaleWidth = 800 / width;
			scaleHeight = 480 / height;
			scale = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;

		}

	}

}
