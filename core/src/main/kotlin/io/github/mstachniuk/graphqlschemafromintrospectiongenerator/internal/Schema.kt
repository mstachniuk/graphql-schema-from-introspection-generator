package io.github.mstachniuk.graphqlschemafromintrospectiongenerator.internal

import com.beust.klaxon.Json

internal data class IntrospectionResponse(val data: DataNode)
internal data class DataNode(@Json(name = "__schema") val schema: Schema)

internal data class Schema(
    val queryType: QueryType?,
    val mutationType: MutationType? = null,
    val subscriptionType: SubscriptionType? = null,
    val types: List<GraphQLType>? = null,
    val directives: List<GraphQLDirective>? = null
)

internal data class QueryType(val name: String)
internal data class MutationType(val name: String)
internal data class SubscriptionType(val name: String)
internal data class GraphQLType(
    val kind: String,
    val name: String,
    override val description: String = "",
    val fields: List<GraphQLField> = listOf(),
    val enumValues: List<GraphQLEnumType> = listOf(),
    val inputFields: List<GraphQLField> = listOf(),
    val interfaces: List<GraphQLFieldType> = listOf(),
    val possibleTypes: List<GraphQLFieldType> = listOf()
) : Descriptable

internal data class GraphQLField(
    val name: String,
    override val description: String = "",
    val args: List<GraphQLField> = listOf(),
    val type: GraphQLFieldType,
    val defaultValue: String = ""
) : Descriptable

internal data class GraphQLFieldType(
    val kind: String,
    val name: String = "",
    val ofType: GraphQLFieldType? = null
)

internal data class GraphQLEnumType(
    val name: String,
    override val description: String = ""
) : Descriptable

internal interface Descriptable {
    val description: String
}

internal data class GraphQLDirective(
    val name: String,
    override val description: String = "",
    val locations: List<String> = listOf(),
    val args: List<GraphQLField> = listOf()
) : Descriptable
