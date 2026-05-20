// port-lint: source src/lib.rs
package io.github.kotlinmania.envie

/** Load a `.env` file from a specified path and parse it into a new Envie instance. */
public fun Envie.Companion.loadWithPath(path: String): Result<Envie> {
    val content = readFileToString(path).getOrElse {
        return Result.failure(
            RuntimeException("Failed to read .env file from '$path'. Make sure it exists."),
        )
    }
    val variables = parse(content).toMutableMap()
    return Result.success(Envie(variables))
}
