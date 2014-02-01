package sealion.client.page.gadget;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import sealion.client.Display;
import sealion.client.asset.Fonts;
import sealion.client.asset.Graphics;
import sealion.client.net.Connection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * 
 * 채팅 메시지 입력 박스
 * 
 * @author 김현준
 * 
 */
public class ChatBox extends WidgetGroup implements Gadget {

	/**
	 * UI 스타일
	 */
	public LabelStyle labelStyle;
	public TextFieldStyle textFieldStyle;

	/**
	 * UI 컴포넌트
	 */
	public Image chatbox_background;
	public Image inputbox_background;
	public Label chatbox_label;
	public TextField inputbox_textfield;

	/**
	 * 상태
	 */
	public boolean opened = false;

	/**
	 * 메시지 큐
	 */
	public Queue<String> messageQueue;

	public ChatBox() {

		loadStyles();
		initializeUI();

		// 메시지 큐 초기화
		messageQueue = new LinkedList<String>();
		for (int i = 0; i < 9; i++) {
			messageQueue.add("");
		}

	}

	/**
	 * UI 스타일 로드
	 */
	private void loadStyles() {
		labelStyle = new LabelStyle();
		labelStyle.font = Fonts.get(Fonts.Font.NANUM_GOTHIC);
		labelStyle.fontColor = Color.BLACK;

		textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = Fonts.get(Fonts.Font.CLEAR_GOTHIC);
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.disabledFontColor = Color.WHITE;
	}

	/**
	 * UI 초기화
	 */
	private void initializeUI() {

		// 배경 구하기
		chatbox_background = new Image(Graphics.sheets.get("CHAT_BOX"));
		inputbox_background = new Image(Graphics.sheets.get("CHAT_INPUT"));

		// UI 초기화
		chatbox_label = new Label("", labelStyle);
		inputbox_textfield = new TextField("", textFieldStyle);

		// UI 설정
		chatbox_label.setSize(300, 200);
		inputbox_textfield.setSize(600 - 20, 40);

		chatbox_background.setPosition(0, Display.height * Display.scale - 200);
		inputbox_background
				.setPosition((Display.width * Display.scale - 600) / 2, Display.height * Display.scale - 240);

		chatbox_label.setPosition(chatbox_background.getX() + 20, chatbox_background.getY());
		inputbox_textfield.setPosition(inputbox_background.getX() + 10, inputbox_background.getY() + 3);

		addActor(chatbox_background);
		addActor(inputbox_background);
		addActor(chatbox_label);
		addActor(inputbox_textfield);

		inputbox_textfield.addListener(new InputListener() {

			@Override
			public boolean keyTyped(InputEvent event, char character) {

				if (character == 13) {

					String message = inputbox_textfield.getText().trim();

					// 너무 짧을 경우 보내지 않는다.
					if (message.equals(""))
						return true;

					// 너무 길면 자른다.
					if (message.length() > 70)
						message = message.substring(0, 70);

					// 서버로 메시지를 보낸다.
					Connection.chat(message);

					// 내 캐릭터 위에 메시지를 띄운다.
					addMessage(Connection.me.character.name + ":" + message);
					Connection.me.character.say(Connection.me.character.name + ":" + message);

					// 입력 창을 비운다.
					inputbox_textfield.setText("");
				}

				return true;
			}
		});

		// close();
	}

	/**
	 * 채팅 메시지 입력 창을 연다.
	 */
	public void open() {

		opened = true;

		inputbox_textfield.setVisible(true);
		inputbox_background.setVisible(true);
		inputbox_textfield.setText("");
	}

	/**
	 * 채팅 메시지 입력 창을 닫는다.
	 */
	public void close() {

		opened = false;

		inputbox_textfield.setVisible(false);
		inputbox_background.setVisible(false);
	}

	/**
	 * 메시지를 새로 띄운다.
	 * 
	 * @param message
	 */
	public void addMessage(String message) {

		String trail = message;
		// 메시지가 길면 여러 줄로 나누어 출력한다.
		int count = (int) Math.floor(message.length() / 20);

		if (count > 0) {
			for (int i = 0; i < count; i++) {
				messageQueue.add(trail.substring(i * 20, (i + 1) * 20));
				if (messageQueue.size() > 9)
					messageQueue.poll();
			}
			trail = trail.substring(count * 20, trail.length());
		}
		messageQueue.add(trail);
		if (messageQueue.size() > 9)
			messageQueue.poll();

		StringBuffer buffer = new StringBuffer();
		Iterator<String> iterator = messageQueue.iterator();

		while (iterator.hasNext()) {
			buffer.append(iterator.next() + "\n");
		}

		chatbox_label.setText(buffer.toString());
	}

}
