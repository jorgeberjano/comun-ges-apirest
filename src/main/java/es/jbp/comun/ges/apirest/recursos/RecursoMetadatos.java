package es.jbp.comun.ges.apirest.recursos;

import es.jbp.comun.ges.apirest.servicio.IServicioMetadatos;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para los recursos
 * @author jorge
 */
@RestController
@RequestMapping("/{idioma}")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RecursoMetadatos {
    
    @Autowired
    private HttpServletRequest request;
    
    @Autowired
    @Qualifier("metadatos")
    private IServicioMetadatos servicioMetadatos;        
    
    @GetMapping(value="/entidades", produces={ MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "Obtener nombre de consultas de entidades", description = "Obtiene un array con los nombres de las consultas de entidades disponibles")
    public String obtenerConsultas(
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma) {
        return servicioMetadatos.getIdsConsultas(idioma);
    }
    
    @GetMapping(value="/metadatos", produces={ MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "Obtener metadatos", description = "Obtiene los metadatos de todas las consultas")
    public String obtenerMetadatos(
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma) {
        return servicioMetadatos.getMetadatosConsultasJson(idioma);
    }
    
    @Operation(summary = "Obtener metadatos de una consulta", description = "Obtiene los metadatos de una consulta de entidades indicando su identificador")
    @GetMapping(value="/metadatos/{idConsulta}", produces={ MediaType.APPLICATION_JSON_VALUE })
    public String obtenerMetadatosConsulta(
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta) {
        return servicioMetadatos.getMetadatosConsultaJson(idioma, idConsulta);
    }    
}
