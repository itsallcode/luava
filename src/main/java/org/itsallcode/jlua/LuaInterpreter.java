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

    public String getGlobalString(final String name) {
        lua.getGlobal(name);
        final String value = lua.stack().toString(-1);
        lua.stack().pop(1);
        return value;
    }

    public void setString(final String name, final String value) {
        lua.stack().pushLString(value);
        lua.setGlobal(name);
    }

    public void exec(final String chunk) {
        lua.loadString(chunk);
        lua.pcall(0, 0, 0, 0);
    }
}
