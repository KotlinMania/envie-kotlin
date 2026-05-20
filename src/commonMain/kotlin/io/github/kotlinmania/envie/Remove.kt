// port-lint: source src/lib.rs
package io.github.kotlinmania.envie

/** Remove a key-value pair and update the `.env` file. */
public fun Envie.remove(key: String): Result<Unit> {
    variables.remove(key)
    return saveToEnvFile()
}
