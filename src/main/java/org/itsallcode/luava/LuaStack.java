package org.itsallcode.luava;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;

import org.itsallcode.luava.ffi.Lua;
import org.itsallcode.luava.ffi.lua_CFunction;

class LuaStack {
    private final MemorySegment state;
    private final Arena arena;

    LuaStack(final MemorySegment state, final Arena arena) {
        this.state = state;
        this.arena = arena;
    }

    LuaType getType(final int idx) {
        final int type = Lua.lua_type(state, idx);
        return LuaType.forCode(type);
    }

    boolean isString(final int idx) {
        return Lua.lua_isstring(state, idx) != 0;
    }

    boolean isInteger(final int idx) {
        return Lua.lua_isinteger(state, idx) != 0;
    }

    boolean isNumber(final int idx) {
        return Lua.lua_isnumber(state, idx) != 0;
    }

    void pushNil() {
        Lua.lua_pushnil(state);
    }

    void pushBoolean(final boolean value) {
        Lua.lua_pushboolean(state, value ? 1 : 0);
    }

    void pushNumber(final double n) {
        Lua.lua_pushnumber(state, n);
    }

    void pushInteger(final long n) {
        Lua.lua_pushinteger(state, n);
    }

    void pushString(final String value) {
        final MemorySegment segment = arena.allocateFrom(value);
        Lua.lua_pushlstring(state, segment, segment.byteSize() - 1);
    }

    void pushCFunction(final lua_CFunction.Function fn) {
        pushClosure(fn, 0);
    }

    void pushClosure(final lua_CFunction.Function fn, final int n) {
        final MemorySegment functionSegment = lua_CFunction.allocate(fn, arena);
        Lua.lua_pushcclosure(state, functionSegment, n);
    }

    void pop(final int n) {
        final int size = getTop();
        if (n > size) {
            throw new IllegalStateException("Trying to pop " + n + " elements but stack has size " + size);
        }
        System.out.println("Popping " + n + " elements from stack with size " + size);
        setTop(-n - 1);
    }

    void setTop(final int n) {
        System.out.println(this.printStack());
        Lua.lua_settop(state, n);
        System.out.println(this.printStack());
    }

    boolean toBoolean(final int idx) {
        return Lua.lua_toboolean(state, idx) != 0;
    }

    String toString(final int idx) {
        if (!isString(idx)) {
            throw new LuaException("Expected string at index " + idx + " but was " + getType(idx));
        }
        final MemorySegment len = arena.allocateFrom(Lua.size_t, 0);
        final MemorySegment result = Lua.lua_tolstring(state, idx, len);
        final long stringLength = len.get(Lua.size_t, 0);
        final byte[] bytes = result.reinterpret(stringLength).toArray(Lua.C_CHAR);

        int length = bytes.length;
        if (bytes.length > 0 && bytes[bytes.length - 1] == 0x0) {
            length = length - 1;
        }
        return new String(bytes, 0, length, StandardCharsets.UTF_8);
    }

    double toNumber(final int idx) {
        final MemorySegment isNumber = arena.allocateFrom(Lua.C_INT, 0);
        final double value = Lua.lua_tonumberx(state, idx, isNumber);
        if (isNumber.get(Lua.C_INT, 0) != 1) {
            throw new LuaException("No number at index " + idx + " but is " + getType(idx));
        }
        return value;
    }

    long toInteger(final int idx) {
        final MemorySegment isNumber = arena.allocateFrom(Lua.C_INT, 0);
        final long value = Lua.lua_tointegerx(state, idx, isNumber);
        if (isNumber.get(Lua.C_INT, 0) != 1) {
            throw new LuaException("No integer at index " + idx + " but is " + getType(idx));
        }
        return value;
    }

    int getTop() {
        return Lua.lua_gettop(state);
    }

    String printStack() {
        final StringBuilder b = new StringBuilder();
        final int top = this.getTop();
        b.append("Stack size: " + top + ": ");
        for (int idx = 1; idx <= top; idx++) {
            b.append(format(idx));
            if (idx < top) {
                b.append(", ");
            }
        }
        return b.toString();
    }

    private String format(final int idx) {
        final LuaType type = getType(idx);
        final String result = "#" + idx + " " + type;
        final String value = switch (type) {
            case STRING -> toString(idx);
            case NUMBER -> String.valueOf(toNumber(idx));
            case BOOLEAN -> String.valueOf(toBoolean(idx));
            default -> "";
        };
        if (!value.isEmpty()) {
            return result + ": " + value;
        }
        return result;
    }
}
