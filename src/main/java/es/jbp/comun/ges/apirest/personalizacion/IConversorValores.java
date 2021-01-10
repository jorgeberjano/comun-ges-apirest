package es.jbp.comun.ges.apirest.personalizacion;

import es.jbp.comun.ges.dao.EntidadGes;
import es.jbp.comun.ges.entidad.CampoGes;

/**
 * Contrato para los conversiores de valores. Sirven para personalizar la forma
 * en la que se convierten los valores de los campos de la base de datos
 * (consulando) y en la que se graban (guardando).
 *
 * @author jberjano
 */
public interface IConversorValores {
    
    Object aValorApi(Object valorBd, CampoGes campo);
    
    Object aValorBd(Object valorApi, CampoGes campo);
    
    void consultando(EntidadGes entidad, CampoGes campo);

    void guardando(EntidadGes entidad, CampoGes campo);

}
