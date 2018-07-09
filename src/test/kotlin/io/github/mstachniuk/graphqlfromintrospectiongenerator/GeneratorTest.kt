package io.github.mstachniuk.graphqlfromintrospectiongenerator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class GeneratorTest {

    @ParameterizedTest
    @MethodSource("sourceProvider")
    fun `should generate schema`(args: Pair<String, String>) {
        javaClass.getResource(args.first).readText()
        val input = javaClass.getResource(args.first).readText().trimIndent()
        val expected = javaClass.getResource(args.second).readText().trimIndent()

        val gen = Generator()
        val result = gen.generate(input)

        assertEquals(expected, result);
    }

    fun sourceProvider() : Stream<Pair<String, String>> {
        return Stream.of(Pair("/testdata/input-1.json", "/testdata/schema-1.graphqls"))
    }
}