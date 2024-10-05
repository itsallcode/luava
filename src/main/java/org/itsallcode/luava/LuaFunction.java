package org.itsallcode.luava;

import static java.util.Collections.emptyList;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class LuaFunction {
    private static final Logger LOG = Logger.getLogger(LuaFunction.class.getName());
    private static final int DEFAULT_MESSAGE_HANDLER = 0;
    private final LowLevelLua lua;
    private final String name;
    private List<Object> argumentValues = emptyList();
    private List<Class<?>> resultTypes = emptyList();

    LuaFunction(final LowLevelLua lowLevelLua, final String name) {
        this.lua = lowLevelLua;
        this.name = name;
    }

    @SuppressWarnings("java:S923") // Using varargs by intention
    public LuaFunction argumentValues(final Object... argumentValues) {
        this.argumentValues = Arrays.asList(argumentValues);
        return this;
    }

    @SuppressWarnings("java:S923") // Using varargs by intention
    public LuaFunction resultTypes(final Class<?>... resultTypes) {
        this.resultTypes = Arrays.asList(resultTypes);
        return this;
    }

    public List<Object> call() {
        lua.getGlobal(name);
        pushArguments();
        lua.pcall(this.argumentValues.size(), this.resultTypes.size(), DEFAULT_MESSAGE_HANDLER);
        return getResultValues();
    }

    private void pushArguments() {
        this.argumentValues.forEach(v -> lua.stack().pushObject(v));
    }

    private List<Object> getResultValues() {
        return this.resultTypes.stream().map(t -> lua.stack().popObject(t)).toList();
    }
}
