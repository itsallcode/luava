package org.itsallcode.jlua;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
        final String actual = stack.toString(-1);
        assertThat(actual, equalTo(value));
    }

    @Test
    void pushNil() {
        stack.pushNil();
        assertThat(stack.getType(-1), equalTo(LuaType.NIL));
    }

    @Test
    void pushNumber() {
        stack.pushNumber(3.14);
        assertThat(stack.toNumber(-1), equalTo(3.14));
    }

    @Test
    void pushInteger() {
        stack.pushInteger(42);
        assertThat(stack.toInteger(-1), equalTo(42L));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void pushBoolean(final boolean value) {
        stack.pushBoolean(value);
        assertThat(stack.toBoolean(-1), equalTo(value));
    }

    @Test
    void getTop() {
        assertThat(stack.getTop(), equalTo(0));
        stack.pushBoolean(false);
        assertThat(stack.getTop(), equalTo(1));
        stack.pushBoolean(true);
        assertThat(stack.getTop(), equalTo(2));
        stack.pop(1);
        assertThat(stack.getTop(), equalTo(1));
    }
}
