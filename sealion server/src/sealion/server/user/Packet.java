package sealion.server.user;

import java.util.HashMap;
import java.util.Map;

public class Packet {

	/**
	 * 패킷 타입
	 * 
	 * @author 현준
	 * 
	 */
	public static enum Type {
		SIGNIN_SUCCESSFUL, SIGNIN_FAILED, SIGNUP_SUCCESSFUL, SIGNUP_FAILED, GREETINGS, ERROR_MAXIMUM_CONNECTION_EXCEEDED, ERROR_DUPLICATE_CONNECTION
	}

	private static Map<Type, Packet> packets;

	static {

		// 자주 쓰는 패킷 선언
		packets = new HashMap<Type, Packet>();

		packets.put(Type.GREETINGS, new Packet(Header.NOTICE, "hello~~"));

		packets.put(Type.SIGNIN_SUCCESSFUL, new Packet(Header.SIGNIN, "1"));
		packets.put(Type.SIGNIN_FAILED, new Packet(Header.SIGNIN, "0"));

		packets.put(Type.SIGNUP_SUCCESSFUL, new Packet(Header.SIGNUP, "1"));
		packets.put(Type.SIGNUP_FAILED, new Packet(Header.SIGNUP, "0"));

		packets.put(Type.ERROR_MAXIMUM_CONNECTION_EXCEEDED, new Packet(Header.ERROR, "0"));
		packets.put(Type.ERROR_DUPLICATE_CONNECTION, new Packet(Header.ERROR, "1"));
	}

	/**
	 * 자주 쓰는 패킷은 보관해 두었다가 쓴다.
	 * 
	 * @param type
	 * @return
	 */
	public static Packet get(Type type) {
		return packets.get(type);
	}

	/**
	 * 패킷 데이터 구분 문자
	 */
	public static final String SPLITTER = ";";
	public static final String PROPERTY = "|";

	/**
	 * 패킷 끝 문자
	 */
	public static final String END = "/";

	/**
	 * 패킷 해더
	 * 
	 * @author 현준
	 * 
	 */
	public static class Header {
		// 메인 해더
		public static final char NOTICE = 'n';
		public static final char ERROR = 'e';
		public static final char BROADCAST = 'b';
		public static final char RELAYED_BROADCAST = 'd';
		public static final char SIGNIN = 'i';
		public static final char SIGNUP = 'u';
		public static final char USER_LIST = 'l';
		public static final char CHARACTER = 'c';
		public static final char HELLO = 'h';
		public static final char FAREWELL = 'g';

		// 릴레이 해더
		public static final char MOVE = 'm';
		public static final char STOP = 's';
	}

	public char header;
	public String data;

	public Packet(char header, String data) {
		this.header = header;
		this.data = data;
	}

	public String toString() {
		return header + SPLITTER + data + END;
	}

}
