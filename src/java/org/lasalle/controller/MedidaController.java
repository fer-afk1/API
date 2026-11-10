/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.controller;

import java.util.ArrayList;
import java.util.List;
import org.lasalle.connection.ConnectionMysql;
import org.lasalle.model.Medida;
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
public class MedidaController {
    public List<Medida> getMedidas()throws SQLException{
        //Declaramos la estructura de datos
        List<Medida> lista = new ArrayList<>();
        //Declaramos la sentencia ejecutar
        String sql = "SELECT id_lectura, id_usuario, valor_mg_dl, tipo_lectura, "
                   + "notas, fecha_registro FROM lectura_glucosa;";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        try (PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while(rs.next()){
                Medida medida = new Medida();
                medida.setIdMedida(rs.getInt("id_lectura"));
                medida.setIdUsuario(rs.getInt("id_usuario"));
                medida.setGlucosa(rs.getInt("valor_mg_dl"));
                medida.setFechaHora(rs.getString("fecha_registro"));
                medida.setContexto(rs.getString("tipo_lectura"));
                medida.setNotas(rs.getString("notas"));
                lista.add(medida);
            }
        } finally {
            connMysql.close();
        }
        return lista;
    }

    /**
     * Inserta una medida de glucosa en la tabla lectura_glucosa.
     * Si no se envia fecha, se guarda la fecha actual.
     *
     * @param medida datos de la lectura a registrar
     * @return id generado para la nueva medida
     */
    public int insertMedida(Medida medida)throws SQLException{
        boolean conFecha = medida.getFechaHora() != null && !medida.getFechaHora().isBlank();
        String sql = "INSERT INTO lectura_glucosa "
                   + "(id_usuario, valor_mg_dl, tipo_lectura, notas, fecha_registro) "
                   + "VALUES (?, ?, ?, ?, " + (conFecha ? "?" : "CURDATE()") + ");";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        try (PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstm.setInt(1, medida.getIdUsuario());
            pstm.setInt(2, medida.getGlucosa());
            if (medida.getContexto() == null || medida.getContexto().isBlank()) {
                pstm.setNull(3, Types.VARCHAR);
            } else {
                pstm.setString(3, medida.getContexto());
            }
            if (medida.getNotas() == null || medida.getNotas().isBlank()) {
                pstm.setNull(4, Types.LONGVARCHAR);
            } else {
                pstm.setString(4, medida.getNotas());
            }
            if (conFecha) {
                pstm.setDate(5, java.sql.Date.valueOf(medida.getFechaHora()));
            }

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
}
