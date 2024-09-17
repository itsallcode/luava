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

    @Test
    void getGlobal() {
        lua.exec("var = 'hello'");
        assertThat(lua.getGlobalString("var"), equalTo("hello"));
    }

    @Test
    void getGlobalMultipleTimes() {
        lua.exec("var1 = 'hello1'; var2 = 'hello2'");
        assertThat(lua.getGlobalString("var1"), equalTo("hello1"));
        assertThat(lua.getGlobalString("var2"), equalTo("hello2"));
    }

    @Test
    void setGetGlobalString() {
        lua.setString("input", "value");
        lua.exec("result = '/' .. input .. '/'");
        final String globalString = lua.getGlobalString("result");
        assertThat(globalString, equalTo("/value/"));
    }

    void assertFails(final Executable executable, final String expectedErrorMessage) {
        final FunctionCallException exception = assertThrows(FunctionCallException.class, executable);
        assertThat(exception.getRootError(), equalTo(expectedErrorMessage));
    }
}
