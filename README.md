# GraphQL Schema from Introspection generator

[![MIT License](http://img.shields.io/badge/license-MIT-green.svg) ](https://github.com/mstachniuk/graphql-schema-from-introspection-generator/blob/master/LICENSE)
[![Build Status](https://travis-ci.org/mstachniuk/graphql-schema-from-introspection-generator.svg?branch=master)](https://travis-ci.org/mstachniuk/graphql-schema-from-introspection-generator)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mstachniuk/graphql-schema-from-introspection-generator-core.svg)](https://search.maven.org/artifact/io.github.mstachniuk/graphql-schema-from-introspection-generator-core)
[![Bintray](https://api.bintray.com/packages/mstachniuk/mstachniuk-maven-repo/maven/images/download.svg) ](https://bintray.com/mstachniuk/mstachniuk-maven-repo/maven/_latestVersion)
[![codecov](https://codecov.io/gh/mstachniuk/graphql-schema-from-introspection-generator/branch/master/graph/badge.svg)](https://codecov.io/gh/mstachniuk/graphql-schema-from-introspection-generator)

This library helps you generate [GraphQL Schema](https://graphql.org/learn/schema/) (also called [GraphQL DSL or SDL](https://graphql-java.readthedocs.io/en/latest/schema.html)) based on Introspection Query response. 
It's useful when you use [graphql-java](https://github.com/graphql-java/graphql-java) and [Code First approach](https://graphql-java.readthedocs.io/en/latest/schema.html#creating-a-schema-programmatically) and want to migrate to [Schema First approach](https://graphql-java.readthedocs.io/en/latest/schema.html#creating-a-schema-using-the-sdl).

## How to use it?

1. Download Command Line Tool from [releases](https://github.com/mstachniuk/graphql-schema-from-introspection-generator/releases) page.
2. Run `java -jar graphql-schema-from-introspection-generator-cli-X.X.X.jar input.json output.graphqls`

   File *input.json* should contain the output of GrpahQL Introspection query.
   If you don't have this file yet, you can use one from: *core/src/test/resources/testdata/*   
3. In *output.graphqls* you will find generated GraphQL Schema.

## How get Introspection Query result?

1. Run your application.
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
         }
       }
   ```
   
   This query based on Introspection Queries in [graphql-java](https://github.com/graphql-java/graphql-java) and [GraphiQL](https://github.com/graphql/graphiql) projects.
   
   </details>

3. Store result in a file and use Command Line tool for generating the schema (See: [How to use it?](#how-to-use-it)).



## Release Notes

Release notes: [docs/release-notes.md](/docs/release-notes.md)

## How to build project?

1. Clone repo
2. Run `./gradlew build`
3. You can find Command Line Tool in *cli/build/libs*
4. You can find core library in *core/build/libs*

## Another usage

You can use the core library in your projects if you want. Just add a dependency (in Gradle):

`compile group: 'io.github.mstachniuk', name: 'graphql-schema-from-introspection-generator-core'`

## How to contribute? 

Please Send PR's, issues and feedback via GitHub. 

## Alternatives

During finishing this project I found that similar tool already exists in 
[graphql-java](https://github.com/graphql-java/graphql-java) project, see `IntrospectionResultToSchema` class.

Another possibility is to use [graphql-js](https://github.com/graphql/graphql-js) and this code snippet (NodeJS):

```javascript
const graphql = require("graphql");
const schema = require("path/to/schema.json");

const clientSchema = graphql.buildClientSchema(schema.data);
const schemaString = graphql.printSchema(clientSchema);
console.log(schemaString)
```

Unfortunately, I didn't know that before :-( 
