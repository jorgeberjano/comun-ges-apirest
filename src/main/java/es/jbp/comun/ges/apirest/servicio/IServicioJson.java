package es.jbp.comun.ges.apirest.servicio;

import es.jbp.comun.ges.entidad.ClavePrimaria;
import es.jbp.comun.ges.entidad.EntidadGes;
import es.jbp.comun.ges.entidad.ConsultaGes;

/**
 * Contratro del servicio para serialización Json
 * @author jberjano
 */
public interface IServicioJson {

    EntidadGes toEntidad(String json, ConsultaGes consulta);

    EntidadGes toEntidad(ClavePrimaria clave, String json, ConsultaGes consulta);

    String toJson(Object objeto, ConsultaGes consulta);

    String toJson(Object objeto);
}
