package org.itsallcode.jlua;

public class LuaException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String function;
    private final int errorCode;
    private final String message;

    public LuaException(final String function, final int errorCode) {
        this(function, errorCode, null);
    }

    public LuaException(final String function, final int errorCode, final String message) {
        super(buildMessage(function, errorCode, message));
        this.function = function;
        this.errorCode = errorCode;
        this.message = message;
    }

    private static String buildMessage(final String function, final int errorCode, final String message) {
        return "Function '" + function + "' failed with error " + errorCode + ": " + message;
    }

    public String getFunction() {
        return function;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getRootError() {
        return message;
    }
}
