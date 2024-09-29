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
        return stack.popString();
    }

    public long getInteger(final String key) {
        getField(key, LuaType.NUMBER);
        return stack.popInteger();
    }

    public double getNumber(final String key) {
        getField(key, LuaType.NUMBER);
        return stack.popNumber();
    }

    public boolean getBoolean(final String key) {
        getField(key, LuaType.BOOLEAN);
        return stack.popBoolean();
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
