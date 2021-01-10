package es.jbp.comun.ges.apirest.servicio;

import java.util.Map;

/**
 * Contratro del servicio para manejo de tokens JWT
 *
 * @author jberjano
 */
public interface IServicioJWT {
    
    static final String TOKEN_HEADER_PARAMETER = "Authorization";

    void setActivo(boolean activo);
    
    boolean isActivo();
    
    void setSecret(String secret);

    String crearJWT(String issuer, String subject, long ttlMillis);

    Map<String, Object> parsearJWT(String jwt);

}
