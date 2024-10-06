package org.itsallcode.luava;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.function.IntSupplier;
import java.util.logging.Logger;

import org.itsallcode.luava.ffi.Lua;
import org.itsallcode.luava.ffi.lua_KFunction;

class LowLevelLua implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(LowLevelLua.class.getName());
    private final Arena arena;
    final MemorySegment state;
    private final LuaStack stack;

    LowLevelLua(final Arena arena, final MemorySegment state) {
        this.arena = arena;
        this.state = state;
        this.stack = new LuaStack(state, arena);
    }

    static LowLevelLua create() {
        final Arena arena = Arena.ofConfined();
        final MemorySegment state = Lua.luaL_newstate();
        return new LowLevelLua(arena, state);
    }

    LowLevelLua forState(final MemorySegment newState) {
        return new LowLevelLua(arena, newState);
    }

    void openLibs() {
        Lua.luaL_openlibs(state);
    }

    void pcall(final int nargs, final int nresults) {
        pcallk(nargs, nresults, 0, 0, null);
    }

    void pcall(final int nargs, final int nresults, final int errfunc) {
        pcallk(nargs, nresults, errfunc, 0, null);
    }

    /**
     * This function behaves exactly like {@link #pcall(int, int, int, long)},
     * except that it allows the
     * called function to yield.
     * 
     * @param nargs
     * @param nresults
     * @param msgHandler
     * @param ctx
     * @param upcallFunction
     */
    private void pcallk(final int nargs, final int nresults, final int msgHandler, final long ctx,
            final lua_KFunction.Function upcallFunction) {
        LOG.info(
                () -> "Calling function with " + nargs + " args and " + nresults + " results, msgHandler: " + msgHandler
                        + " context: " + ctx);
        final MemorySegment k = upcallFunction == null ? Lua.NULL() : lua_KFunction.allocate(upcallFunction, arena);
        checkStatus("lua_pcallk", () -> Lua.lua_pcallk(state, nargs, nresults, msgHandler, ctx, k));
    }

    void loadString(final String chunk) {
        checkStatus("luaL_loadstring", () -> Lua.luaL_loadstring(state, arena.allocateFrom(chunk)));
    }

    void checkStatus(final String functionName, final IntSupplier nativeFunctionCall) {
        final int status = nativeFunctionCall.getAsInt();
        if (status != Lua.LUA_OK()) {
            LOG.warning(
                    () -> "Lua API call '" + functionName + "' failed with status " + status
                            + ": getting error message...");
            final String message = stack.popString();
            throw new FunctionCallException(functionName, status, message);
        }
    }

    void getGlobal(final String name) {
        Lua.lua_getglobal(state, arena.allocateFrom(name));
    }

    void setGlobal(final String name) {
        Lua.lua_setglobal(state, arena.allocateFrom(name));
    }

    LuaStack stack() {
        return stack;
    }

    LuaTable table(final int idx) {
        assertType(idx, LuaType.TABLE);
        return new LuaTable(state, stack, arena, idx);
    }

    private void assertType(final int idx, final LuaType expectedType) {
        final LuaType type = stack().getType(idx);
        if (type != expectedType) {
            throw new LuaException("Expected " + expectedType + " at " + idx + " but got " + type);
        }
    }

    boolean isYieldable() {
        return Lua.lua_isyieldable(state) != 0;
    }

    public void close() {
        Lua.lua_close(state);
        arena.close();
    }
}
