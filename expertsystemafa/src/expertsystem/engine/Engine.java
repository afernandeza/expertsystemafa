package expertsystem.engine;

import java.io.File;
import java.util.ArrayList;

public interface Engine {

	public void leeBase(String reglas);
	public void leeBase(File archivo);
	public ArrayList<String> getHechos();
	public void agregaHecho(String nuevo, String signo);
	public void agregaHecho(String nuevo, boolean signo);
	public void cambiaTipoEvaluacion(String tipo);
	public void setPorcentaje(double nuevo);
	public void borrar();
	public ArrayList<String> getObjetivos();
	public String imprimeBase();
	public String deduccion();
	public String induccion(String objetivo);
	public String abduccion();
	public String continuar ();
	public String sumaProb ();
}
