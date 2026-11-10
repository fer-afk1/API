/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.controller;

import java.util.ArrayList;
import java.util.List;
import org.lasalle.connection.ConnectionMysql;
import org.lasalle.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
/**
 *
 * @author ferto
 */
public class UsuarioController {
    
    public List<Usuario> getUsuarios()throws SQLException{
        //Declaramos la estructura de datos
        List<Usuario> lista = new ArrayList<>();
        //Declaramos la sentencia ejecutar
        String sql = "SELECT id_usuario, nombre, primer_apellido, segundo_apellido, "
                   + "fecha_nacimiento, tipo_diabetes, email FROM users;";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        try (PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while(rs.next()){
                Usuario user = new Usuario();
                user.setIdUsuario(rs.getInt("id_usuario"));
                user.setNombre(rs.getString("nombre"));
                user.setPrimerApellido(rs.getString("primer_apellido"));
                user.setSegundoApellido(rs.getString("segundo_apellido"));
                user.setFechaNacimiento(rs.getString("fecha_nacimiento"));
                user.setTipoDiabetes(rs.getString("tipo_diabetes"));
                user.setEmail(rs.getString("email"));
                lista.add(user);
            }
        } finally {
            connMysql.close();
        }
        return lista;
    }

    /**
     * Inserta un usuario en la tabla users.
     * La fecha_registro se llena automaticamente con la fecha actual.
     *
     * @param user datos del usuario a registrar
     * @return id generado para el nuevo usuario
     */
    public int insertUsuario(Usuario user)throws SQLException{
        String sql = "INSERT INTO users (nombre, primer_apellido, segundo_apellido, "
                   + "fecha_nacimiento, tipo_diabetes, fecha_registro, email) "
                   + "VALUES (?, ?, ?, ?, ?, CURDATE(), ?);";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        try (PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstm.setString(1, user.getNombre());
            pstm.setString(2, user.getPrimerApellido());
            setNullableString(pstm, 3, user.getSegundoApellido());
            if (user.getFechaNacimiento() == null || user.getFechaNacimiento().isBlank()) {
                pstm.setNull(4, Types.DATE);
            } else {
                pstm.setDate(4, java.sql.Date.valueOf(user.getFechaNacimiento()));
            }
            setNullableString(pstm, 5, user.getTipoDiabetes());
            setNullableString(pstm, 6, user.getEmail());

            pstm.executeUpdate();
            try (ResultSet keys = pstm.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } finally {
            connMysql.close();
        }
        return -1;
    }

    /**
     * Verifica si existe un usuario, util para validar la llave foranea de medida.
     */
    public boolean existeUsuario(int idUsuario)throws SQLException{
        String sql = "SELECT 1 FROM users WHERE id_usuario = ?;";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, idUsuario);
            try (ResultSet rs = pstm.executeQuery()) {
                return rs.next();
            }
        } finally {
            connMysql.close();
        }
    }

    private void setNullableString(PreparedStatement pstm, int index, String value)throws SQLException{
        if (value == null || value.isBlank()) {
            pstm.setNull(index, Types.VARCHAR);
        } else {
            pstm.setString(index, value);
        }
    }
}
