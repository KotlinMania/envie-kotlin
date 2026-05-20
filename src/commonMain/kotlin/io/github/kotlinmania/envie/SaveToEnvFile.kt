// port-lint: source src/lib.rs
package io.github.kotlinmania.envie

/** Save the current state of the environment variables to the `.env` file. */
internal fun Envie.saveToEnvFile(): Result<Unit> {
    val content = buildString {
        for ((key, value) in variables) {
            append(key).append('=').append(value).append('\n')
        }
    }
    return writeStringToFile(".env", content)
        .recoverCatching { throw RuntimeException("Failed to write to .env file") }
}
