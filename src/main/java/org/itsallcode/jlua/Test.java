package org.itsallcode.jlua;

import java.lang.foreign.Arena;

import org.itsallcode.jlua.ffi.Lua;

//import org.itsallcode.jlua.ffi.*;
public class Test {
    public static void main(final String[] args) {
        try (var arena = Arena.ofConfined()) {
            final var format = arena.allocateUtf8String("Hello");
            final var value = arena.allocateUtf8String("World");
            System.out.println(Lua.LUA_IDSIZE());
        }
    }
}
