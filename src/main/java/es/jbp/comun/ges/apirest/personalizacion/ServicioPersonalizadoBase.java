package es.jbp.comun.ges.apirest.personalizacion;

import es.jbp.comun.ges.apirest.servicio.OperacionCrud;
import es.jbp.comun.ges.apirest.servicio.ServicioEntidad;
import es.jbp.comun.ges.dao.EntidadGes;

/**
 * Implementación base de un servicio personalizado
 *
 * @author jberjano
 */
public class ServicioPersonalizadoBase implements IServicioPersonalizado {
    protected ServicioEntidad servicioEntidad;
    private String mensajeError;

    @Override
    public String getMensajeError() {
        return mensajeError;
    }

    @Override
    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }
    
    @Override
    public void setServicioEntidad(ServicioEntidad servicio) {
        this.servicioEntidad = servicio;
    }
    
    @Override
    public boolean validar(EntidadGes entidad, OperacionCrud operacion) {
        return validar(entidad);
    }
    
    public boolean validar(EntidadGes entidad) {
        return true;
    }

    @Override
    public EntidadGes preOperacion(EntidadGes entidad, OperacionCrud operacion) {
        switch (operacion) {            
            case INSERCCION:                
            case MODIFICACION:
                return guardando(entidad);
            default:
                return entidad;           
        }
    }

    @Override
    public EntidadGes postOperacion(EntidadGes entidad, OperacionCrud operacion) {        
        switch (operacion) {
            case CONSULTA:
                return consultando(entidad);
                
            case INSERCCION:
                inserccionRealizada(entidad);
                break;
            case MODIFICACION:
                modificionRealizada(entidad);
                break;            
        }
        return entidad;
    }
    
    public EntidadGes consultando(EntidadGes entidad) {
        return entidad;
    }

    @Deprecated
    public EntidadGes guardando(EntidadGes entidad) {
        return entidad;
    }

    public void inserccionRealizada(EntidadGes entidad) {        
    }
    
    public void modificionRealizada(EntidadGes entidad) {
    }
}
