package sealion.client.net;

import java.util.List;

public interface UserEvent extends ConnectionEvent {
	public void onBroadcast(String data);

	public void onArrive(User user);

	public void onLeave(User user);

	public void onUserListLoaded(List<User> list);

	public void onCharacterLoaded(User me);

	public void onChatMessageArrived(User user, String message);

	public void onMove(User user, int direction);

	public void onStop(User user);
}
