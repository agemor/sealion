package sealion.client.page.gadget;

import sealion.client.Display;
import sealion.client.asset.Graphics;
import sealion.client.world.Renderable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyInfoPage implements Renderable {

	public Menu itemSlot;

	public Sprite UNIT_SLOT_1;
	public Sprite UNIT_SLOT_2;
	public Sprite UNIT_SLOT_3;
	public Sprite UNIT_SLOT_N;

	public Sprite EQUIPMENT_SLOT_HEAD;
	public Sprite EQUIPMENT_SLOT_BODY;
	public Sprite EQUIPMENT_SLOT_LEGS;

	public MyInfoPage(Menu itemSlot) {

		this.itemSlot = itemSlot;

		UNIT_SLOT_1 = new Sprite(Graphics.sheets.get("MY_PAGE_UNIT_SLOT"));
		UNIT_SLOT_2 = new Sprite(Graphics.sheets.get("MY_PAGE_UNIT_SLOT"));
		UNIT_SLOT_3 = new Sprite(Graphics.sheets.get("MY_PAGE_UNIT_SLOT"));
		UNIT_SLOT_N = new Sprite(Graphics.sheets.get("MY_PAGE_UNIT_SLOT"));

		EQUIPMENT_SLOT_HEAD = new Sprite(Graphics.sheets.get("MY_PAGE_EQUIPMENT_SLOT"));
		EQUIPMENT_SLOT_BODY = new Sprite(Graphics.sheets.get("MY_PAGE_EQUIPMENT_SLOT"));
		EQUIPMENT_SLOT_LEGS = new Sprite(Graphics.sheets.get("MY_PAGE_EQUIPMENT_SLOT"));

	}

	public void draw(SpriteBatch spriteBatch, float delta) {
		UNIT_SLOT_1.draw(spriteBatch);
		UNIT_SLOT_2.draw(spriteBatch);
		UNIT_SLOT_3.draw(spriteBatch);
		UNIT_SLOT_N.draw(spriteBatch);
	}

	private static final int padding = 25;

	public void update(float delta) {
		UNIT_SLOT_1.setPosition(itemSlot.position.x + padding + 90 * 0 + 10 * 0, itemSlot.position.y + Display.height
				- 170);
		UNIT_SLOT_2.setPosition(itemSlot.position.x + padding + 90 * 1 + 10 * 1, itemSlot.position.y + Display.height
				- 170);
		UNIT_SLOT_3.setPosition(itemSlot.position.x + padding + 90 * 2 + 10 * 2, itemSlot.position.y + Display.height
				- 170);
		UNIT_SLOT_N.setPosition(itemSlot.position.x + padding + 90 * 4 + 10 * 19, itemSlot.position.y + Display.height
				- 170);
	}

}
