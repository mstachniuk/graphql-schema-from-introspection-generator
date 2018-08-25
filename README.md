# GraphQL Schema from Introspection generator

[![MIT License](http://img.shields.io/badge/license-MIT-green.svg) ](https://github.com/mstachniuk/graphql-schema-from-introspection-generator/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mstachniuk/graphql-schema-from-introspection-generator-core.svg)](https://search.maven.org/artifact/io.github.mstachniuk/graphql-schema-from-introspection-generator-core)
[![Bintray](https://api.bintray.com/packages/mstachniuk/mstachniuk-maven-repo/maven/images/download.svg) ](https://bintray.com/mstachniuk/mstachniuk-maven-repo/maven/_latestVersion)

This library helps you generate [GraphQL Schema](https://graphql.org/learn/schema/) (also called (GraphQL DSL or SDL)[https://graphql-java.readthedocs.io/en/latest/schema.html]) based on Introspection Query response. 
It's useful when you use [graphql-java](https://github.com/graphql-java/graphql-java) and (Code First approach)[https://graphql-java.readthedocs.io/en/latest/schema.html#creating-a-schema-programmatically] and want to migrate to (Schema First approach)[https://graphql-java.readthedocs.io/en/latest/schema.html#creating-a-schema-using-the-sdl].

## How to use it?

1. Download Command Line Tool from [releases](https://github.com/mstachniuk/graphql-schema-from-introspection-generator/releases) page.
2. Run `java -jar graphql-schema-from-introspection-generator-cli-X.X.X.jar input.json output.graphqls`

   File *input.json* should contains output of GrpahQL introspection query.
   If you don't have this file yet, you can use one from *core/src/test/resources/testdata/*   
3. In *output.graphqls* you will find generated GraphQL Schema.

## How get Introspection Query result?

1. Run you application.
2. Run 
   <details>
     <summary>Introspection Query</summary>
     
   ```
       query IntrospectionQuery {
         __schema {
           queryType { name }
           mutationType { name }
           subscriptionType { name }
           types {
             ...FullType
           }
           directives {
             name
             description
             locations
             args {
               ...InputValue
             }
           }
         }
       }
     
       fragment FullType on __Type {
         kind
         name
         description
         fields(includeDeprecated: true) {
           name
           description
           args {
             ...InputValue
           }
           type {
             ...TypeRef
           }
           isDeprecated
           deprecationReason
         }
         inputFields {
           ...InputValue
         }
         interfaces {
           ...TypeRef
         }
         enumValues(includeDeprecated: true) {
           name
           description
           isDeprecated
           deprecationReason
         }
         possibleTypes {
           ...TypeRef
         }
       }
     
       fragment InputValue on __InputValue {
         name
         description
         type { ...TypeRef }
         defaultValue
       }
     
       fragment TypeRef on __Type {
         kind
         name
         ofType {
           kind
           name
           ofType {
             kind
             name
             ofType {
               kind
               name
               ofType {
                 kind
                 name
                 ofType {
                   kind
                   name
                 }
               }
             }
           }
         }
       }
   ```
   
   This query based on Introspection Query in [graphql-java](https://github.com/graphql-java/graphql-java) project.
   
   </details>

3. Store result in file and use Command Line tool for generating schema (See (How to use it?)[#how-to-use-it]).



## Release Notes

Release notes: [docs/release-notes.md](/docs/release-notes.md)

## How to build project?

1. Clone repo
2. Run `./gradlew build`
3. You can find Command Line Tool in *cli/build/libs*
4. You can find core library in *core/build/libs*

## Another usage

You can use core library in your projects if you want. Just add a dependency (in Gradle):

`compile group: 'io.github.mstachniuk', name: 'graphql-schema-from-introspection-generator-core'`

## How to contribute? 

Please Send PR's, issues and feedback via GitHub. 
