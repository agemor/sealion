package sealion.client.net;

public interface SignupEvent extends ConnectionEvent {
	public void onSignup(boolean succeed);
}
