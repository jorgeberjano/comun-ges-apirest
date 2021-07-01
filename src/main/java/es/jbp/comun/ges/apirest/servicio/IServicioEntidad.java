package es.jbp.comun.ges.apirest.servicio;

import es.jbp.comun.ges.entidad.ClavePrimaria;
import es.jbp.comun.ges.entidad.EntidadGes;
import es.jbp.comun.ges.entidad.ConsultaGes;
import es.jbp.comun.ges.apirest.builder.BuilderConsulta;
import es.jbp.comun.ges.filtroyorden.ExpresionFiltro;
import es.jbp.comun.ges.filtroyorden.ExpresionOrden;
import es.jbp.comun.ges.filtroyorden.ExpresionPagina;
import es.jbp.comun.utiles.sql.PaginaEntidades;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Contratro del servicio para manipulación de entidades.
 *
 * @author jberjano
 */
public interface IServicioEntidad {

    void asignarConsulta(String idioma, String nombre);

    ConsultaGes getConsulta();
    
    String getIdioma();

    ClavePrimaria crearClavePrimaria(Map<String, String> mapa);

    ClavePrimaria crearClavePrimaria(String... valoresClave);

    EntidadGes getEntidad(ClavePrimaria clavePrimaria);
    
    String getEntidadJson(ClavePrimaria clavePrimaria);

    EntidadGes getEntidad(String id);
    
    String getEntidadJson(String id);

    PaginaEntidades<EntidadGes> getPaginaEntidades(Map<String, String> parametros);
    
    PaginaEntidades<EntidadGes> getPaginaEntidades(ExpresionFiltro filtro, ExpresionOrden orden, ExpresionPagina pagina);
    
    List<EntidadGes> getListaEntidades(Map<String, String> parametros);
    
    List<EntidadGes> getListaEntidades(ExpresionFiltro filtro, ExpresionOrden orden);

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

    public BuilderConsulta builder();
}
