package Prodandes.fachada;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import Prodandes.dao.consultaDAO;
import Prodandes.vod.Cliente;
import Prodandes.vod.ComponentesProduccion;
import Prodandes.vod.EtapasDeProducion;
import Prodandes.vod.MateriasPrimas;
import Prodandes.vod.PedidoMaterial;
import Prodandes.vod.Producto;
import Prodandes.vod.Usuario;


public class Prodandes 
{
	/**
	 * Conexi�n con la clase que maneja la base de datos
	 */
	private consultaDAO dao;
	
    /**
     * Conexi�n a la base de datos
     */
    private Connection conexion;
	
	private ArrayList<Cliente> clientes;
	private ArrayList<Usuario> usuarios;
	
    // -----------------------------------------------------------------
    // Singleton
    // -----------------------------------------------------------------

    /**
     * Instancia �nica de la clase
     */
    private static Prodandes instancia;
    
    /**
     * Devuelve la instancia �nica de la clase
     * @return Instancia �nica de la clase
     */
    public static Prodandes darInstancia( )
    {
        if( instancia == null )
        {
            instancia = new Prodandes( );
        }
        return instancia;
    }
	
	/**
	 * contructor de la clase. Inicializa el atributo dao.
	 */
	private Prodandes()
	{
		dao = new consultaDAO();
		clientes = new ArrayList<Cliente>();
		usuarios= new ArrayList<Usuario>();
		dao.inicializarMensajes();
	}
	
	/**
	 * inicializa el dao, d�ndole la ruta en donde debe encontrar
	 * el archivo properties.
	 * @param ruta ruta donde se encuentra el archivo properties
	 */
	public void inicializarRuta(String ruta)
	{
		dao.inicializar();
	}
	
	public Cliente darCliente(int id)
	{
		for (int i = 0; i < clientes.size(); i++) 
		{
		if (clientes.get(i).getIdentificacion() == id)
		{
			return clientes.get(i);
		}
		}
		return null;
	}
	
	
	public void agregaCliente(Cliente z)
	{
	clientes.add(z);
dao.agregarCliente(z);
	}
	

	
	public String estaEnCapacidad(String nombre, int volumen, Date fecha)
	{
		//Existe el producto
		Producto x = dao.buscarProducto(nombre);
		String rta = "";
		//Hay en el inventario
		if ( x != null && x.getCantidad() >= volumen&& x.darEstado()== true)
		{
			int cantidad = x.getCantidad();
			x.setCantidad(cantidad - volumen);
			dao.actualizarProducto(x);
			rta = "Se despachara el producto protamente";
		}
		//hay que hacerlo
		else if (x != null)
		{
			rta = "Se necesitan: ";
			// se los materiales necesitados
			ArrayList<MateriasPrimas> mat = x.darMaterialesrequeridos();
			for (int i = 0; i < mat.size(); i++)
			{
				MateriasPrimas z = dao.darMaterial(mat.get(i).darNombre(), nombre);
				if (	z == null && z.darVolumen() == 0 )
					{
					rta += mat.get(i).darNombre();
					}
				

			}
			
			ArrayList<ComponentesProduccion> comp = x.darComponentesrequeridos();
			for (int i = 0; i < comp.size(); i++)
			{

				if (	dao.componentes(comp.get(i).getNombre()) == null)
					{
					rta += comp.get(i).getNombre();
					}

			}
			
			if (rta.equals(""))
			{
				
			return "Se producira en los siguientes dias";

			}
			else return rta;
			
		

		
		}
		else
		{
			return "no se conoce el producto";
		}
		
		
		
		
		return null;
		
		

		
		
		
		
	}
	
	
	public  String darEtapasEntreAnios(String x, String y) throws SQLException
	{
		dao.inicializar();
		return dao.darEtapasProduccion(x, y);
		
		
	}
	
	
	public boolean registrarMaterial(String nombre, double costo, String tipo, String unidad, int cantidad) throws SQLException
	{
		return dao.registrarMaterial(nombre, costo, tipo, unidad, cantidad);
	}
	
	public boolean registrarEtapa(String nombre, int id, int secuencia, int empleados, String fechaIncio, String fechaFin) throws SQLException
	{
		return dao.registrarEtapa(nombre, id, secuencia, empleados, fechaIncio, fechaFin);
	}
	
	public String actualizarInventario(String nombreP, int z) 
	{
		Producto x = dao.buscarProducto(nombreP);
		int asd = x.getCantidad();
		x.setCantidad(asd - z);
		dao.actualizarProducto(x);
		return "Se ha entregado exitosamente" + x.getNombre();
	
	}
	
	
	
