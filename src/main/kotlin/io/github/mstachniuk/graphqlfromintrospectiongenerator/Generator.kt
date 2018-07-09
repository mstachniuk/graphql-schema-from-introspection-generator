package io.github.mstachniuk.graphqlfromintrospectiongenerator

import com.beust.klaxon.Klaxon

class Generator {

    val margin = "    "

    fun generate(input: String): String {

        val response = Klaxon().parse<IntrospectionResponse>(input)

        println(response!!.data.schema)

        var output = printTypes(response)
        output += printQueries(response)
        output += printMutations(response)

        return output.trimIndent().trimIndent()
    }

    private fun printTypes(response: IntrospectionResponse?): String {
        val queryTypeName = response!!.data.schema.queryType!!.name
        val mutationTypeName = response!!.data.schema.mutationType?.name
        val subscriptionTypeName = response!!.data.schema.subscriptionType?.name

        val types = response!!.data.schema.types!!
                .filter { !it.name.startsWith("__") }
                .filter { it.kind != "SCALAR" }
                .filter { it.name != queryTypeName }
                .filter { it.name != mutationTypeName }
                .filter { it.name != subscriptionTypeName }

        types.forEach { println(it) }
        println()

        var output = ""

        types.forEach {
            val kind = when {
                it.kind == "OBJECT" -> "type"
                it.kind == "ENUM" -> "enum"
                it.kind == "INPUT_OBJECT" -> "input"
                else -> "UNKNOWN_TYPE"
            }

            output += "$kind ${it.name} {\n"
            it.fields.forEach {
                output += printDescription(it)
                output += "$margin${it.name}: ${printType(it.type)}\n"
            }
            it.inputFields.forEach {
                output += printDescription(it)
                output += "$margin${it.name}: ${printType(it.type)}\n"
            }
            output += printEnumTypes(it.enumValues)

            output += "}\n\n"
        }
        return output
    }

    private fun printEnumTypes(enumValues: List<GraphQLEnumType>): String {
        val enumValuesText = enumValues.map { it.name }
                .joinToString(", ")
        if (enumValuesText.isNotBlank()) {
            return "$margin$enumValuesText\n"
        } else {
            return ""
        }
    }

    private fun printDescription(it: Descriptable): String {
        var output = ""
        if (it.description.isNotBlank()) {
            output += "$margin#${it.description}\n"
        }
        return output
    }

    private fun printType(type: GraphQLFieldType): String {
        if (type.kind == "NON_NULL") {
            return "${printType(type.ofType!!)}!"
        }
        if (type.kind == "LIST") {
            return "[${printType(type.ofType!!)}]"
        }
        return type.name
    }


    private fun printQueries(response: IntrospectionResponse): String {
        if (response!!.data.schema.queryType == null) {
            return ""
        }
        val queryTypeName = response!!.data.schema.queryType!!.name

        val queries = response!!.data.schema.types!!
                .filter { it.name == queryTypeName }

        var queriesText = printOperations(queries)
        if (queriesText.isBlank()) {
            return ""
        }
        return "type Query {\n$queriesText}\n\n"
    }

    private fun printOperations(queries: List<GraphQLType>): String {
        var queriesText = ""
        queries.forEach {
            it.fields.forEach {
                queriesText += printDescription(it)
                queriesText += "$margin${it.name}${printArguments(it.args)}: ${printType(it.type)}\n"
            }
        }
        return queriesText
    }

    private fun printArguments(args: List<GraphQLField>): String {
        val arguments = args
                .map { "${it.name}: ${printType(it.type)}" }
                .joinToString(", ")
        if (arguments.isNotBlank()) {
            return "($arguments)"
        }
        return ""
    }

    private fun printMutations(response: IntrospectionResponse): String {
        if (response!!.data.schema.mutationType == null) {
            return ""
        }
        val mutationTypeName = response!!.data.schema.mutationType!!.name
        val mutations = response!!.data.schema.types!!
                .filter { it.name == mutationTypeName }

        var queriesText = printOperations(mutations)
        if (queriesText.isBlank()) {
            return ""
        }
        return "type Mutation {\n$queriesText}\n\n"
    }
}