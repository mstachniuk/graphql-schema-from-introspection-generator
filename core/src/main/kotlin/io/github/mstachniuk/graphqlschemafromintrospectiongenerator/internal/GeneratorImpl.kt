package io.github.mstachniuk.graphqlschemafromintrospectiongenerator.internal

import com.beust.klaxon.Klaxon
import io.github.mstachniuk.graphqlschemafromintrospectiongenerator.GeneratorSettings

internal class GeneratorImpl {

    val margin = "    "
    val primitiveScalars = listOf("Boolean", "Float", "ID", "Int", "String")
    val primitiveDirectives = listOf("include", "skip", "defer", "deprecated")

    fun generate(
        input: String,
        settings: GeneratorSettings
    ): String {
        val response = Klaxon().parse<IntrospectionResponse>(input) ?: return ""

        val output = printSchema(response) + printTypes(response, settings) + printDirectives(response, settings)

        return output.trimIndent().trimIndent()
    }

    private fun printDirectives(response: IntrospectionResponse, settings: GeneratorSettings): String {
        return response.data.schema.directives!!
            .filter { !primitiveDirectives.contains(it.name) }
            .sortedBy { it.name }
            .map {
                "${printDescription(it, settings, false)}directive @${it.name}" +
                    "${printArguments(it.args, settings)} " +
                    "on ${printDirectiveLocation(it)}"
            }
            .joinToString("\n\n")
    }

    private fun printDirectiveLocation(directive: GraphQLDirective): String {
        return directive.locations
            .sortedBy { it }
            .joinToString(" | ")
    }

    private fun printSchema(response: IntrospectionResponse): String =
        if (isCustomQueryType(response) || isCustomMutationType(response) || isCustomSubscriptionType(response)
        ) {
            "schema {\n" +
                if (isCustomQueryType(response)) {
                    "${margin}query: ${response.data.schema.queryType?.name}\n"
                } else {
                    ""
                } +
                if (isCustomMutationType(response)) {
                    "${margin}mutation: ${response.data.schema.mutationType?.name}\n"
                } else {
                    ""
                } +
                if (isCustomSubscriptionType(response)) {
                    "${margin}subscription: ${response.data.schema.subscriptionType?.name}\n"
                } else {
                    ""
                } +
                "}\n\n"
        } else {
            ""
        }

    private fun isCustomQueryType(response: IntrospectionResponse) =
        response.data.schema.queryType != null && response.data.schema.queryType.name != "Query"

    private fun isCustomMutationType(response: IntrospectionResponse) =
        response.data.schema.mutationType != null && response.data.schema.mutationType.name != "Mutation"

    private fun isCustomSubscriptionType(response: IntrospectionResponse) =
        response.data.schema.subscriptionType != null && response.data.schema.subscriptionType.name != "Subscription"

    private fun printTypes(response: IntrospectionResponse, settings: GeneratorSettings): String {
        val types = response.data.schema.types!!
            .filter { !it.name.startsWith("__") }
            .filter { !primitiveScalars.contains(it.name) }

        var output = ""

        types.sortedBy { it.name }.forEach {
            val kind = when {
                it.kind == "OBJECT" -> "type"
                it.kind == "ENUM" -> "enum"
                it.kind == "INPUT_OBJECT" -> "input"
                it.kind == "INTERFACE" -> "interface"
                it.kind == "UNION" -> "union"
                it.kind == "SCALAR" -> "scalar"
                else -> "UNKNOWN_TYPE"
            }

            output += printDescription(it, settings, false)
            output += "$kind ${it.name}${printInterfaces(it.interfaces)}"
            output += printBody(it, settings)
        }
        return output
    }

    private fun printBody(type: GraphQLType, settings: GeneratorSettings): String {
        if (type.kind == "UNION") {
            val types = type.possibleTypes
                ?.sortedBy { it.name }
                ?.joinToString(" | ") { it.name!! }
            return " = ${types}\n\n"
        }
        if (type.kind == "SCALAR") {
            return "\n\n"
        }
        var output = " {\n"
        type.fields?.sortedBy { it.name }
            ?.forEach {
                output += printField(it, settings)
            }
        type.inputFields?.sortedBy { it.name }
            ?.forEach {
                output += printField(it, settings)
            }
        output += printEnumTypes(type.enumValues, settings)

        output += "}\n\n"
        return output
    }

    private fun printEnumTypes(enumValues: List<GraphQLEnumType>?, settings: GeneratorSettings): String =
        if (containsDescription(enumValues)) {
            val enums = enumValues
                ?.sortedBy { it.name }
                ?.map { "${printDescription(it, settings)}$margin${it.name}" }
                ?.joinToString("\n")
            "$enums\n"
        } else {
            val enumValuesText = enumValues?.joinToString(", ") { it.name }
            if (enumValuesText?.isNotBlank() == true) {
                "$margin$enumValuesText\n"
            } else {
                ""
            }
        }

    private fun printDescription(it: Descriptable, settings: GeneratorSettings, addMargin: Boolean = true): String {
        var output = ""
        if (it.description.isNotBlank()) {
            if (addMargin) {
                output += margin
            }
            var desc = if (settings.trimStartComments) it.description.trimStart() else it.description
            desc = if (settings.trimEndComments) desc.trimEnd() else desc
            output += "#${desc.replace("\n", "\n$margin#")}\n"
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
        return type.name ?: ""
    }

    private fun printField(field: GraphQLField, settings: GeneratorSettings): String {
        val arguments = printArguments(field.args?.sortedBy { it.name }, settings)
        return "${printDescription(field, settings)}$margin${field.name}$arguments: ${printType(field.type)}" +
            "${printDefaultValue(field)}\n"
    }

    private fun printDefaultValue(field: GraphQLField): String {
        if (field.defaultValue?.isNotBlank() == true) {
            if (containsEnumType(field.type)) {
                return " = ${field.defaultValue.replace("\"", "")}"
            }
            return " = ${field.defaultValue}"
        }
        return ""
    }

    private fun containsEnumType(type: GraphQLFieldType): Boolean {
        return when {
            type.kind == "ENUM" -> return true
            type.ofType == null -> return false
            else -> containsEnumType(type.ofType)
        }
    }

    private fun printArguments(args: List<GraphQLField>?, settings: GeneratorSettings): String {
        if (containsDescription(args)) {
            val arguments = args
                ?.map {
                    "${printDescription(it, settings)}$margin${it.name}: ${printType(it.type)}${printDefaultValue(it)}"
                }
                ?.joinToString("\n")
            if (arguments?.isNotBlank() == true) {
                return "(\n$arguments\n)"
            }
        } else {
            val arguments = args
                ?.map { "${it.name}: ${printType(it.type)}${printDefaultValue(it)}" }
                ?.joinToString(", ")
            if (arguments?.isNotBlank() == true) {
                return "($arguments)"
            }
        }
        return ""
    }

    private fun containsDescription(args: List<Descriptable>?) =
        args?.any { it.description.isNotBlank() } ?: false

    private fun printInterfaces(interfaces: List<GraphQLFieldType>?): String {
        val interfaceList = interfaces?.sortedBy { it.name }
            ?.map { it.name }
            ?.joinToString(" & ")
        if (interfaceList?.isNotBlank() == true) {
            return " implements $interfaceList"
        }
        return ""
    }
}
