// port-lint: source src/lib.rs
package io.github.kotlinmania.envie

/** Get a value by key. */
public fun Envie.get(key: String): String? {
    return variables[key] ?: getenv(key)
}
