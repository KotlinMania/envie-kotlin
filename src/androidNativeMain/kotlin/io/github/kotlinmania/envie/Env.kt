// port-lint: ignore (Android Native POSIX getenv/setenv/unsetenv; environ is not enumerated)
@file:OptIn(ExperimentalForeignApi::class)

package io.github.kotlinmania.envie

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
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

public actual fun environ(): List<Pair<String, String>> = emptyList()
