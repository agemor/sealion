package sealion.client.net;

public interface ErrorEvent extends ConnectionEvent {
	public void onError(int errorCode);
}
