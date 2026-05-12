// port-lint: ignore (Wasm-JS / Node + browser implementations of getenv/setenv/unsetenv/environ)
@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.envie

// Browser-hosted WasmJS has no process.env; Node does. The overlay lets setenv / unsetenv work
// uniformly (write-through to process.env when present, in-memory only otherwise) so that
// getenv reflects the change in both cases.
private val overlay = HashMap<String, String?>()

public actual fun getenv(name: String): String? {
    if (overlay.containsKey(name)) return overlay[name]
    return jsGetEnv(name)
}

public actual fun setenv(name: String, value: String) {
    overlay[name] = value
    jsSetEnv(name, value)
}

public actual fun unsetenv(name: String) {
    overlay[name] = null
    jsDeleteEnv(name)
}

public actual fun environ(): List<Pair<String, String>> {
    val merged = LinkedHashMap<String, String>()
    val n = jsEnvCount()
    for (i in 0 until n) {
        val key = jsEnvKeyAt(i) ?: continue
        val value = jsGetEnv(key) ?: continue
        merged[key] = value
    }
    for ((k, v) in overlay) {
        if (v == null) merged.remove(k) else merged[k] = v
    }
    return merged.entries.map { it.key to it.value }
}

private fun jsGetEnv(name: String): String? = js(
    "(typeof process !== 'undefined' && process && process.env && typeof process.env[name] === 'string') ? process.env[name] : null",
)

private fun jsSetEnv(name: String, value: String) {
    js("if (typeof process !== 'undefined' && process && process.env) { process.env[name] = value; }")
}

private fun jsDeleteEnv(name: String) {
    js("if (typeof process !== 'undefined' && process && process.env) { delete process.env[name]; }")
}

private fun jsEnvCount(): Int = js(
    "(typeof process !== 'undefined' && process && process.env) ? Object.keys(process.env).length : 0",
)

private fun jsEnvKeyAt(index: Int): String? = js(
    "(typeof process !== 'undefined' && process && process.env) ? Object.keys(process.env)[index] : null",
)
