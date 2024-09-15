package org.itsallcode.jlua;

public class Test {
    public static void main(final String[] args) {
        try (LuaInterpreter lua = LuaInterpreter.create()) {
            lua.exec("print('hello world')");
        }
    }
}
