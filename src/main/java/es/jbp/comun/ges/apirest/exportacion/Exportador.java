package es.jbp.comun.ges.apirest.exportacion;

import es.jbp.comun.ges.dao.EntidadGes;
import es.jbp.comun.ges.entidad.ConsultaGes;
import java.io.OutputStream;
import java.util.List;

/**
 * Exportador
 * @author jorge
 */
public interface Exportador {
    void generar(OutputStream outputStream, ConsultaGes consulta, List<EntidadGes> listaEntidades) throws Exception;
}
