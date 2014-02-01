package sealion.server.world;

public class Location {

	/**
	 * 스테이지
	 */
	public int stage;

	/**
	 * 스테이지 상의 좌표
	 */
	public int x;
	public int y;
	public int z;

	/**
	 * 좌표 생성
	 * 
	 * @param x
	 * @param y
	 */
	public Location(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Location(int stage, int x, int y, int z) {
		this.stage = stage;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * 파싱 문자열로 좌표 생성
	 * 
	 * @param packet
	 */
	public Location(String packet) {
		String[] chunk = packet.split(":");

		stage = Integer.parseInt(chunk[0]);
		x = Integer.parseInt(chunk[1]);
		y = Integer.parseInt(chunk[2]);
		z = Integer.parseInt(chunk[3]);
	}

	public String getPacket() {
		return stage + ":" + x + ":" + y + ":" + z;
	}
}