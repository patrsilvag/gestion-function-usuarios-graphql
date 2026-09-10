package com.function.graphql;

import com.function.model.Rol;
import com.function.model.Usuario;
import com.function.repository.UsuarioRepository;

import graphql.GraphQL;
import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

import java.util.Optional;

public class GraphQLProvider {

    private final UsuarioRepository repository;

    public GraphQLProvider() {

        this.repository = new UsuarioRepository();
    }

    /**
     * Construye y retorna la instancia principal de GraphQL.
     */
    public GraphQL buildGraphQL() {

        // ==========================================
        // TIPO ROL
        // ==========================================

        GraphQLObjectType rolType = GraphQLObjectType.newObject().name("Rol")

                .field(GraphQLFieldDefinition.newFieldDefinition().name("idRol")
                        .type(Scalars.GraphQLID))

                .field(GraphQLFieldDefinition.newFieldDefinition().name("nombreRol")
                        .type(Scalars.GraphQLString))

                .field(GraphQLFieldDefinition.newFieldDefinition().name("estado")
                        .type(Scalars.GraphQLString))

                .build();


        // ==========================================
        // TIPO USUARIO
        // ==========================================

        GraphQLObjectType usuarioType = GraphQLObjectType.newObject().name("Usuario")

                .field(GraphQLFieldDefinition.newFieldDefinition().name("idUsuario")
                        .type(Scalars.GraphQLID))

                .field(GraphQLFieldDefinition.newFieldDefinition().name("idRol")
                        .type(Scalars.GraphQLID))

                .field(GraphQLFieldDefinition.newFieldDefinition().name("nombreUsuario")
                        .type(Scalars.GraphQLString))

                .field(GraphQLFieldDefinition.newFieldDefinition().name("email")
                        .type(Scalars.GraphQLString))

                .field(GraphQLFieldDefinition.newFieldDefinition().name("estado")
                        .type(Scalars.GraphQLString))

                // ==========================================
                // RELACIÓN
                // USUARIO -> ROL
                // ==========================================

                .field(GraphQLFieldDefinition.newFieldDefinition().name("rol").type(rolType)
                        .dataFetcher(environment -> {

                            Usuario usuario = environment.getSource();

                            try {

                                if (usuario.getIdRol() == null) {
                                    return null;
                                }

                                Optional<Rol> rol =
                                        repository.buscarRolPorUsuario(usuario.getIdRol());

                                return rol.orElse(null);

                            } catch (Exception e) {

                                throw new RuntimeException("Error al consultar el rol del usuario",
                                        e);
                            }
                        }))

                .build();


        // ==========================================
        // CONSULTAS GRAPHQL
        // ==========================================

        GraphQLObjectType queryType = GraphQLObjectType.newObject().name("Query")


                // ==========================================
                // CONSULTA 1
                // usuarios
                // ==========================================

                .field(GraphQLFieldDefinition.newFieldDefinition().name("usuarios")
                        .type(GraphQLList.list(usuarioType)).dataFetcher(environment -> {

                            try {

                                return repository.buscarUsuarios();

                            } catch (Exception e) {

                                throw new RuntimeException("Error al consultar usuarios", e);
                            }
                        }))


                // ==========================================
                // CONSULTA 2
                // usuario(id)
                // ==========================================

                .field(GraphQLFieldDefinition.newFieldDefinition().name("usuario").type(usuarioType)

                        .argument(argument -> argument.name("id").type(Scalars.GraphQLID))

                        .dataFetcher(environment -> {

                            Object argumento = environment.getArgument("id");

                            Long idUsuario = convertirALong(argumento);

                            try {

                                Optional<Usuario> usuario =
                                        repository.buscarUsuarioPorId(idUsuario);

                                return usuario.orElse(null);

                            } catch (Exception e) {

                                throw new RuntimeException("Error al consultar usuario", e);
                            }
                        }))

                .build();


        // ==========================================
        // ESQUEMA GRAPHQL
        // ==========================================

        GraphQLSchema schema = GraphQLSchema.newSchema().query(queryType).build();


        // ==========================================
        // INSTANCIA GRAPHQL
        // ==========================================

        return GraphQL.newGraphQL(schema).build();
    }


    // ==========================================
    // CONVERSIÓN SEGURA A LONG
    // ==========================================

    private Long convertirALong(Object valor) {

        if (valor == null) {

            return null;
        }

        if (valor instanceof Long) {

            return (Long) valor;
        }

        if (valor instanceof Number) {

            return ((Number) valor).longValue();
        }

        return Long.valueOf(valor.toString());
    }
}
