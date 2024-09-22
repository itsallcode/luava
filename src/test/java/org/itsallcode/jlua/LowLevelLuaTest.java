package org.itsallcode.luava;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

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


}
