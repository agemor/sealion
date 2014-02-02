package sealion.client.net;

public interface NoticeEvent extends ConnectionEvent {

	public void onNotice(String message);

}
