// port-lint: source src/lib.rs
package io.github.kotlinmania.envie

/** Get all environment variables as a Map. */
public fun Envie.getAll(): Map<String, String> {
    return variables.toMap()
}
