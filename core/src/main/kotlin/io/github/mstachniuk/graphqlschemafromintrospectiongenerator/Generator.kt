package io.github.mstachniuk.graphqlschemafromintrospectiongenerator

import io.github.mstachniuk.graphqlschemafromintrospectiongenerator.internal.GeneratorImpl

class Generator {

    fun generate(input: String): String {
        return GeneratorImpl().generate(input)
    }
}
