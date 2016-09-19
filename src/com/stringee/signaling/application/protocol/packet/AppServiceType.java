package com.stringee.signaling.application.protocol.packet;

import java.util.HashMap;
import java.util.Map;

public enum AppServiceType {

	DEFAULT(0),
	PING(1),
	LOGIN(2),
	LOGOUT(3),
	CALL_INVITE(4),
	CALL_STATUS_CHANGE(5),
	CALL_BYE(6),
	MESSAGE(7),
	MESSAGE_RESULT(8),;

	static final Map<Integer, AppServiceType> listType = new HashMap<>();

	static {
		final AppServiceType[] all = AppServiceType.values();
		for (AppServiceType all1 : all) {
			listType.put(Integer.valueOf(all1.getValue()), all1);
		}
	}
	private final short value;

	public static AppServiceType getServiceType(short value) {
		return listType.get((int) value);
	}

	private AppServiceType(final int value) {
		this.value = (short) value;
	}

	public short getValue() {
		return value;
	}
}
