package org.itsallcode.luava;

public class LuaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LuaException(final String message) {
        super(message);
    }
}
