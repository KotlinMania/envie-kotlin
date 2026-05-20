// port-lint: source src/lib.rs
package io.github.kotlinmania.envie

/** Load `.env` file from the current directory and parse it into a new Envie instance. */
public fun Envie.Companion.load(): Result<Envie> {
    return loadWithPath(".env")
}
