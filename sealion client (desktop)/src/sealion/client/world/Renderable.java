package sealion.client.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Renderable {

	public void draw(SpriteBatch spriteBatch, float delta);

	public void update(float delta);
}
