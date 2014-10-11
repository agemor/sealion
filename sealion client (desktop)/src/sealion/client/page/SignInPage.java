package sealion.client.page;

import sealion.client.Display;
import sealion.client.Main;
import sealion.client.asset.Graphics;
import sealion.client.net.Connection;
import sealion.client.net.Packet;
import sealion.client.net.SigninEvent;
import sealion.client.ui.CommonUI;
import sealion.client.ui.InputManager;
import sealion.client.ui.Style;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SignInPage extends ChangeListener implements Page, SigninEvent {

	/**
	 * 시스템 변수
	 */
	public GL20 gl = Gdx.graphics.getGL20();

	/**
	 * UI
	 */
	public Stage stage;
	public Image background;
	public TextField id_input_textfield;
	public TextField password_input_textfield;
	public Button signin_button;
	public Button signup_button;
	public Camera camera;

	public SignInPage() {

		super();

		// 랜더링 관련 변수 초기화
		camera = new OrthographicCamera();
		initializeUI();
	}

	/**
	 * UI 초기화
	 */
	private void initializeUI() {

		// 스테이지 생성
		stage = new Stage();

		// 배경 이미지
		background = new Image(Graphics.sheets.get("LOGIN"));

		// 텍스트 필드
		id_input_textfield = new TextField("", Style.inputTextFieldStyle);
		password_input_textfield = new TextField("", Style.inputTextFieldStyle);

		password_input_textfield.setPasswordCharacter('●');
		password_input_textfield.setPasswordMode(true);

		id_input_textfield.setWidth(250);
		password_input_textfield.setWidth(250);

		// 버튼
		signin_button = new Button(Style.getButtonStyle("LOGIN_BUTTON_UP", "LOGIN_BUTTON_DOWN"));
		signup_button = new Button(Style.getButtonStyle("REGISTER_BUTTON_UP", "REGISTER_BUTTON_DOWN"));

		// 위치 설정
		id_input_textfield.setPosition(320, 480 - 38 - 46);
		password_input_textfield.setPosition(320, 480 - 95 - 46);
		signin_button.setPosition(590, 480 - 110 - 35);
		signup_button.setPosition(590, 480 - 110 - 180);

		// 스테이지에 추가
		stage.addActor(background);
		stage.addActor(id_input_textfield);
		stage.addActor(password_input_textfield);
		stage.addActor(signin_button);
		stage.addActor(signup_button);

		// 이벤트 리스너 추가
		Connection.addEventListener(this);

		signin_button.addListener(this);
		signup_button.addListener(this);
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

	@Override
	public void changed(ChangeEvent event, Actor actor) {

		if (CommonUI.isBusy())
			return;

		// 로그인일 경우
		if (actor == signin_button) {

			// 서버에 로그인 요청을 한다.
			String userID = id_input_textfield.getText().trim();
			String userPassword = password_input_textfield.getText().trim();

			if (userID.length() < 1 || userPassword.length() < 1) {
				CommonUI.alert("아이디와 비밀번호를 모두 입력해 주세요.");
				return;
			}

			// UI를 얼린다.
			CommonUI.freeze();

			Connection.signin(userID, userPassword);
		}

		// 회원 가입일 경우
		else if (actor == signup_button) {
			Main.self.show(Main.Page.SIGNUP);
		}

	}

	@Override
	public void onSignin(boolean succeed) {

		// UI를 녹인다.
		CommonUI.melt();

		if (succeed) {
			Gdx.app.log("login", "success");

			// 월드로 들어간다.
			Main.self.show(Main.Page.GAME);

			// 여러 자료를 요청한다.
			Connection.send(new Packet(Packet.Header.CHARACTER, ""));
			Connection.send(new Packet(Packet.Header.USER_LIST, ""));

		} else {
			CommonUI.alert("로그인에 실패하였습니다.");
		}
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, InputManager.self));
		InputManager.targetPage = this;
	}

	@Override
	public void render(float delta) {

		// 프레임버퍼 비우기
		Gdx.gl.glClearColor(01f, 1f, 1f, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// 카메라 업데이트
		camera.update();

		stage.draw();

		CommonUI.draw(delta);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// Connection.removeEventListener(this);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// 화면 해상도에 맞추어 정렬
		camera.viewportWidth = (float) Math.ceil(Display.scale * width);
		camera.viewportHeight = (float) Math.ceil(Display.scale * height);

		camera.position.set(Display.width / 2, Display.height / 2, 0);
		stage.getViewport().setScreenWidth((int) camera.viewportWidth);
		stage.getViewport().setScreenHeight((int) camera.viewportHeight);
		//stage.setViewport(camera.viewportWidth, camera.viewportHeight, false);
	}

	@Override
	public void resume() {
	}

}
