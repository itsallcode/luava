package org.itsallcode.jlua;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;

import org.itsallcode.jlua.ffi.Lua;
import org.itsallcode.jlua.ffi.lua_KFunction;
import org.itsallcode.jlua.ffi.lua_KFunction.Function;

class LowLevelLua implements AutoCloseable {
    private final Arena arena;
    private final MemorySegment state;
    private final LuaTypeDetector typeDetector;

    private LowLevelLua(final Arena arena, final MemorySegment state) {
        this.arena = arena;
        this.state = state;
        this.typeDetector = new LuaTypeDetector(state);
    }

    static LowLevelLua create() {
        final Arena arena = Arena.ofConfined();
        final MemorySegment state = Lua.luaL_newstate();
        return new LowLevelLua(arena, state);
    }

    void openLibs() {
        Lua.luaL_openlibs(state);
    }

    void pcall(final int nargs, final int nresults, final int errfunc, final long ctx) {
        final Function function = (final MemorySegment l, final int status, final long ctx1) -> {
            System.out.println("Upcall callback");
            return 0;
        };
        pcall(nargs, nresults, errfunc, ctx, function);
    }

    void pcall(final int nargs, final int nresults, final int errfunc, final long ctx, final Function upcallFunction) {
        final MemorySegment k = lua_KFunction.allocate(upcallFunction, arena);
        final int error = Lua.lua_pcallk(state, nargs, nresults, errfunc, ctx, k);
        if (error != 0) {
            final String message = toString(-1);
            pop(1);
            throw new FunctionCallException("lua_pcallk", error, message);
        }
    }

    void loadString(final String chunk) {
        final int error = Lua.luaL_loadstring(state, arena.allocateFrom(chunk));
        if (error != 0) {
            final String message = toString(-1);
            pop(1);
            throw new FunctionCallException("luaL_loadstring", error, message);
        }
    }

    void pushNil() {
        Lua.lua_pushnil(state);
    }

    void pushBoolean(final boolean value) {
        Lua.lua_pushboolean(state, value ? 1 : 0);
    }

    void pushNumber(final double n) {
        Lua.lua_pushnumber(state, n);
    }

    void pushInteger(final long n) {
        Lua.lua_pushinteger(state, n);
    }

    void pushLString(final String value) {
        final MemorySegment segment = arena.allocateFrom(value);
        Lua.lua_pushlstring(state, segment, segment.byteSize());
    }

    void pop(final int n) {
        setTop(-n - 1);
    }

    void setTop(final int n) {
        Lua.lua_settop(state, n);
    }

    String toString(final int idx) {
        return toString(idx, null);
    }

    String toString(final int idx, final Long len) {
        if (!typeDetector.isString(idx)) {
            throw new LuaException("Expected string at index " + idx + " but was " + typeDetector.getType(idx));
        }
        final MemorySegment result = Lua.lua_tolstring(state, idx,
                len != null ? arena.allocateFrom(Lua.size_t, len) : Lua.NULL());
        return result.getString(0, StandardCharsets.UTF_8);
    }

    private boolean isYieldable() {
        return Lua.lua_isyieldable(state) != 0;
    }
    public void close() {
        Lua.lua_close(state);
        arena.close();
    }
}
