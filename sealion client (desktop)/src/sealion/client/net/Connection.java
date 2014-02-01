package sealion.client.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import sealion.client.net.crypto.SHA256;
import sealion.client.world.Path;
import sealion.client.world.Point;

import com.badlogic.gdx.Gdx;

public class Connection {

	public static final String ENCODING = "utf-8";

	/**
	 * 서버 연결 정보
	 */
	public static String host;
	public static int port;
	public static boolean connected = false;

	/**
	 * 연결 소켓
	 */
	protected static Socket socket;
	protected static BufferedReader reader;
	protected static BufferedWriter writer;

	/**
	 * 데이터 리시버
	 */
	private static Thread receiver;

	/**
	 * 이벤트 리스너 목록
	 */
	private static List<SigninEvent> signinEventListeners;
	private static List<SignupEvent> signupEventListeners;
	private static List<ErrorEvent> errorEventListeners;
	private static List<UserEvent> userEventListeners;
	private static List<NoticeEvent> noticeEventListeners;

	/**
	 * 유저 리스트
	 */
	private static List<User> users;

	/**
	 * 내 정보
	 */
	public static User me;

	/**
	 * 연결을 초기화한다.
	 */
	public static void initialize() {
		users = new ArrayList<User>();
		me = new User();
		me.character.isMe = true;

		signinEventListeners = new ArrayList<SigninEvent>();
		signupEventListeners = new ArrayList<SignupEvent>();
		errorEventListeners = new ArrayList<ErrorEvent>();
		userEventListeners = new ArrayList<UserEvent>();
		noticeEventListeners = new ArrayList<NoticeEvent>();

	}

