package es.jbp.comun.ges.apirest.servicio;

import es.jbp.comun.ges.apirest.exportacion.Exportador;
import es.jbp.comun.ges.apirest.exportacion.FactoriaExportadores;
import es.jbp.comun.ges.dao.AccesoEntidadesGes;
import es.jbp.comun.ges.dao.ClavePrimaria;
import es.jbp.comun.ges.dao.EntidadGes;
import es.jbp.comun.ges.entidad.CampoGes;
import es.jbp.comun.ges.entidad.ConsultaGes;
import es.jbp.comun.ges.filtroyorden.CondicionOrden;
import es.jbp.comun.ges.filtroyorden.ExpresionFiltro;
import es.jbp.comun.ges.filtroyorden.ExpresionOrden;
import es.jbp.comun.ges.filtroyorden.ExpresionPagina;
import es.jbp.comun.ges.utilidades.GestorSimbolos;
import es.jbp.comun.utiles.conversion.Conversion;
import es.jbp.comun.utiles.sql.GestorConexiones;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import es.jbp.comun.ges.filtroyorden.ConstructorFiltro;
import es.jbp.comun.ges.utilidades.ConversionValores;
import es.jbp.comun.utiles.depuracion.GestorLog;
import es.jbp.comun.utiles.sql.PaginaEntidades;
import java.io.OutputStream;
import org.springframework.context.annotation.Scope;
import es.jbp.comun.ges.apirest.personalizacion.IConversorValores;
import es.jbp.comun.ges.apirest.personalizacion.IServicioPersonalizado;
import es.jbp.comun.ges.apirest.builder.BuilderConsulta;
import es.jbp.comun.utiles.sql.TipoDato;

/**
 * Servicio para acceder a entidades genericas GES.
 *
 * @author Jorge Berjano
 */
@Service
@Scope("prototype")
public class ServicioEntidad implements IServicioEntidad {

    private String idioma;
    private ConsultaGes consulta;
    private AccesoEntidadesGes accesoEntidadesGes;
    private IServicioPersonalizado personalizadorEntidades;
    private IConversorValores conversorValores;
    private ConstructorFiltro constructorFiltro;

    private static final String CONSULTA_NO_EXISTE = "La consulta no existe";
    private static final String ENTIDAD_NO_EXISTE = "La entidad no existe";
    private static final String CLAVE_NO_VALIDA = "La clave primaria no es válida";

    @Autowired
    private IServicioGes servicioGes;

    @Autowired
    private IServicioJson servicioJson;
    
    @Autowired IServicioConexion servicioConexiones;

    @Override
    public void asignarConsulta(String idioma, String idConsulta) {
        this.idioma = idioma;
        consulta = servicioGes.getConsultaPorId(idioma, idConsulta);
        GestorConexiones gestorConexiones = servicioConexiones.getGestorConexiones();
        GestorSimbolos gestorSimbolos = servicioGes.getGestorSimbolos(idioma);
        accesoEntidadesGes = new AccesoEntidadesGes(consulta, gestorConexiones, gestorSimbolos);
        personalizadorEntidades = servicioGes.obtenerServicioPersonalizado(idConsulta);
        personalizadorEntidades.setServicioEntidad(this);
        //personalizadorEntidades.setIdioma(idioma);
        conversorValores = servicioGes.obtenerConversorValores();

        constructorFiltro = new ConstructorFiltro(consulta, conversorValores);
    }

    @Override
    public ConsultaGes getConsulta() {
        return consulta;
    }

    public String getIdioma() {
        return idioma;
    }    

    public IConversorValores getConversorValores() {
        return conversorValores;
    }
    
    

    @Override
    public ClavePrimaria crearClavePrimaria(Map<String, String> mapa) {
        if (consulta == null) {
            return null;
        }
        return new ClavePrimaria(consulta.convertirValores(mapa));
    }

    @Override
    public ClavePrimaria crearClavePrimaria(String... valoresClave) {
        return consulta.construirClavePrimaria(valoresClave);
    }
    
