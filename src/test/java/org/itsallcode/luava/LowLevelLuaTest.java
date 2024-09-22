package org.itsallcode.luava;

import org.junit.jupiter.api.*;

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

    @Test
    void functionWithErrorLeavesStackEmpty() {

    }
}
