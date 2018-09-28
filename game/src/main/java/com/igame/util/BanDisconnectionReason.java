package com.igame.util;

import com.smartfoxserver.v2.util.IDisconnectionReason;

/**
 * 
 * @author Marcus.Z
 *
 */
public class BanDisconnectionReason implements IDisconnectionReason {

	@Override
	public byte getByteValue() {
		return 2;
	}

	@Override
	public int getValue() {
		return 2;
	}

}
