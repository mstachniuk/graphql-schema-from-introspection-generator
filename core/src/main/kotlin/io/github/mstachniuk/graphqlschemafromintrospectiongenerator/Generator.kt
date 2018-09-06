package io.github.mstachniuk.graphqlschemafromintrospectiongenerator

import io.github.mstachniuk.graphqlschemafromintrospectiongenerator.internal.GeneratorImpl

/**
 * This class contains function for generate GraphQL Schema
 */
class Generator {

    /**
     * This function generate GraphQL schema language (also called Graphql DSL or SDL) from
     * <a href="https://graphql.org/learn/introspection/">introspection query</a> result.
     *
     * Argument for this function should be in JSON format returned by introspection query:
     * ```
     * {
     *   "data": {
     *     "__schema": {
     *       "queryType": {
     *         "name": "Query"
     *       },
     *       "types": [...],
     *       "directives": [...]
     *     }
     *   }
     * }
     * ```
     *
     * @param input Introspection query result
     * @return GraphQL Schema definition
     */
    fun generate(input: String): String {
        return GeneratorImpl().generate(input, GeneratorSettings())
    }

    /**
     * Another version of [generate] method with additional settings argument.
     *
     * @param input Introspection query result
     * @param settings Settings how to generate Schema
     * @return GraphQL Schema definition
     * @since 0.2.0
     */
    fun generate(input: String, settings: GeneratorSettings): String {
        return GeneratorImpl().generate(input, settings)
    }
}
