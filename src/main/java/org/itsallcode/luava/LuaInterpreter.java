package org.itsallcode.luava;

import java.lang.foreign.MemorySegment;
import java.util.function.Function;

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
        final String value = lua.stack().toString(-1);
        lua.stack().pop(1);
        return value;
    }

    public long getGlobalInteger(final String name) {
        lua.getGlobal(name);
        final long value = lua.stack().toInteger(-1);
        lua.stack().pop(1);
        return value;
    }

    public double getGlobalNumber(final String name) {
        lua.getGlobal(name);
        final double value = lua.stack().toNumber(-1);
        lua.stack().pop(1);
        return value;
    }

    public boolean getGlobalBoolean(final String name) {
        lua.getGlobal(name);
        final boolean value = lua.stack().toBoolean(-1);
        lua.stack().pop(1);
        return value;
    }

    public LuaTable getGlobalTable(final String name) {
        lua.getGlobal(name);
        return lua.table(-1);
    }

    public LuaFunction getGlobalFunction(final String name) {
        return this.getGlobalFunction(name, null);
    }

    public LuaFunction getGlobalFunction(final String name, final Function<LuaInterpreter, Integer> messageHandler) {
        int errorHandlerIdx = 0;
        if (messageHandler != null) {
            lua.stack().pushCFunction((final MemorySegment newState) -> {
                return messageHandler.apply(new LuaInterpreter(this.lua.forState(newState)));
            });
            errorHandlerIdx = lua.stack().getTop();
        }
        lua.getGlobal(name);
        return lua.function(-1, errorHandlerIdx);
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
