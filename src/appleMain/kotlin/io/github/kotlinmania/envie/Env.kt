package io.github.kotlinmania.envie

import io.github.kotlinmania.libc.unix.getenv as libcGetenv
import io.github.kotlinmania.libc.unix.setenv as libcSetenv
import io.github.kotlinmania.libc.unix.unsetenv as libcUnsetenv

public actual fun getenv(name: String): String? = libcGetenv(name)

public actual fun setenv(name: String, value: String) {
    libcSetenv(name, value, 1)
}

public actual fun unsetenv(name: String) {
    libcUnsetenv(name)
}
