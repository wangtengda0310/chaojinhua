package com.igame.util;

import com.smartfoxserver.v2.util.IDisconnectionReason;

/**
 * 
 * @author Marcus.Z
 *
 */
public class KickIDisconnectionReason implements IDisconnectionReason {

	@Override
	public byte getByteValue() {
		return 1;
	}

	@Override
	public int getValue() {
		return 1;
	}

}
