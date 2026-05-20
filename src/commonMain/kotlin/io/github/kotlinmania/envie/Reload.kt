// port-lint: source src/lib.rs
package io.github.kotlinmania.envie

/** Reload the `.env` file from the current directory. */
public fun Envie.reload(): Result<Unit> {
    val content = readFileToString(".env")
        .getOrElse {
            return Result.failure(
                RuntimeException("Failed to read .env file. Make sure it exists in the current directory."),
            )
        }
    variables.clear()
    variables.putAll(Envie.parse(content))
    return Result.success(Unit)
}
