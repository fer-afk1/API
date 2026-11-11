/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.connection;
import java.sql.Connection;
import java.sql.DriverManager;
/**
 *
 * @author ferto
 */
public class ConnectionMysql {
    
    Connection conn;
    
    public Connection open(){
        // DB_URL, DB_USER y DB_PASSWORD se definen en el entorno (Render).
        // Sin variables definidas se usa el respaldo local (127.0.0.1 / root).
        String url      = env("DB_URL",
                "jdbc:mysql://127.0.0.1:3306/healthBlood"
                + "?allowPublicKeyRetrieval=true&useSSL=false"
                + "&useUnicode=true&characterEncoding=utf-8");
        String user     = env("DB_USER", "root");
        String password = env("DB_PASSWORD", "root");
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static String env(String key, String valorRespaldo){
        String valor = System.getenv(key);
        return (valor == null || valor.isBlank()) ? valorRespaldo : valor;
    }
    
    public void close(){
        if (conn != null){
            try{
                conn.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