	/**
	 * 서버와 연결한다.
	 * 
	 * @param host
	 * @param port
	 */
	public static void connect(String host, int port) {
		Connection.host = host;
		Connection.port = port;

		try {

			// 소켓을 생성한다.
			socket = new Socket();

			// 소켓을 호스트와 연결한다.
			socket.connect(new InetSocketAddress(host, port));

			if (socket.isConnected())
				connected = true;

			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), ENCODING));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), ENCODING));

			// 데이터 리시버 시작
			receiver = new Receiver();
			receiver.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 서버로 패킷을 전송한다.
	 * 
	 * @param packet
	 */
	public static void send(Packet packet) {
		if (!connected)
			return;
		try {

			writer.write(packet.header + Packet.SPLITTER + packet.data + Packet.END);
			writer.newLine();
			writer.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void send(String rawPacket) {
		if (!connected)
			return;
		try {

			writer.write(rawPacket + Packet.END);
			writer.newLine();
			writer.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 로그인
	 * 
	 * @param id
	 * @param password
	 */
	public static void signin(String id, String password) {
		send(new Packet(Packet.Header.SIGNIN, id + Packet.SPLITTER + SHA256.digest(password)));

	}

	/**
	 * 회원 가입
	 * 
	 * @param id
	 * @param password
	 * @param character
	 * @param email
	 */
	public static void signup(String id, String password, String character, String email) {
		send(new Packet(Packet.Header.SIGNUP, id + Packet.SPLITTER + SHA256.digest(password) + Packet.SPLITTER
				+ character + Packet.SPLITTER + email));
	}

	/**
	 * 캐릭터 이동
	 * 
	 * @param direction
	 * @param position
	 */
	public static void move(int direction, Point position) {
		send(String.valueOf(Packet.Header.RELAYED_BROADCAST) + String.valueOf(Packet.Header.MOVE) + Packet.SPLITTER
				+ direction + Packet.SPLITTER + position.getPacket());

	}

	/**
	 * 캐릭터 정지
	 * 
	 * @param position
	 */
	public static void stop(Point position) {
		send(String.valueOf(Packet.Header.RELAYED_BROADCAST) + String.valueOf(Packet.Header.STOP) + Packet.SPLITTER
				+ position.getPacket());
	}

	public static void chat(String message) {
		Connection.send(String.valueOf(Packet.Header.BROADCAST) + String.valueOf(Packet.Header.CHAT) + Packet.SPLITTER
				+ message);
	}

	/**
	 * 이벤트 리스너를 추가한다.
	 * 
	 * @param event
	 */
	public static void addEventListener(ConnectionEvent event) {

		// 이벤트의 세부 타입에 따라 다른 컨테이너에 추가한다.
		if (event instanceof SigninEvent)
			signinEventListeners.add((SigninEvent) event);

		else if (event instanceof SignupEvent)
			signupEventListeners.add((SignupEvent) event);

		else if (event instanceof ErrorEvent)
			errorEventListeners.add((ErrorEvent) event);

		else if (event instanceof UserEvent)
			userEventListeners.add((UserEvent) event);

		else if (event instanceof NoticeEvent)
			noticeEventListeners.add((NoticeEvent) event);
	}

	/**
	 * 이벤트 리스너를 제거한다.
	 * 
	 * @param event
	 */
	public static void removeEventListener(ConnectionEvent event) {
		if (event instanceof SigninEvent)
			signinEventListeners.remove((SigninEvent) event);

		else if (event instanceof SignupEvent)
			signupEventListeners.remove((SignupEvent) event);

		else if (event instanceof ErrorEvent)
			errorEventListeners.remove((ErrorEvent) event);

		else if (event instanceof UserEvent)
			userEventListeners.remove((UserEvent) event);

		else if (event instanceof NoticeEvent)
			noticeEventListeners.remove((NoticeEvent) event);
	}

	/**
	 * 서버에서 온 데이터를 취득한다.
	 * 
	 * @author 현준
	 * 
	 */
	private static class Receiver extends Thread {

		@Override
		public void run() {

			// 소켓이 연결되어 있는 동안 지속
			while (socket.isConnected()) {

				try {

					// 데이터 취득
					String rawData = reader.readLine();

					// 원시 패킷으로 나눈다
					String[] rawPackets = rawData.split(Packet.END);

					// 각각의 패킷을 처리한다.
					for (String rawPacket : rawPackets)
						handlePacket(rawPacket.trim());
				}

				// 데이터 처리 중 에러가 발생했다면
				catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
		}

		/**
		 * 원시 패킷을 처리한다.
		 * 
		 * @param rawPacket
		 */
		private void handlePacket(String rawPacket) {

			// 빈 패킷은 버린다.
			if (rawPacket.length() < 1)
				return;

			// 분리 문자로 나눈다.
			String[] data = null;

			// 패킷 해더를 기준으로 쪼갠다.
			switch (rawPacket.charAt(0)) {

			// 로그인 결과
			case Packet.Header.SIGNIN:

				data = rawPacket.split(Packet.SPLITTER);

				// 로그인 성공 여부
				boolean signinSucceed = Integer.parseInt(data[1]) == 1;

				for (SigninEvent listener : signinEventListeners)
					listener.onSignin(signinSucceed);

				break;

			// 회원가입 결과
			case Packet.Header.SIGNUP:

				data = rawPacket.split(Packet.SPLITTER);

				// 회원가입 성공 여부
				boolean signupSucceed = Integer.parseInt(data[1]) == 1;

				for (SignupEvent listener : signupEventListeners)
					listener.onSignup(signupSucceed);

				break;

			// 에러
			case Packet.Header.ERROR:

				data = rawPacket.split(Packet.SPLITTER);

				// 에러 코드
				int errorCode = Integer.parseInt(data[1]);

				for (ErrorEvent listener : errorEventListeners)
					listener.onError(errorCode);

				break;

			// 브로드캐스트
			case Packet.Header.BROADCAST:
				
				String broadcastData = rawPacket.substring(2);
				
				if (rawPacket.length() < 2)
					for (UserEvent listener : userEventListeners)
						listener.onBroadcast(broadcastData);
				else {

					// 브로드캐스팅 내용을 분석한다.
					switch (rawPacket.charAt(1)) {

					// 채팅 메시지 전송
					case Packet.Header.CHAT:

						if (true) {
							Gdx.app.log(rawPacket, rawPacket.charAt(1)+"");
							// 구분 문자로 분할
							String[] packet = rawPacket.substring(3).split(Packet.SPLITTER);

							if (packet.length < 2)
								break;

							// 유저네임으로 유저를 찾는다.
							User targetUser = null;
							for (User user : users) {
								if (user.character.name.equals(packet[1])) {
									targetUser = user;
									break;
								}
							}

							for (UserEvent listener : userEventListeners)
								listener.onChatMessageArrived(targetUser, packet[0]);

						}

						break;
					default:
					}

				}

				break;

			// 캐릭터 정보 로드
			case Packet.Header.CHARACTER:

				data = rawPacket.substring(2).split(Packet.SPLITTER);

				// 캐릭터 정보를 업데이트한다
				me.loadCharacterPacket(data);

				for (UserEvent listener : userEventListeners)
					listener.onCharacterLoaded(me);

				break;

			// 공지 메시지
			case Packet.Header.NOTICE:

				data = rawPacket.split(Packet.SPLITTER);

				String noticeMessage = data[1].trim();

				for (NoticeEvent listener : noticeEventListeners)
					listener.onNotice(noticeMessage);

				break;

			// 유저 퇴장
			case Packet.Header.FAREWELL:

				data = rawPacket.split(Packet.SPLITTER);

				// 퇴장한 유저의 이름을 취득한다.
				String leavingUserName = data[1].trim();
				User leavingUser = null;

				// 유저를 목록에서 찾아 모두에게 전달한다.
				for (User user : users) {
					if (user.character.name.equals(leavingUserName)) {
						for (UserEvent listener : userEventListeners)
							listener.onLeave(user);
						leavingUser = user;
						break;
					}
				}

				users.remove(leavingUser);

				break;

			// 유저 입장
			case Packet.Header.HELLO:

				data = rawPacket.substring(2).split(Packet.SPLITTER);

				// 입장한 유저에 대한 인스턴스를 만든다.
				User arrivingUser = new User();

				// 캐릭터 정보를 업데이트한다
				arrivingUser.loadCharacterPacket(data);

				// 유저 목록에 추가한다.
				users.add(arrivingUser);

				// 디스패치
				for (UserEvent listener : userEventListeners)
					listener.onArrive(arrivingUser);

				break;

			// 유저 목록
			case Packet.Header.USER_LIST:

				// 해더를 제거한 부분을 속성 분리 문자로 나눈다.
				String[] userPackets = rawPacket.substring(2).split(Packet.PROPERTY);

				// 유저 목록 초기화
				users.clear();

				// 리스트에 추가
				for (String userPacket : userPackets) {
					if (userPacket.trim().length() < 1)
						continue;

					System.out.println(userPacket);

					User user = new User();
					user.loadCharacterPacket(userPacket.split(Packet.SPLITTER));
					users.add(user);
				}

				// 디스패치
				for (UserEvent listener : userEventListeners)
					listener.onUserListLoaded(users);

				break;

			// 서버를 거친 브로드캐스트
			case Packet.Header.RELAYED_BROADCAST:

				data = rawPacket.substring(3).split(Packet.SPLITTER);

				switch (rawPacket.charAt(1)) {

				// 캐릭터 이동
				case Packet.Header.MOVE:
					if (true) {

						int direction = Integer.parseInt(data[0]);
						int x = Integer.parseInt(data[1]);
						int y = Integer.parseInt(data[2]);
						int z = Integer.parseInt(data[3]);
						String userName = data[4].trim();

						Point newPoint = new Point(x, y, z);

						User user = null;

						for (User one : users)
							if (one.character.name.equals(userName)) {
								user = one;
								break;
							}

						// 보관 중이거나 실행 중인 방향 이동 명령을 비활성화시킨다.
						if (user.character.currentPath != null)
							user.character.currentPath.cancel();

						// 한계값 체크 (이동 - 정지 명령인지 확인)
						if (user.character.position.getDistance(newPoint) > 3) {

							// 새로운 패스 생성
							Path path = new Path(user.character.getDirection(newPoint), newPoint);
							user.character.path.add(path);
						}

						// 캐릭터의 스택에 이동 명령 추가
						Path move = new Path(direction);
						user.character.path.add(move);

						user.character.currentPath = move;

						// 디스패치
						for (UserEvent listener : userEventListeners)
							listener.onMove(user, direction);

					}
					break;

				// 캐릭터 정지
				case Packet.Header.STOP:
					if (true) {

						int x = Integer.parseInt(data[0]);
						int y = Integer.parseInt(data[1]);
						int z = Integer.parseInt(data[2]);
						String userName = data[3].trim();

						Point newPoint = new Point(x, y, z);

						User user = null;

						for (User one : users)
							if (one.character.name.equals(userName)) {
								user = one;
								break;
							}

						// 보관중인 이동 명령을 모두 취소
						user.character.currentPath.cancel();

						// 멈춘 위치와 너무 차이가 많이 나면 올바른 위치로 이동한다.
						if (user.character.position.getDistance(newPoint) > 3) {

							// 새로운 패스 생성
							Path path = new Path(user.character.getDirection(newPoint), newPoint);
							user.character.path.add(path);
						}

						// 디스패치
						for (UserEvent listener : userEventListeners)
							listener.onStop(user);

					}

					break;
				}

				break;

			}
		}
	}
}
