package es.jbp.comun.ges.apirest.servicio;

import es.jbp.comun.ges.entidad.ConsultaGes;
import es.jbp.comun.ges.utilidades.GestorSimbolos;
import es.jbp.comun.utiles.sql.GestorConexiones;
import java.util.List;
import java.util.Map;
import es.jbp.comun.ges.apirest.personalizacion.IConversorValores;
import es.jbp.comun.ges.apirest.personalizacion.IServicioPersonalizado;

/**
 * Contratro del servicio que proporciona la funcionalidad GES.
 * @author jberjano
 */
public interface IServicioGes {
    
    void inicializarConexion(String driverClass, String url, String username, String password);
    
    void crearGestor(String idioma, String archivoGes, Map<String, Object> simbolos);

    ConsultaGes getConsultaPorId(String idioma, String idConsulta);

    List<ConsultaGes> getConsultas(String idioma);

    GestorConexiones getGestorConexiones();

    GestorSimbolos getGestorSimbolos(String idioma);    
    
    void definirSimbolo(String idioma, String nombre, String valor);

    void registrarServicioPersonalizado(String idConsulta, IServicioPersonalizado servicio);
    
    void registrarConversorValores(IConversorValores manipulador);
    
    IServicioPersonalizado obtenerServicioPersonalizado(String idConsulta);    

    IConversorValores obtenerConversorValores();

    
}
