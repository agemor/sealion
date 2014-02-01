package sealion.client.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Terrain {

	// format data
	public static final int WIDTH = 100;
	public static final int HEIGHT = 50;
	public static final float RADIAN_45 = (float) (Math.PI / 4);

	// position data
	public int x;
	public int y;
	public int z = 0;
	public Index index;

	// graphics data
	private TextureRegion texture;

	public Terrain(int x, int y, int z, Index index, TextureRegion texture) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.index = index;
		this.texture = texture;
	}

	public Terrain(int x, int y, Index index) {
		this.x = x;
		this.y = y;
		this.index = index;
	}

	public void setTexture(TextureRegion textureRegion) {
		this.texture = textureRegion;
	}

	public TextureRegion getTexture() {
		return texture;
	}

	public static class Index {

		public int x;
		public int y;

		public Index(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
