package org.itsallcode.jlua;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class LuaInterpreterTest {
    private LuaInterpreter lua;

    @BeforeEach
    void setup() {
        lua = LuaInterpreter.create();
    }

    @AfterEach
    void stop() {
        lua.close();
    }

    @Test
    void runHelloWorld() {
        assertDoesNotThrow(() -> lua.exec("print('hello world')"));
    }

    @Test
    void compileFails() {
        assertFails(() -> lua.exec("invalid"),
                "[string \"invalid\"]:1: syntax error near <eof>");
    }

    @Test
    void executionFails() {
        assertFails(() -> lua.exec("invalid('hello world')"),
                "[string \"invalid('hello world')\"]:1: attempt to call a nil value (global 'invalid')");
    }

    void assertFails(final Executable executable, final String expectedErrorMessage) {
        final FunctionCallException exception = assertThrows(FunctionCallException.class, executable);
        assertThat(exception.getRootError(), equalTo(expectedErrorMessage));
    }
}
