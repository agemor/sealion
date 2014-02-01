package sealion.server.user;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import sealion.server.Launcher;
import sealion.server.Server;
import sealion.server.security.Session;
import sealion.server.security.SessionManager;
import sealion.server.util.Display;
import sealion.server.util.Logger;
import sealion.server.world.Character;
import sealion.server.world.Stage;

public class User extends Thread {

	/**
	 * 유저 정보
	 */
	public Entry info;
	public Character character;

	/**
	 * 유저 접속 정보
	 */
	public Socket connection;
	public Server server;

	// 세션
	public Session session;
	private int signinFailedCount = 0;

	/**
	 * 유저가 속해 있는 스테이지
	 */
	public Stage stage;

	/**
	 * 입출력 관련 데이터
	 */
	private BufferedReader reader;
	private BufferedWriter writer;

	/**
	 * 새로운 유저 접속을 생성한다.
	 * 
	 * @param connection
	 * @param config
	 */
	public User(Socket connection, Server server) {
		this.connection = connection;
		this.server = server;

		try {

			// 입 출력 데이터 초기화
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), server.config.encoding));
			writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), server.config.encoding));
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 클라이언트에 패킷을 전송한다.
	 * 
	 * @throws IOException
	 */
	public void send(Packet packet) throws IOException {
		writer.write(packet.toString());
		writer.newLine();
		writer.flush();
	}

	public void send(String rawPacket) throws IOException {
		writer.write(rawPacket);
		writer.newLine();
		writer.flush();
	}

	/**
	 * 유저의 정보를 바탕으로 데이터베이스를 업데이트한다.
	 */
	public void update() {
		server.database.updateCharacter(character);
	}

	/**
	 * 유저와의 접속을 끊는다.
	 */
	public void goodbye() {

		Display.show("유저와의 연결을 끊었습니다. (" + getIPAddressAndPort() + ")");
		Logger.info("유저와의 연결을 끊었습니다. (" + getIPAddressAndPort() + ")");

		try {

			// 세션을 종료한다.
			SessionManager.expire(session);

			// 인증된 유저의 경우
			if (session.valid) {

				// 캐릭터 정보를 저장한다.
				update();

				// 유저 그룹 내의 모든 유저에게 작별 인사를 한 후 찬다.
				stage.users.farewell(this);
				stage.users.kick(this);

			}

			// 모든 연결을 종료한다.
			writer.close();
			reader.close();

			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 유저의 IP주소를 취득한다.
	 * 
	 * @return
	 */
	public String getIPAddress() {
		return String.valueOf(connection.getInetAddress());
	}

	/**
	 * 유저의 IP주소와 포트 정보를 취득한다.
	 * 
	 * @return
	 */
	public String getIPAddressAndPort() {
		return String.valueOf(connection.getInetAddress()) + ":" + String.valueOf(connection.getPort());
	}

	/**
	 * 활성 유저 스레드
	 */
	@Override
	public void run() {

		try {

			// 만약 최대 동시 접속자 수를 넘어섰다면 접속을 종료한다.
			if (SessionManager.total() >= server.config.maximumConnection) {

				// 클라이언트에 인원이 다 찼다고 알린다.
				send(Packet.get(Packet.Type.ERROR_MAXIMUM_CONNECTION_EXCEEDED));

				// 클라이언트에게 작별 인사를 한다.
				goodbye();

				return;
			}

			// 중복 접속 여부를 체크한다.
			if (!server.config.allowDuplicateConnection)
				if (SessionManager.getByAddress(getIPAddress()) != null) {

					// 클라이언트에 중복 접속이 불가능하다고 알린다.
					send(Packet.get(Packet.Type.ERROR_DUPLICATE_CONNECTION));

					// 클라이언트에게 작별 인사를 한다.
					goodbye();

					return;
				}

			// 활성 세션을 심는다. 중복 접속이 가능할 경우에는 포트까지 쓴다.
			if (server.config.allowDuplicateConnection)
				session = SessionManager.register(getIPAddressAndPort());
			else
				session = SessionManager.register(getIPAddress());

			Launcher.updateConnectionCount(SessionManager.total());

			// 환영 인사를 보낸다.
			send(Packet.get(Packet.Type.GREETINGS));

			// 연결이 활성화되어 있는 동안 반복한다.
			while (!connection.isClosed()) {

				// 클라이언트의 메시지를 받는다.
				String rawPacket = reader.readLine();

				// 큰 패킷이 한 번에 들어오면 그냥 연결을 끊어버린다.
				if (rawPacket.length() > 1024 * 5)
					goodbye();

				// 패킷을 끝 문자로 나눈다.
				String[] rawPackets = rawPacket.split(Packet.END);

				// 멀티 패킷 처리
				for (String packet : rawPackets)
					handlePacket(packet.trim());
			}
		}

		// 에러가 발생했을 경우
		catch (Exception e) {
			e.printStackTrace();

			// 저장한 후 종료
			if (session.valid)
				update();

			goodbye();

		}

		Launcher.updateConnectionCount(SessionManager.total());

	}

	/**
	 * 패킷을 처리한다.
	 * 
	 * @param rawPacket
	 * @throws IOException
	 */
	public void handlePacket(String rawPacket) throws IOException {

		String[] data = rawPacket.split(Packet.SPLITTER);

		Logger.info(rawPacket + "(" + getIPAddressAndPort() + ")");

		switch (rawPacket.charAt(0)) {

		// 로그인: 세션 유효성을 받는 과정
		case Packet.Header.SIGNIN:

			// 로그인에 실패했다면 실패 메시지를 보낸다.
			if (!server.database.signin(data[1].trim(), data[2].trim())) {
				send(Packet.get(Packet.Type.SIGNIN_FAILED));
				signinFailedCount++;

				// 10회 이상 틀리면 굿바이
				if (signinFailedCount > 10)
					goodbye();

			} else {
				// 세션을 유효화한다.
				send(Packet.get(Packet.Type.SIGNIN_SUCCESSFUL));
				session.valid = true;

				// 유저 정보를 DB에서 취득한다.
				info = server.database.getUserEntry(data[1].trim());
				character = server.database.getCharacter(info.character);

				// 캐릭터의 스테이지 정보를 바탕으로 서버에서 스테이지를 취득한다.
				if (server.stages.length > character.location.stage)
					stage = server.stages[character.location.stage];
				else
					stage = server.stages[0];

			}

			break;

		// 회원 가입
		case Packet.Header.SIGNUP:
			
			// u, user_id, user_password, character_name, user_email

			String userName = data[1].trim();
			String userPassword = data[2].trim();
			String characterName = data[3].trim();
			String userEmail = data[4].trim();

			// 정보가 잘못되었다면
			if (userName.length() < 1 || userPassword.length() < 1 || characterName.length() < 1
					|| userEmail.length() < 6) {
				send(Packet.get(Packet.Type.SIGNUP_FAILED));
				break;
			}

			// 회원가입을 시도하고, 거짓이 리턴되었다면 회원가입 실패
			if (server.database.signup(userName, userPassword, characterName, userEmail))
				send(Packet.get(Packet.Type.SIGNUP_SUCCESSFUL));
			else
				send(Packet.get(Packet.Type.SIGNUP_FAILED));

			break;

		// 기존 접속 유저 목록
		case Packet.Header.USER_LIST:

			// 세션이 유효하지 않다면 무시한다.
			if (!session.valid)
				break;

			send(new Packet(Packet.Header.USER_LIST, stage.users.getPacketData()));

			break;

		// 캐릭터 정보
		case Packet.Header.CHARACTER:

			// 세션이 유효하지 않다면 무시한다.
			if (!session.valid)
				break;

			send(new Packet(Packet.Header.CHARACTER, character.getPacketData()));

			break;

		// 기존 유저들에게 인사를 건넨다. (신고식)
		case Packet.Header.HELLO:

			stage.users.hello(this);
			stage.users.invite(this);

			break;

		// 기존 유저들에게 작별 인사를 한다.
		case Packet.Header.FAREWELL:

			stage.users.farewell(this);
			stage.users.kick(this);

			break;

		// 다수에게 메시지 전달 (relay 없음)
		case Packet.Header.BROADCAST:

			// 세션이 유효하지 않다면 무시한다.
			if (!session.valid)
				break;

			// 그대로 전달한다.
			stage.users.broadcast(rawPacket + Packet.SPLITTER + character.name + Packet.END, this);

			break;

		// 다수에게 메시지 전달 (relay 있음)
		case Packet.Header.RELAYED_BROADCAST:

			// 세션이 유효하지 않다면 무시한다.
			if (!session.valid)
				break;

			if (rawPacket.length() < 3)
				break;

			// 두 번째 서브해더
			switch (rawPacket.charAt(1)) {

			// dm,direction,x,y,z
			case Packet.Header.MOVE:
				if (true) {
					String[] position = rawPacket.split(Packet.SPLITTER);

					// 불량 패킷이면 버린다.
					if (position.length != 5)
						break;

					character.direction = Integer.parseInt(position[1]);
					character.location.x = Integer.parseInt(position[2]);
					character.location.y = Integer.parseInt(position[3]);
					character.location.z = Integer.parseInt(position[4]);
				}

				// 패킷 끝에 대상을 추가하여 모든 유저에게 뿌린다.
				stage.users.broadcast(rawPacket + Packet.SPLITTER + character.name + Packet.END, this);

				break;

			// ds,x,y,z
			case Packet.Header.STOP:

				if (true) {
					String[] position = rawPacket.split(Packet.END)[0].split(Packet.SPLITTER);

					// 불량 패킷이면 버린다.
					if (position.length != 4)
						break;

					character.location.x = Integer.parseInt(position[1]);
					character.location.y = Integer.parseInt(position[2]);
					character.location.z = Integer.parseInt(position[3]);
				}

				// 패킷 끝에 대상을 추가하여 모든 유저에게 뿌린다.
				stage.users.broadcast(rawPacket + Packet.SPLITTER + character.name + Packet.END, this);

				break;
			}

			break;

		// 알 수 없는 명령
		default:

		}
	}

	/**
	 * 유저 데이터 엔트리
	 * 
	 * @author 현준
	 * 
	 */
	public static class Entry {

		// 유저 이름
		public String name;

		// 캐릭터 이름
		public String character;

		// 권한 레벨
		public int level = 0;

		// 차단 여부
		public boolean banned = false;

		/**
		 * 데이터 엔트리 생성
		 * 
		 * @param name
		 * @param character
		 */
		public Entry(String name) {
			this.name = name;
		}

	}

}
