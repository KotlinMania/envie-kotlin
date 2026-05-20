// port-lint: source src/lib.rs
package io.github.kotlinmania.envie

/** Set and apply the variable to the system environment. */
public fun Envie.setSystemEnv(key: String, value: String): Result<Unit> {
    set(key, value).getOrElse { return Result.failure(it) }
    setenv(key, value)
    return Result.success(Unit)
}
