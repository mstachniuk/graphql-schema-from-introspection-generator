package io.github.mstachniuk.graphqlschemafromintrospectiongenerator.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable internal data class IntrospectionResponse(val data: DataNode)
@Serializable internal data class DataNode(@SerialName("__schema") val schema: Schema)

@Serializable internal data class Schema(
    val queryType: QueryType?,
    val mutationType: MutationType? = null,
    val subscriptionType: SubscriptionType? = null,
    val types: List<GraphQLType>? = null,
    val directives: List<GraphQLDirective>? = null
)

@Serializable internal data class QueryType(val name: String)
@Serializable internal data class MutationType(val name: String)
@Serializable internal data class SubscriptionType(val name: String)
@Serializable internal data class GraphQLType(
    val kind: String,
    val name: String,
    override val description: String = "",
    val fields: List<GraphQLField> = listOf(),
    val enumValues: List<GraphQLEnumType> = listOf(),
    val inputFields: List<GraphQLField> = listOf(),
    val interfaces: List<GraphQLFieldType> = listOf(),
    val possibleTypes: List<GraphQLFieldType> = listOf()
) : Descriptable

@Serializable internal data class GraphQLField(
    val name: String,
    override val description: String = "",
    val args: List<GraphQLField> = listOf(),
    val type: GraphQLFieldType,
    val defaultValue: String = ""
) : Descriptable

@Serializable internal data class GraphQLFieldType(
    val kind: String,
    val name: String = "",
    val ofType: GraphQLFieldType? = null
)

@Serializable internal data class GraphQLEnumType(
    val name: String,
    override val description: String = ""
) : Descriptable

internal interface Descriptable {
    val description: String
}

@Serializable internal data class GraphQLDirective(
    val name: String,
    override val description: String = "",
    val locations: List<String> = listOf(),
    val args: List<GraphQLField> = listOf()
) : Descriptable
