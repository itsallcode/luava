package org.itsallcode.jlua;

import java.lang.foreign.Arena;

public class Test {
    public static void main(final String[] args) {
        try (var arena = Arena.ofConfined()) {
            final var format = arena.allocateUtf8String("Hello");
            final var value = arena.allocateUtf8String("World");

            // final var c = RuntimeHelper.class;
        }
    }
}
