package com.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

public class FunctionTest {

    /**
     * Prueba POST /api/graphql cuando no se envía body.
     */
    @Test
    public void testGraphQLSinBody() {

        @SuppressWarnings("unchecked")
        HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        // Simulamos POST
        doReturn(HttpMethod.POST).when(req).getHttpMethod();

        // Body vacío
        doReturn(Optional.empty()).when(req).getBody();

        configurarResponseBuilder(req);

        ExecutionContext context = mock(ExecutionContext.class);

        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Ejecutar Function
        HttpResponseMessage ret = new Function().run(req, context);

        // Verificar respuesta
        assertEquals(HttpStatus.BAD_REQUEST, ret.getStatus());
    }


    /**
     * Prueba POST /api/graphql cuando se envía una consulta vacía.
     */
    @Test
    public void testGraphQLConsultaVacia() {

        @SuppressWarnings("unchecked")
        HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        // Simulamos POST
        doReturn(HttpMethod.POST).when(req).getHttpMethod();

        // Consulta vacía
        doReturn(Optional.of("")).when(req).getBody();

        configurarResponseBuilder(req);

        ExecutionContext context = mock(ExecutionContext.class);

        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Ejecutar Function
        HttpResponseMessage ret = new Function().run(req, context);

        // Verificar respuesta
        assertEquals(HttpStatus.BAD_REQUEST, ret.getStatus());
    }


    /**
     * Prueba POST /api/graphql con una consulta GraphQL válida.
     *
     * Esta prueba utiliza la consulta inicial:
     *
     * { mensaje }
     */
    @Test
    public void testGraphQLConsultaMensaje() {

        @SuppressWarnings("unchecked")
        HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        // Simulamos POST
        doReturn(HttpMethod.POST).when(req).getHttpMethod();

        // Consulta GraphQL válida
        String query = """
                {
                    mensaje
                }
                """;

        doReturn(Optional.of(query)).when(req).getBody();

        configurarResponseBuilder(req);

        ExecutionContext context = mock(ExecutionContext.class);

        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Ejecutar Function
        HttpResponseMessage ret = new Function().run(req, context);

        // Verificar respuesta HTTP
        assertEquals(HttpStatus.OK, ret.getStatus());
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
