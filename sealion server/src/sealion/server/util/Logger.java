package sealion.server.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

	public static final String path = "./log.txt";
	public static final int FLUSH_PERIOD = 5;

	private static FileWriter writer;
	private static int flushCount = 0;
	private static StringBuffer buffer;

	/**
	 * 로거 초기화
	 */
	public static void initialize() {

		try {

			File logFile = new File(path);
			if (!logFile.exists())
				logFile.createNewFile();

			writer = new FileWriter(logFile, true);

			buffer = new StringBuffer();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void info(Object message) {
		buffer.append(Timestamp.get() + " INFO: " + message.toString() + "\n");
		flush();
	}

	public static void error(Object message) {
		buffer.append(Timestamp.get() + " ERROR: " + message.toString() + "\n");
		flush();
	}

	private static void flush() {
		if (flushCount++ > FLUSH_PERIOD) {
			flushCount = 0;

			try {
				writer.write(buffer.toString());
				writer.flush();
				buffer.delete(0, buffer.length() - 1);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public static void forceFlush() {
		try {
			writer.write(buffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
