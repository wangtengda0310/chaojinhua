package com.igame.work.chat.exception;

import com.smartfoxserver.v2.exceptions.SFSErrorData;
import com.smartfoxserver.v2.exceptions.SFSException;

/**
 * @author xym
 */
public class MessageException extends SFSException {

    public MessageException() {
    }

    public MessageException(String message) {
        super(message);
    }

    public MessageException(String message, SFSErrorData data) {
        super(message, data);
    }
}
