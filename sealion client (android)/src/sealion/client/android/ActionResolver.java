package sealion.client.android;

import sealion.client.ui.DialogEvent;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 안드로이드 네이티브 API를 사용하게 해 준다.
 * 
 * @author 현준
 * 
 */
public class ActionResolver {

	private static Handler uiThread;
	private static Context context;

	public static void initialize(Context appContext) {
		uiThread = new Handler();
		context = appContext;
	}

	/**
	 * 토스트 메시지 띄우기 (김)
	 * 
	 * @param toastMessage
	 */
	public static void showShortToast(final CharSequence toastMessage) {
		uiThread.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 토스트 메시지 띄우기 (짧음)
	 * 
	 * @param toastMessage
	 */
	public static void showLongToast(final CharSequence toastMessage) {
		uiThread.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show();
			}
		});
	}

	/**
	 * 경고창 띄우기
	 * 
	 * @param alertBoxTitle
	 * @param alertBoxMessage
	 * @param alertBoxButtonText
	 */
	public static void showAlertBox(final String alertBoxTitle, final String alertBoxMessage,
			final String alertBoxButtonText) {

		uiThread.post(new Runnable() {

			@Override
			public void run() {
				new AlertDialog.Builder(context).setTitle(alertBoxTitle).setMessage(alertBoxMessage)
						.setNeutralButton(alertBoxButtonText, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
							}
						}).create().show();
			}
		});
	}

	public static void showSigninBox(final String signinBoxTitle, final String signinButtonText,
			final String cancelButtonText, final DialogEvent event) {

		uiThread.post(new Runnable() {

			@Override
			public void run() {

				// 레이아웃 파라미터 지정
				LinearLayout.LayoutParams layoutParms = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F);

				LinearLayout.LayoutParams editorParms = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F);

				// 입력 필드가 있는 레이아웃
				LinearLayout layout = new LinearLayout(context);

				layout.setLayoutParams(layoutParms);
				layout.setOrientation(LinearLayout.VERTICAL);

				// 아이디/비밀번호 입력
				final EditText id_input = new EditText(context);
				final EditText password_input = new EditText(context);

				id_input.setLayoutParams(editorParms);
				id_input.setHint("아이디");

				password_input.setLayoutParams(editorParms);
				password_input.setHint("비밀번호");
				password_input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

				// ui 추가
				layout.addView(id_input);
				layout.addView(password_input);

				// 로그인 대화창 생성
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(signinBoxTitle);
				builder.setView(layout);

				// 취소 버튼
				builder.setNegativeButton(cancelButtonText, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});

				// 로그인 버튼
				builder.setPositiveButton(signinButtonText, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						event.onDialogFinished(id_input.getText().toString(), password_input.getText().toString());
					}
				});

				builder.create().show();

			}
		});

	}

	public static void showSignupBox(final String signupBoxTitle, final String signupButtonText,
			final String cancelButtonText, final DialogEvent event) {
		uiThread.post(new Runnable() {

			@Override
			public void run() {

				// 레이아웃 파라미터 지정
				LinearLayout.LayoutParams layoutParms = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F);

				LinearLayout.LayoutParams editorParms = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F);

				// 입력 필드가 있는 레이아웃
				LinearLayout layout = new LinearLayout(context);

				layout.setLayoutParams(layoutParms);
				layout.setOrientation(LinearLayout.VERTICAL);

				// 아이디/비밀번호 입력
				final EditText id_input = new EditText(context);
				final EditText password_input = new EditText(context);
				final EditText character_input = new EditText(context);
				final EditText email_input = new EditText(context);

				id_input.setLayoutParams(editorParms);
				id_input.setHint("아이디");

				password_input.setLayoutParams(editorParms);
				password_input.setHint("비밀번호");

				character_input.setLayoutParams(editorParms);
				character_input.setHint("캐릭터 이름");

				email_input.setLayoutParams(editorParms);
				email_input.setHint("이메일 주소");

				// ui 추가
				layout.addView(id_input);
				layout.addView(password_input);
				layout.addView(character_input);
				layout.addView(email_input);

				// 로그인 대화창 생성
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(signupBoxTitle);
				builder.setView(layout);

				// 취소 버튼
				builder.setNegativeButton(cancelButtonText, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});

				// 로그인 버튼
				builder.setPositiveButton(signupButtonText, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						String userID = id_input.getText().toString();
						String password = password_input.getText().toString();
						String characterName = character_input.getText().toString();
						String email = email_input.getText().toString();

						event.onDialogFinished(userID, password, characterName, email);
					}
				});

				builder.create().show();
			}
		});

	}

	public static void showChatBox(final String chatBoxTitle, final String sendButtonText,
			final String cancelButtonText, final DialogEvent event) {
		uiThread.post(new Runnable() {

			@Override
			public void run() {

				// 레이아웃 파라미터 지정
				LinearLayout.LayoutParams layoutParms = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F);

				LinearLayout.LayoutParams editorParms = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F);

				// 입력 필드가 있는 레이아웃
				LinearLayout layout = new LinearLayout(context);

				layout.setLayoutParams(layoutParms);
				layout.setOrientation(LinearLayout.VERTICAL);

				// 메시지 입력칸
				final EditText message_input = new EditText(context);

				message_input.setLayoutParams(editorParms);
				message_input.setHint("메시지");

				// ui 추가
				layout.addView(message_input);

				// 로그인 대화창 생성
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(chatBoxTitle);
				builder.setView(layout);

				// 취소 버튼
				builder.setNegativeButton(cancelButtonText, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});

				// 보내기 버튼
				builder.setPositiveButton(sendButtonText, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						event.onDialogFinished(message_input.getText().toString());
					}
				});

				builder.create().show();

			}
		});
	}

	public static void openUri(String uri) {
		Uri myUri = Uri.parse(uri);
		Intent intent = new Intent(Intent.ACTION_VIEW, myUri);
		context.startActivity(intent);
	}
}
