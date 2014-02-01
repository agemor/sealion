package sealion.client.page.gadget;

import sealion.client.asset.Graphics;
import sealion.client.ui.Style;
import sealion.client.world.Point;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * 
 * 아이템 슬롯 랜더링 담당 UI객체
 * 
 * @author 김현준
 * 
 */
public class Menu extends WidgetGroup implements Gadget {

	private static final int padding = 25;
	private static final int space = 10;

	/**
	 * UI 컴포넌트
	 */
	public Image pannel;
	public MenuButton my_info_button;
	public MenuButton unit_button;
	public MenuButton matrial_button;
	public MenuButton item_button;
	public MenuButton menu_button;

	/**
	 * 내 정보 페이지
	 */
	public MyInfoPage myInfoPage;

	// 위치 세팅 변수
	public boolean shrinked = true;
	public Point position_pop = new Point(0, 1, 0);
	public Point position_shrink = new Point(0, 481, 0);
	public Point position_target = position_shrink; // 레퍼런스 변수

	public Point position;

	public Menu() {

		// 페이지 초기화
		myInfoPage = new MyInfoPage(this);

		position = new Point(position_shrink.x, position_shrink.y, 0);
		initializeUI();
	}

	/**
	 * UI 초기화
	 */
	private void initializeUI() {

		// 컴포넌트 생성
		pannel = new Image(Graphics.sheets.get("MENU_PANNEL"));
		my_info_button = new MenuButton(this,
				Style.getButtonStyle("MENU_MY_INFO_BUTTON_UP", "MENU_MY_INFO_BUTTON_DOWN"));
		unit_button = new MenuButton(this, Style.getButtonStyle("MENU_UNIT_BUTTON_UP", "MENU_UNIT_BUTTON_DOWN"));
		matrial_button = new MenuButton(this,
				Style.getButtonStyle("MENU_MATRIAL_BUTTON_UP", "MENU_MATRIAL_BUTTON_DOWN"));
		item_button = new MenuButton(this, Style.getButtonStyle("MENU_ITEM_BUTTON_UP", "MENU_ITEM_BUTTON_DOWN"));
		menu_button = new MenuButton(this, Style.getButtonStyle("MENU_MENU_BUTTON_UP", "MENU_MENU_BUTTON_DOWN"));

		// 그래픽 세팅
		pannel.setSize(701, 480);

		addActor(pannel);
		addActor(my_info_button);
		addActor(unit_button);
		addActor(matrial_button);
		addActor(item_button);
		addActor(menu_button);
	}

	public void update(float delta) {

		position.x += 0.3f * (position_target.x - position.x);
		position.y += 0.3f * (position_target.y - position.y);

		pannel.setPosition(0, position.y);
		my_info_button.setPosition(padding + 120 * 0 + position.x + space * 0, 420 + position.y);
		unit_button.setPosition(padding + 120 * 1 + position.x + space * 1, 420 + position.y);
		matrial_button.setPosition(padding + 120 * 2 + position.x + space * 2, 420 + position.y);
		item_button.setPosition(padding + 120 * 3 + position.x + space * 3, 420 + position.y);
		menu_button.setPosition(padding + 120 * 4 + position.x + space * 4, 420 + position.y);

		myInfoPage.update(delta);
	}

	public void close() {
		shrinked = true;
		position_target = position_shrink;
	}

	public void open() {
		shrinked = false;
		position_target = position_pop;
	}

	/**
	 * 메뉴 전용 버튼
	 * 
	 * @author 현준
	 * 
	 */
	public class MenuButton extends Button {

		public Menu itemSlot;
		public boolean selected = false;

		public MenuButton(Menu itemSlot, ButtonStyle style) {

			super(style);
			this.itemSlot = itemSlot;

		}
	}

}
