package io.github.mstachniuk.graphqlschemafromintrospectiongenerator.internal

import com.beust.klaxon.Json

data class IntrospectionResponse(val data: DataNode)
data class DataNode(@Json(name = "__schema") val schema: Schema)

data class Schema(
    val queryType: QueryType?,
    val mutationType: MutationType? = null,
    val subscriptionType: SubscriptionType? = null,
    val types: List<GraphQLType>? = null
)

data class QueryType(val name: String)
data class MutationType(val name: String)
data class SubscriptionType(val name: String)
data class GraphQLType(
    val kind: String,
    val name: String,
    override val description: String = "",
    val fields: List<GraphQLField> = listOf(),
    val enumValues: List<GraphQLEnumType> = listOf(),
    val inputFields: List<GraphQLField> = listOf(),
    val interfaces: List<GraphQLFieldType> = listOf(),
    val possibleTypes: List<GraphQLFieldType> = listOf()
) : Descriptable

data class GraphQLField(
    val name: String,
    override val description: String = "",
    val args: List<GraphQLField> = listOf(),
    val type: GraphQLFieldType,
    val defaultValue: String = ""
) : Descriptable

data class GraphQLFieldType(
    val kind: String,
    val name: String = "",
    val ofType: GraphQLFieldType? = null
)

data class GraphQLEnumType(
    val name: String,
    override val description: String = ""
) : Descriptable

interface Descriptable {
    val description: String
}
