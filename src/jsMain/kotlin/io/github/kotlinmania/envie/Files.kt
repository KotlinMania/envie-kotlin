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
// bundle, where `fs` is unresolvable. Resolving `require` through `eval('require')` keeps the
// string opaque to webpack so the call is only attempted at runtime in environments that have a
// real `require` (Node).
private fun jsReadFile(path: String): dynamic = js(
    "(function(){ try { var r = eval('typeof require !== \"undefined\" ? require : null'); if (!r) return undefined; return r('fs').readFileSync(path, 'utf-8'); } catch (e) { return undefined; } })()",
)

private fun jsWriteFile(path: String, content: String): Boolean = js(
    "(function(){ try { var r = eval('typeof require !== \"undefined\" ? require : null'); if (!r) return false; r('fs').writeFileSync(path, content, 'utf-8'); return true; } catch (e) { return false; } })()",
)

private fun undefined(): dynamic = js("undefined")
