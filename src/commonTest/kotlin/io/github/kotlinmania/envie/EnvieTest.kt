// port-lint: source src/lib.rs (#[cfg(test)] mod tests)
package io.github.kotlinmania.envie

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EnvieTest {
    @Test
    fun test_parse() {
        val content = "KEY1=VALUE1\nKEY2=VALUE2\n"
        val variables = Envie.parse(content)
        assertEquals("VALUE1", variables["KEY1"])
        assertEquals("VALUE2", variables["KEY2"])
    }

    @Test
    fun test_get() {
        val env = Envie(mutableMapOf())
        setenv("TEST_KEY", "test_value")
        assertEquals("test_value", env.get("TEST_KEY"))
        unsetenv("TEST_KEY")
    }

    @Test
    fun test_get_f64() {
        val env = Envie(mutableMapOf("PI" to "3.14"))
        assertEquals(3.14, env.getF64("PI").getOrThrow())
    }

    @Test
    fun test_contains_key() {
        val env = Envie(mutableMapOf("EXISTS" to "value"))
        assertTrue(env.containsKey("EXISTS"))
        unsetenv("DOES_NOT_EXIST")
        assertFalse(env.containsKey("DOES_NOT_EXIST"))
    }

    @Test
    fun test_export_to_system_env() {
        val env = Envie(mutableMapOf("SYSTEM_KEY" to "system_value"))
        env.exportToSystemEnv().getOrThrow()
        assertEquals("system_value", getenv("SYSTEM_KEY"))
        unsetenv("SYSTEM_KEY")
    }

    @Test
    fun parse_skips_blank_and_comment_lines() {
        val content = """
            # this is a comment
            KEY1=VALUE1

            # another comment
            KEY2=VALUE2
        """.trimIndent()
        val variables = Envie.parse(content)
        assertEquals(mapOf("KEY1" to "VALUE1", "KEY2" to "VALUE2"), variables)
    }

    @Test
    fun parse_trims_keys_and_values() {
        val content = "  KEY1  =  VALUE1  \n"
        val variables = Envie.parse(content)
        assertEquals("VALUE1", variables["KEY1"])
    }

    @Test
    fun parse_keeps_value_empty_when_no_equals_sign() {
        val variables = Envie.parse("BARE_KEY\n")
        assertEquals("", variables["BARE_KEY"])
    }

    @Test
    fun get_bool_true_aliases() {
        val env = Envie(mutableMapOf("T1" to "true", "T2" to "TRUE", "T3" to "1"))
        assertTrue(env.getBool("T1").getOrThrow())
        assertTrue(env.getBool("T2").getOrThrow())
        assertTrue(env.getBool("T3").getOrThrow())
    }

    @Test
    fun get_bool_false_aliases() {
        val env = Envie(mutableMapOf("F1" to "false", "F2" to "FALSE", "F3" to "0"))
        assertFalse(env.getBool("F1").getOrThrow())
        assertFalse(env.getBool("F2").getOrThrow())
        assertFalse(env.getBool("F3").getOrThrow())
    }

    @Test
    fun get_bool_rejects_garbage() {
        val env = Envie(mutableMapOf("X" to "maybe"))
        assertTrue(env.getBool("X").isFailure)
    }

    @Test
    fun get_int_parses_and_rejects() {
        val env = Envie(mutableMapOf("N" to "42", "BAD" to "forty-two"))
        assertEquals(42, env.getInt("N").getOrThrow())
        assertTrue(env.getInt("BAD").isFailure)
    }
}
