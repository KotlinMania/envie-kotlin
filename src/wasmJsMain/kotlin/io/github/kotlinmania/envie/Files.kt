// port-lint: ignore (Wasm-JS file I/O via Node fs; browser falls through to Result.failure)
@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.envie

internal actual fun readFileToString(path: String): Result<String> {
    val raw = jsReadFile(path)
    return if (raw == null) {
        Result.failure(RuntimeException("Failed to read '$path' (no Node fs or read failed)"))
    } else {
        Result.success(raw)
    }
}

internal actual fun writeStringToFile(path: String, content: String): Result<Unit> {
    val ok = jsWriteFile(path, content)
    return if (ok) {
        Result.success(Unit)
    } else {
        Result.failure(RuntimeException("Failed to write '$path' (no Node fs or write failed)"))
    }
}

private fun jsReadFile(path: String): String? = js(
    "{ try { if (typeof require !== 'function') return null; var fs = require('fs'); return fs.readFileSync(path, 'utf-8'); } catch (e) { return null; } }",
)

private fun jsWriteFile(path: String, content: String): Boolean = js(
    "{ try { if (typeof require !== 'function') return false; var fs = require('fs'); fs.writeFileSync(path, content, 'utf-8'); return true; } catch (e) { return false; } }",
)
