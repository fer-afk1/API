/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.lasalle.controller.UsuarioController;
import org.lasalle.controller.MedidaController;
import org.lasalle.model.Usuario;
import org.lasalle.model.Medida;

/**
 *
 * @author ferto
 */
@Path("glucosa")
public class RestGlucosa {

    private static final Gson GSON = new Gson();
    private static final String EMAIL_REGEX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
    private static final String FECHA_REGEX = "^\\d{4}-\\d{2}-\\d{2}$";

    @Path("saludar")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludar(){
        String saludo = """
                        {"Saludo":"Hola desde java"}
                        """;
        return Response.ok(saludo).build();
    }
    
    @Path("saludarCumpleanios")
    @GET 
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludarCumpleanios(
            @QueryParam("nombre") String nombre
    ){
        String out = """
                     {"response": "Feliz cumple %s"}
                     """;
        out = String.format(out,nombre);
        return Response.ok(out).build();
    }
    @Path("getAllUsuarios")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllusuarios(){
        UsuarioController uc = new UsuarioController();
        List<Usuario> lista = new ArrayList();
        Gson gson = new Gson();
        String out = "";
        try{
            lista = uc.getUsuarios();
            out = gson.toJson(lista);
        } catch (Exception e) {
            e.printStackTrace();
            out = """
                  {"result":"Empty table"}
                  """;
        }
        return Response.ok(out).build();
    }
    @Path("getAllMedidas")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllmedidas(){
        MedidaController uc = new MedidaController();
        List<Medida> lista = new ArrayList();
        Gson gson = new Gson();
        String out = "";
        try{
            lista = uc.getMedidas();
            out = gson.toJson(lista);
        } catch (Exception e) {
            e.printStackTrace();
            out = """
                  {"result":"Empty table"}
                  """;
        }
        return Response.ok(out).build();
    }

