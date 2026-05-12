// port-lint: ignore (Android / JVM implementations of getenv/setenv/unsetenv/environ)
package io.github.kotlinmania.envie

// JVM has no portable way to mutate its own process-environment block. To still honour the
// setenv / unsetenv contract for in-process consumers (tests, env-dependent libraries running on
// the same JVM), this implementation maintains an overlay map that getenv consults first.
// External JVM code reading System.getenv(name) directly will NOT see the overlay.
private val overlay = HashMap<String, String?>()

public actual fun getenv(name: String): String? {
    val pair = overlayLookup(name)
    return if (pair.present) pair.value else System.getenv(name)
}

public actual fun setenv(name: String, value: String) {
    synchronized(overlay) {
        overlay[name] = value
    }
}

public actual fun unsetenv(name: String) {
    synchronized(overlay) {
        overlay[name] = null
    }
}

public actual fun environ(): List<Pair<String, String>> {
    val base = System.getenv()
    val merged = LinkedHashMap<String, String>(base.size + 8)
    for ((k, v) in base.entries) {
        merged[k] = v
    }
    synchronized(overlay) {
        for ((k, v) in overlay) {
            if (v == null) merged.remove(k) else merged[k] = v
        }
    }
    return merged.entries.map { it.key to it.value }
}

private class OverlayHit(val present: Boolean, val value: String?)

private fun overlayLookup(name: String): OverlayHit = synchronized(overlay) {
    if (overlay.containsKey(name)) OverlayHit(true, overlay[name]) else OverlayHit(false, null)
}
