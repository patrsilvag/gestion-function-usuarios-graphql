package com.function.repository;

import com.function.OracleConnection;
import com.function.model.Rol;
import com.function.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {

    /**
     * Obtiene todos los usuarios.
     */
    public List<Usuario> buscarUsuarios() throws SQLException {

        String sql = """
                SELECT
                    ID_USUARIO,
                    ID_ROL,
                    NOMBRE_USUARIO,
                    EMAIL,
                    ESTADO
                FROM USUARIOS
                ORDER BY ID_USUARIO
                """;

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection connection = OracleConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Usuario usuario = new Usuario();

                usuario.setIdUsuario(resultSet.getLong("ID_USUARIO"));

                usuario.setIdRol(resultSet.getLong("ID_ROL"));

                usuario.setNombreUsuario(resultSet.getString("NOMBRE_USUARIO"));

                usuario.setEmail(resultSet.getString("EMAIL"));

                usuario.setEstado(resultSet.getString("ESTADO"));

                usuarios.add(usuario);
            }
        }

        return usuarios;
    }

    /**
     * Obtiene un usuario mediante su identificador.
     */
    public Optional<Usuario> buscarUsuarioPorId(Long idUsuario) throws SQLException {

        String sql = """
                SELECT
                    ID_USUARIO,
                    ID_ROL,
                    NOMBRE_USUARIO,
                    EMAIL,
                    ESTADO
                FROM USUARIOS
                WHERE ID_USUARIO = ?
                """;

        try (Connection connection = OracleConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, idUsuario);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    Usuario usuario = new Usuario();

                    usuario.setIdUsuario(resultSet.getLong("ID_USUARIO"));

                    usuario.setIdRol(resultSet.getLong("ID_ROL"));

                    usuario.setNombreUsuario(resultSet.getString("NOMBRE_USUARIO"));

                    usuario.setEmail(resultSet.getString("EMAIL"));

                    usuario.setEstado(resultSet.getString("ESTADO"));

                    return Optional.of(usuario);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Obtiene el rol asociado a un usuario.
     */
    public Optional<Rol> buscarRolPorUsuario(Long idRol) throws SQLException {

        String sql = """
                SELECT
                    ID_ROL,
                    NOMBRE_ROL,
                    ESTADO
                FROM ROLES
                WHERE ID_ROL = ?
                """;

        try (Connection connection = OracleConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, idRol);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    Rol rol = new Rol();

                    rol.setIdRol(resultSet.getLong("ID_ROL"));

                    rol.setNombreRol(resultSet.getString("NOMBRE_ROL"));

                    rol.setEstado(resultSet.getString("ESTADO"));

                    return Optional.of(rol);
                }
            }
        }

        return Optional.empty();
    }
}
