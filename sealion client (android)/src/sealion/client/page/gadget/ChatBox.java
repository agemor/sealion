package sealion.client.page.gadget;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import sealion.client.Display;
import sealion.client.asset.Fonts;
import sealion.client.asset.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
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

	/**
	 * UI 컴포넌트
	 */
	public Image chatbox_background;
	public Label chatbox_label;

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
	}

	/**
	 * UI 초기화
	 */
	private void initializeUI() {

		// 배경 구하기
		chatbox_background = new Image(Graphics.sheets.get("CHAT_BOX"));

		// UI 초기화
		chatbox_label = new Label("", labelStyle);

		// UI 설정
		chatbox_label.setSize(300, 200);

		chatbox_background.setPosition(0, Display.height * Display.scale - 200);

		chatbox_label.setPosition(chatbox_background.getX() + 20, chatbox_background.getY());

		addActor(chatbox_background);
		addActor(chatbox_label);

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
