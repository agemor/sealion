package sealion.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sealion.server.user.User;
import sealion.server.util.Display;
import sealion.server.world.Character;
import sealion.server.world.Location;

import com.mysql.jdbc.Driver;

@SuppressWarnings("unused")
public class Database {

	public static enum Status {
		PENDING, CONNECTED, CLOSED, ERROR
	}

	// 호스트
	public String host;

	// 서버 연결
	public Connection connection;

	// 퀴리
	public Statement query;

	// 데이터베이스 상태
	public Status status = Status.PENDING;

	// 연결된 계정
	public Account account;

	/**
	 * 데이터베이스 생성
	 */
	public Database(String host) {
		this.host = host;
	}

	/**
	 * 데이터베이스 서버와 연결한다.
	 * 
	 * @param account
	 * @return
	 */
	public boolean connect(Account account) {

		try {
			// 연결 취득
			connection = DriverManager.getConnection(host, account.user, account.password);
			
			// 쿼리 실행 통로 취득
			query = connection.createStatement();

			status = Status.CONNECTED;

		} catch (SQLException e) {
			e.printStackTrace();

			Display.error("Cannot connect to DB server.");
			Display.raw(e.getMessage());

			status = Status.ERROR;

			return false;
		}

		return true;
	}

	public boolean connect() {

		if (account == null)
			return false;

		return connect(account);
	}

	/**
	 * 유저의 로그인 정보가 유효한지 확인한다.
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	public boolean signin(String user, String password) {
		if (status != Status.CONNECTED)
			return false;

		try {

			// 아이디와 비밀번호가 일치하는지 확인한다.
			ResultSet users = query.executeQuery(Query.get(Query.Type.SELECT_USER, user));

			while (users.next()) {
				if (users.getString("password").equals(password)) {
					// 로그인 정보가 일치한다면 최종 로그인 시간을 업데이트한다.
					updateLastSigninTime(user);
					
					// 로그인 성공
					return true;
				}
			}

			return false;
		}

		// 에러가 발생할 경우 로그인 실패
		catch (SQLException e) {
			e.printStackTrace();

			Display.error("Signin failed.");
			Display.raw(e.getMessage());
		}
		return false;
	}

	/**
	 * 새로운 유저 정보를 생성한다.
	 * 
	 * @param user
	 * @param password
	 * @param character
	 * @param email
	 * @return
	 */
	public boolean signup(String user, String password, String character, String email) {
		if (status != Status.CONNECTED)
			return false;

		// 유저 네임과 캐릭터 네임이 모두 고유값인지 체크한다.
		if (getUserEntry(user) != null || getCharacter(character) != null)
			return false;

		try {

			// 데이터베이스에 유저 정보와 캐릭터 정보를 생성한다.
			query.execute(Query.get(Query.Type.INSERT_USER, user, password, character, email));
			query.execute(Query.get(Query.Type.INSERT_CHARACTER, character));

			return true;
		}

		// 에러가 발생할 경우 로그인 실패
		catch (SQLException e) {
			e.printStackTrace();

			Display.error("Signup failed.");
			Display.raw(e.getMessage());
		}
		return false;
	}

	/**
	 * 유저 엔트리를 취득한다.
	 * 
	 * @param user
	 * @return
	 */
	public User.Entry getUserEntry(String user) {
		if (status != Status.CONNECTED)
			return null;

		try {

			// 유저 정보를 얻어 온다.
			ResultSet users = query.executeQuery(Query.get(Query.Type.SELECT_USER, user));

			while (users.next()) {
				User.Entry entry = new User.Entry(user);
				entry.character = users.getString("character").trim();
				entry.level = users.getInt("level");
				entry.banned = users.getInt("banned") == 1 ? true : false;

				return entry;
			}
		}

		// 에러가 발생할 경우
		catch (SQLException e) {
			e.printStackTrace();

			Display.error("Cannot get user entry.");
			Display.raw(e.getMessage());
		}
		return null;
	}

	/**
	 * 캐릭터를 취득한다.
	 * 
	 * @param name
	 * @return
	 */
	public Character getCharacter(String name) {
		if (status != Status.CONNECTED)
			return null;

		try {

			// 유저 정보를 얻어 온다.
			ResultSet data = query.executeQuery(Query.get(Query.Type.SELECT_CHARACTER, name));

			while (data.next()) {

				// 유저 정보를 인스턴스에 넣는다.
				Character character = new Character(name);
				character.level = data.getInt("level");
				character.experience = data.getInt("exp");
				character.maxHealth = data.getInt("maxhealth");
				character.health = data.getInt("health");
				character.money = data.getInt("money");

				// 위치 정보 설정
				character.location = new Location(data.getString("location"));

				// 장비 정보 설정
				character.equipment.hat = data.getInt("hat");
				character.equipment.clothes = data.getInt("clothes");
				character.equipment.weapon = data.getInt("weapon");

				return character;
			}
		}

		// 에러가 발생할 경우
		catch (SQLException e) {
			e.printStackTrace();

			Display.error("Cannot get character info.");
			Display.raw(e.getMessage());
		}
		return null;
	}

	/**
	 * 캐릭터 정보를 업데이트한다.
	 * 
	 * @param character
	 * @return
	 */
	public boolean updateCharacter(Character character) {
		if (status != Status.CONNECTED)
			return false;

		try {

			// 업데이트한다.
			query.executeUpdate(Query.get(Query.Type.UPDATE_CHARACTER, character.name, String.valueOf(character.level),
					String.valueOf(character.experience), String.valueOf(character.maxHealth),
					String.valueOf(character.health), String.valueOf(character.money),
					String.valueOf(character.equipment.hat), String.valueOf(character.equipment.clothes),
					String.valueOf(character.equipment.weapon), character.location.getPacket()));

			return true;
		}

		// 실패할 경우
		catch (SQLException e) {
			e.printStackTrace();

			Display.error("Cannot update character info");
			Display.raw(e.getMessage());
		}

		return false;
	}

	/**
	 * 유저의 최종 로그인 시간을 업데이트한다.
	 * 
	 * @param user
	 * @return
	 * @throws SQLException
	 */

	private boolean updateLastSigninTime(String user) throws SQLException {
		if (status != Status.CONNECTED)
			return false;

		// 쿼리 실행
		query.executeUpdate(Query.get(Query.Type.UPDATE_LAST_LOGIN_TIME, user));

		return true;
	}

	/**
	 * 서버와의 연결을 끊는다.
	 * 
	 * @return
	 */
	public boolean close() {
		try {

			// 연결을 종료한다.
			connection.close();

			status = Status.CLOSED;

		} catch (SQLException e) {
			e.printStackTrace();

			status = Status.ERROR;

			return false;
		}

		return true;
	}

	/**
	 * 데이터베이스 연결 취득
	 * 
	 * @param host
	 * @param account
	 * @return
	 */
	public static Database getConnection(String host, Account account) {

		Database connection = new Database(host);
		connection.account = account;

		return connection;
	}

	/**
	 * 데이터베이스 계정
	 * 
	 * @author 현준
	 * 
	 */
	public static class Account {

		// 데이터베이스 아이디
		public String user;

		// 데이터베이스 비밀번호
		public String password;

		/**
		 * 계정 생성
		 * 
		 * @param user
		 * @param password
		 */
		public Account(String user, String password) {
			this.user = user;
			this.password = password;
		}
	}
}
