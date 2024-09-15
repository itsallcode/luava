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

    private LowLevelLua(final Arena arena, final MemorySegment state) {
        this.arena = arena;
        this.state = state;
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
            throw new LuaException("lua_pcallk", error, message);
        }
    }

    void loadString(final String chunk) {
        final int error = Lua.luaL_loadstring(state, arena.allocateFrom(chunk));
        if (error != 0) {
            throw new LuaException("luaL_loadstring", error);
        }
    }

    String toString(final int idx) {
        return toString(idx, -1L);
    }

    String toString(final int idx, final long len) {
        final MemorySegment result = Lua.lua_tolstring(state, idx, arena.allocateFrom(Lua.size_t, len));
        return result.getString(0, StandardCharsets.UTF_8);
    }

    public void close() {
        Lua.lua_close(state);
        arena.close();
    }
}
