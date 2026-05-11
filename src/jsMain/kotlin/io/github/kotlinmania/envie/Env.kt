// port-lint: ignore (Node / browser JS implementations of getenv/setenv/unsetenv/environ)
package io.github.kotlinmania.envie

// Browser-hosted JS has no process.env; Node does. The overlay lets setenv/unsetenv work
// uniformly (write-through to process.env when present, in-memory only otherwise) so that
// getenv reflects the change in both cases.
private val overlay = HashMap<String, String?>()

public actual fun getenv(name: String): String? {
    if (overlay.containsKey(name)) return overlay[name]
    val raw: dynamic = jsGetEnv(name)
    return if (raw == null || raw == undefined()) null else raw.unsafeCast<String>()
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
    val names = jsEnvKeys()
    val length = names.length
    for (i in 0 until length) {
        val key = names[i].unsafeCast<String>()
        val value = jsGetEnv(key)
        if (value != null && value != undefined()) {
            merged[key] = value.unsafeCast<String>()
        }
    }
    for ((k, v) in overlay) {
        if (v == null) merged.remove(k) else merged[k] = v
    }
    return merged.entries.map { it.key to it.value }
}

private fun jsGetEnv(name: String): dynamic = js(
    "(typeof process !== 'undefined' && process && process.env) ? process.env[name] : undefined",
)

private fun jsSetEnv(name: String, value: String): Unit = js(
    "if (typeof process !== 'undefined' && process && process.env) { process.env[name] = value; }",
)

private fun jsDeleteEnv(name: String): Unit = js(
    "if (typeof process !== 'undefined' && process && process.env) { delete process.env[name]; }",
)

private fun jsEnvKeys(): dynamic = js(
    "(typeof process !== 'undefined' && process && process.env) ? Object.keys(process.env) : []",
)

private fun undefined(): dynamic = js("undefined")
