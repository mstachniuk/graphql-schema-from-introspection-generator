package io.github.mstachniuk.graphqlschemafromintrospectiongenerator

/**
 * Class for Generator Settings
 *
 * @property trimStartComments Comments in GraphQL Schema usually start from leading space (between # and first letter). This lead that graphql-java will generate description field also with leading space what can lead to different generated schema. To avoid that this property is set to false by default.
 */
data class GeneratorSettings(val trimStartComments: Boolean = false)
