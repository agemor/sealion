package sealion.server.security;

public class Session {

	/**
	 * 技记狼 措惑 林家
	 */
	public String address;

	/**
	 * 技记狼 蜡瓤己
	 */
	public boolean valid = false;

	public Session(String address) {
		this.address = address;
	}
}
