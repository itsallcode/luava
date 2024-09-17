package org.itsallcode.jlua;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import org.itsallcode.jlua.ffi.Lua;
import org.itsallcode.jlua.ffi.lua_KFunction;
import org.itsallcode.jlua.ffi.lua_KFunction.Function;

class LowLevelLua implements AutoCloseable {
    private final Arena arena;
    private final MemorySegment state;
    private final LuaStack stack;

    private LowLevelLua(final Arena arena, final MemorySegment state) {
        this.arena = arena;
        this.state = state;
        this.stack = new LuaStack(state, arena);
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
            final String message = stack.toString(-1);
            stack.pop(1);
            throw new FunctionCallException("lua_pcallk", error, message);
        }
    }

    void loadString(final String chunk) {
        final int error = Lua.luaL_loadstring(state, arena.allocateFrom(chunk));
        if (error != 0) {
            final String message = stack.toString(-1);
            stack.pop(1);
            throw new FunctionCallException("luaL_loadstring", error, message);
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

    private boolean isYieldable() {
        return Lua.lua_isyieldable(state) != 0;
    }

    public void close() {
        Lua.lua_close(state);
        arena.close();
    }
}
