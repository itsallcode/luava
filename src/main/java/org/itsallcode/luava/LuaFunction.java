package org.itsallcode.luava;

import java.lang.foreign.Arena;

public class LuaFunction {

    private final LowLevelLua lowLevelLua;
    private final Arena arena;
    private final int idx;
    private final int errorHandlerIdx;

    LuaFunction(final LowLevelLua lowLevelLua, final Arena arena, final int idx, final int errorHandlerIdx) {
        this.lowLevelLua = lowLevelLua;
        this.arena = arena;
        this.idx = idx;
        this.errorHandlerIdx = errorHandlerIdx;
    }

    public void addArgInteger(final int value) {
        lowLevelLua.stack().pushInteger(value);
    }

    public void call(final int nargs, final int nresults) {
        lowLevelLua.pcall(nargs, nresults, errorHandlerIdx);
    }

    public long getIntegerResult() {
        final long value = lowLevelLua.stack().toInteger(-1);
        lowLevelLua.stack().pop(1);
        return value;
    }
}
