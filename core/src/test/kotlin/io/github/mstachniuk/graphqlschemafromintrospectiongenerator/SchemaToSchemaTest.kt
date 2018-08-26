package io.github.mstachniuk.graphqlschemafromintrospectiongenerator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class SchemaToSchemaTest {

    /**
     * This test make those conversions:
     * GraphQL Schema -> Introspection Query result -> GraphQL Schema
     * and compare begin schema with end schema. It takes more time then [GeneratorTest] and should be used for
     * exploratory tests of Generator
     */
    @Test
    fun `should test from schema to schema`() {
        // given
        val path = System.getProperty("user.dir") + "/src/test/resources/testdata/schema2schema.graphqls"
        val schemaText = File(path).readText()

        val graphql = GraphQLServer.createGraphQLServer(schemaText)
        val generator = Generator()

        // when
        val jsonString = graphql.runIntrospectionQuery()
        val generatedSchema = generator.generate(jsonString)

        // then
        assertEquals(schemaText, generatedSchema)
    }
}
