package sealion.client.page;

import sealion.client.Display;
import sealion.client.Main;
import sealion.client.asset.Graphics;
import sealion.client.net.Connection;
import sealion.client.net.SignupEvent;
import sealion.client.ui.CommonUI;
import sealion.client.ui.Style;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SignUpPage extends ChangeListener implements Page, SignupEvent {

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
	public TextField character_input_textfield;
	public TextField email_input_textfield;
	public Button signup_button;
	public Button cancel_button;
	public Camera camera;

	/**
	 * 이벤트 처리
	 */
	private InputProcessor backProcessor;

	public SignUpPage() {

		super();

		// 랜더링 관련 변수 초기화
		camera = new OrthographicCamera();

		initializeUI();
	}

	/**
	 * UI 초기화
	 */
	private void initializeUI() {
		stage = new Stage();

		// 배경 이미지
		background = new Image(Graphics.sheets.get("REGISTER"));

		id_input_textfield = new TextField("", Style.inputTextFieldStyle);
		password_input_textfield = new TextField("", Style.inputTextFieldStyle);
		email_input_textfield = new TextField("", Style.inputTextFieldStyle);
		character_input_textfield = new TextField("", Style.inputTextFieldStyle);
		cancel_button = new Button(Style.getButtonStyle("CANCEL_BUTTON_UP", "CANCEL_BUTTON_DOWN"));
		signup_button = new Button(Style.getButtonStyle("REGISTER_BUTTON_UP", "REGISTER_BUTTON_DOWN"));

		id_input_textfield.setWidth(250);
		password_input_textfield.setWidth(250);
		email_input_textfield.setWidth(250);
		character_input_textfield.setWidth(250);

		id_input_textfield.setPosition(320, 480 - 46 - 40);
		password_input_textfield.setPosition(320, 480 - 46 - 105);
		email_input_textfield.setPosition(320, 480 - 46 - 170);
		character_input_textfield.setPosition(320, 480 - 46 - 234);
		
		signup_button.setPosition(590, 480 - 110 - 170);
		cancel_button.setPosition(590, 480 - 110 - 350);
		
		stage.addActor(background);
		stage.addActor(id_input_textfield);
		stage.addActor(password_input_textfield);
		stage.addActor(email_input_textfield);
		stage.addActor(character_input_textfield);
		stage.addActor(signup_button);
		stage.addActor(cancel_button);

		cancel_button.addListener(this);
		signup_button.addListener(this);

		// 뒤로 가기 키가 눌렸을 때
		backProcessor = new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {

				if ((keycode == Keys.F5) || (keycode == Keys.BACK))

					// 뒤로가기 버튼이 눌린 것과 같은 효과
					changed(null, cancel_button);

				return false;
			}
		};
	}

	/**
	 * 클릭되었을 때 실행됨
	 */
	@Override
	public void changed(ChangeEvent event, Actor actor) {

		if (CommonUI.isBusy())
			return;

		// 회원가입 버튼이 눌렸을 경우
		if (actor == signup_button) {

			String userID = id_input_textfield.getText().trim();
			String userPassword = password_input_textfield.getText().trim();
			String userCharacter = character_input_textfield.getText().trim();
			String userEmail = email_input_textfield.getText().trim();

			if (userID.length() < 1 || userPassword.length() < 1 || userCharacter.length() < 1
					|| userEmail.length() < 6) {
				CommonUI.alert("모든 항목을 올바르게 입력해 주세요.");
				return;
			}

			CommonUI.freeze();

			Connection.signup(userID, userPassword, userCharacter, userEmail);
		}

		// 취소 버튼이 눌렸을 경우
		else if (actor == cancel_button) {

			// 모든 항목을 지운다.
			id_input_textfield.setText("");
			password_input_textfield.setText("");
			character_input_textfield.setText("");
			email_input_textfield.setText("");

			// 로그인 페이지로 돌아간다.
			Main.self.show(Main.Page.SIGNIN);

		}

	}

	@Override
	public void onSignup(boolean succeed) {

		CommonUI.melt();

		// 성공했다면
		if (succeed) {
			CommonUI.notice("새로운 계정을 만들었습니다.");
		}

		// 실패
		else {
			CommonUI.alert("회원 가입에 실패했습니다.");
		}

	}

	@Override
	public void show() {

		Gdx.input.setInputProcessor(new InputMultiplexer(stage, backProcessor));

		// 이벤트 리스너 추가
		Connection.addEventListener(this);
	}

	@Override
	public void render(float delta) {
		// 프레임버퍼 비우기
		Gdx.gl.glClearColor(01f, 1f, 1f, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// 카메라 업데이트
		camera.update();

		stage.draw();

		CommonUI.draw();
	}

	@Override
	public void resize(int width, int height) {
		// 화면 해상도에 맞추어 정렬
		camera.viewportWidth = (float) Math.ceil(Display.scale * width);
		camera.viewportHeight = (float) Math.ceil(Display.scale * height);

		camera.position.set(Display.width / 2, Display.height / 2, 0);
		stage.setViewport(camera.viewportWidth, camera.viewportHeight, false);
	}

	@Override
	public void hide() {
		Connection.removeEventListener(this);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

}
