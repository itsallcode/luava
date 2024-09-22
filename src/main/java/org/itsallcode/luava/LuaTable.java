package org.itsallcode.luava;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import org.itsallcode.luava.ffi.Lua;

public class LuaTable {
    private final LuaStack stack;
    private final MemorySegment state;
    private final int tableIndex;
    private final Arena arena;

    LuaTable(final MemorySegment state, final LuaStack stack, final Arena arena, final int tableIndex) {
        this.state = state;
        this.stack = stack;
        this.arena = arena;
        this.tableIndex = tableIndex;
    }

    public String getString(final String key) {
        getField(key, LuaType.STRING);
        final String value = stack.toString(-1);
        stack.pop(1);
        return value;
    }

    public long getInteger(final String key) {
        getField(key, LuaType.NUMBER);
        final long value = stack.toInteger(-1);
        stack.pop(1);
        return value;
    }

    public double getNumber(final String key) {
        getField(key, LuaType.NUMBER);
        final double value = stack.toNumber(-1);
        stack.pop(1);
        return value;
    }

    public boolean getBoolean(final String key) {
        getField(key, LuaType.BOOLEAN);
        final boolean value = stack.toBoolean(-1);
        stack.pop(1);
        return value;
    }

    private LuaType getFieldType(final String key) {
        final int fieldType = Lua.lua_getfield(state, tableIndex, arena.allocateFrom(key));
        return LuaType.forCode(fieldType);
    }

    private void getField(final String key, final LuaType expectedFieldType) {
        final LuaType fieldType = getFieldType(key);
        if (fieldType != expectedFieldType) {
            throw new LuaException(
                    "Expected field '" + key + "' of type " + expectedFieldType + " but was " + fieldType);
        }
    }
}
