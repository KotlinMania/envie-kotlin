// port-lint: source src/lib.rs
package io.github.kotlinmania.envie

/** Check if a key exists in the environment variables. */
public fun Envie.containsKey(key: String): Boolean {
    return variables.containsKey(key) || getenv(key) != null
}