    @Override
    public EntidadGes getEntidad(ClavePrimaria clavePrimaria) {
        if (consulta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CONSULTA_NO_EXISTE);
        }
        if (clavePrimaria == null || clavePrimaria.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CLAVE_NO_VALIDA);
        }
        EntidadGes entidad = accesoEntidadesGes.getEntidad(clavePrimaria);
        if (entidad == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ENTIDAD_NO_EXISTE);
        }

        return entidad;
    }

    @Override
    public String getEntidadJson(ClavePrimaria clavePrimaria) {
        EntidadGes entidad = getEntidad(clavePrimaria);
        return serializar(entidad);
    }
    
    @Override
    public EntidadGes getEntidad(String id) {
        return getEntidad(crearClavePrimaria(id));
    }

    @Override
    public String getEntidadJson(String id) {
        return getEntidadJson(crearClavePrimaria(id));
    }

    @Override
    public PaginaEntidades<EntidadGes> getPaginaEntidades(Map<String, String> parametros) {
        ExpresionFiltro filtro = crearFiltro(parametros);
        ExpresionOrden orden = crearOrden(parametros);
        ExpresionPagina pagina = crearPagina(parametros);
        
        return getPaginaEntidades(filtro, orden, pagina);
    }
       
    @Override
    public PaginaEntidades<EntidadGes> getPaginaEntidades(ExpresionFiltro filtro, ExpresionOrden orden, ExpresionPagina pagina) {
        if (consulta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CONSULTA_NO_EXISTE);
        }
        PaginaEntidades<EntidadGes> entidades = accesoEntidadesGes.getPagina(filtro, orden, pagina);
        if (accesoEntidadesGes.huboError()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, accesoEntidadesGes.getMensajeCompletoError());
        }
        if (entidades == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ENTIDAD_NO_EXISTE);
        }
        return entidades;
    }
    
    @Override
    public List<EntidadGes> getListaEntidades(Map<String, String> parametros) {
        ExpresionFiltro filtro = crearFiltro(parametros);
        ExpresionOrden orden = crearOrden(parametros);
        return getListaEntidades(filtro, orden);
    }
    
    @Override
    public List<EntidadGes> getListaEntidades(ExpresionFiltro filtro, ExpresionOrden orden) {
        if (consulta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CONSULTA_NO_EXISTE);
        }



        List<EntidadGes> entidades = accesoEntidadesGes.getLista(filtro, orden);
        if (accesoEntidadesGes.huboError()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, accesoEntidadesGes.getMensajeCompletoError());
        }
        if (entidades == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ENTIDAD_NO_EXISTE);
        }
        return entidades;
    }

    @Override
    public List<EntidadGes> getListaEntidades() {
        if (consulta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CONSULTA_NO_EXISTE);
        }
        List<EntidadGes> entidades = accesoEntidadesGes.getLista();
        if (entidades == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ENTIDAD_NO_EXISTE);
        }
        return entidades;
    }

    @Override
    public String getEntidadesJson() {
        return serializar(getListaEntidades());
    }

    private String serializar(EntidadGes entidad) {

        entidad = procesarConsultaEntidad(entidad);
        return servicioJson.toJson(entidad, consulta);
    }

    public String serializar(List<EntidadGes> entidades) {
        List<EntidadGes> entidadesManipuladas = entidades.stream().map(entidad -> procesarConsultaEntidad(entidad)).collect(Collectors.toList());
        return servicioJson.toJson(entidadesManipuladas, consulta);
    }

    /**
     * Procesa la consulta de entidades para que actuen los manipuladores.
     *
     * @param entidad
     * @return
     */
    private EntidadGes procesarConsultaEntidad(EntidadGes entidad) {
        if (entidad == null) {
            return null;
        }
        consulta.getCampos().stream().forEach(campo -> procesarConsultaValor(entidad, campo));
        return personalizadorEntidades.postOperacion(entidad, OperacionCrud.CONSULTA);
    }

    /**
     * Procesa el guardado de entidades para que actuen los servicios personalizados.
     *
     * @param entidad
     * @return
     */
    private EntidadGes procesarGuardadoEntidad(EntidadGes entidad, OperacionCrud operacion) {
        consulta.getCampos().stream().forEach(campo -> procesarGuardadoValor(entidad, campo));
        
        boolean ok = personalizadorEntidades.validar(entidad, operacion);
        if (!ok) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, personalizadorEntidades.getMensajeError());
        }
        
        return personalizadorEntidades.preOperacion(entidad, operacion);
    }

    /**
     * Procesa la consulta de un valor de un campo para que actue el manipulador
     *
     * @param entidad
     * @return
     */
    private void procesarConsultaValor(EntidadGes entidad, CampoGes campo) {
        Object valor = entidad.getValor(campo.getIdCampo());
        valor = ConversionValores.aValorAPI(valor, campo);
        entidad.setValor(campo.getIdCampo(), valor);

        if (conversorValores != null) {
            conversorValores.consultando(entidad, campo);
        }
    }

    /**
     * Procesa el guardado de un valor de un campo para convertir el valor y
     * permitir que actue el manipulador
     *
     * @param entidad
     * @return
     */
    private void procesarGuardadoValor(EntidadGes entidad, CampoGes campo) {
        if (!entidad.contiene(campo.getIdCampo())) {
            return;
        }
        Object valor = entidad.getValor(campo.getIdCampo());

        if (valor == null) {
            String valorNulo = campo.getValorNulo();
            if (!Conversion.isBlank(valorNulo)) {
                valor = Conversion.convertirValor(valorNulo, campo.getTipoDato());
            }
        }
        valor = ConversionValores.aValorBD(valor, campo);
        entidad.setValor(campo.getIdCampo(), valor);

        if (conversorValores != null) {
            conversorValores.guardando(entidad, campo);
        }
    }
    @Override
    public EntidadGes insertarEntidad(EntidadGes entidad) {
        entidad = procesarGuardadoEntidad(entidad, OperacionCrud.INSERCCION);
        comprobarEntidad(entidad);

        boolean ok = accesoEntidadesGes.insertar(entidad);
        if (!ok) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, accesoEntidadesGes.getMensajeCompletoError());
        }
        
        // Se recupera para que se devuelvan tambien los campos relacionados
        entidad = accesoEntidadesGes.getEntidad(entidad.getClavePrimaria());

        personalizadorEntidades.postOperacion(entidad, OperacionCrud.INSERCCION);
        
        return entidad;
    }

    @Override
    public String insertarEntidadJson(String json) {
        if (consulta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CONSULTA_NO_EXISTE);
        }

        EntidadGes entidad = servicioJson.toEntidad(json, consulta);
        
        entidad = insertarEntidad(entidad);

        return serializar(entidad);
    }
    
    @Override
    public EntidadGes modificarEntidad(EntidadGes entidad) {
        entidad = procesarGuardadoEntidad(entidad, OperacionCrud.MODIFICACION);
        comprobarEntidad(entidad);

        boolean ok = accesoEntidadesGes.modificar(entidad);        
        if (accesoEntidadesGes.huboError()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, accesoEntidadesGes.getMensajeCompletoError());
        } else if (!ok) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ENTIDAD_NO_EXISTE);
        }

        
        // Se recupera para que se devuelvan tambien los campos relacionados
        entidad = accesoEntidadesGes.getEntidad(entidad.getClavePrimaria());
        
        personalizadorEntidades.postOperacion(entidad, OperacionCrud.MODIFICACION);
        
        return entidad;
    }

    @Override
    public String modificarEntidadJson(String json) {
        if (consulta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CONSULTA_NO_EXISTE);
        }

        EntidadGes entidad = servicioJson.toEntidad(json, consulta);
        
        entidad = modificarEntidad(entidad);

        return serializar(entidad);
    }
    
    @Override
    public String modificarEntidadJson(ClavePrimaria clave, String json) {
        if (consulta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CONSULTA_NO_EXISTE);
        }

        EntidadGes entidad = servicioJson.toEntidad(clave, json, consulta);
        
        entidad = modificarEntidad(entidad);

        return serializar(entidad);
    }

    @Override
    public String modificarEntidadJson(String id, String json) {
        if (consulta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CONSULTA_NO_EXISTE);
        }
        ClavePrimaria clave = consulta.construirClavePrimariaDeId(id);
        return modificarEntidadJson(clave, json);
    }

    @Override
    public void borrarEntidad(ClavePrimaria clave) {
        if (consulta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CONSULTA_NO_EXISTE);
        }
        
        EntidadGes entidad = new EntidadGes();
        entidad.setClavePrimaria(clave);
        personalizadorEntidades.preOperacion(entidad, OperacionCrud.BORRADO);
        
        boolean ok = accesoEntidadesGes.borrar(clave);
        if (accesoEntidadesGes.huboError()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, accesoEntidadesGes.getMensajeCompletoError());
        } else if (!ok) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ENTIDAD_NO_EXISTE);
        }        
        personalizadorEntidades.postOperacion(entidad, OperacionCrud.BORRADO);
    }

    @Override
    public void borrarEntidadPorId(String id) {
        if (consulta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CONSULTA_NO_EXISTE);
        }
        ClavePrimaria clave = consulta.construirClavePrimariaDeId(id);
        if (clave == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CLAVE_NO_VALIDA);
        }
        borrarEntidad(clave);
    }
    
    public BuilderConsulta builder() {        
        return new BuilderConsulta(this);//consulta, conversorValores);
    }

    public ExpresionFiltro crearFiltro(Map<String, String> parametros) {
        ExpresionFiltro filtro = constructorFiltro.crearFiltro(parametros);
        if (constructorFiltro.huboError()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, constructorFiltro.getMensajeError());
        }
        return filtro;
    }

    public ExpresionOrden crearOrden(Map<String, String> parametros) {
        ExpresionOrden expresion = new ExpresionOrden();
        if (parametros == null) {
            return expresion;
        }
        String sort = parametros.get("_sort");
        String order = parametros.get("_order");

        List<String> listaSort = Conversion.convertirTextoEnLista(sort);
        List<String> listaOrder = Conversion.convertirTextoEnLista(order);
        
        List<String> listaCamposInexistentes = listaSort.stream()
                .filter(idCampo -> consulta.getCampoPorId(idCampo) == null)
                .collect(Collectors.toList());
        
        if (!listaCamposInexistentes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se puede ordenar por " + Conversion.convertirListaEnTexto(listaCamposInexistentes, ", "));
        }

        List<CampoGes> campos = listaSort.stream()
                .map(e -> consulta.getCampoPorId(e))
                .filter(c -> c != null)
                .collect(Collectors.toList());
        List<Boolean> descendentes = listaOrder.stream()
                .map(e -> "desc".equals(e.toLowerCase()))
                .collect(Collectors.toList());
        
        for (int i = 0; i < campos.size(); i++) {
            CampoGes campo = campos.get(i);
            boolean descendente = false;
            if (i < descendentes.size()) {
                descendente = Boolean.TRUE.equals(descendentes.get(i));
            }
            expresion.agregarCondicion(new CondicionOrden(campo, descendente));
        }
        return expresion;
    }

    public ExpresionPagina crearPagina(Map<String, String> parametros) {
        ExpresionPagina pagina = new ExpresionPagina();
        if (parametros == null) {
            return pagina;
        }
        pagina.setPagina(Conversion.toInteger(parametros.get("_page")));
        pagina.setLimite(Conversion.toInteger(parametros.get("_limit")));
        pagina.setPrimero(Conversion.toInteger(parametros.get("_start")));
        pagina.setUltimo(Conversion.toInteger(parametros.get("_end")));
        return pagina;
    }

    private void comprobarEntidad(EntidadGes entidad) {
        if (entidad == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ENTIDAD_NO_EXISTE);
        }
        for (CampoGes campo : consulta.getCampos()) {
            Object valor = entidad.getValor(campo.getIdCampo());
            comprobarRequerido(campo, valor);
            comprobarCadena(campo, valor);
        }
    }

    private void comprobarRequerido(CampoGes campo, Object valor) {
        if (campo.isRequerido() && valor == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo " + campo.getTitulo() + " debe tener un valor");
        }
    }

    private void comprobarCadena(CampoGes campo, Object valor) {
        if (campo.getTipoDato() != TipoDato.CADENA) {
            return;
        }
        String valorString = Conversion.toString(valor);
        boolean vacia = Conversion.isBlank(valorString);
        if (campo.isRequerido() && vacia) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El valor del campo " + campo.getTitulo() + " no puede estar vacío");
        } else if (!vacia && campo.getTamano() > 0 && valorString.length() > campo.getTamano()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El valor del campo " + campo.getTitulo() + " excede su tamaño máximio de " + campo.getTamano() + " caracteres");
        }
    }

    @Override
    public void exportar(OutputStream out, String formato, Map<String, String> parametros) {
        Exportador exportador = FactoriaExportadores.crearExportador(formato);
        if (exportador == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "En formato de exportación " + formato + " no está soportado");
        }

        List<EntidadGes> listaEntidades = getListaEntidades(parametros);

        try {
            exportador.generar(out, consulta, listaEntidades);
        } catch (Exception ex) {
            String mensajeError = "No se ha podido exportar a " + formato;
            GestorLog.error(mensajeError, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, mensajeError);
        }
    }

}
