// port-lint: source src/lib.rs
package io.github.kotlinmania.envie

/** Set a value for a given key and update the `.env` file. */
public fun Envie.set(key: String, value: String): Result<Unit> {
    variables[key] = value
    return saveToEnvFile()
}
