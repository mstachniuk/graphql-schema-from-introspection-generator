package io.github.mstachniuk.graphqlfromintrospectiongenerator

import com.beust.klaxon.Json

data class IntrospectionResponse(val data: DataNode)
data class DataNode(@Json(name = "__schema")val schema: Schema)

data class Schema(val queryType: QueryType?,
                  val mutationType: MutationType? = null,
                  val subscriptionType: SubscriptionType? = null,
                  val types: List<GraphQLType>? = null)

data class QueryType(val name: String)
data class MutationType(val name: String)
data class SubscriptionType(val name: String)
data class GraphQLType(val kind: String,
                       val name: String,
                       override val description: String = "",
                       val fields: List<GrpahQLField> = listOf()) : Descriptable

data class GrpahQLField(val name: String,
                        override val description: String = "",
                        val args: List<GrpahQLField> = listOf(),
                        val type: GrpahQLFieldType) : Descriptable
data class GrpahQLFieldType(val kind: String,
                            val name: String = "",
                            val ofType: GrpahQLFieldType? = null)

interface Descriptable{val description: String}