package sealion.server.util;

import sealion.server.Launcher;

public class Display {
	public static void show(Object message) {
		Launcher.show("" + Timestamp.get() + " - " + message.toString() + "\n");
	}

	public static void error(Object message) {
		Launcher.show("" + Timestamp.get() + " - ERROR: " + message.toString() + "\n");
	}

	public static void raw(Object message) {
		Launcher.show(message.toString() + "\n");
	}
}
