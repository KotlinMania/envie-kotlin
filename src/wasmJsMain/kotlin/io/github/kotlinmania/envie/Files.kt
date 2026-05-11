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

// Same eval('require')-trick as the jsMain implementation: webpack's static analyzer would
// otherwise see the literal `require('fs')` and try to resolve `fs` for the browser bundle,
// which fails. Going through eval keeps the lookup opaque so it's only attempted at runtime in
// environments that actually expose `require` (Node).
private fun jsReadFile(path: String): String? = js(
    "{ try { var r = eval('typeof require !== \"undefined\" ? require : null'); if (!r) return null; return r('fs').readFileSync(path, 'utf-8'); } catch (e) { return null; } }",
)

private fun jsWriteFile(path: String, content: String): Boolean = js(
    "{ try { var r = eval('typeof require !== \"undefined\" ? require : null'); if (!r) return false; r('fs').writeFileSync(path, content, 'utf-8'); return true; } catch (e) { return false; } }",
)
