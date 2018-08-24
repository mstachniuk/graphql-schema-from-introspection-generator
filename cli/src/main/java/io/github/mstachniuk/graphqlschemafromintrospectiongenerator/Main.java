package io.github.mstachniuk.graphqlschemafromintrospectiongenerator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Application generate GpaphQL Schema from Introspection Query result");
            System.out.println("Please specify 2 arguments: input and output file, e.g.:");
            System.out.println("java -jar graphql-schema-from-introspection-generator-cli-X.X.X.jar input.json output.graphqls");
            System.exit(1);
        }

        String input = readFile(args[0]);

        String schema = generateSchema(input);
        saveSchemaToFile(args[1], schema);
    }

    private static String readFile(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
    }

    private static String generateSchema(String input) {
        Generator generator = new Generator();
        return generator.generate(input);
    }

    private static void saveSchemaToFile(String filename, String schema) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(filename)) {
            out.println(schema);
        }
    }
}
