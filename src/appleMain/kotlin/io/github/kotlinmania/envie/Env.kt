// port-lint: ignore (Apple POSIX implementations of getenv/setenv/unsetenv; environ is per-leaf)
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
