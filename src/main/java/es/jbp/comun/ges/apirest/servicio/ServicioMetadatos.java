package es.jbp.comun.ges.apirest.servicio;

import es.jbp.comun.ges.entidad.ConsultaGes;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author jorge
 */
@Service(value = "metadatos")
public class ServicioMetadatos implements IServicioMetadatos {

    @Autowired
    private ServicioGes servicioGes;
    
    @Autowired
    private IServicioJson servicioJson;
    
    @Override
    public String getMetadatosConsultasJson(String idioma) {
        List<ConsultaGes> consultas = servicioGes.getConsultas(idioma);
        if (consultas == null || consultas.size() == 0) {         
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se han definido consultas");
        }
        
        return servicioJson.toJson(consultas);
    }

    @Override
    public String getMetadatosConsultaJson(String idioma, String idConsulta) {
        ConsultaGes consulta = servicioGes.getConsultaPorId(idioma, idConsulta);
        if (consulta == null) {         
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la consulta " + idConsulta);
        }
        
        return servicioJson.toJson(consulta);
    }

    @Override
    public String getIdsConsultas(String idioma) {
        return servicioJson.toJson(servicioGes.getIdsConsultas(idioma), null);
    }
    
    
    
}
