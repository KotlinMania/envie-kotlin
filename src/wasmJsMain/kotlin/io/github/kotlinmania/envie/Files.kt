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

// Same `new Function(...)()` trick as the jsMain implementation: webpack's static analyzer
// can't see what's being constructed at runtime, so the literal `require('fs')` lookup is only
// attempted in environments that actually expose `require` (Node). The earlier eval form
// triggered webpack's eval-source-map devtool wrapping, which broke an embedded ternary at
// bundle time.
private fun jsReadFile(path: String): String? = js(
    "{ try { var rq = (new Function('return typeof require === \"function\" ? require : null'))(); if (!rq) return null; return rq('fs').readFileSync(path, 'utf-8'); } catch (e) { return null; } }",
)

private fun jsWriteFile(path: String, content: String): Boolean = js(
    "{ try { var rq = (new Function('return typeof require === \"function\" ? require : null'))(); if (!rq) return false; rq('fs').writeFileSync(path, content, 'utf-8'); return true; } catch (e) { return false; } }",
)
