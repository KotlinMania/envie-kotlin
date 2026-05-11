// port-lint: source src/lib.rs
package io.github.kotlinmania.envie

/**
 * Envie is a lightweight and user-friendly library for managing environment variables. It helps
 * you load and parse `.env` files, retrieve variables with ease, and provides type-safe access to
 * boolean and other data types.
 */
public class Envie internal constructor(
    public val variables: MutableMap<String, String>,
) {
    /** Reload the `.env` file from the current directory. */
    public fun reload(): Result<Unit> {
        val content = readFileToString(".env")
            .getOrElse { return Result.failure(RuntimeException("Failed to read .env file. Make sure it exists in the current directory.")) }
        variables.clear()
        variables.putAll(parse(content))
        return Result.success(Unit)
    }

    /** Get a value by key. */
    public fun get(key: String): String? {
        return variables[key] ?: getenv(key)
    }

    /** Get a value as a boolean. */
    public fun getBool(key: String): Result<Boolean> {
        val raw = get(key)?.lowercase()
            ?: return Result.failure(RuntimeException("Key '$key' not found"))
        return when (raw) {
            "true", "1" -> Result.success(true)
            "false", "0" -> Result.success(false)
            else -> Result.failure(RuntimeException("Invalid boolean value for key '$key'"))
        }
    }

    /** Get a value as an integer. */
    public fun getInt(key: String): Result<Int> {
        val raw = get(key) ?: return Result.failure(RuntimeException("Key '$key' not found"))
        return raw.toIntOrNull()
            ?.let { Result.success(it) }
            ?: Result.failure(RuntimeException("Invalid integer value for key '$key'"))
    }

    /** Get a value as a double (f64). */
    public fun getF64(key: String): Result<Double> {
        val raw = get(key) ?: return Result.failure(RuntimeException("Key '$key' not found"))
        return raw.toDoubleOrNull()
            ?.let { Result.success(it) }
            ?: Result.failure(RuntimeException("Invalid float value for key '$key'"))
    }

    /** Check if a key exists in the environment variables. */
    public fun containsKey(key: String): Boolean {
        return variables.containsKey(key) || getenv(key) != null
    }

    /** Get all environment variables as a Map. */
    public fun getAll(): Map<String, String> {
        return variables.toMap()
    }

    /** Set a value for a given key and update the `.env` file. */
    public fun set(key: String, value: String): Result<Unit> {
        variables[key] = value
        return saveToEnvFile()
    }

    /** Remove a key-value pair and update the `.env` file. */
    public fun remove(key: String): Result<Unit> {
        variables.remove(key)
        return saveToEnvFile()
    }

    /** Set and apply the variable to the system environment. */
    public fun setSystemEnv(key: String, value: String): Result<Unit> {
        set(key, value).getOrElse { return Result.failure(it) }
        setenv(key, value)
        return Result.success(Unit)
    }

    /** Export all loaded variables to the system environment. */
    public fun exportToSystemEnv(): Result<Unit> {
        for ((key, value) in variables) {
            setenv(key, value)
        }
        return Result.success(Unit)
    }

    /** Save the current state of the environment variables to the `.env` file. */
    private fun saveToEnvFile(): Result<Unit> {
        val content = buildString {
            for ((k, v) in variables) {
                append(k).append('=').append(v).append('\n')
            }
        }
        return writeStringToFile(".env", content)
            .recoverCatching { throw RuntimeException("Failed to write to .env file") }
    }

    public companion object {
        /** Load `.env` file from the current directory and parse it into a new Envie instance. */
        public fun load(): Result<Envie> {
            return loadWithPath(".env")
        }

        /** Load a `.env` file from a specified path and parse it into a new Envie instance. */
        public fun loadWithPath(path: String): Result<Envie> {
            val content = readFileToString(path).getOrElse {
                return Result.failure(
                    RuntimeException("Failed to read .env file from '$path'. Make sure it exists."),
                )
            }
            val variables = parse(content).toMutableMap()
            return Result.success(Envie(variables))
        }

        /** Parse the content of a `.env` file into a Map. */
        internal fun parse(content: String): Map<String, String> {
            val result = LinkedHashMap<String, String>()
            for (rawLine in content.lineSequence()) {
                val line = rawLine.trim()
                if (line.isEmpty() || line.startsWith('#')) continue
                val eq = line.indexOf('=')
                val (k, v) = if (eq >= 0) {
                    line.substring(0, eq).trim() to line.substring(eq + 1).trim()
                } else {
                    line to ""
                }
                result[k] = v
            }
            return result
        }
    }
}
