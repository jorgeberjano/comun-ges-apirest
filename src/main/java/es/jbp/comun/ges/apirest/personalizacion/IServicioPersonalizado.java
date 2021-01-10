package es.jbp.comun.ges.apirest.personalizacion;

import es.jbp.comun.ges.apirest.servicio.OperacionCrud;
import es.jbp.comun.ges.apirest.servicio.ServicioEntidad;
import es.jbp.comun.ges.dao.EntidadGes;

/**
 * Contrato para los manipuladores de entidades.
 * Sirven para personalizar la forma en la que se obtienen las entidades de la
 * base de datos (consulando) y en la que se graban (guardando).
 * @author jberjano
 */
public interface IServicioPersonalizado {
      
    void setServicioEntidad(ServicioEntidad servicio);
//    void setIdioma(String idioma);
    
    boolean validar(EntidadGes entidad, OperacionCrud operacion);
    EntidadGes preOperacion(EntidadGes entidad, OperacionCrud operacion);    
    EntidadGes postOperacion(EntidadGes entidad, OperacionCrud operacion);
    //void inserccionRealizada(EntidadGes entidad);
    //void modificacionRealizada(EntidadGes entidad);
    //void borradoRealizado(EntidadGes entidad);

    
    void setMensajeError(String mensaje);
    String getMensajeError();

    
}
