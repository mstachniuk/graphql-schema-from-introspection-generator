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
                it.kind == "UNION" -> "union"
                else -> "UNKNOWN_TYPE"
            }

            output += printDescription(it, false)
            output += "$kind ${it.name}${printInterfaces(it.interfaces)} "
            output += printBody(it)
        }
        return output
    }

    private fun printBody(type: GraphQLType): String {
        if (type.kind == "UNION") {
            return "= ${type.possibleTypes
                    .sortedBy { it.name }
                    .joinToString(" | ") { it.name }}\n"
        }
        var output = "{\n"
        type.fields.sortedBy { it.name }
                .forEach {
                    output += printField(it)
                }
        type.inputFields.sortedBy { it.name }
                .forEach {
                    output += printField(it)
                }
        output += printEnumTypes(type.enumValues)

        output += "}\n\n"
        return output
    }

    private fun printEnumTypes(enumValues: List<GraphQLEnumType>): String {
        if (containsDescription(enumValues)) {
            val enums = enumValues
                    .sortedBy { it.name }
                    .map { "${printDescription(it)}$margin${it.name}" }
                    .joinToString("\n")
            return "$enums\n"
        }
        val enumValuesText = enumValues.joinToString(", ") { it.name }
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
            output += "# ${it.description.trim().replace("\n", "\n$margin# ")}\n"
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

    private fun printField(field: GraphQLField): String {
        val arguments = printArguments(field.args.sortedBy { it.name })
        return "${printDescription(field)}$margin${field.name}${arguments}: ${printType(field.type)}" +
                "${printDefaultValue(field)}\n"
    }

    private fun printDefaultValue(field: GraphQLField): String {
        if (field.defaultValue.isNotBlank()) {
            return " = ${field.defaultValue}"
        }
        return "";
    }

    private fun printArguments(args: List<GraphQLField>): String {
        if (containsDescription(args)) {
            val arguments = args
                    .map {
                        "${printDescription(it)}$margin${it.name}: ${printType(it.type)}${printDefaultValue(it)}"
                    }
                    .joinToString("\n")
            if (arguments.isNotBlank()) {
                return "(\n$arguments\n)"
            }
        } else {
            val arguments = args
                    .map { "${it.name}: ${printType(it.type)}" }
                    .joinToString(", ")
            if (arguments.isNotBlank()) {
                return "($arguments)"
            }
        }
        return ""
    }

    private fun containsDescription(args: List<Descriptable>) =
            args.any { it.description.isNotBlank() }

    private fun printInterfaces(interfaces: List<GraphQLFieldType>): String {
        val interfaceList = interfaces.sortedBy { it.name }
                .map { it.name }
                .joinToString(" & ")
        if (interfaceList.isNotBlank()) {
            return " implements $interfaceList"
        }
        return ""
    }
}
