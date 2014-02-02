package sealion.client.page;

import sealion.client.Display;
import sealion.client.Main;
import sealion.client.android.ActionResolver;
import sealion.client.asset.Graphics;
import sealion.client.net.Connection;
import sealion.client.net.Packet;
import sealion.client.net.SigninEvent;
import sealion.client.net.SignupEvent;
import sealion.client.ui.CommonUI;
import sealion.client.ui.DialogEvent;
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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SignInPage extends ChangeListener implements Page, SigninEvent, SignupEvent, DialogEvent {

	/**
	 * 시스템 변수
	 */
	public GL20 gl = Gdx.graphics.getGL20();

	/**
	 * UI
	 */
	public Stage stage;
	public Image background;
	public Button signin_button;
	public Button signup_button;
	public Camera camera;

	/**
	 * Back Key 처리 관련
	 */
	private float elapsedTime = 0;
	private boolean areYouSureToExit = false;

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

		// 버튼
		signin_button = new Button(Style.getButtonStyle("LOGIN_BUTTON_UP", "LOGIN_BUTTON_DOWN"));
		signup_button = new Button(Style.getButtonStyle("REGISTER_BUTTON_UP", "REGISTER_BUTTON_DOWN"));
		signin_button.setPosition(200, 480 - 110 - 140);
		signup_button.setPosition(440, 480 - 110 - 140);

		// 스테이지에 추가
		stage.addActor(background);
		stage.addActor(signin_button);
		stage.addActor(signup_button);

		// 이벤트 리스너 추가
		Connection.signinEventListeners.add(this);
		Connection.signupEventListeners.add(this);

		signin_button.addListener(this);
		signup_button.addListener(this);
	}

	@Override
	public void backKeyPressed() {

		if (areYouSureToExit) {
			Connection.close();
			Gdx.app.exit();
		}

		else {
			areYouSureToExit = true;
			ActionResolver.showShortToast("뒤로가기 버튼을 한 번 더 누르면 종료합니다.");
		}
	}

	@Override
	public void onDialogFinished(String... args) {

		if (args.length == 2) {

			String userID = args[0].trim();
			String userPassword = args[1].trim();

			if (userID.length() < 1 || userPassword.length() < 1) {
				ActionResolver.showAlertBox("로그인 오류", "모든 항목을 입력해 주세요", "확인");
				return;
			}

			CommonUI.freeze();

			Connection.signin(userID, userPassword);
		} else {
			String userID = args[0].trim();
			String userPassword = args[1].trim();
			String userCharacter = args[2].trim();
			String userEmail = args[3].trim();

			if (userID.length() < 1 || userPassword.length() < 1 || userCharacter.length() < 1
					|| userEmail.length() < 6) {
				ActionResolver.showAlertBox("회원 가입 오류", "모든 항목을 올바르게 입력해 주세요.", "확인");
				return;
			}

			CommonUI.freeze();

			Connection.signup(userID, userPassword, userCharacter, userEmail);

		}
	}

	@Override
	public void changed(ChangeEvent event, Actor actor) {

		// 로그인일 경우
		if (actor == signin_button) {
			ActionResolver.showSigninBox("로그인", "확인", "취소", this);
		}

		// 회원 가입일 경우
		else if (actor == signup_button) {
			ActionResolver.showSignupBox("회원 가입", "계정 생성", "취소", this);
		}

	}

	@Override
	public void onSignin(boolean succeed) {

		// UI를 녹인다.
		CommonUI.melt();

		if (succeed) {
			ActionResolver.showShortToast("로그인에 성공하였습니다.");
			// 월드로 들어간다.
			Main.self.show(Main.Page.GAME);

			// 여러 자료를 요청한다.
			Connection.send(new Packet(Packet.Header.CHARACTER, ""));
			Connection.send(new Packet(Packet.Header.USER_LIST, ""));

		} else {
			ActionResolver.showAlertBox("로그인 오류", "아이디 또는 비밀번호가 올바르지 않습니다.", "확인");
		}
	}

	@Override
	public void onSignup(boolean succeed) {
		CommonUI.melt();

		// 성공했다면
		if (succeed) {
			ActionResolver.showAlertBox("회원 가입 완료", "회원 가입을 완료하였습니다.", "확인");
		}

		// 실패
		else {
			ActionResolver.showAlertBox("회원 가입 실패", "이미 사용 중인 아이디나 캐릭터 이름입니다.", "확인");
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

		if (areYouSureToExit) {
			elapsedTime += delta;

			if (elapsedTime > 3) {
				areYouSureToExit = false;
				elapsedTime = 0;
			}
		}
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
		stage.setViewport(camera.viewportWidth, camera.viewportHeight, false);
	}

	@Override
	public void resume() {
	}

}
