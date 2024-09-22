package org.itsallcode.luava;

import java.util.Arrays;

import org.itsallcode.luava.ffi.Lua;

public enum LuaType {
    NONE(Lua.LUA_TNONE()), NIL(Lua.LUA_TNIL()), BOOLEAN(Lua.LUA_TBOOLEAN()), LIGHT_USERDATA(Lua.LUA_TLIGHTUSERDATA()),
    NUMBER(Lua.LUA_TNUMBER()), STRING(Lua.LUA_TSTRING()), TABLE(Lua.LUA_TTABLE()), FUNCTION(Lua.LUA_TFUNCTION()),
    USERDATA(Lua.LUA_TUSERDATA()), THREAD(Lua.LUA_TTHREAD()), NUMTYPES(Lua.LUA_NUMTYPES());

    private final int type;

    private LuaType(final int type) {
        this.type = type;
    }

    static LuaType forCode(final int type) {
        return Arrays.stream(LuaType.values())
                .filter(t -> t.type == type).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No type found for code " + type));
    }
}
