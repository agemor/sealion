package sealion.client.net;

import sealion.client.world.Character;

public class User {

	/**
	 * 캐릭터 정보
	 */
	public Character character;

	public User() {

		// 캐릭터 정보 초기화
		character = new Character();
	}

	/**
	 * 패킷을 분석하여 캐릭터 정보를 완성한다.
	 * 
	 * @param data
	 */
	public void loadCharacterPacket(String[] data) {
		character.setName(data[0].trim());
		character.level = Integer.parseInt(data[1]);
		character.experience = Integer.parseInt(data[2]);
		character.maxHealth = Integer.parseInt(data[3]);
		character.health = Integer.parseInt(data[4]);
		character.money = Integer.parseInt(data[5]);
		character.hat = Integer.parseInt(data[6]);
		character.clothes = Integer.parseInt(data[7]);
		character.weapon = Integer.parseInt(data[8]);
		character.position.stage = Integer.parseInt(data[9]);
		character.position.x = Integer.parseInt(data[10]);
		character.position.y = Integer.parseInt(data[11]);
		character.position.z = Integer.parseInt(data[12]);

	}
}
