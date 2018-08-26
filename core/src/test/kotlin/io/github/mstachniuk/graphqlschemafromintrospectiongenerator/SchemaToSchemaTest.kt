package io.github.mstachniuk.graphqlschemafromintrospectiongenerator

import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class SchemaToSchemaTest {
    companion object {
        const val INTROSPECTION_QUERY = "query IntrospectionQuery {\n" +
                " __schema {\n" +
                "   queryType { name }\n" +
                "   mutationType { name }\n" +
                "   subscriptionType { name }\n" +
                "   types {\n" +
                "     ...FullType\n" +
                "   }\n" +
                "   directives {\n" +
                "     name\n" +
                "     description\n" +
                "     locations\n" +
                "     args {\n" +
                "       ...InputValue\n" +
                "     }\n" +
                "   }\n" +
                " }\n" +
                "}\n" +
                "\n" +
                "fragment FullType on __Type {\n" +
                " kind\n" +
                " name\n" +
                " description\n" +
                " fields(includeDeprecated: true) {\n" +
                "   name\n" +
                "   description\n" +
                "   args {\n" +
                "     ...InputValue\n" +
                "   }\n" +
                "   type {\n" +
                "     ...TypeRef\n" +
                "   }\n" +
                "   isDeprecated\n" +
                "   deprecationReason\n" +
                " }\n" +
                " inputFields {\n" +
                "   ...InputValue\n" +
                " }\n" +
                " interfaces {\n" +
                "   ...TypeRef\n" +
                " }\n" +
                " enumValues(includeDeprecated: true) {\n" +
                "   name\n" +
                "   description\n" +
                "   isDeprecated\n" +
                "   deprecationReason\n" +
                " }\n" +
                " possibleTypes {\n" +
                "   ...TypeRef\n" +
                " }\n" +
                "}\n" +
                "\n" +
                "fragment InputValue on __InputValue {\n" +
                " name\n" +
                " description\n" +
                " type { ...TypeRef }\n" +
                " defaultValue\n" +
                "}\n" +
                "\n" +
                "fragment TypeRef on __Type {\n" +
                " kind\n" +
                " name\n" +
                " ofType {\n" +
                "   kind\n" +
                "   name\n" +
                "   ofType {\n" +
                "     kind\n" +
                "     name\n" +
                "     ofType {\n" +
                "       kind\n" +
                "       name\n" +
                "       ofType {\n" +
                "         kind\n" +
                "         name\n" +
                "         ofType {\n" +
                "           kind\n" +
                "           name\n" +
                "           ofType {\n" +
                "             kind\n" +
                "             name\n" +
                "             ofType {\n" +
                "               kind\n" +
                "               name\n" +
                "             }\n" +
                "           }\n" +
                "         }\n" +
                "       }\n" +
                "     }\n" +
                "   }\n" +
                " }\n" +
                "}"
    }

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

        val schemaParser = SchemaParser()
        val registry = schemaParser.parse(schemaText)
        val schemaGenerator = SchemaGenerator()
        val runtimeWiring = RuntimeWiring.newRuntimeWiring().build()
        val schema = schemaGenerator.makeExecutableSchema(registry, runtimeWiring)
        val graphql = GraphQL.newGraphQL(schema).build()
        val generator = Generator()

        // when
        val introspectionQueryResult = graphql.execute(INTROSPECTION_QUERY)
        val jsonString = JSONObject(introspectionQueryResult).toString()
        val generatedSchema = generator.generate(jsonString)

        // then
        assertEquals(schemaText, generatedSchema)
    }
}
