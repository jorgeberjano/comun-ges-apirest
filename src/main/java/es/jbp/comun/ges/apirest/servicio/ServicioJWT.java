package es.jbp.comun.ges.apirest.servicio;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 *
 * @author jberjano
 */
@Service
public class ServicioJWT implements IServicioJWT {

    private boolean activo;
    private String secret = "palabra secreta";

    public String getSecret() {
        return secret;
    }

    @Override
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    @Override
    public boolean isActivo() {
        return this.activo;
    }

    @Override
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * Crea un token JWT
     */
    @Override
    public String crearJWT(String issuer, String subject, long duracionMilisegundos) {

        SignatureAlgorithm algoritmo = SignatureAlgorithm.HS256;

        long millisegundosAhora = System.currentTimeMillis();

        // Se firma el JWT con la palabra secreta
        Key claveSecreta = new SecretKeySpec(secret.getBytes(), algoritmo.getJcaName());

        String id = UUID.randomUUID().toString();
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(new Date(millisegundosAhora))
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(algoritmo, claveSecreta);

        // Tiempo de expiracion
        long milisegundosExpiracion = duracionMilisegundos > 0 ? millisegundosAhora + duracionMilisegundos : Long.MAX_VALUE;
        Date expiracion = new Date(milisegundosExpiracion);
        builder.setExpiration(expiracion);

        return builder.compact();
    }

    /**
     * Valida un token JWT
     */
    @Override
    public Map<String, Object> parsearJWT(String jwt) {
        
        if (!activo) {
            return null;
        }
        
        Claims claims = Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(jwt).getBody();

        return claims;
    }

}
