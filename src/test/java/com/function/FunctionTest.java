package com.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

public class FunctionTest {

    /**
     * Prueba GET /api/graphql cuando no se envía consulta.
     */
    @Test
    public void testGraphQLSinConsulta() {

        @SuppressWarnings("unchecked")
        HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        configurarRequestGet(req);

        // No se envía parámetro query
        doReturn(Map.of()).when(req).getQueryParameters();

        configurarResponseBuilder(req);

        ExecutionContext context = mock(ExecutionContext.class);

        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Ejecutar Function
        HttpResponseMessage ret = new Function().run(req, context);

        // Verificar respuesta
        assertEquals(HttpStatus.BAD_REQUEST, ret.getStatus());
    }


    /**
     * Prueba GET /api/graphql cuando la consulta está vacía.
     */
    @Test
    public void testGraphQLConsultaVacia() {

        @SuppressWarnings("unchecked")
        HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        configurarRequestGet(req);

        // Consulta vacía
        doReturn(Map.of("query", "")).when(req).getQueryParameters();

        configurarResponseBuilder(req);

        ExecutionContext context = mock(ExecutionContext.class);

        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Ejecutar Function
        HttpResponseMessage ret = new Function().run(req, context);

        // Verificar respuesta
        assertEquals(HttpStatus.BAD_REQUEST, ret.getStatus());
    }


    /**
     * Prueba GET /api/graphql con una consulta GraphQL válida.
     *
     * Consulta de prueba:
     *
     * { mensaje }
     */
    @Test
    public void testGraphQLConsultaMensaje() {

        @SuppressWarnings("unchecked")
        HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        configurarRequestGet(req);

        // Consulta GraphQL válida
        String query = """
                {
                    mensaje
                }
                """;

        doReturn(Map.of("query", query)).when(req).getQueryParameters();

        configurarResponseBuilder(req);

        ExecutionContext context = mock(ExecutionContext.class);

        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Ejecutar Function
        HttpResponseMessage ret = new Function().run(req, context);

        // Verificar respuesta HTTP
        assertEquals(HttpStatus.OK, ret.getStatus());
    }


    /**
     * Configura la solicitud como GET.
     */
    private void configurarRequestGet(HttpRequestMessage<Optional<String>> req) {

        // Simulamos GET
        doReturn(com.microsoft.azure.functions.HttpMethod.GET).when(req).getHttpMethod();
    }


    /**
     * Configura el Response Builder utilizado por las pruebas.
     */
    private void configurarResponseBuilder(HttpRequestMessage<Optional<String>> req) {

        doAnswer(invocation -> {

            HttpStatus status = (HttpStatus) invocation.getArguments()[0];

            return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);

        }).when(req).createResponseBuilder(any(HttpStatus.class));
    }
}
