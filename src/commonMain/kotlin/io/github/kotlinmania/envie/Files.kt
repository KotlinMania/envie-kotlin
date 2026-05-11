// port-lint: ignore (file-I/O primitives needed by Envie.loadWithPath / saveToEnvFile)
package io.github.kotlinmania.envie

/**
 * Reads the entire contents of the regular file at [path] as a UTF-8 string. Returns
 * `Result.failure` when the file is missing, unreadable, or the host has no filesystem
 * (browser JS / WasmJS).
 */
internal expect fun readFileToString(path: String): Result<String>

/**
 * Writes [content] to the file at [path] as UTF-8, replacing any existing contents. Returns
 * `Result.failure` when the path is not writable or the host has no filesystem (browser).
 */
internal expect fun writeStringToFile(path: String, content: String): Result<Unit>
