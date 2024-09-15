package org.itsallcode.jlua;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LowLevelLuaTest {
    private LowLevelLua lua;

    @BeforeEach
    void setup() {
        lua = LowLevelLua.create();
    }

    @AfterEach
    void stop() {
        lua.close();
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "abc", "öäüß", "!§$%&", "String with \0 zero" })
    void pushString(final String value) {
        lua.pushLString(value);
        assertThat(lua.toString(-1), equalTo(value));
    }

    @Test
    void nilToString() {
        lua.pushNil();
        assertThat(lua.toString(-1), equalTo("xx"));
    }
}
