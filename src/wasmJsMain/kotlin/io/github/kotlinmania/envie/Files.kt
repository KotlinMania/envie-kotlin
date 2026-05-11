// port-lint: ignore (Wasm-JS file I/O — see prose below)
@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.envie

// Wasm-JS file I/O is intentionally not implemented. The runtime targets are:
//   - wasmJsNodeTest: would have Node's `fs` via `require`, but the wasmJs
//     `js(...)` intrinsic compiles its body into the JS module that webpack
//     bundles for the browser test target too; any `require('fs')` literal
//     trips webpack's static analyzer in the browser bundle even when
//     wrapped via `new Function(...)`. The dirs-kotlin baseline that landed
//     green in CI sidesteps this by having wasmJsMain return null/failure
//     for filesystem ops — Wasm-in-browser has no filesystem anyway.
//   - wasmJsBrowserTest: legitimately has no filesystem.
// Callers that need real `.env` file loading should use jsMain (Node JS,
// via the `(new Function('return require'))()` pattern), the native targets
// (POSIX fopen/fread/fwrite), or androidMain (kotlin.io.path).
// See workspace CLAUDE.md "Hiding require('fs') from webpack" for the
// full failure-mode chain that motivated this choice.

internal actual fun readFileToString(path: String): Result<String> =
    Result.failure(RuntimeException("readFileToString is not supported on wasmJs (no filesystem)"))

internal actual fun writeStringToFile(path: String, content: String): Result<Unit> =
    Result.failure(RuntimeException("writeStringToFile is not supported on wasmJs (no filesystem)"))
