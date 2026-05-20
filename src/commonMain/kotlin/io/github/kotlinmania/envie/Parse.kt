// port-lint: source src/lib.rs
package io.github.kotlinmania.envie

/** Parse the content of a `.env` file into a Map. */
internal fun Envie.Companion.parse(content: String): Map<String, String> {
    val result = LinkedHashMap<String, String>()
    for (rawLine in content.lineSequence()) {
        val line = rawLine.trim()
        if (line.isEmpty() || line.startsWith('#')) continue
        val eq = line.indexOf('=')
        val (key, value) = if (eq >= 0) {
            line.substring(0, eq).trim() to line.substring(eq + 1).trim()
        } else {
            line to ""
        }
        result[key] = value
    }
    return result
}
