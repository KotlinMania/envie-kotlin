// port-lint: ignore (Node JS file I/O via fs.readFileSync / fs.writeFileSync; browser falls
//                    through to Result.failure because there is no filesystem)
package io.github.kotlinmania.envie

internal actual fun readFileToString(path: String): Result<String> {
    val fs = jsRequireFsOrNull()
        ?: return Result.failure(RuntimeException("Failed to read '$path' (no Node fs)"))
    return try {
        val content: dynamic = fs.readFileSync(path, "utf-8")
        Result.success(content.unsafeCast<String>())
    } catch (t: Throwable) {
        Result.failure(RuntimeException("Failed to read '$path': ${t.message}"))
    }
}

internal actual fun writeStringToFile(path: String, content: String): Result<Unit> {
    val fs = jsRequireFsOrNull()
        ?: return Result.failure(RuntimeException("Failed to write '$path' (no Node fs)"))
    return try {
        fs.writeFileSync(path, content, "utf-8")
        Result.success(Unit)
    } catch (t: Throwable) {
        Result.failure(RuntimeException("Failed to write '$path': ${t.message}"))
    }
}

// `require('fs')` as a literal here would be parsed by webpack's static analyzer and pulled
// into the browser bundle, where `fs` is unresolvable — jsBrowserTest fails with
// `Module not found: Error: Can't resolve 'fs'`. An `eval('require')` form trips webpack's
// eval-source-map devtool, which wraps eval() calls and mangles embedded ternaries at bundle
// time, producing a runtime `Uncaught SyntaxError: Unexpected token :` from the wrapped
// commons.js. The narrowest pattern that survives both gates (and matches the dirs-kotlin
// working baseline confirmed green in CI) is: have the js(...) body return just the fs
// module via `(new Function('return require'))()` and call methods on it from Kotlin code,
// so the JS string itself contains no method-call syntax webpack can choke on. See workspace
// CLAUDE.md "Hiding require('fs') from webpack".
private fun jsRequireFsOrNull(): dynamic = js(
    "(function(){ try { var rq = (new Function('return typeof require === \"function\" ? require : null'))(); return rq ? rq('fs') : null; } catch (e) { return null; } })()",
)
