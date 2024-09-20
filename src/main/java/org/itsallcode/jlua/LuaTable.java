package org.itsallcode.jlua;

import java.lang.foreign.MemorySegment;

import org.itsallcode.jlua.ffi.Lua;

public class LuaTable {
    private final LuaStack stack;
    private final MemorySegment state;
    private final int tableIndex;

    LuaTable(final MemorySegment state, final LuaStack stack, final int tableIndex) {
        this.state = state;
        this.stack = stack;
        this.tableIndex = tableIndex;
    }

    public String getString(final String keyName) {
        stack.pushString(keyName);
        final int errorCode = Lua.lua_gettable(state, tableIndex);
        if (errorCode != 0) {
            throw new FunctionCallException("lua_gettable", errorCode);
        }
        final String value = stack.toString(-1);
        stack.pop(1);
        return value;
    }
}
