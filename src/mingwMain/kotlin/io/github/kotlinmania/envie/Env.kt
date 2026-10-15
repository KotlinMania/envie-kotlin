package io.github.kotlinmania.envie

import io.github.kotlinmania.libc.windows.getenv as libcGetenv
import io.github.kotlinmania.libc.windows.putenvS as libcPutenvS

public actual fun getenv(name: String): String? = libcGetenv(name)

public actual fun setenv(name: String, value: String) {
    libcPutenvS(name, value)
}

public actual fun unsetenv(name: String) {
    libcPutenvS(name, "")
}

public actual fun environ(): List<Pair<String, String>> = emptyList()