    /**
     * Registra un usuario.
     * Ruta: POST /api/glucosa/registrarUsuario
     * Content-Type: application/x-www-form-urlencoded
     *
     * Parametros:
     *  - nombre           (obligatorio, max 45)
     *  - primerApellido   (obligatorio, max 45)
     *  - segundoApellido  (opcional, max 45)
     *  - email            (obligatorio, max 50)
     *  - fechaNacimiento  (opcional, formato yyyy-MM-dd)
     *  - tipoDiabetes     (opcional, max 30)
     */
    @Path("registrarUsuario")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarUsuario(
            @FormParam("nombre") String nombre,
            @FormParam("primerApellido") String primerApellido,
            @FormParam("segundoApellido") String segundoApellido,
            @FormParam("email") String email,
            @FormParam("fechaNacimiento") String fechaNacimiento,
            @FormParam("tipoDiabetes") String tipoDiabetes
    ){
        nombre = limpiar(nombre);
        primerApellido = limpiar(primerApellido);
        segundoApellido = limpiar(segundoApellido);
        email = limpiar(email);
        fechaNacimiento = limpiar(fechaNacimiento);
        tipoDiabetes = limpiar(tipoDiabetes);

        if (nombre == null) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'nombre' es obligatorio");
        }
        if (nombre.length() > 45) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'nombre' no puede superar 45 caracteres");
        }
        if (primerApellido == null) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'primerApellido' es obligatorio");
        }
        if (primerApellido.length() > 45) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'primerApellido' no puede superar 45 caracteres");
        }
        if (segundoApellido != null && segundoApellido.length() > 45) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'segundoApellido' no puede superar 45 caracteres");
        }
        if (email == null) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'email' es obligatorio");
        }
        if (email.length() > 50) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'email' no puede superar 50 caracteres");
        }
        if (!email.matches(EMAIL_REGEX)) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'email' no tiene un formato valido");
        }
        if (fechaNacimiento != null && !fechaValida(fechaNacimiento)) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'fechaNacimiento' debe tener formato yyyy-MM-dd");
        }
        if (tipoDiabetes != null && tipoDiabetes.length() > 30) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'tipoDiabetes' no puede superar 30 caracteres");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setPrimerApellido(primerApellido);
        usuario.setSegundoApellido(segundoApellido);
        usuario.setEmail(email);
        usuario.setFechaNacimiento(fechaNacimiento);
        usuario.setTipoDiabetes(tipoDiabetes);

        try {
            UsuarioController uc = new UsuarioController();
            int idUsuario = uc.insertUsuario(usuario);
            JsonObject json = new JsonObject();
            json.addProperty("result", "ok");
            json.addProperty("mensaje", "Usuario registrado correctamente");
            json.addProperty("idUsuario", idUsuario);
            return Response.status(Response.Status.CREATED)
                    .entity(GSON.toJson(json))
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
            return error(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al registrar el usuario: " + e.getMessage());
        }
    }

    /**
     * Registra una medida de glucosa de un usuario existente.
     * Ruta: POST /api/glucosa/registrarMedida
     * Content-Type: application/x-www-form-urlencoded
     *
     * Parametros:
     *  - idUsuario  (obligatorio, debe existir en users)
     *  - glucosa    (obligatorio, mg/dL, entre 1 y 1000)
     *  - contexto   (opcional, max 45. Ej: AYUNAS, ANTES DE DORMIR, DESPUES DE ALIMENTOS)
     *  - notas      (opcional)
     *  - fechaHora  (opcional, formato yyyy-MM-dd. Si no se envia se usa la fecha actual)
     */
    @Path("registrarMedida")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarMedida(
            @FormParam("idUsuario") String idUsuario,
            @FormParam("glucosa") String glucosa,
            @FormParam("contexto") String contexto,
            @FormParam("notas") String notas,
            @FormParam("fechaHora") String fechaHora
    ){
        idUsuario = limpiar(idUsuario);
        glucosa = limpiar(glucosa);
        contexto = limpiar(contexto);
        notas = limpiar(notas);
        fechaHora = limpiar(fechaHora);

        if (idUsuario == null) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'idUsuario' es obligatorio");
        }
        int idUsuarioInt;
        try {
            idUsuarioInt = Integer.parseInt(idUsuario);
        } catch (NumberFormatException e) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'idUsuario' debe ser un numero entero");
        }
        if (idUsuarioInt <= 0) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'idUsuario' debe ser mayor a 0");
        }

        if (glucosa == null) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'glucosa' es obligatorio");
        }
        int glucosaInt;
        try {
            glucosaInt = Integer.parseInt(glucosa);
        } catch (NumberFormatException e) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'glucosa' debe ser un numero entero (mg/dL)");
        }
        if (glucosaInt < 1 || glucosaInt > 1000) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'glucosa' debe estar entre 1 y 1000 mg/dL");
        }
        if (contexto != null && contexto.length() > 45) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'contexto' no puede superar 45 caracteres");
        }
        if (fechaHora != null && !fechaValida(fechaHora)) {
            return error(Response.Status.BAD_REQUEST, "El parametro 'fechaHora' debe tener formato yyyy-MM-dd");
        }

        try {
            UsuarioController uc = new UsuarioController();
            if (!uc.existeUsuario(idUsuarioInt)) {
                return error(Response.Status.NOT_FOUND,
                        "No existe un usuario con idUsuario = " + idUsuarioInt);
            }

            Medida medida = new Medida();
            medida.setIdUsuario(idUsuarioInt);
            medida.setGlucosa(glucosaInt);
            medida.setContexto(contexto);
            medida.setNotas(notas);
            medida.setFechaHora(fechaHora);

            MedidaController mc = new MedidaController();
            int idMedida = mc.insertMedida(medida);

            JsonObject json = new JsonObject();
            json.addProperty("result", "ok");
            json.addProperty("mensaje", "Medida de glucosa registrada correctamente");
            json.addProperty("idMedida", idMedida);
            json.addProperty("idUsuario", idUsuarioInt);
            return Response.status(Response.Status.CREATED)
                    .entity(GSON.toJson(json))
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
            return error(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al registrar la medida: " + e.getMessage());
        }
    }

    private static Response error(Response.Status status, String mensaje){
        JsonObject json = new JsonObject();
        json.addProperty("result", "error");
        json.addProperty("mensaje", mensaje);
        return Response.status(status)
                .entity(GSON.toJson(json))
                .build();
    }

    private static String limpiar(String valor){
        if (valor == null) {
            return null;
        }
        String limpio = valor.trim();
        return limpio.isEmpty() ? null : limpio;
    }

    private static boolean fechaValida(String fecha){
        if (!fecha.matches(FECHA_REGEX)) {
            return false;
        }
        try {
            java.sql.Date.valueOf(fecha);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
