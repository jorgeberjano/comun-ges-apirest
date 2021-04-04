package es.jbp.comun.ges.apirest.recursos;

import es.jbp.comun.ges.apirest.servicio.IServicioEntidad;
import es.jbp.comun.ges.dao.ClavePrimaria;
import es.jbp.comun.utiles.conversion.Conversion;
import es.jbp.comun.utiles.sql.PaginaEntidades;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/{idioma}/entidades/{idConsulta}")
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "X-Total-Count")
public class RecursoEntidades {

//    @Context
//    private UriInfo context;
    @Autowired
    IServicioEntidad servicio;

    /**
     * Creates a new instance of RecursoEntidad
     */
    public RecursoEntidades() {
    }

    /**
     * Obtiene una lista de entidades.
     */
    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Obtener entidades", description = "Obtiene la lista de entidades de una consulta según los parámetros de filtro, orden y paginación.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exito. La respuesta contiene el JSON con la lista de entidades"),
        @ApiResponse(responseCode = "500", description = "Error interno en el servidor")
    })
    public String obtenerEntidades(
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Parámetros de filtro, orden y paginación")
            @RequestParam Map<String, String> params,
            HttpServletResponse response) {

        servicio.asignarConsulta(idioma, idConsulta);
        PaginaEntidades entidades = servicio.getPaginaEntidades(params);
        String json = servicio.serializar(entidades.getListaEntidades());
        response.addHeader("X-Total-Count", Conversion.toString(entidades.getNumeroTotalEntidades()));
        return json;
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Obtener entidad", description = "Obtiene una entidad por su identificador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exito. La respuesta contiene el JSON con la entidad"),
        @ApiResponse(responseCode = "404", description = "No existe la entidad con ese identificador."),
        @ApiResponse(responseCode = "500", description = "Error interno en el servidor")
    })
    public String obtenerEntidadPorId(
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Identificador de la entidad")
            @PathVariable final String id) {

        servicio.asignarConsulta(idioma, idConsulta);

        return servicio.getEntidadJson(id);
    }

    @GetMapping(value = "/{id1}/{id2}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Obtener entidad", description = "Obtiene una entidad por los valores de su clave primaria doble.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exito. La respuesta contiene el JSON con la entidad"),
        @ApiResponse(responseCode = "404", description = "No existe la entidad con esa clave primaria."),
        @ApiResponse(responseCode = "500", description = "Error interno en el servidor")
    })
    public String obtenerEntidadPorIdDoble(
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Primer valor de la clave primaria")
            @PathVariable final String id1,
            @Parameter(required = true, description = "Segundo valor de la clave primaria")
            @PathVariable final String id2) {

        servicio.asignarConsulta(idioma, idConsulta);

        return servicio.getEntidadJson(servicio.crearClavePrimaria(id1, id2));
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Modificar entidad", description = "Modifica una entidad especificada por su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exito. La respuesta contiene el JSON con los valores de la entidad"),
        @ApiResponse(responseCode = "404", description = "No existe la entidad con ese identificador."),
        @ApiResponse(responseCode = "500", description = "Error interno en el servidor")
    })
    public String modificarEntidadPorId(
            @RequestBody String content,
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Identificador de la entidad")
            @PathVariable final String id) {

        servicio.asignarConsulta(idioma, idConsulta);

        return servicio.modificarEntidadJson(id, content);
    }

    @PutMapping(value = "/{id1}/{id2}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Modificar entidad", description = "Modifica una entidad especificando su clave primaria doble.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exito. La respuesta contiene el JSON con los valores de la entidad"),
        @ApiResponse(responseCode = "404", description = "No existe la entidad con esa clave primaria."),
        @ApiResponse(responseCode = "500", description = "Error interno en el servidor")
    })
    public String modificarEntidadPorIdDoble(
            @RequestBody String content,
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Primer valor de la clave primaria")
            @PathVariable final String id1,
            @Parameter(required = true, description = "Segundo valor de la clave primaria")
            @PathVariable final String id2) {

        servicio.asignarConsulta(idioma, idConsulta);

        return servicio.modificarEntidadJson(servicio.crearClavePrimaria(id1, id2), content);
    }

    @PutMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Modificar entidad", description = "Modifica una entidad especificando valores de sus campos en el cuerpo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exito. La respuesta contiene el JSON con los valores de la entidad"),
        @ApiResponse(responseCode = "500", description = "Error interno en el servidor")
    })
    public String modificarEntidad(
            @Parameter(required = true, description = "Entidad en formato JSON")
            @RequestBody String entidadJson,
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Parámetros que indican los valores de los campos clave")
            @RequestParam Map<String, String> params) {

        servicio.asignarConsulta(idioma, idConsulta);

        ClavePrimaria clavePrimaria = servicio.crearClavePrimaria(params);
        if (clavePrimaria.isEmpty()) {
            return servicio.modificarEntidadJson(entidadJson);
        } else {
            return servicio.modificarEntidadJson(clavePrimaria, entidadJson);
        }
    }

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Insertar entidad", description = "Inserta una entidad")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exito. La respuesta contiene el JSON con los valores de la entidad insertada"),
        @ApiResponse(responseCode = "500", description = "Error interno en el servidor")
    })
    public String insertarEntidad(
            @Parameter(required = true, description = "Entidad en formato JSON")
            @RequestBody String entidadJson,
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta) {

        servicio.asignarConsulta(idioma, idConsulta);

        return servicio.insertarEntidadJson(entidadJson);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Borrar entidad", description = "Borra una entidad especificada por su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exito."),
        @ApiResponse(responseCode = "404", description = "No existe la entidad con ese identificador."),
        @ApiResponse(responseCode = "500", description = "Error interno en el servidor")
    })
    public void borrarEntidadPorId(
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Identificador de la entidad")
            @PathVariable final String id) {

        servicio.asignarConsulta(idioma, idConsulta);

        servicio.borrarEntidadPorId(id);
    }

    @DeleteMapping(value = "/{id1}/{id2}")
    @Operation(summary = "Borrar entidad", description = "Borra una entidad especificada por su clave primaria doble")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exito."),
        @ApiResponse(responseCode = "404", description = "No existe la entidad con esa clave primaria."),
        @ApiResponse(responseCode = "500", description = "Error interno en el servidor")
    })
    public void borrarEntidadPorIdDoble(
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Primer valor de la clave primaria")
            @PathVariable final String id1,
            @Parameter(required = true, description = "Segundo valor de la clave primaria")
            @PathVariable final String id2) {

        servicio.asignarConsulta(idioma, idConsulta);

        servicio.borrarEntidad(servicio.crearClavePrimaria(id1, id2));
    }

    @DeleteMapping(value = "")
    @Operation(summary = "Borrar entidad con clave multiple", description = "Borra una entidad especificando valores de filtro de uno o varios campos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exito."),
        @ApiResponse(responseCode = "500", description = "Error interno en el servidor")
    })
    public void borrarEntidadPorFiltro(
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Parámetros que indican los valores de los campos clave")
            @RequestParam Map<String, String> params) {

        servicio.asignarConsulta(idioma, idConsulta);

        ClavePrimaria clave = servicio.crearClavePrimaria(params);
        servicio.borrarEntidad(clave);
    }

    @Operation(summary = "Exportar entidades", description = "Obtiene un archivo en un formato determinado con los datos de una consulta indicando filtro y orden")
    @GetMapping(value = "/exportacion/{formato}", produces = {MediaType.APPLICATION_PDF_VALUE})
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exito. En el cuerpo de la respuesta va el archivo de exportación"),
        @ApiResponse(responseCode = "500", description = "Error interno en el servidor")
    })
    public void exportar(
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Formato a exportar (pdf, csv, xlsx)")
            @PathVariable final String formato,
            @Parameter(required = true, description = "Parámetros de filtro y orden")
            @RequestParam Map<String, String> params,
            HttpServletResponse response) {

        servicio.asignarConsulta(idioma, idConsulta);

        ServletOutputStream out;
        try {
            response.setContentType("application/" + formato);
            response.setHeader("Content-disposition", "attachment; filename=" + idConsulta + "." + formato);
            out = response.getOutputStream();
        } catch (IOException ex) {
            return;
        }
        servicio.exportar(out, formato, params);
    }

}
