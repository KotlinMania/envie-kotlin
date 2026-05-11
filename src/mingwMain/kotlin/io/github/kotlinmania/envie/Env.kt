// port-lint: ignore (Windows MinGW implementations of getenv/setenv/unsetenv/environ via MSVCRT)
@file:OptIn(ExperimentalForeignApi::class)

package io.github.kotlinmania.envie

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.cinterop.toKString
import platform.posix._environ
import platform.posix._putenv_s
import platform.posix.getenv

public actual fun getenv(name: String): String? = getenv(name)?.toKString()

public actual fun setenv(name: String, value: String) {
    _putenv_s(name, value)
}

public actual fun unsetenv(name: String) {
    // MSVCRT documents that _putenv_s with an empty value removes the binding (and keeps
    // CRT-side getenv and Win32 GetEnvironmentVariable in sync).
    _putenv_s(name, "")
}

public actual fun environ(): List<Pair<String, String>> {
    val envPtr = _environ ?: return emptyList()
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
