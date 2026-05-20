// port-lint: source src/lib.rs
package io.github.kotlinmania.envie

/**
 * Envie is a lightweight and user-friendly library for managing environment variables. It helps
 * you load and parse `.env` files, retrieve variables with ease, and provides type-safe access to
 * boolean and other data types.
 */
public class Envie(
    public val variables: MutableMap<String, String>,
) {
    public companion object
}
