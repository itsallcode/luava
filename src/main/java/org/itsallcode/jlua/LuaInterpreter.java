package org.itsallcode.jlua;

public class LuaInterpreter implements AutoCloseable {

    private final LowLevelLua lua;

    public LuaInterpreter(final LowLevelLua lua) {
        this.lua = lua;
    }

    public static LuaInterpreter create() {
        final LowLevelLua lua = LowLevelLua.create();
        lua.openLibs();
        return new LuaInterpreter(lua);
    }

    public void close() {
        this.lua.close();
    }

    public void exec(final String chunk) {
        lua.loadString(chunk);
        lua.pcall(0, 0, 0, 0);
    }
}
