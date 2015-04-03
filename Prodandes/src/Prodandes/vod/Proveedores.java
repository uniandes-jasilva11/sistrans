package Prodandes.vod;

/**
 * @author javie_000
 * @version 1.0
 * @created 14-Mar-2015 20:30:03
 */
public class Proveedores extends Personas 
{

	private String materiaPrima;
	private int volumenMaximo;
	private int tiempoEntrega;
	private String nomRepresentanteLegal;

	public Proveedores(String pdireccion,String pnombre,int ptelefono,String pciudad, String ptipoId, String pMateriaPrima, int pId, int vol, int tiempo)
	{
		super( pdireccion, pnombre, ptelefono, pciudad,  pId, ptipoId);
		materiaPrima = pMateriaPrima;
		volumenMaximo = vol;
		tiempoEntrega = tiempo;
	}
	
	public String darMaterial()
	{
		return materiaPrima;
	}
	
	public int darVolumen()
	{
		return volumenMaximo;
	}
	
	public int darTiempo()
	{
		return tiempoEntrega;
	}

	public void finalize() throws Throwable {
		super.finalize();
	}
}//end Proveedores