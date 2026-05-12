// port-lint: ignore (JVM / Android file I/O via kotlin.io.path; avoids `import java.*`)
package io.github.kotlinmania.envie

import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

internal actual fun readFileToString(path: String): Result<String> = runCatching {
    Path(path).readText()
}

internal actual fun writeStringToFile(path: String, content: String): Result<Unit> = runCatching {
    Path(path).writeText(content)
}
