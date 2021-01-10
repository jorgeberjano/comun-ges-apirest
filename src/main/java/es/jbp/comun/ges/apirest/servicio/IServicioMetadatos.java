package es.jbp.comun.ges.apirest.servicio;

/**
 *
 * @author jorge
 */
public interface IServicioMetadatos {

    public String getMetadatosConsultasJson(String idioma);

    public String getMetadatosConsultaJson(String idioma, String idConsulta);
    
    public String getIdsConsultas(String idioma);
}
