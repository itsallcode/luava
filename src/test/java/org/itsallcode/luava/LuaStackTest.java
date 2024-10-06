package org.itsallcode.luava;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

class LuaStackTest {
    private LowLevelLua lua;
    private LuaStack stack;

    @BeforeEach
    void setup() {
        lua = LowLevelLua.create();
        stack = lua.stack();
    }

    @AfterEach
    void stop() {
        lua.close();
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "abc", "öäüß", "!§$%&", "String with \0 zero" })
    void pushString(final String value) {
        stack.pushString(value);
        final String actual = stack.popString();
        assertThat(actual, equalTo(value));
    }

    @Test
    void pushNil() {
        stack.pushNil();
        assertThat(stack.getType(-1), equalTo(LuaType.NIL));
    }

    @Test
    void popNilAsBoolean() {
        stack.pushNil();
        assertThat(stack.popBoolean(), equalTo(false));
    }

    @Test
    void popNilAsString() {
        stack.pushNil();
        final LuaException exception = assertThrows(LuaException.class, stack::popString);
        assertThat(exception.getMessage(), equalTo("Expected string at index -1 but was NIL"));
    }

    @Test
    void pushPopNumber() {
        stack.pushNumber(3.14);
        assertThat(stack.popNumber(), equalTo(3.14));
    }

    @Test
    void pushPopNumberButIsInteger() {
        stack.pushInteger(42);
        assertThat(stack.popNumber(), equalTo(42.0));
    }

    @Test
    void popNumberWrongType() {
        stack.pushString("NaN");
        final LuaException exception = assertThrows(LuaException.class, stack::popNumber);
        assertThat(exception.getMessage(), equalTo("No number at index -1 but is STRING"));
    }

    @Test
    void pushInteger() {
        stack.pushInteger(42);
        assertThat(stack.popInteger(), equalTo(42L));
    }

    @Test
    void popIntegerWrongType() {
        stack.pushString("NaN");
        final LuaException exception = assertThrows(LuaException.class, stack::popInteger);
        assertThat(exception.getMessage(), equalTo("No integer at index -1 but is STRING"));
    }

    @Test
    void popIntegerButIsNumber() {
        stack.pushNumber(3.14);
        final LuaException exception = assertThrows(LuaException.class, stack::popInteger);
        assertThat(exception.getMessage(), equalTo("No integer at index -1 but is NUMBER"));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void pushBoolean(final boolean value) {
        stack.pushBoolean(value);
        assertThat(stack.popBoolean(), equalTo(value));
    }

    @Test
    void getTop() {
        assertThat(stack.getTop(), equalTo(0));
        stack.pushBoolean(false);
        assertThat(stack.getTop(), equalTo(1));
        stack.pushBoolean(true);
        assertThat(stack.getTop(), equalTo(2));
        stack.pop();
        assertThat(stack.getTop(), equalTo(1));
    }

    static Arguments nilMapping(final Class<?> type) {
        return mapping("nil", type, null);
    }

    static Arguments stringMapping(final String value) {
        return mapping("'" + value + "'", String.class, value);
    }

    static Arguments longMapping(final long value) {
        return mapping(String.valueOf(value), Long.class, value);
    }

    static Arguments doubleMapping(final double value) {
        return mapping(String.valueOf(value), Double.class, value);
    }

    static Arguments booleanMapping(final boolean value) {
        return mapping(String.valueOf(value), Boolean.class, value);
    }

    static Arguments mapping(final String luaValue, final Class<?> type, final Object expected) {
        return Arguments.of(luaValue, type, expected);
    }

    static Stream<Arguments> toObjectArgs() {
        return Stream.of(nilMapping(String.class), nilMapping(Long.class), nilMapping(long.class),
                nilMapping(Double.class), nilMapping(double.class), nilMapping(boolean.class),
                nilMapping(Boolean.class), stringMapping(""), stringMapping("value"),
                stringMapping("value with space"), longMapping(Long.MAX_VALUE), longMapping(Long.MIN_VALUE),
                longMapping(0), doubleMapping(Double.MIN_NORMAL), doubleMapping(Double.MAX_VALUE),
                doubleMapping(Double.MIN_VALUE), doubleMapping(0.0), booleanMapping(true), booleanMapping(true));
    }

    @ParameterizedTest
    @MethodSource("toObjectArgs")
    void toObject(final String luaValue, final Class<?> type, final Object expected) {
        lua.loadString("var = " + luaValue);
        lua.pcall(0, 0);
        lua.getGlobal("var");
        assertThat(stack.toObject(type), equalTo(expected));
    }
}
