package io.github.mstachniuk.graphqlschemafromintrospectiongenerator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class GeneratorTest {

    @ParameterizedTest
    @MethodSource("sourceProvider")
    fun `should generate schema`(args: Pair<String, String>) {
        val input = File(args.first).readText().trimIndent()
        val expected = File(args.second).readText().trimIndent()
        val gen = Generator()

        val result = gen.generate(input)

        assertEquals(expected, result);
    }

    fun sourceProvider() : Stream<Pair<String, String>> {
        val path = System.getProperty("user.dir") + "/src/test/resources/testdata"
        val file = File(path)
        val list = file.list { _ , name -> name.matches("input-(\\d*)\\.json$".toRegex()) }
                .map { name -> Pair("$path/$name", "$path/" + name.replace("input", "schema")
                        .replace(".json", ".graphqls")) }
        return list.stream()
    }
}
