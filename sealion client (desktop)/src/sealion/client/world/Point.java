package sealion.client.world;

import sealion.client.net.Packet;

public class Point {

	/**
	 * 스테이지
	 */
	public int stage;

	/**
	 * 스테이지 상의 좌표
	 */
	public float x;
	public float y;
	public float z;

	/**
	 * 좌표 생성
	 * 
	 * @param x
	 * @param y
	 */
	public Point(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point(int stage, float x, float y, float z) {
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
	public Point(String packet) {
		String[] chunk = packet.split(":");

		stage = Integer.parseInt(chunk[0]);
		x = Float.parseFloat(chunk[1]);
		y = Float.parseFloat(chunk[2]);
		z = Float.parseFloat(chunk[3]);
	}

	/**
	 * 두 점 사이 거리 구하기
	 * 
	 * @param location
	 * @return
	 */
	public double getDistance(Point location) {
		return Math.sqrt(Math.pow(x - location.x, 2) + Math.pow(y - location.y, 2));

	}

	public String getPacket() {
		return String.valueOf((int) x) + Packet.SPLITTER + String.valueOf((int) y) + Packet.SPLITTER
				+ String.valueOf((int) z);
	}

}
