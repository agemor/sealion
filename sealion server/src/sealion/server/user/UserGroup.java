package sealion.server.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserGroup {

	public List<User> users;

	public UserGroup() {
		users = new ArrayList<User>();
	}

	/**
	 * 그룹 내의 모든 유저에게 패킷을 발송한다.
	 * 
	 * @param packet
	 * @throws IOException
	 */
	public void broadcast(Packet packet, User exception) throws IOException {
		for (User user : users)
			if (user != exception)
				user.send(packet);
	}

	public void broadcast(String rawPacket, User exception) throws IOException {
		for (User user : users)
			if (user != exception)
				user.send(rawPacket);
	}

	/**
	 * 그룹 내의 모든 유저에게 특정 유저가 떠났음을 알린다.
	 * 
	 * @param user
	 * @throws IOException
	 */
	public void farewell(User user) throws IOException {
		broadcast(new Packet(Packet.Header.FAREWELL, user.character.name), user);
	}

	/**
	 * 그룹 내의 모든 유저에게 자신이 왔음을 신고한다.
	 * 
	 * @param user
	 * @throws IOException
	 */
	public void hello(User user) throws IOException {
		broadcast(new Packet(Packet.Header.HELLO, user.character.getPacketData()), user);
	}

	public void update() {
		for (User user : users)
			user.update();
	}

	/**
	 * 그룹에서 특정 유저를 제거한다.
	 * 
	 * @param user
	 */
	public void kick(User user) {
		users.remove(user);
	}

	/**
	 * 그룹에 유저를 추가한다.
	 * 
	 * @param user
	 */
	public void invite(User user) {
		users.add(user);
	}

	/**
	 * 그룹의 유저 목록을 패킷으로 포장하여 리턴한다.
	 * 
	 * @return
	 */
	public String getPacketData() {

		if(users.size() < 1)
			return "";
		
		StringBuffer packet = new StringBuffer();

		for (User user : users) {
			packet.append(user.character.getPacketData());
			packet.append(Packet.PROPERTY);
		}

		// 맨 마지막의 프로퍼티 문자를 제거한다.
		packet.deleteCharAt(packet.length() - 1);

		return packet.toString();
	}
}
