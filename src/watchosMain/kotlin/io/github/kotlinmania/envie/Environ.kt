// port-lint: ignore (watchOS environ() - Apple does not expose stable process-env enumeration)
package io.github.kotlinmania.envie

public actual fun environ(): List<Pair<String, String>> = emptyList()
