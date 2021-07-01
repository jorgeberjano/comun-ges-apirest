package es.jbp.comun.ges.apirest.exportacion;

import es.jbp.comun.ges.entidad.EntidadGes;
import es.jbp.comun.ges.entidad.CampoGes;
import es.jbp.comun.ges.entidad.ConsultaGes;
import es.jbp.comun.ges.utilidades.ConversionValores;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Genera exportacion en CSV a partir de los datos de sesión de una tabla.
 *
 * @author jberjano
 */
public class ExportadorCsv implements Exportador {

    /**
     * Crea un informe a partir de los datos de sesión de una tabla
     */
    public ExportadorCsv() {
    }

    /**
     * Genera la exportación
     */
    public void generar(OutputStream outputStream, ConsultaGes consulta, List<EntidadGes> listaEntidades) throws Exception {
                
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        
        for (EntidadGes entidad : listaEntidades) {

            boolean primero = true;
            for (CampoGes campo : consulta.getCampos()) {
                if (campo.isOculto()) {
                    continue;
                }
                if (primero) {
                    primero = false;                    
                } else {
                    writer.append(";");
                }
                Object valor = entidad.getValor(campo.getIdCampo());
                String texto = ConversionValores.aValorUI(valor, campo);
                writer.append(texto);                
            }
            writer.append("\n");
        }
        writer.flush();
    }
}
