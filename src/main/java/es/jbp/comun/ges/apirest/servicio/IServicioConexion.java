package es.jbp.comun.ges.apirest.servicio;

import es.jbp.comun.utiles.sql.GestorConexiones;

/**
 *
 * @author jorge
 */
public interface IServicioConexion {
    void inicializarConexion(String driverClass, String url, String username, String password);
    
    GestorConexiones getGestorConexiones();
}
