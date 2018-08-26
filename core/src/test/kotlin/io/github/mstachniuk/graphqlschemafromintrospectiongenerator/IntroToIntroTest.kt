package io.github.mstachniuk.graphqlschemafromintrospectiongenerator

import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.io.File

class IntroToIntroTest {

    /**
     * This test make this conversions:
     * Introspection Query result -> GraphQL Schema -> Introspection Query result
     * and compare first and last result. It should be the same - it's JSON format, the order of elements doesn't
     * matter. Sometimes result can be different because of another behaviour in case of null values - some
     * frameworks add e.g. `description: null` node, another not.
     * This test is slower then [GeneratorTest] and is considered for exploratory tests of Generator.
     */
    @Test
    fun `should test from introspection result to introspection result`() {
        // given
        val path = System.getProperty("user.dir") + "/src/test/resources/testdata/intro2intro.json"
        val introspectionText = File(path).readText()
        val generator = Generator()

        // when
        val schemaText = generator.generate(introspectionText)
        val graphql = GraphQLServer.createGraphQLServer(schemaText)
        val jsonString = graphql.runIntrospectionQuery()

        // then
        JSONAssert.assertEquals(introspectionText, jsonString, false)
    }
}
