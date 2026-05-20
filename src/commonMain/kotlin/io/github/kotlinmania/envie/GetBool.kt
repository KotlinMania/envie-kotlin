// port-lint: source src/lib.rs
package io.github.kotlinmania.envie

/** Get a value as a boolean. */
public fun Envie.getBool(key: String): Result<Boolean> {
    val raw = get(key)?.lowercase()
        ?: return Result.failure(RuntimeException("Key '$key' not found"))
    return when (raw) {
        "true", "1" -> Result.success(true)
        "false", "0" -> Result.success(false)
        else -> Result.failure(RuntimeException("Invalid boolean value for key '$key'"))
    }
}
