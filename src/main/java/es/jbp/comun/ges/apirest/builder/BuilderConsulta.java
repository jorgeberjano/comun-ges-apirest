package es.jbp.comun.ges.apirest.builder;

import es.jbp.comun.ges.apirest.servicio.ServicioEntidad;
import es.jbp.comun.ges.dao.EntidadGes;
import es.jbp.comun.ges.entidad.CampoGes;
import es.jbp.comun.ges.filtroyorden.CondicionOrden;
import es.jbp.comun.ges.filtroyorden.CondicionSimpleFiltro;
import es.jbp.comun.ges.filtroyorden.ExpresionFiltro;
import es.jbp.comun.ges.filtroyorden.ExpresionOrden;
import es.jbp.comun.ges.filtroyorden.ExpresionPagina;
import es.jbp.comun.ges.filtroyorden.OperadorFiltro;
import es.jbp.comun.ges.utilidades.ConversionEntidades;
import es.jbp.comun.utiles.sql.PaginaEntidades;

/**
 * Builder para consulta de entidades
 * @author jorge
 */
public class BuilderConsulta {

    private ExpresionFiltro expresionFiltro;
    private ExpresionOrden expresionOrden;
    private ExpresionPagina expresionPagina;
    
    private ServicioEntidad servicio;
    
    public BuilderConsulta(ServicioEntidad servicio) {
        this.servicio = servicio;
    }

    public BuilderConsulta filtrar(String idCampo, OperadorFiltro operadorFiltro, Object valor) {
        if (expresionFiltro == null) {
            expresionFiltro = new ExpresionFiltro(servicio.getConversorValores());
        }
        CampoGes campo = servicio.getConsulta().getCampoPorId(idCampo);
        CondicionSimpleFiltro condicion = new CondicionSimpleFiltro(campo, operadorFiltro, valor);
        expresionFiltro.agregarCondicion(condicion);
        return this;
    }
    
    public BuilderConsulta ordenar(String idCampo, boolean descendente) {
        if (expresionOrden == null) {
            expresionOrden = new ExpresionOrden();            
        }
        CampoGes campo = servicio.getConsulta().getCampoPorId(idCampo);
        CondicionOrden condicion = new CondicionOrden(campo, descendente);
        expresionOrden.agregarCondicion(condicion);
        return this;
    }

    public BuilderConsulta limite(Integer limite) {
        if (expresionPagina == null) {
            expresionPagina = new ExpresionPagina(); 
        }
        expresionPagina.setLimite(limite);
        return this;
    }
    
    public BuilderConsulta pagina(Integer numeroPagina) {
        if (expresionPagina == null) {
            expresionPagina = new ExpresionPagina(); 
        }
        expresionPagina.setLimite(numeroPagina);
        return this;
    }
    
    public BuilderConsulta primero(Integer indice) {
        if (expresionPagina == null) {
            expresionPagina = new ExpresionPagina(); 
        }
        expresionPagina.setPrimero(indice);
        return this;
    }
    
    public BuilderConsulta ultimo(Integer indice) {
        if (expresionPagina == null) {
            expresionPagina = new ExpresionPagina(); 
        }
        expresionPagina.setUltimo(indice);
        return this;
    }
    

    public ExpresionFiltro getExpresionFiltro() {
        return expresionFiltro;
    }

    public ExpresionOrden getExpresionOrden() {
        return expresionOrden;
    }
    
    public ExpresionPagina getExpresionPagina() {
        return expresionPagina;
    }
    
    public <T> PaginaEntidades<T> getPagina(Class<T> clazz) throws Exception {
        PaginaEntidades<EntidadGes> paginaEntidadesGes = servicio.getPaginaEntidades(expresionFiltro, expresionOrden, expresionPagina);
        if (paginaEntidadesGes == null) {
            return null;
        }
        PaginaEntidades<T> pagina = new PaginaEntidades<>();
        for (EntidadGes entidadGes : paginaEntidadesGes.getListaEntidades()) {
            T entidad = clazz.newInstance();
            ConversionEntidades.deEntidadGesAEntidadObjeto(entidadGes, entidad);
            pagina.agregar(entidad);
        }        
        return pagina;
    }   

    public <T> T getPrimero(Class<T> clazz) throws Exception {
        PaginaEntidades<EntidadGes> paginaEntidadesGes = servicio.getPaginaEntidades(expresionFiltro, expresionOrden, expresionPagina);
        if (paginaEntidadesGes == null) {
            return null;
        }
        T entidad = null;
        for (EntidadGes entidadGes : paginaEntidadesGes.getListaEntidades()) {
            entidad = clazz.newInstance();
            ConversionEntidades.deEntidadGesAEntidadObjeto(entidadGes, entidad);
            break;
        }        
        return entidad;
    }
}
