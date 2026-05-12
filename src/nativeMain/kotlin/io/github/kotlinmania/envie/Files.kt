// port-lint: ignore (Native POSIX file I/O via fopen / fread / fwrite / fclose)
@file:OptIn(ExperimentalForeignApi::class)

package io.github.kotlinmania.envie

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.refTo
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fread
import platform.posix.fwrite

private const val IO_CHUNK_SIZE = 4096

internal actual fun readFileToString(path: String): Result<String> {
    val fp = fopen(path, "rb") ?: return Result.failure(
        RuntimeException("Failed to open '$path' for reading"),
    )
    return try {
        // Read in fixed-size chunks rather than seek-and-bulk-read so the implementation works
        // uniformly across POSIX (fseek/ftell using `long`) and mingw (using `int`).
        memScoped {
            val buf = allocArray<ByteVar>(IO_CHUNK_SIZE)
            val accum = ArrayList<Byte>(IO_CHUNK_SIZE)
            while (true) {
                val n = fread(buf, 1u, IO_CHUNK_SIZE.toULong(), fp).toInt()
                if (n <= 0) break
                val chunk = buf.readBytes(n)
                for (b in chunk) accum.add(b)
            }
            Result.success(accum.toByteArray().decodeToString())
        }
    } finally {
        fclose(fp)
    }
}

internal actual fun writeStringToFile(path: String, content: String): Result<Unit> {
    val fp = fopen(path, "wb") ?: return Result.failure(
        RuntimeException("Failed to open '$path' for writing"),
    )
    return try {
        val bytes = content.encodeToByteArray()
        if (bytes.isEmpty()) {
            Result.success(Unit)
        } else {
            val written = fwrite(bytes.refTo(0), 1u, bytes.size.toULong(), fp).toInt()
            if (written != bytes.size) {
                Result.failure(
                    RuntimeException("Short write on '$path': expected ${bytes.size}, got $written"),
                )
            } else {
                Result.success(Unit)
            }
        }
    } finally {
        fclose(fp)
    }
}
