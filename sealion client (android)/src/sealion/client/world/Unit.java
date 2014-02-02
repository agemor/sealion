package sealion.client.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Unit implements Renderable {

	public static enum Type {
		NPC, MOB, OBJECT
	}

	/**
	 * 위치, 방향
	 */
	public Point position;
	public int direction = 0;

	/**
	 * 타입
	 */
	public Type type = Type.OBJECT;

	/**
	 * 텍스쳐
	 */
	public TextureRegion[] texture;

	public Unit(Type type, TextureRegion[] graphic) {
		this.type = type;
		this.texture = graphic;
		position = new Point(0, 0, 0);
	}

	public Unit() {
		position = new Point(0, 0, 0);
	}

	/**
	 * 유닛의 위치를 리턴한다.
	 * 
	 * @return
	 */
	public Point getPosition() {
		return position;
	}

}
