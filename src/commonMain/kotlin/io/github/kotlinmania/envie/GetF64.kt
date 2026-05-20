// port-lint: source src/lib.rs
package io.github.kotlinmania.envie

/** Get a value as a double (f64). */
public fun Envie.getF64(key: String): Result<Double> {
    val raw = get(key) ?: return Result.failure(RuntimeException("Key '$key' not found"))
    return raw.toDoubleOrNull()
        ?.let { Result.success(it) }
        ?: Result.failure(RuntimeException("Invalid float value for key '$key'"))
}
