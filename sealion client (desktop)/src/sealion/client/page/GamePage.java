package sealion.client.page;

import java.util.List;

import sealion.client.Display;
import sealion.client.asset.Graphics;
import sealion.client.net.Connection;
import sealion.client.net.Packet;
import sealion.client.net.User;
import sealion.client.net.UserEvent;
import sealion.client.page.gadget.ChatBox;
import sealion.client.page.gadget.Menu;
import sealion.client.ui.CommonUI;
import sealion.client.ui.InputManager;
import sealion.client.ui.Style;
import sealion.client.world.Character;
import sealion.client.world.unit.Tree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class GamePage extends ChangeListener implements Page, UserEvent {

	/**
	 * 상태 변수
	 */
	public boolean loaded = false;
	public boolean characterLoaded = false;
	public boolean userListLoaded = false;

	/**
	 * 그래픽 관련 변수
	 * 
	 */
	public GL20 gl = Gdx.graphics.getGL20();
	public SpriteBatch spriteBatch;

	/**
	 * 스테이지
	 */
	public sealion.client.world.Stage world;

	/**
	 * UI
	 */
	public Stage stage;
	public Touchpad touchpad;
	public Button open_chat_button;
	public Button open_menu_button;

	public Menu menu;
	public ChatBox chat_box;

	/**
	 * Touchpad 관련
	 */
	private int previousDirection = -1;

	public GamePage() {

		// 랜더러 초기화
		spriteBatch = new SpriteBatch();

		// 월드 생성
		world = new sealion.client.world.Stage();
		world.load();

		initializeUI();
		initializeWorld();
	}

	@Override
	public void backKeyPressed() {

		if (CommonUI.isBusy() && !CommonUI.areYouSureToExit) {
			CommonUI.closeAll();
			return;
		}

		if (CommonUI.areYouSureToExit)
			Gdx.app.exit();

		// 한번 더 누르면 종료합니다.
		else
			CommonUI.areYouSureToExit();
	}

	/**
	 * UI를 초기화한다.
	 */
	private void initializeUI() {

		// 컴포넌트 스테이지 생성
		stage = new Stage();

		// 컴포넌트 생성
		touchpad = new Touchpad(40, Style.touchpadStyle);
		open_chat_button = new Button(Style.openChatButtonStyle);
		open_menu_button = new Button(Style.openMenuButtonStyle);
		menu = new Menu();
		chat_box = new ChatBox();

		touchpad.setPosition(50, 50);
		open_chat_button.setPosition(Display.width * Display.scale - 100, Display.height * Display.scale - 120);
		open_menu_button.setPosition(Display.width * Display.scale - 100, Display.height * Display.scale - 60);

		touchpad.addListener(this);
		open_chat_button.addListener(this);
		open_menu_button.addListener(this);

		stage.addActor(chat_box);
		stage.addActor(touchpad);
		stage.addActor(menu);

		stage.addActor(open_menu_button);
		stage.addActor(open_chat_button);

		menu.close();
		chat_box.close();

	}

	/**
	 * 월드 초기화
	 */
	private void initializeWorld() {
		for (int i = 0; i < 40; i++) {
			Tree unit = new Tree(Graphics.animations.get("TREE"));
			unit.position.x = (int) (Math.random() * 700) + 600;
			unit.position.y = (int) (Math.random() * 700) + 100;
			world.units.add(unit);
		}
	}

	@Override
	public void changed(ChangeEvent event, Actor actor) {

		// 터치패드 조작
		if (actor == touchpad) {
			float knobX = touchpad.getKnobX() - touchpad.getWidth() / 2;
			float knobY = touchpad.getKnobY() - touchpad.getHeight() / 2;
			int direction = getDirection((float) (Math.atan2(knobY, knobX) * 180 / Math.PI));

			if (previousDirection != direction) {

				if (direction == -1) {
					world.me.status = Character.Status.STOP;
					world.me.stop();
				} else {
					world.me.status = Character.Status.MOVE;
					world.me.direction = direction;
					world.me.move(direction);
				}
				previousDirection = direction;
			}
		}

		// 채팅창 열기 버튼
		else if (actor == open_chat_button) {
			menu.close();

			if (chat_box.opened)
				chat_box.close();
			else
				chat_box.open();
		}

		// 메뉴 열기 버튼
		else if (actor == open_menu_button) {
			chat_box.close();

			if (menu.shrinked)
				menu.open();
			else
				menu.close();

		}

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, InputManager.self));
		InputManager.targetPage = this;

		// 유저 이벤트 받기
		Connection.addEventListener(this);
	}

	private float elapsedTime = 0;

	@Override
	public void render(float delta) {
		if (!loaded)
			return;

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// 월드를 그린다.
		spriteBatch.begin();

		world.update(delta);
		world.draw(spriteBatch, delta);

		spriteBatch.end();

		menu.update(delta);

		// UI를 그린다.
		stage.draw();

		CommonUI.draw(delta);

	}

	@Override
	public void resize(int width, int height) {
		world.camera.viewportWidth = (float) Math.ceil(Display.scale * width);
		world.camera.viewportHeight = (float) Math.ceil(Display.scale * height);		
		world.camera.position.set(Display.width / 2, Display.width / 2, 0);
		stage.getViewport().setScreenWidth((int) world.camera.viewportWidth);
		stage.getViewport().setScreenHeight((int) world.camera.viewportHeight);
		
		//stage.setViewport(world.camera.viewportWidth, world.camera.viewportHeight, false);

	}

	@Override
	public void onBroadcast(String data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onArrive(User user) {
		world.characters.add(user.character);
		world.units.add(user.character);
	}

	@Override
	public void onLeave(User user) {
		world.characters.remove(user.character);
		world.units.remove(user.character);
	}

	@Override
	public void onUserListLoaded(List<User> list) {

		userListLoaded = true;

		for (User user : list) {

			if (user.character.name.equals(Connection.me.character.name))
				continue;

			world.characters.add(user.character);
			world.units.add(user.character);
		}

		if (userListLoaded && characterLoaded) {
			loaded = true;
			// 안녕 인사 한다.
			Connection.send(new Packet(Packet.Header.HELLO, ""));
		}
	}

	@Override
	public void onCharacterLoaded(User me) {

		characterLoaded = true;

		world.me = me.character;
		world.characters.add(world.me);
		world.units.add(world.me);

		if (userListLoaded && characterLoaded) {
			loaded = true;
			// 안녕 인사 한다.
			Connection.send(new Packet(Packet.Header.HELLO, ""));
		}
	}

	@Override
	public void onChatMessageArrived(User user, String message) {
		chat_box.addMessage(user.character.name + ":" + message);
		user.character.say(user.character.name + ":" + message);
	}

	@Override
	public void onMove(User user, int direction) {
		user.character.status = Character.Status.MOVE;
	}

	@Override
	public void onStop(User user) {
		user.character.status = Character.Status.STOP;
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	private final float K = 90f / 4f;

	public int getDirection(float A) {
		if (A == 0) {
			return -1;
		} else if (K <= A && A < 3 * K) {
			return 4;
		} else if (3 * K <= A && A < 5 * K) {
			return 3;
		} else if (5 * K <= A && A < 7 * K) {
			return 2;
		} else if (7 * K <= Math.abs(A) && Math.abs(A) < 8 * K) {
			return 1;
		} else if (-7 * K <= A && A < -5 * K) {
			return 0;
		} else if (-5 * K <= A && A < -3 * K) {
			return 7;
		} else if (-3 * K <= A && A < -K) {
			return 6;
		} else {
			return 5;
		}
	}

}
