package io.github.kotlinmania.envie

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EnvTest {
    @Test
    fun getenvReturnsNullIfEnvVarIsNotSet() {
        unsetenv("ENVIE_TEST_NON_EXISTING")
        assertNull(getenv("ENVIE_TEST_NON_EXISTING"))
    }

    @Test
    fun setenvThenGetenvRoundTrip() {
        setenv("ENVIE_TEST_RT", "hello")
        assertEquals("hello", getenv("ENVIE_TEST_RT"))
        unsetenv("ENVIE_TEST_RT")
    }

    @Test
    fun setenvOverwritesExistingValue() {
        setenv("ENVIE_TEST_OVERWRITE", "first")
        setenv("ENVIE_TEST_OVERWRITE", "second")
        assertEquals("second", getenv("ENVIE_TEST_OVERWRITE"))
        unsetenv("ENVIE_TEST_OVERWRITE")
    }

    @Test
    fun unsetenvRemovesASetValue() {
        setenv("ENVIE_TEST_UNSET", "to-remove")
        assertEquals("to-remove", getenv("ENVIE_TEST_UNSET"))
        unsetenv("ENVIE_TEST_UNSET")
        assertNull(getenv("ENVIE_TEST_UNSET"))
    }

    @Test
    fun environContainsASetVariableWhenEnumerationIsSupported() {
        setenv("ENVIE_TEST_ENVIRON", "marker")
        val all = environ()
        val hit = all.firstOrNull { it.first == "ENVIE_TEST_ENVIRON" }
        unsetenv("ENVIE_TEST_ENVIRON")
        // environ() returns an empty list on platforms that don't expose process-env enumeration
        // (Apple, browser-hosted JS / WasmJS). On those targets the overlay still drives getenv;
        // we only assert the round-trip when enumeration is actually supported.
        if (all.isNotEmpty()) {
            assertNotNull(hit, "environ() reported a non-empty list but did not contain ENVIE_TEST_ENVIRON")
            assertEquals("marker", hit.second)
        }
    }

    @Test
    fun environIsASnapshotNotALiveView() {
        unsetenv("ENVIE_TEST_SNAPSHOT")
        val before = environ()
        setenv("ENVIE_TEST_SNAPSHOT", "v")
        // Trivially holds when environ() returned empty (enumeration not supported).
        assertTrue(
            before.none { it.first == "ENVIE_TEST_SNAPSHOT" },
            "environ() snapshot leaked a later setenv into the pre-snapshot list",
        )
        unsetenv("ENVIE_TEST_SNAPSHOT")
    }
}
