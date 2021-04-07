package io.github.mstachniuk.graphqlschemafromintrospectiongenerator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
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

        assertEquals(expected, result)
    }

    fun sourceProvider(): Stream<Pair<String, String>> {
        val path = System.getProperty("user.dir") + "/src/test/resources/testdata"
        val file = File(path)
        val list = file.list { _, name -> name.matches("input-(\\d*)\\.json$".toRegex()) }
            .map { name ->
                Pair(
                    "$path/$name",
                    "$path/" + name.replace("input", "schema")
                        .replace(".json", ".graphqls")
                )
            }
        return list.stream()
    }

    @Test
    fun `should trim space in comments`() {
        val path = System.getProperty("user.dir") + "/src/test/resources/testdata/input-6.json"
        val input = File(path).readText().trimIndent()
        val gen = Generator()

        val result = gen.generate(input, GeneratorSettings(true))

        val expectedResult = """#description of customer
type Customer {
    #unique id
    id: ID!
    #lastname comment with 5 leading spaces
    lastname: String
    #name comment without leading space
    name: String
}

type Query {
    customer(id: String): Customer
}"""
        assertEquals(expectedResult, result)
    }
}
