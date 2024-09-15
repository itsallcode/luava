package org.itsallcode.jlua;

import java.lang.foreign.MemorySegment;

import org.itsallcode.jlua.ffi.Lua;

class LuaTypeDetector {
    private final MemorySegment state;

    LuaTypeDetector(final MemorySegment state) {
        this.state = state;
    }

    LuaType getType(final int idx) {
        if (isString(idx)) {
            return LuaType.STRING;
        } else if (isInteger(idx)) {
            return LuaType.INTEGER;
        } else if (isNumber(idx)) {
            return LuaType.NUMBER;
        } else if (isUserdata(idx)) {
            return LuaType.USERDATA;
        } else if (isCFunction(idx)) {
            return LuaType.CFUNCTION;
        } else {
            throw new LuaException("Unknown type at index " + idx);
        }
    }

    boolean isString(final int idx) {
        return Lua.lua_isstring(state, idx) != 0;
    }

    boolean isCFunction(final int idx) {
        return Lua.lua_iscfunction(state, idx) != 0;
    }

    boolean isInteger(final int idx) {
        return Lua.lua_isinteger(state, idx) != 0;
    }

    boolean isNumber(final int idx) {
        return Lua.lua_isnumber(state, idx) != 0;
    }

    boolean isUserdata(final int idx) {
        return Lua.lua_isuserdata(state, idx) != 0;
    }
}
