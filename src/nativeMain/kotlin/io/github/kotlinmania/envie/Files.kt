package io.github.kotlinmania.envie

import io.github.kotlinmania.libc.unix.fclose
import io.github.kotlinmania.libc.unix.fgetc
import io.github.kotlinmania.libc.unix.fopen
import io.github.kotlinmania.libc.unix.fputc

private const val EOF: Int = -1

internal actual fun readFileToString(path: String): Result<String> {
    val fp =
        fopen(path, "rb") ?: return Result.failure(
            RuntimeException("Failed to open '$path' for reading"),
        )
    return try {
        val bytes = ArrayList<Byte>()
        while (true) {
            val next = fgetc(fp)
            if (next == EOF) break
            bytes.add(next.toByte())
        }
        Result.success(bytes.toByteArray().decodeToString())
    } finally {
        fclose(fp)
    }
}

internal actual fun writeStringToFile(path: String, content: String): Result<Unit> {
    val fp =
        fopen(path, "wb") ?: return Result.failure(
            RuntimeException("Failed to open '$path' for writing"),
        )
    return try {
        val bytes = content.encodeToByteArray()
        for ((index, byte) in bytes.withIndex()) {
            if (fputc(byte.toInt() and 0xff, fp) == EOF) {
                return Result.failure(
                    RuntimeException("Short write on '$path': failed at byte $index of ${bytes.size}"),
                )
            }
        }
        Result.success(Unit)
    } finally {
        fclose(fp)
    }
}
