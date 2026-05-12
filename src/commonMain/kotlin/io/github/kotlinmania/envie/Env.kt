// port-lint: ignore (low-level process-environment primitives — common expect declarations)
package io.github.kotlinmania.envie

/**
 * Returns the value of the named process environment variable, or `null` if the variable is not
 * set. Mirrors POSIX `getenv(3)`, `System.getenv(name)` on the JVM, and `process.env[name]` in
 * Node-hosted JS / WasmJS.
 *
 * In browser-hosted JS / WasmJS the host has no environment and this always returns `null`.
 */
public expect fun getenv(name: String): String?

/**
 * Sets the named process environment variable to [value], overwriting any prior binding. Mirrors
 * POSIX `setenv(name, value, 1)` and `_putenv_s(name, value)` on Windows. On Android the JVM
 * does not expose a portable way to mutate its own environment block, so the implementation
 * maintains an in-process overlay observed by [getenv] from this library only — other JVM code
 * reading `System.getenv(name)` will not see the change. In browser JS / WasmJS the same
 * overlay model applies because the host has no real environment block.
 */
public expect fun setenv(name: String, value: String)

/**
 * Removes the named process environment variable. Mirrors POSIX `unsetenv(name)` and
 * `_putenv_s(name, "")` on Windows. On Android the same overlay limitation as [setenv] applies.
 */
public expect fun unsetenv(name: String)

/**
 * Returns a snapshot of every environment-variable binding currently visible to this process,
 * as a list of (key, value) pairs in the platform's reported order. Mirrors POSIX `environ`
 * iteration, `System.getenv().entries` on the JVM, and `Object.entries(process.env)` in
 * Node-hosted JS / WasmJS. Returns an empty list when the host has no environment (browser).
 */
public expect fun environ(): List<Pair<String, String>>
