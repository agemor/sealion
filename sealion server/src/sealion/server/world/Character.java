package sealion.server.world;

import sealion.server.user.Packet;

public class Character {

	// 캐릭터 이름
	public String name;

	/**
	 * 캐릭터 정보
	 */
	public int direction = 0;
	public int level = 0;
	public int experience = 0;
	public int maxHealth = 0;
	public int health = 0;
	public int money = 0;

	// 아직은 사용되지 않는 정보들
	public String creature;
	public String item;
	public String quest;

	/**
	 * 캐릭터 위치
	 */
	public Location location;

	/**
	 * 캐릭터 장비
	 */
	public Equipment equipment;

	public Character(String name) {
		this.name = name;

		// 장비 초기화
		equipment = new Equipment();
	}

	/**
	 * 캐릭터 장비
	 * 
	 * @author 현준
	 * 
	 */
	public static class Equipment {

		public int hat;
		public int clothes;
		public int weapon;

		public Equipment() {

		}
	}

	/**
	 * 캐릭터의 패킷 정보를 반환한다.
	 * 
	 * @return
	 */
	public String getPacketData() {
		String packet = name //
				+ Packet.SPLITTER + level//
				+ Packet.SPLITTER + experience//
				+ Packet.SPLITTER + maxHealth//
				+ Packet.SPLITTER + health//
				+ Packet.SPLITTER + money//
				+ Packet.SPLITTER + equipment.hat//
				+ Packet.SPLITTER + equipment.clothes//
				+ Packet.SPLITTER + equipment.weapon//
				+ Packet.SPLITTER + location.stage//
				+ Packet.SPLITTER + location.x //
				+ Packet.SPLITTER + location.y//
				+ Packet.SPLITTER + location.z;//

		return packet;
	}
}
