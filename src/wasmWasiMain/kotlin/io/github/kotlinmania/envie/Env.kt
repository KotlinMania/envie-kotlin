// port-lint: ignore (Wasm-WASI process-environment overlay)
package io.github.kotlinmania.envie

private val overlay = HashMap<String, String?>()

public actual fun getenv(name: String): String? = overlay[name]

public actual fun setenv(name: String, value: String) {
    overlay[name] = value
}

public actual fun unsetenv(name: String) {
    overlay[name] = null
}

public actual fun environ(): List<Pair<String, String>> =
    overlay.entries.mapNotNull { (key, value) -> value?.let { key to it } }
