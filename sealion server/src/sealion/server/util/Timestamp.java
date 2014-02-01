package sealion.server.util;

import java.util.Calendar;

public final class Timestamp {
	private static Calendar time;

	static {
		time = Calendar.getInstance();
	}

	public static String get() {
		int MM = time.get(Calendar.MONTH);
		int dd = time.get(Calendar.DAY_OF_MONTH);
		int hh = time.get(Calendar.HOUR_OF_DAY);
		int mm = time.get(Calendar.MINUTE);
		int ss = time.get(Calendar.SECOND);
		StringBuffer buffer = new StringBuffer();
		buffer.append(MM + 1);
		buffer.append("/");
		buffer.append(dd);
		buffer.append(" ");
		buffer.append(hh);
		buffer.append(":");
		buffer.append(mm);
		buffer.append(":");
		buffer.append(ss);
		return buffer.toString();
	}

}
