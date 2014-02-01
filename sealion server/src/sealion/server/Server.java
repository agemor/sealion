package sealion.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;

import sealion.server.db.Database;
import sealion.server.security.SessionManager;
import sealion.server.user.User;
import sealion.server.util.Display;
import sealion.server.util.Logger;
import sealion.server.world.Stage;

public class Server {

	public static enum Status {
		PENDING, RUNNING, CLOSED
	}

	public Server self = this;

	// 서버 소켓
	public ServerSocket server;
	public Receiver receiver;

	// 서버 설정
	public Configuration config;

	// 서버 상태
	public Status status = Status.PENDING;

	// 데이터베이스 연결
	public Database database;

	// 스테이지
	public Stage[] stages;

	/**
	 * 서버 생성
	 * 
	 * @param name
	 * @param port
	 */
	public Server(String configPath) {

		Launcher.updateConnectionCount(SessionManager.total());

		// 설정 로드
		config = Configuration.load(configPath);

		Display.raw(config.toString());

		// 스테이지 로드
		stages = new Stage[1];

		for (int i = 0; i < stages.length; i++)
			stages[i] = new Stage();
	}

	/**
	 * 서버 시작
	 */
	public void start() {

		// 이미 서버가 실행 중일 경우 명령 무시
		if (status == Status.RUNNING)
			return;

		try {

			// 데이터베이스 취득 밎 연결
			database = Database.getConnection(config.dbHost, new Database.Account(config.dbUser, config.dbPassword));
			if (database.connect())
				Display.show("데이터베이스 서버에 연결되었습니다.");
			else
				return;

			// 서버 초기화
			server = new ServerSocket(config.port);
			receiver = new Receiver();

			// 서버 시작
			receiver.start();

			status = Status.RUNNING;

			Display.show("모든 준비를 마쳤습니다.");
			Logger.info("모든 준비를 마쳤습니다.");

		} catch (IOException e) {
			e.printStackTrace();

			Display.error("서버를 시작할 수 없습니다.");
			Display.raw(e.getMessage());

			Logger.error("cannot start server.");
			Logger.error("서버를 시작할 수 없습니다.");
		}
	}

	/**
	 * 서버 중단
	 */
	public void stop() {

	}

	/**
	 * 모든 활성 유저 정보 저장
	 */
	public void update() {
		for (Stage stage : stages)
			stage.users.update();

		Display.show("데이터베이스에 유저 정보를 업데이트했습니다.");
		Logger.info("데이터베이스에 유저 정보를 업데이트했습니다.");
	}

	/**
	 * IP주소 취득
	 * 
	 * @return
	 */
	public static String getIP() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
							&& inetAddress.isSiteLocalAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * 클라이언트 접속을 받는 스레드
	 * 
	 * @author 현준
	 * 
	 */
	public class Receiver extends Thread {

		@Override
		public void run() {

			// 서버가 살아있는 동안은 계속 받는다.
			while (!server.isClosed()) {

				try {

					// 클라이언트 접속을 받는다.
					Socket client = server.accept();

					// 유저와의 통신을 시작한다.
					User user = new User(client, self);
					user.start();

					Display.show("새로운 연결이 만들어졌습니다. (" + user.getIPAddressAndPort() + ")");
					Logger.info("새로운 연결이 만들어졌습니다. (" + user.getIPAddressAndPort() + ")");
				}

				// 클라이언트 접속에 문제가 있을 경우
				catch (IOException e) {
					e.printStackTrace();

					Display.error("클라이언트 접속을 받을 수 없습니다.");
					Display.raw(e.getMessage());

					Logger.error("클라이언트 접속을 받을 수 없습니다.");
					Logger.error(e.getMessage());
				}

			}
		}
	}
}
