package org.itsallcode.luava;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import org.itsallcode.luava.ffi.Lua;

public class LuaFunction {

    private final MemorySegment state;
    private final LuaStack stack;
    private final Arena arena;
    private final int idx;

    LuaFunction(final MemorySegment state, final LuaStack stack, final Arena arena, final int idx) {
        this.state = state;
        this.stack = stack;
        this.arena = arena;
        this.idx = idx;
    }

    public void addArgInteger(final int value) {
        stack.pushInteger(value);
    }

    public void call(final int nargs, final int nresults) {
        call(nargs, nresults, 0, 0, Lua.NULL());
    }

    private void call(final int nargs, final int nresults, final int errfunc, final long ctx, final MemorySegment k) {
        final int status = Lua.lua_pcallk(state, nargs, nresults, errfunc, ctx, k);
        if (status != Lua.LUA_OK()) {
            final String errorMessage = stack.toString(-1);
            stack.pop(1);
            throw new FunctionCallException("lua_pcallk", status, errorMessage);
        }
    }

    public long getIntegerResult() {
        final long value = stack.toInteger(-1);
        stack.pop(1);
        return value;
    }
}
