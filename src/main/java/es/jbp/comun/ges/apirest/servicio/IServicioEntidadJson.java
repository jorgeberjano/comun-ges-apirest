package es.jbp.comun.ges.apirest.servicio;

import es.jbp.comun.ges.dao.ClavePrimaria;
import es.jbp.comun.ges.dao.EntidadGes;
import es.jbp.comun.ges.entidad.ConsultaGes;
import es.jbp.comun.utiles.sql.PaginaEntidades;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Contratro del servicio para manipulación de entidades.
 *
 * @author jberjano
 */
public interface IServicioEntidadJson {

    void asignarConsulta(String idioma, String nombre);

    ConsultaGes getConsulta();

    ClavePrimaria crearClavePrimaria(Map<String, String> mapa);

    ClavePrimaria crearClavePrimaria(String... valoresClave);

    String getEntidadJson(ClavePrimaria clavePrimaria);

    String getEntidadJson(String id);

    PaginaEntidades<EntidadGes> getPaginaEntidades(Map<String, String> parametros);

    public List<EntidadGes> getListaEntidades(Map<String, String> parametros);

    List<EntidadGes> getListaEntidades();

    String getEntidadesJson();

    EntidadGes insertarEntidad(EntidadGes entidad);

    String insertarEntidadJson(String json);

    EntidadGes modificarEntidad(EntidadGes entidad);
    
    String modificarEntidadJson(String json);
    
    String modificarEntidadJson(ClavePrimaria clave, String json);

    String modificarEntidadJson(String id, String json);

    void borrarEntidad(ClavePrimaria clave);

    void borrarEntidadPorId(String id);

    String serializar(List<EntidadGes> entidades);

    void exportar(OutputStream out, String formato, Map<String, String> params);
}
