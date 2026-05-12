// port-lint: ignore (iOS environ() — Apple does not expose `environ` on iOS, returns empty list)
package io.github.kotlinmania.envie

// Apple intentionally does not export the POSIX `environ` symbol on iOS; reading the process
// environment beyond `getenv` for known names is not supported. Callers that need the full list
// (e.g. tests clearing every env var) must use the overlay-aware [setenv] / [unsetenv] for the
// specific names they care about; what they read here is just whatever the host has pre-baked.
public actual fun environ(): List<Pair<String, String>> = emptyList()
