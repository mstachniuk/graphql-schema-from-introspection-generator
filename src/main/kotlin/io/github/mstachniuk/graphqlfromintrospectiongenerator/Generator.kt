package io.github.mstachniuk.graphqlfromintrospectiongenerator

import com.beust.klaxon.Klaxon

class Generator {

    val margin = "    "

    fun generate(input: String): String {
        val response = Klaxon().parse<IntrospectionResponse>(input) ?: return ""

        var output = printTypes(response)

        return output.trimIndent().trimIndent()
    }

    private fun printTypes(response: IntrospectionResponse): String {

        val types = response.data.schema.types!!
                .filter { !it.name.startsWith("__") }
                .filter { it.kind != "SCALAR" }

        var output = ""

        types.sortedBy { it.name }.forEach {
            val kind = when {
                it.kind == "OBJECT" -> "type"
                it.kind == "ENUM" -> "enum"
                it.kind == "INPUT_OBJECT" -> "input"
                it.kind == "INTERFACE" -> "interface"
                else -> "UNKNOWN_TYPE"
            }

            output += printDescription(it, false)
            output += "$kind ${it.name} {\n"
            it.fields.sortedBy { it.name }
                    .forEach {
                output += printField(it)
            }
            it.inputFields.sortedBy { it.name }
                    .forEach {
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

    private fun printDescription(it: Descriptable, addMargin: Boolean = true): String {
        var output = ""
        if (it.description.isNotBlank()) {
            if (addMargin) {
                output += "$margin";
            }
            output += "# ${it.description}\n"
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

    private fun printField(field: GraphQLField) : String {
         return "${printDescription(field)}$margin${field.name}${printArguments(field.args)}: ${printType(field.type)}\n"
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
}