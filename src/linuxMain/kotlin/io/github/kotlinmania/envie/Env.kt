// port-lint: ignore (Linux POSIX implementations of getenv/setenv/unsetenv/environ)
@file:OptIn(ExperimentalForeignApi::class)

package io.github.kotlinmania.envie

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.cinterop.toKString
import platform.posix.__environ
import platform.posix.getenv
import platform.posix.setenv
import platform.posix.unsetenv

public actual fun getenv(name: String): String? = getenv(name)?.toKString()

public actual fun setenv(name: String, value: String) {
    setenv(name, value, 1)
}

public actual fun unsetenv(name: String) {
    unsetenv(name)
}

public actual fun environ(): List<Pair<String, String>> {
    val envPtr = __environ ?: return emptyList()
    val result = mutableListOf<Pair<String, String>>()
    var i = 0
    while (true) {
        val entry = envPtr[i] ?: break
        val s = entry.toKString()
        val eq = s.indexOf('=')
        if (eq > 0) result.add(s.substring(0, eq) to s.substring(eq + 1))
        i++
    }
    return result
}
