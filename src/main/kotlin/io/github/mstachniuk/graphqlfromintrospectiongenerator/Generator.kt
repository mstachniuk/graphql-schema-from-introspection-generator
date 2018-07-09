package io.github.mstachniuk.graphqlfromintrospectiongenerator

import com.beust.klaxon.Klaxon

class Generator {

    val margin = "    "

    fun generate(input: String): String {

        val response = Klaxon().parse<IntrospectionResponse>(input)

        println(response!!.data.schema)

        var output = printTypes(response)
        output += printQueries(response)

        return output.trimIndent()
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
            output += "type ${it.name} {\n"
            it.fields.forEach {
                output += printDescription(it)
                output += "$margin${it.name}: ${printType(it.type)}\n"
            }
            output += "}\n\n"
        }
        return output
    }

    private fun printDescription(it: Descriptable): String {
        var output = ""
        if (it.description.isNotBlank()) {
            output += "$margin#${it.description}\n"
        }
        return output
    }

    private fun printType(type: GrpahQLFieldType): String {
        if (type.kind == "NON_NULL") {
            return "${type.ofType!!.name}!"
        }
        return type.name
    }


    private fun printQueries(response: IntrospectionResponse): String {
        val queryTypeName = response!!.data.schema.queryType!!.name

        val queries = response!!.data.schema.types!!
                .filter { it.name == queryTypeName }

        var output = "type Query {\n"

        queries.forEach {
            it.fields.forEach {
                output += printDescription(it)
                output += "$margin${it.name}(${printArguments(it.args)}): ${printType(it.type)}\n"
            }
        }
        output += "}\n"
        return output
    }

    private fun printArguments(args: List<GrpahQLField>): String {
        return args
                .map { "${it.name}: ${printType(it.type)}" }
                .joinToString(", ")
    }
}