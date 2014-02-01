package sealion.server.db;

public class Query {

	public static enum Type {
		INSERT_USER, INSERT_CHARACTER, SELECT_USER, SELECT_CHARACTER, UPDATE_USER, UPDATE_CHARACTER, UPDATE_LAST_LOGIN_TIME
	}

	public static String get(Type type, String... args) {

		// 타입에 따라 다른 쿼리를 출력한다.
		switch (type) {

		// 유저 정보 생성
		case INSERT_USER:
			return "INSERT INTO `lionse`.`user` (`no`, `id`, `password`, `character`, `email`, `level`, `banned`, `date`, `lastlogin`) VALUES (NULL, '"
					+ args[0] // ID
					+ "', '" + args[1] // PASSWORD
					+ "', '" + args[2] // CHARACTER
					+ "', '" + args[3] // E-MAIL
					+ "', '0', '0', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);";

			// 캐릭터 정보 생성
		case INSERT_CHARACTER:
			return "INSERT INTO `lionse`.`character` (`name`, `level`, `maxhealth`, `health`, `money`, `hat`, `clothes`, `weapon`, `creature`, `item`, `quest`, `exp`, `location`) VALUES ('"
					+ args[0] // CHARACTER
					+ "', '1', '100', '100', '0', '1', '1', '1', '0', '0', '0', '0', '1:1000:500:1');";

			// 유저 정보 취득
		case SELECT_USER:
			return "SELECT * FROM `lionse`.`user` WHERE `id` = '" + args[0] + "';";

			// 캐릭터 정보 취득
		case SELECT_CHARACTER:
			return "SELECT * FROM `lionse`.`character` WHERE `name` = '" + args[0] + "';";

		case UPDATE_USER:
			return "";
		case UPDATE_CHARACTER:
			return "UPDATE `lionse`.`character` SET `level`='" + args[1] // level
					+ "', `exp` = '" + args[2] // experience
					+ "', `maxhealth` = '" + args[3] // max health
					+ "', `health` = '" + args[4] // health
					+ "', `money` = '" + args[5] // money
					+ "', `hat` = '" + args[6] // hat
					+ "', `clothes` = '" + args[7] // clothes
					+ "', `weapon` = '" + args[8] // weapon
					+ "', `location` = '" + args[9] // location
					+ "'  WHERE `name` = '" + args[0] + "';"; // name

			// 최종 로그인 시간 갱신
		case UPDATE_LAST_LOGIN_TIME:
			return "UPDATE `lionse`.`user` SET `lastlogin`= CURRENT_TIMESTAMP WHERE `id` = '" + args[0] + "';";
		default:
		}

		return null;
	}
}