	public String cancelarProducto(Cliente jesus2,String producto) throws Exception 
	{
		
	Cliente jesus = jesus2;	
   try
   {
	   
	 if(jesus.darProductos().get(0).contains("existen 0 unidades en espera de ser producidas."))
	 {
			dao.cacelarPedidosCliente(jesus.getIdentificacion(), producto);

	 }
	 else
	 {
		int unidadesProblematicas = dao.darUnidadesEnEsperaDeSerProducidas(jesus.getIdentificacion(), producto);
	    int unidadesEnEspera = dao.darUnidadesProducto(producto);
	    int unidadesFinales = unidadesEnEspera - unidadesProblematicas;
	   ArrayList<String> materialsCantidad= dao.darMateriasDeUnProducto(producto);
	   System.out.print(materialsCantidad.size() + "ASDASDASD");

	   for (int i = 0; i < materialsCantidad.size(); i=i+2)
	   {
		 String x = materialsCantidad.get(i);
		 String cantidad = materialsCantidad.get(i+1);
         int comparacion = Integer.parseInt(cantidad);
		 int numero =dao.darCantidadReservadaMaterial(x);
	     		 
		 int diferencia = numero - comparacion ;
		   System.out.print("MAter" + cantidad);

		 if (diferencia <= 0)
	     {
	    	 dao.actualizarCantidadReservada(0, x);

	     }
	     else
	     {
	    	 dao.actualizarCantidadReservada(diferencia, x);

	     }
	   
	   } 
	   
	   
	   if (unidadesFinales < 0)
	    {
	    	
	    	
	    	dao.actualizarUnidadesEnEspera(0, producto);
	    
	    }
	    else
	    {
	    	dao.actualizarUnidadesEnEspera(unidadesFinales, producto);
            
	    	
	    }

		dao.cacelarPedidosCliente(jesus.getIdentificacion(), producto);
        dao.conexion.close();
	 }
	   

   
   } 
   
   catch (Exception e)
{
	   System.out.print("ALGO");
dao.hiperRollback();}
   
   
return "";
	}
	
	
    // ---------------------------------------------------
    // M�todos asociados a los casos de uso: Consulta
    // ---------------------------------------------------
    
	public ArrayList conMateriales(String parametro, String parametro2, String tipo) throws SQLException
	{
		return dao.buscarMateriales(parametro, parametro2, tipo);
	}
	
	public ArrayList conProveedores(String parametro, int numero, String tipo) throws SQLException
	{
		return dao.buscarProveedores(parametro, numero, tipo);
	}
	
	public ArrayList conOperarios(String pEtapa) throws SQLException
	{
		return dao.buscarOperarios(pEtapa);
	}
	
	public ArrayList generarPedidos(String pProducto, String tipo) throws SQLException
	{
		return dao.generarPedidos(pProducto, tipo);
	}

	public String darTipoUsuario(String usr,String pass) 
	{
		return dao.darTipoUsuario(usr,pass);
	}
	
	public String cambiarEstado(int estacion) throws SQLException
	{
		return dao.cambiarEstado(estacion);
	}
	

	public Producto darProducto(String nombre) 
	{
		return dao.buscarProducto(nombre);
	}
	
	public PedidoMaterial crearPedido(int pIdProveedor, String pMaterial, int pCantidad, int pTiempo, int pCosto) throws SQLException
	{
		return dao.crearPedido(pIdProveedor, pMaterial, pCantidad, pTiempo, pCosto);
	}
	
	public ArrayList darClientes(String parametro, int numero, String tipo) throws Exception
	{
		return dao.darClientesBusqueda(parametro, numero, tipo);
	}
	
	public ArrayList darPedidos(String parametro, int numero, String tipo) throws Exception
	{
		return dao.darPedidosBusqueda(parametro, numero, tipo);
	}

	public ArrayList conMateriales2(int pId) throws SQLException
	{
		return dao.buscarMateriales2(pId);
	}
	
	public ArrayList buscarEtapasFecha(String fecha1, String fecha2, String parametro, String tema) throws SQLException
	{
		return dao.buscarEtapasFecha(fecha1, fecha2, parametro, tema);
	}
	
	public ArrayList darLosPedidos2(String tipo, int costoMayor) throws SQLException
	{
		return dao.darLosPedidosBusqueda(tipo, costoMayor);
	}
	
	public ArrayList buscarEtapasNOFecha(String fecha1, String fecha2, String parametro, String tema) throws Exception
	{
		return dao.buscarEtapasNOFecha(fecha1, fecha2, parametro, tema);
	}
	
	public String cambiarEstadoMensaje(int estacion) throws SQLException
	{
		return dao.cambiarEstadoMensaje(estacion);
	}
	
	public ArrayList buscarEtapasFecha2(String fecha1, String fecha2) throws SQLException
	{
		return dao.buscarEtapasFecha2(fecha1, fecha2);
	}
	
}
