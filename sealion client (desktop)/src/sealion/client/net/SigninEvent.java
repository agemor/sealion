package sealion.client.net;

public interface SigninEvent extends ConnectionEvent {
	public void onSignin(boolean succeed);
}
