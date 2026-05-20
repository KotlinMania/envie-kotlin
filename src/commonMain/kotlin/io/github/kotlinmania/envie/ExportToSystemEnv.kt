// port-lint: source src/lib.rs
package io.github.kotlinmania.envie

/** Export all loaded variables to the system environment. */
public fun Envie.exportToSystemEnv(): Result<Unit> {
    for ((key, value) in variables) {
        setenv(key, value)
    }
    return Result.success(Unit)
}
