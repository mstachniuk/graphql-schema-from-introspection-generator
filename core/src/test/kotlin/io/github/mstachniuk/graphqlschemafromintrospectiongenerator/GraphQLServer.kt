package io.github.mstachniuk.graphqlschemafromintrospectiongenerator

import graphql.GraphQL
import graphql.language.InterfaceTypeDefinition
import graphql.language.UnionTypeDefinition
import graphql.schema.Coercing
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLScalarType
import graphql.schema.TypeResolver
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import graphql.schema.idl.TypeRuntimeWiring
import org.json.JSONObject

class GraphQLServer {
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

        val defaultScalars = listOf("Int", "Float", "String", "Boolean", "ID", "Long", "BigInteger", "BigDecimal",
                "Short", "Char")

        fun createGraphQLServer(schemaText: String): GraphQLSerer {
            val schemaParser = SchemaParser()
            val registry = schemaParser.parse(schemaText)
            val schemaGenerator = SchemaGenerator()
            val runtimeWiring = createRuntimeWiring(registry)
            val schema = schemaGenerator.makeExecutableSchema(registry, runtimeWiring)
            val graphql = GraphQL.newGraphQL(schema).build()
            return GraphQLSerer(graphql)
        }

        private fun createRuntimeWiring(registry: TypeDefinitionRegistry): RuntimeWiring {
            val wiring = RuntimeWiring.newRuntimeWiring()
            registry.scalars()
                    .filterKeys { key -> !defaultScalars.contains(key) }
                    .forEach { wiring.scalar(dummyScalar(it.key)) }
            val interfaceTypes = registry.getTypes(InterfaceTypeDefinition::class.java)
            interfaceTypes.forEach {
                wiring.type(TypeRuntimeWiring.newTypeWiring(it.name).typeResolver(defaultTypeResolver()))
            }
            val unionTypes = registry.getTypes(UnionTypeDefinition::class.java)
            unionTypes.forEach {
                wiring.type(TypeRuntimeWiring.newTypeWiring(it.name).typeResolver(defaultTypeResolver()))
            }
            return wiring.build()
        }

        private fun defaultTypeResolver(): TypeResolver {
            return TypeResolver { GraphQLObjectType.newObject().build(); }
        }

        private fun dummyScalar(name: String): GraphQLScalarType {
            return GraphQLScalarType(name, "desc", object : Coercing<Any, Any> {
                override fun parseValue(input: Any?): Any {
                    return ""
                }

                override fun parseLiteral(input: Any?): Any {
                    return ""
                }

                override fun serialize(dataFetcherResult: Any?): Any {
                    return ""
                }
            })
        }
    }

    class GraphQLSerer(val graphql: GraphQL) {
        fun runIntrospectionQuery(): String {
            val introspectionQueryResult = graphql.execute(INTROSPECTION_QUERY)
            return JSONObject(introspectionQueryResult).toString()
        }
    }
}
