package org.itsallcode.luava;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LuaInterpreterTest {
    private LuaInterpreter lua;

    @BeforeEach
    void setup() {
        lua = LuaInterpreter.create();
        assertStackSize(0);
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

    @ParameterizedTest
    @ValueSource(strings = { "", "\0", "some text", "öäüß", "zero\0byte" })
    void setGetGlobalString(final String value) {
        lua.setGlobalString("input", value);
        lua.exec("result = '/' .. input .. '/'");
        final String globalString = lua.getGlobalString("result");
        assertThat(globalString, equalTo("/" + value + "/"));
    }

    @ParameterizedTest
    @ValueSource(longs = { Long.MIN_VALUE, Long.MAX_VALUE, -1, 0, -1 })
    void setGetGlobalInteger(final long value) {
        lua.setGlobalInteger("input", value);
        lua.exec("result = input");
        final long globalInteger = lua.getGlobalInteger("result");
        assertThat(globalInteger, equalTo(value));
    }

    @ParameterizedTest
    @ValueSource(doubles = { Double.MIN_NORMAL, Double.MIN_VALUE, Double.MAX_VALUE, -1, 0, 1, Math.PI })
    void setGetGlobalNumber(final double value) {
        lua.setGlobalNumber("input", value);
        lua.exec("result = input");
        final double globalNumber = lua.getGlobalNumber("result");
        assertThat(globalNumber, equalTo(value));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void setGetGlobalBoolean(final boolean value) {
        lua.setGlobalBoolean("input", value);
        lua.exec("result = input");
        final boolean globalBoolean = lua.getGlobalBoolean("result");
        assertThat(globalBoolean, equalTo(value));
    }

    @Test
    void getCallGlobalFunction() {
        lua.exec("function increment(x) return x+1 end");
        assertStackSize(0);
        final List<Object> result = lua.getGlobalFunction("increment").argumentValues(42).resultTypes(Long.class)
                .call();
        assertThat(result.get(0), equalTo(43L));
    }

    @Test
    void getCallGlobalFunctionFails() {
        lua.exec("function increment(x) error('failure') end");
        assertStackSize(0);
        final LuaFunction function = lua.getGlobalFunction("increment").argumentValues(42).resultTypes(Long.class);
        final LuaException exception = assertThrows(LuaException.class, function::call);
        assertStackSize(0);
        assertThat(exception.getMessage(), equalTo(
                "Function 'lua_pcallk' failed with error 2: [string \"function increment(x) error('failure') end\"]:1: failure"));
    }

    void assertStackSize(final int expectedSize) {
        assertThat("stack size", lua.stack().getTop(), equalTo(expectedSize));
    }

    @Test
    void getTableStringValue() {
        lua.exec("result = { key = 'value' }");
        final LuaTable table = lua.getGlobalTable("result");
        assertThat(table.getString("key"), equalTo("value"));
    }

    @Test
    void getTableIntegerValue() {
        lua.exec("result = { key = 42 }");
        final LuaTable table = lua.getGlobalTable("result");
        assertThat(table.getInteger("key"), equalTo(42L));
    }

    @Test
    void getTableIntegerValueButIsDouble() {
        lua.exec("result = { key = 42.2 }");
        final LuaTable table = lua.getGlobalTable("result");
        final LuaException exception = assertThrows(LuaException.class, () -> table.getInteger("key"));
        assertThat(exception.getMessage(), equalTo("No integer at index -1 but is NUMBER"));
    }

    @Test
    void getTableNumberValue() {
        lua.exec("result = { key = 3.14 }");
        final LuaTable table = lua.getGlobalTable("result");
        assertThat(table.getNumber("key"), equalTo(3.14));
    }

    @Test
    void getTableBooleanValue() {
        lua.exec("result = { key = true }");
        final LuaTable table = lua.getGlobalTable("result");
        assertThat(table.getBoolean("key"), equalTo(true));
    }

    @Test
    void getTableValueWrongType() {
        lua.exec("result = { key = 42 }");
        final LuaTable table = lua.getGlobalTable("result");
        final LuaException exception = assertThrows(LuaException.class, () -> table.getString("key"));
        assertThat(exception.getMessage(), equalTo("Expected field 'key' of type STRING but was NUMBER"));
    }

    @Test
    void getTableStringFailsWrongType() {
        lua.exec("result = 'not a table'");
        final LuaException exception = assertThrows(LuaException.class, () -> lua.getGlobalTable("result"));
        assertThat(exception.getMessage(), equalTo("Expected TABLE at -1 but got STRING"));
    }

    void assertFails(final Executable executable, final String expectedErrorMessage) {
        final FunctionCallException exception = assertThrows(FunctionCallException.class, executable);
        assertThat(exception.getRootError(), equalTo(expectedErrorMessage));
    }
}
