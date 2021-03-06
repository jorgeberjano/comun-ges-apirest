package es.jbp.comun.ges.filtroyorden;

import es.jbp.comun.ges.dao.Filtro;
import es.jbp.comun.ges.entidad.ConsultaGes;
import es.jbp.comun.utiles.sql.compatibilidad.FormateadorSql;
import es.jbp.comun.ges.apirest.personalizacion.IConversorValores;

/**
 * Expresión de un filtro mediante una lista de condiciones que se evaluarán 
 * mediante un operador logico OR
 * @author jberjano
 */
public class ExpresionFiltro implements Filtro {

    private CondicionCompuestaFiltro condiciones = new CondicionCompuestaFiltro("AND");
    private IConversorValores conversorValores;
    
    public ExpresionFiltro(IConversorValores manipuladorValores) {
        this.conversorValores = manipuladorValores;
    }

    @Override
    public String generarSql(FormateadorSql formateadorSql, ConsultaGes consulta) {
        return condiciones.generarSql(formateadorSql, consulta, conversorValores);
    }

    public void agregarCondicion(CondicionFiltro condicion) {
        condiciones.agregarCondicion(condicion);
    }

    @Override
    public String getDescripcion() {
        return "...";
    }

    @Override
    public String getMensajeError() {
        return condiciones.getMensajeError();
    }
}
