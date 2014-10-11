package sealion.server;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import sealion.server.util.Display;

public class Configuration {

	/**
	 * 버전 정보
	 */
	public String version = "1,0";

	/**
	 * 서버 이름 정보
	 */
	public String name = "lionse";

	/**
	 * 서버 포트
	 */
	public int port = 17343;

	/**
	 * 인코딩 정보
	 */
	public String encoding = "utf-8";

	/**
	 * 최대 접속 가능 유저 수
	 */
	public int maximumConnection = 1000;

	/**
	 * 같은 IP에서 중복 접속 허용 여부
	 */
	public boolean allowDuplicateConnection = false;

	/**
	 * 데이터베이스 연결 정보
	 */
	public String dbHost = "jdbc:mysql://localhost:3306/lionse";
	public String dbUser = "root";
	public String dbPassword = "asd123";

	/**
	 * 설정 생성
	 */
	public Configuration() {

	}

	/**
	 * 서버 설정을 로드한다.
	 * 
	 * @param path
	 * @return
	 */
	public static Configuration load(String path) {

		// 서버 설정 파일 (server.config)
		File configFile = new File(path);

		if (!configFile.exists()) {
			Display.error("설정 파일이 존재하지 않으므로 기본 설정을 사용합니다.");
			return new Configuration();
		}
		try {

			// 프로퍼티 객체 생성
			Properties property = new Properties();

			// 프로퍼티를 로드한다.
			property.load(new FileInputStream(configFile));

			// 커스텀 설정 생성
			Configuration config = new Configuration();

			config.version = property.getProperty("version", "1.0");
			config.name = property.getProperty("name", "lionse");

			config.encoding = property.getProperty("encoding", "utf-8");
			config.allowDuplicateConnection = Boolean.parseBoolean(property.getProperty("allow-duplicate-connection",
					"false"));
			config.maximumConnection = Integer.parseInt(property.getProperty("maximum-connection", "1000"));

			config.dbHost = property.getProperty("db-host", "jdbc:mysql://localhost:3306/lionse");
			config.dbUser = property.getProperty("db-user", "root");
			config.dbPassword = property.getProperty("db-password", "asd123");

			return config;

		}

		// 에러가 발생했을 경우
		catch (Exception e) {
			e.printStackTrace();
		}

		Display.error("설정 파일을 불러오는 도중 에러가 발생하여 기본 설정을 사용합니다.");
		return new Configuration();

	}

	public String toString() {
		String info = new String();

		info = "version: " + version//
				+ "\nname: " + name//
				+ "\nport: " + port//
				+ "\nencoding: " + encoding//
				+ "\nmaximum connection: " + maximumConnection//
				+ "\nallow duplicate connection: " + String.valueOf(allowDuplicateConnection)//
				+ "\ndb host: " + dbHost//
				+ "\ndb user:" + dbUser + "\n";

		return info;
	}
}
