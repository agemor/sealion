package sealion.client.ui;

import sealion.client.asset.Fonts;
import sealion.client.asset.Graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Style {

	public static TextFieldStyle inputTextFieldStyle;
	public static LabelStyle talkBalloonLabelStyle;
	public static LabelStyle nameTagLabelStyle;
	public static TouchpadStyle touchpadStyle;
	public static ButtonStyle openChatButtonStyle;
	public static ButtonStyle openMenuButtonStyle;

	public static void load() {

		// 입력 텍스트 스타일 설정
		inputTextFieldStyle = new TextFieldStyle();
		inputTextFieldStyle.font = Fonts.get(Fonts.Font.CLEAR_GOTHIC);
		inputTextFieldStyle.fontColor = Color.BLACK;
		inputTextFieldStyle.disabledFontColor = Color.GRAY;
		inputTextFieldStyle.cursor = new TextureRegionDrawable(Graphics.sheets.get("TEXT_FIELD_POINTER"));
		inputTextFieldStyle.background = new TextureRegionDrawable(Graphics.sheets.get("TEXTFIELD_BACKGROUND"));
		
		// 말풍선 라벨
		talkBalloonLabelStyle = new LabelStyle();
		talkBalloonLabelStyle.font = Fonts.get(Fonts.Font.NANUM_GOTHIC);
		talkBalloonLabelStyle.fontColor = Color.WHITE;
		
		// 네임 태그
		nameTagLabelStyle = new LabelStyle();
		nameTagLabelStyle.font = Fonts.get(Fonts.Font.NANUM_GOTHIC);
		nameTagLabelStyle.fontColor = Color.BLACK;
		
		// 터치패드
		touchpadStyle = new TouchpadStyle();
		touchpadStyle.background = new TextureRegionDrawable(Graphics.sheets.get("TOUCHPAD_BACKGROUND"));
		touchpadStyle.knob = new TextureRegionDrawable(Graphics.sheets.get("TOUCHPAD_KNOB"));

		// 채팅창 열기 버튼
		openChatButtonStyle = new ButtonStyle();
		openChatButtonStyle.up = new TextureRegionDrawable(Graphics.sheets.get("OPEN_CHAT_BUTTON_UP"));
		openChatButtonStyle.down = new TextureRegionDrawable(Graphics.sheets.get("OPEN_CHAT_BUTTON_DOWN"));
		openChatButtonStyle.checked = openChatButtonStyle.down;

		// 메뉴 열기 버튼
		openMenuButtonStyle = new ButtonStyle();
		openMenuButtonStyle.up = new TextureRegionDrawable(Graphics.sheets.get("OPEN_MENU_BUTTON_UP"));
		openMenuButtonStyle.down = new TextureRegionDrawable(Graphics.sheets.get("OPEN_MENU_BUTTON_DOWN"));
		openMenuButtonStyle.checked = openMenuButtonStyle.down;

	}

	/**
	 * 버튼 스타일을 만들어 준다.
	 * 
	 * @param up
	 * @param down
	 * @return
	 */
	public static ButtonStyle getButtonStyle(String up, String down) {

		ButtonStyle style = new ButtonStyle();
		style.up = new TextureRegionDrawable(Graphics.sheets.get(up));
		style.down = new TextureRegionDrawable(Graphics.sheets.get(down));

		return style;
	}

}
