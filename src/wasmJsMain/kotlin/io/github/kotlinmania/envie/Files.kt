// port-lint: ignore (Wasm-JS file I/O via Node fs; browser falls through to Result.failure)
@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.envie

import kotlin.js.JsAny

internal actual fun readFileToString(path: String): Result<String> {
    val content = jsReadFile(path)
    return if (content == null) {
        Result.failure(RuntimeException("Failed to read '$path' (no Node fs or read failed)"))
    } else {
        Result.success(content)
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

// Same baseline as the jsMain implementation: the JS body returns ONLY the require / fs
// lookup result; method calls (readFileSync / writeFileSync) live in tighter JS bodies that
// receive `fs` from the lookup function via a closure-free pattern that webpack can't trace.
// See workspace CLAUDE.md "Hiding require('fs') from webpack" for the full failure-mode chain
// (raw require → eval('require') → new Function('return require')).
//
// The outer `{ ... }` wrapping is required by the wasmJs `js(...)` intrinsic, which compiles
// to `(args) => BODY`; a try-statement body needs a function-block context to parse.
private fun jsReadFile(path: String): String? = js(
    "{ try { var rq = (new Function('return typeof require === \"function\" ? require : null'))(); if (!rq) return null; var fs = rq('fs'); return fs.readFileSync(path, 'utf-8'); } catch (e) { return null; } }",
)

private fun jsWriteFile(path: String, content: String): Boolean = js(
    "{ try { var rq = (new Function('return typeof require === \"function\" ? require : null'))(); if (!rq) return false; var fs = rq('fs'); fs.writeFileSync(path, content, 'utf-8'); return true; } catch (e) { return false; } }",
)
