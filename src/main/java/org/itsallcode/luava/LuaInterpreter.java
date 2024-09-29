package org.itsallcode.luava;

public class LuaInterpreter implements AutoCloseable {

    private final LowLevelLua lua;

    private LuaInterpreter(final LowLevelLua lua) {
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

    LuaStack stack() {
        return lua.stack();
    }

    public String getGlobalString(final String name) {
        lua.getGlobal(name);
        return lua.stack().popString();
    }

    public long getGlobalInteger(final String name) {
        lua.getGlobal(name);
        return lua.stack().popInteger();
    }

    public double getGlobalNumber(final String name) {
        lua.getGlobal(name);
        return lua.stack().popNumber();
    }

    public boolean getGlobalBoolean(final String name) {
        lua.getGlobal(name);
        return lua.stack().popBoolean();
    }

    public LuaTable getGlobalTable(final String name) {
        lua.getGlobal(name);
        return lua.table(-1);
    }

    public LuaFunction getGlobalFunction(final String name) {
        return lua.globalFunction(name);
    }

    public void setGlobalString(final String name, final String value) {
        lua.stack().pushString(value);
        lua.setGlobal(name);
    }

    public void setGlobalInteger(final String name, final long value) {
        lua.stack().pushInteger(value);
        lua.setGlobal(name);
    }

    public void setGlobalNumber(final String name, final double value) {
        lua.stack().pushNumber(value);
        lua.setGlobal(name);
    }

    public void setGlobalBoolean(final String name, final boolean value) {
        lua.stack().pushBoolean(value);
        lua.setGlobal(name);
    }

    public void exec(final String chunk) {
        lua.loadString(chunk);
        lua.pcall(0, 0);
    }
}
