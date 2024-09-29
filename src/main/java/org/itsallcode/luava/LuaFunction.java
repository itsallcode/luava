package org.itsallcode.luava;

import static java.util.Collections.emptyList;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

import org.itsallcode.luava.ffi.Lua;
import org.itsallcode.luava.ffi.lua_CFunction;

public class LuaFunction {
    private static final Logger LOG = Logger.getLogger(LuaFunction.class.getName());
    private final LowLevelLua lua;
    private final Arena arena;
    private final String name;
    private lua_CFunction.Function messageHandler;
    private List<Object> argumentValues = emptyList();
    private List<Class<?>> resultTypes = emptyList();

    LuaFunction(final LowLevelLua lowLevelLua, final Arena arena, final String name) {
        this.lua = lowLevelLua;
        this.arena = arena;
        this.name = name;
    }

    public LuaFunction argumentValues(final Object... argumentValues) {
        this.argumentValues = Arrays.asList(argumentValues);
        return this;
    }

    public LuaFunction resultTypes(final Class<?>... resultTypes) {
        this.resultTypes = Arrays.asList(resultTypes);
        return this;
    }

    public LuaFunction messageUpdateHandler(final UnaryOperator<String> messageHandler) {
        return messageHandler((final MemorySegment l) -> {
            LOG.fine("Message handler: popping error message...");
            System.err.println(lua.stack().printStack());
            final String msg = lua.stack().popString();
            final String updatedMsg = messageHandler.apply(msg);
            LOG.fine(() -> "Pushing updated error message '" + updatedMsg + "'");
            lua.stack().pushString(updatedMsg);
            System.err.println(lua.stack().printStack());
            return Lua.LUA_OK();
        });
    }

    public LuaFunction messageHandler(final lua_CFunction.Function messageHandler) {
        this.messageHandler = messageHandler;
        return this;
    }

    public List<Object> call() {
        final int messageHandlerIdx = getMessageHandlerIdx();
        lua.getGlobal(name);
        pushArguments();
        System.out.println(lua.stack().printStack());
        try {
            lua.pcall(this.argumentValues.size(), this.resultTypes.size(), messageHandlerIdx);
            return getResultValues();
        } finally {
            if (this.messageHandler != null) {
                LOG.fine("Popping message handler");
                lua.stack().pop();
            }
        }
    }

    private void pushArguments() {
        this.argumentValues.forEach(v -> lua.stack().pushObject(v));
    }

    private List<Object> getResultValues() {
        return this.resultTypes.stream().map(t -> lua.stack().popObject(t)).toList();
    }

    private int getMessageHandlerIdx() {
        if (messageHandler == null) {
            return 0;
        }
        lua.stack().pushCFunction(messageHandler);
        return lua.stack().getTop();
    }
}
