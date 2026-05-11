// port-lint: ignore (macOS environ() — POSIX `environ` is not directly exposed in a stable way
//                    by Kotlin/Native's `platform.osx._NSGetEnviron` binding (the returned pointer
//                    type's `.value` accessor differs across compiler versions). Consumers needing
//                    the full process env on macOS should use [setenv] / [unsetenv] for the
//                    specific names they care about — those mutate the real process env block, so
//                    later [getenv] calls observe them correctly.)
package io.github.kotlinmania.envie

public actual fun environ(): List<Pair<String, String>> = emptyList()
