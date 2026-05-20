// port-lint: source src/lib.rs
package io.github.kotlinmania.envie

/** Get a value as an integer. */
public fun Envie.getInt(key: String): Result<Int> {
    val raw = get(key) ?: return Result.failure(RuntimeException("Key '$key' not found"))
    return raw.toIntOrNull()
        ?.let { Result.success(it) }
        ?: Result.failure(RuntimeException("Invalid integer value for key '$key'"))
}
