// port-lint: ignore (Node JS file I/O via fs.readFileSync / fs.writeFileSync; browser falls
//                    through to Result.failure because there is no filesystem)
package io.github.kotlinmania.envie

internal actual fun readFileToString(path: String): Result<String> {
    val raw: dynamic = jsReadFile(path)
    if (raw == null || raw == undefined()) {
        return Result.failure(RuntimeException("Failed to read '$path' (no Node fs or read failed)"))
    }
    return Result.success(raw.unsafeCast<String>())
}

internal actual fun writeStringToFile(path: String, content: String): Result<Unit> {
    val ok: Boolean = jsWriteFile(path, content)
    return if (ok) {
        Result.success(Unit)
    } else {
        Result.failure(RuntimeException("Failed to write '$path' (no Node fs or write failed)"))
    }
}

// The `require('fs')` literal would otherwise be parsed by webpack and pulled into the browser
// bundle, where `fs` is unresolvable. The earlier eval('require')-trick form generated valid JS
// but webpack's eval-source-map devtool wraps eval() calls and the wrapping broke an embedded
// ternary at bundle time. `new Function(...)()` is even more opaque: webpack's static analyzer
// can't see what's being constructed, so the lookup is only attempted at runtime in environments
// that actually expose `require` (Node).
private fun jsReadFile(path: String): dynamic = js(
    "(function(){ try { var rq = (new Function('return typeof require === \"function\" ? require : null'))(); if (!rq) return undefined; return rq('fs').readFileSync(path, 'utf-8'); } catch (e) { return undefined; } })()",
)

private fun jsWriteFile(path: String, content: String): Boolean = js(
    "(function(){ try { var rq = (new Function('return typeof require === \"function\" ? require : null'))(); if (!rq) return false; rq('fs').writeFileSync(path, content, 'utf-8'); return true; } catch (e) { return false; } })()",
)

private fun undefined(): dynamic = js("undefined")
