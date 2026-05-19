// port-lint: ignore (Wasm-WASI file I/O is not implemented by this port)
package io.github.kotlinmania.envie

internal actual fun readFileToString(path: String): Result<String> =
    Result.failure(RuntimeException("readFileToString is not supported on wasmWasi"))

internal actual fun writeStringToFile(path: String, content: String): Result<Unit> =
    Result.failure(RuntimeException("writeStringToFile is not supported on wasmWasi"))
