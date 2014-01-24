package com.jayin.utils;

import java.util.UUID;

import android.content.Context;
import android.telephony.TelephonyManager;

public class Common {
    /**
     * 获得设备uuid并加密
     * @param context
     * @return uuid obj
     */
	public static UUID getUUID(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String uniqueId = tm.getDeviceId();
		UUID deviceUuid = new UUID(uniqueId.hashCode(), 64);
		return deviceUuid;
	}
}
