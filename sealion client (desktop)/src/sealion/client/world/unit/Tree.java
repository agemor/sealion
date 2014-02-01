package sealion.client.world.unit;

import sealion.client.Display;
import sealion.client.world.Point;
import sealion.client.world.Unit;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tree extends Unit {

	public Point bottom;

	public Tree(TextureRegion[] graphic) {
		super(Unit.Type.OBJECT, graphic);

		bottom = new Point(0, 0, 0);
	}

	@Override
	public void draw(SpriteBatch spriteBatch, float delta) {
		spriteBatch.draw(texture[direction], position.x, Display.height - position.y);
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public Point getPosition() {
		bottom.x = position.x;
		bottom.y = position.y + 100;
		bottom.z = position.z;
		return bottom;
	}

}
