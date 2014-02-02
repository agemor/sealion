package sealion.client;

import sealion.client.android.ActionResolver;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import android.os.Bundle;

public class Launcher extends AndroidApplication {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionResolver.initialize(this);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useGL20 = true;
		config.useWakelock = true;

		Main main = new Main();
		Main.self = main;

		initialize(main, config);
	}

}
