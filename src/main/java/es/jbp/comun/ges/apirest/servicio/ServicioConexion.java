package es.jbp.comun.ges.apirest.servicio;

import es.jbp.comun.utiles.depuracion.GestorLog;
import es.jbp.comun.utiles.sql.GestorConexiones;
import es.jbp.comun.utiles.sql.PoolConexiones;
import org.springframework.stereotype.Service;

/**
 * Servicio que proporciona las conexiones a la BD
 * @author jorge
 */
@Service
public class ServicioConexion implements IServicioConexion {
    
    private PoolConexiones gestorConexiones;
    
    @Override
    public void inicializarConexion(String driverClass, String url, String username, String password) {    
        
        gestorConexiones = new PoolConexiones(driverClass, url, username, password, true);

        try {
            gestorConexiones.inicializar();
        } catch (ClassNotFoundException ex) {
            GestorLog.error("No se ha podido inicializar el gestor de conexiones", ex);
        }
    }
    
    @Override
    public GestorConexiones getGestorConexiones() {
        return gestorConexiones;
    }
    
}
