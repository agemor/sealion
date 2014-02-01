package sealion.server.security;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

	public static Map<String, Session> sessions = new HashMap<String, Session>();

	/**
	 * IP주소로 세션을 검색하여 반환한다.
	 * 
	 * @param IPAndPort
	 * @return
	 */
	public static Session getByAddress(String address) {
		return sessions.get(address);
	}

	/**
	 * 총 활성 세션 수를 반환한다.
	 * 
	 * @return
	 */
	public static int total() {
		return sessions.size();
	}

	/**
	 * 세션을 파기한다.
	 * 
	 * @param session
	 */
	public static void expire(Session session) {
		sessions.remove(session.address);
	}

	/**
	 * 세션을 등록한다.
	 * 
	 * @param address
	 * @return
	 */
	public static Session register(String address) {

		Session session = new Session(address);
		sessions.put(address, session);

		return session;
	}
}
