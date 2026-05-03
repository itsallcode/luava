package org.itsallcode.luava;

@SuppressWarnings("serial")
public class LuaException extends RuntimeException {
    public LuaException(final String message) {
        super(message);
    }
}
