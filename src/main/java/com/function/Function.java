package com.function;

import com.function.graphql.GraphQLProvider;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;

import java.util.Optional;

public class Function {

    // ==========================================
    // INSTANCIA GRAPHQL
    // ==========================================

    private static final GraphQL graphQL = new GraphQLProvider().buildGraphQL();


    // ==========================================
    // AZURE FUNCTION - GRAPHQL ENDPOINT
    // ==========================================

    @FunctionName("GraphQL")
    public HttpResponseMessage run(

            @HttpTrigger(name = "req", methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.FUNCTION,
                    route = "graphql") HttpRequestMessage<Optional<String>> request,

            final ExecutionContext context) {


        context.getLogger().info("Solicitud GraphQL recibida");


        try {

            // ==========================================
            // OBTENER CONSULTA GRAPHQL
            // ==========================================

            String query = request.getBody().orElse("");


            // ==========================================
            // VALIDAR CONSULTA
            // ==========================================

            if (query.isBlank()) {

                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .header("Content-Type", "application/json").body("""
                                {
                                  "error": "La consulta GraphQL no puede estar vacía"
                                }
                                """).build();
            }


            // ==========================================
            // EJECUTAR GRAPHQL
            // ==========================================

            ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(query).build();


            ExecutionResult executionResult = graphQL.execute(executionInput);


            // ==========================================
            // RESPUESTA GRAPHQL
            // ==========================================

            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(executionResult.toSpecification()).build();

        } catch (Exception e) {

            context.getLogger().severe("Error ejecutando GraphQL: " + e.getMessage());


            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json").body("""
                            {
                              "error": "Error interno ejecutando GraphQL"
                            }
                            """).build();
        }
    }
}
