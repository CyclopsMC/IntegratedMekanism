package org.cyclops.integratedmekanism.test;

/**
 * TODO: use ID variant in 1.21.8+
 * @author rubensworks
 */
public class Asserts {

    public static void check(final boolean expression, final String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

}
