package expertsystem.engine;

import static expertsystem.userinterface.GUI.CQUESTIONM;
import static expertsystem.userinterface.GUI.OACUTE;
import static expertsystem.userinterface.GUI.OQUESTIONM;
import static expertsystem.userinterface.GUI.UACUTE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JOptionPane;



/**
 * Motor de inferencia para el sistema experto. Puede leer una base de
 * conocimientos para hacer deduccion, induccion, abduccion y dar explicaciones
 * de los razonamientos.
 * 
 * @author Juan Carlos Ortiz
 * @author Alfredo Fernandez
 * @author Daniel Tapia
 * 
 */

public class EngineNotCertainty implements Engine{

	private ArrayList<String> objetivos, hechos,
			hechosNoSe;
	private ArrayList<Hecho> hechosInf, hechosIni, hechosPreg;
	private String tipoEvaluacion; // tipo de evaluación para los signos,
									// puede ser: "Total", "Mayoria". por
									// default es Total
	private Object[] options = { "Cierto", "Falso", "No se" };
	private String EXPLICACION;
	private ArrayList<Umbrales_X_Regla> umbrales;
	private ArrayList<ArrayList<Antecedente>> base;
	private ArrayList<Integer> reglasDisp, reglasAgo, reglasIntu;
	private double porcentaje_intuicion=0.5;
	ArrayList<Antecedente> porcentajes;

	/**
	 * Construye un motor de inferencia.
	 */
	public EngineNotCertainty() {
		base = new ArrayList<ArrayList<Antecedente>>();
		objetivos = new ArrayList<String>();
		hechos = new ArrayList<String>();
		hechosIni = new ArrayList<Hecho>();
		hechosInf = new ArrayList<Hecho>();
		hechosPreg = new ArrayList<Hecho>();
		reglasDisp = new ArrayList<Integer>();
		reglasIntu = new ArrayList<Integer>();
		reglasAgo = new ArrayList<Integer>();
		hechosNoSe = new ArrayList<String>();
		tipoEvaluacion = "Total"; // default Total
		umbrales= new ArrayList<Umbrales_X_Regla>();
		EXPLICACION="";
	}

	/**
	 * Lee una base de conocimientos en forma de String.
	 * 
	 * @param reglas
	 *            la base de conocimientos
	 */
	public void leeBase(String reglas) {
		base = new ArrayList<ArrayList<Antecedente>>();
		objetivos = new ArrayList<String>();
		hechos = new ArrayList<String>();
		hechosIni = new ArrayList<Hecho>();
		hechosInf = new ArrayList<Hecho>();
		hechosPreg = new ArrayList<Hecho>();
		hechosNoSe = new ArrayList<String>();
		reglasDisp = new ArrayList<Integer>();
		reglasIntu = new ArrayList<Integer>();
		reglasAgo = new ArrayList<Integer>();
		umbrales= new ArrayList<Umbrales_X_Regla>(); 
		EXPLICACION="";

		//String[] reglasArray = reglas.split("\n");
		//for (String leido : reglasArray) {
			//base.add(leido);
		//}
	}
	
	/**
	 * Lee una base de conocimientos desde un archivo en el sistema.
	 * 
	 * @param archivo
	 *            archivo .txt con reglas
	 */
	public void leeBase(File archivo) {
		base = new ArrayList<ArrayList<Antecedente>>();
		objetivos = new ArrayList<String>();
		hechos = new ArrayList<String>();
		hechosIni = new ArrayList<Hecho>();
		hechosInf = new ArrayList<Hecho>();
		hechosPreg = new ArrayList<Hecho>();
		hechosNoSe = new ArrayList<String>();
		reglasDisp = new ArrayList<Integer>();
		reglasIntu = new ArrayList<Integer>();
		reglasAgo = new ArrayList<Integer>();
		umbrales= new ArrayList<Umbrales_X_Regla>(); 
		EXPLICACION="";
		
		String leido = "";
		String [] aux;
		int cont=0;
		base = new ArrayList<ArrayList<Antecedente>>();
		try {
			FileReader reader = new FileReader(archivo);
			BufferedReader in = new BufferedReader(reader);
			while ((leido = in.readLine()) != null) {
				if (leido.trim().equals("XXCONSECUENTESXX")){
					while (!(leido = in.readLine()).equals("WWCONSECUENTESWW")) {
						objetivos.add(leido.trim());
					}
				} else if (leido.trim().equals("XXREGLASXX")){
					cont=0;
					while (!(leido = in.readLine()).equals("WWREGLASWW")) {
						aux = leido.trim().split(" ");
						base.add(new ArrayList<Antecedente>());
						for (int i=0;i<aux.length/2;i++){
							base.get(cont).add(new Antecedente(Double.parseDouble(aux[(i*2)+1]),hechos.get(Integer.parseInt(aux[i*2])-1)));
						}
						cont++;
					}
				} else if (leido.trim().equals("XXUMBRALESXX")){
					while (!(leido = in.readLine()).equals("WWUMBRALESWW")) {
						aux = leido.trim().split(" ");
						umbrales.add(new Umbrales_X_Regla(Double.parseDouble(aux[0]), Double.parseDouble(aux[1]), aux[2]));
					}
				} else if (leido.trim().equals("XXHECHOSXX")){
					while (!(leido = in.readLine()).equals("WWHECHOSWW")) {
						hechos.add(leido.trim());
					}
				}
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"Error al leer el archivo de entrada", "alert",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Agrega un hecho inicial al motor de inferencia.
	 * 
	 * @param nuevo
	 *            hecho inicial a agregar
	 * @param signo
	 *            el signo del hecho inicial, cuando los hechos no tienen signo,
	 *            simplemente no se deben agregan al motor
	 */
	public void agregaHecho(String nuevo, String signo) {
		if (signo.trim().equals("+"))
			agregaHecho(nuevo, true);
		else if (signo.trim().equals("-"))
			agregaHecho(nuevo, false);
		else
			throw new IllegalArgumentException("Signo Erroneo");

	}

	/**
	 * Agrega un hecho inicial al motor de inferencia.
	 * 
	 * @param nuevo
	 *            nuevo hecho inicial a agregar
	 * @param signo
	 *            signo del hecho inicial, signo positivo es representado por
	 *            true y negativo por false
	 */
	public void agregaHecho(String nuevo, boolean signo) {
		if (!repetido2(nuevo, hechosIni)) {
			hechosIni.add(new Hecho(nuevo, signo));
		}
	}

	/**
	 * Imprime la base de conocimientos que esta actualmente cargada en el
	 * motor.
	 * 
	 * @return
	 */
	public String imprimeBase() {
		String impresion = "";
		for (int i = 0; i < base.size(); i++) {
			for (int j =0; j< base.get(i).size();j++){
				impresion += String.valueOf(hechos.indexOf(base.get(i).get(j).nombre)+1) + "^";
			}
			impresion=impresion.substring(0, impresion.length()-1);
			impresion += "\n";
		}
		return impresion;
	}

	public ArrayList<String> getHechos() {
		return hechos;
	}

	public ArrayList<String> getObjetivos() {
		return objetivos;
	}

	public ArrayList<Hecho> getInferencia() {
		return hechosInf;
	}

	public ArrayList<Hecho>getHechosIniciales() {
		return this.hechosIni;
	}

	public void borrar() {
		hechosIni = new ArrayList<Hecho>();
		hechosInf = new ArrayList<Hecho>();
		hechosPreg = new ArrayList<Hecho>();
		hechosNoSe = new ArrayList<String>();
		reglasIntu = new ArrayList<Integer>();
		reglasAgo = new ArrayList<Integer>();
		reglasDisp = new ArrayList<Integer>();
		umbrales= new ArrayList<Umbrales_X_Regla>(); 
		EXPLICACION="";
	}

	/**
	 * Extrae de la base de conocimientos leida los posibles objetivos y los
	 * posibles hechos de inicio.
	 */

	// metodo para saber si un elemento ya esta repetido dentro del arraylist
	private boolean repetido(String buscar, ArrayList<String> estructura) {
		for (int i = 0; i < estructura.size(); i++) {
			if (buscar.equalsIgnoreCase(estructura.get(i))) {
				return true;
			}
		}
		return false;
	}

	private String hacerExplicacion() {
		String explicacion = "";
		//DecimalFormat percentageFormat = new DecimalFormat("#0.00 %");
		int j=0;
		ArrayList<Antecedente>facts;
		for (int i = 0; i < reglasDisp.size(); i++) {
			facts = base.get(reglasDisp.get(i));
			
			String conf = darPeso(objetivos.get(reglasDisp.get(i)), 2 ,hechosInf);
			
			explicacion += "\nInferencia: "
					+ darPeso(objetivos.get(reglasDisp.get(i)), 0 ,hechosInf) 
					+ objetivos.get(reglasDisp.get(i))
					+ "\nPeso: " 
					+ darPeso(objetivos.get(reglasDisp.get(i)), 1 ,hechosInf)
					+ "\nConfianza: " 
					+ conf 
					+ "\nEvidencia en contra: " 
					+ darPeso(objetivos.get(reglasDisp.get(i)), 3 ,hechosInf)
					+ "\nRegla: " + objetivos.get(reglasDisp.get(i))
					+ "\n\nHechos de Inicio: ";
			for (j = 0; j < facts.size(); j++) {
				if (repetido2(facts.get(j).nombre, hechosIni)) {
					explicacion += "\n   "+darPeso(facts.get(j).nombre, 0 ,hechosIni)
							+ facts.get(j).nombre + ", ";
				}
			}
			explicacion += "\n\nHechos Inferidos: ";
			for (j = 0; j < facts.size(); j++) {
				if (repetido2(facts.get(j).nombre, hechosInf)) {
					explicacion += "\n   "+darPeso(facts.get(j).nombre, 0 ,hechosInf)
							+ facts.get(j).nombre + ", ";
				}
			}
			explicacion += "\n\nHechos Preguntados: ";
			for (j = 0; j < facts.size(); j++) {
				if (repetido2(facts.get(j).nombre, hechosPreg)) {
					explicacion += "\n   "+darPeso(facts.get(j).nombre, 0 ,hechosPreg)
							+ facts.get(j).nombre + ", ";
				}
			}
			explicacion += "\n\n";
		}
		return explicacion;
	}
	
	public String darPeso (String hecho, int id, ArrayList<Hecho> estructura) {
		for (int i=0; i < estructura.size(); i++){
			if (estructura.get(i).data.equals(hecho)){
				if (id ==0){
					if (estructura.get(i).signo){
						return "+";
					}
					else {
						return "-";
					}
				}
				else if (id == 1){
					return String.valueOf(estructura.get(i).confianza);
				}
				else if (id == 2){
					return String.valueOf(estructura.get(i).confianza*100)+"%";
				}
				else if (id == 3){
					return String.valueOf(estructura.get(i).contra*100)+"%";
				}
			}
		}
		return "";
	}
	
	private boolean darSigno (String hecho, ArrayList<Hecho> estructura) {
		for (int i=0; i < estructura.size(); i++){
			if (estructura.get(i).data.equals(hecho)){
				return estructura.get(i).signo;
			}
		}
		return false;
	}
	
	/**
	 * Hace deduccion con la base de conocimientos cargada y los hechos
	 * actualmente conocidos. Asume que la lista de reglas esta ordenada
	 * jerarquicamente.
	 */
	public String deduccion() {
		String valores;
		String aux [];
		for (int i = 0; i < base.size(); i++) {
			if ((valores=dispararDeduccion(base.get(i), i))!=null) {
				aux=valores.split("#");
				if (!repetido2(objetivos.get(i), hechosInf)) {
					hechosInf.add(new Hecho(objetivos.get(i),Boolean.parseBoolean(aux[0]),
							Double.parseDouble(aux[1]), Double.parseDouble(aux[2])));
					reglasDisp.add(i);
				}
			}
		}
		String deducidos= hacerExplicacion();
		if (deducidos.equals("")){
			deducidos="   La deducci"+OACUTE+"n no lleg"+OACUTE+" a ning"+UACUTE+"n resultado\n";
		}
		EXPLICACION += "Deducci"+OACUTE+"n: \n"+deducidos;
		reglasDisp.clear();
		return EXPLICACION;
	}
	
	private String dispararDeduccion (ArrayList<Antecedente> regla, int numRegla){
		double sumPos=0, sumNeg=0, conf, contra, sumatoriaTotal;
		for (int i =0; i < regla.size(); i++){
			if (repetido2(regla.get(i).nombre, hechosIni)){
				if (darSigno(regla.get(i).nombre, hechosIni)){
					sumPos+=regla.get(i).peso;
				}
				else {
					sumNeg+=regla.get(i).peso;
				}
			} else if (repetido2(regla.get(i).nombre, hechosInf)){
				if (darSigno(regla.get(i).nombre, hechosInf)){
					sumPos+=regla.get(i).peso;
				}
				else {
					sumNeg+=regla.get(i).peso;
				}
			} else if (repetido2(regla.get(i).nombre, hechosPreg)){
				if (darSigno(regla.get(i).nombre, hechosPreg)){
					sumPos+=regla.get(i).peso;
				}
				else {
					sumNeg+=regla.get(i).peso;
				}
			}
		}
		if (sumPos>=umbrales.get(numRegla).umbralDisp){
			sumatoriaTotal= sumaTotal(regla);
			conf=sumPos/sumatoriaTotal;
			contra=sumNeg/sumatoriaTotal;
			return "true#"+String.valueOf(conf)+"#"+String.valueOf(contra);
		} else if (sumNeg>=umbrales.get(numRegla).umbralDisp){
			sumatoriaTotal= sumaTotal(regla);
			conf=sumNeg/sumatoriaTotal;
			contra=sumPos/sumatoriaTotal;
			return "false#"+String.valueOf(conf)+"#"+String.valueOf(contra);
		}
		return null;
	}


	/**
	 * Hace induccion con la base de datos cargada. Recibe el objetivo a
	 * inducir.
	 */
	public String induccion(String objetivo) {
		int respuesta;
		Stack<ArrayList<Antecedente>> pila = new Stack<ArrayList<Antecedente>>();
		pila.push(new ArrayList<Antecedente>());
		pila.peek().add(new Antecedente(1, objetivo));
		Stack<Integer> reglasVisitadas = new Stack<Integer>();
		String [] aux;
		String valores;
		if (repetido2(objetivo, hechosIni) || repetido2(objetivo, hechosInf)
				|| repetido2(objetivo, hechosPreg)) {
			EXPLICACION += "Inducci"+OACUTE+"n con objetivo: " + objetivo + "\nEl objetivo ya era un hecho.\n\n";
			reglasDisp.clear();
			return EXPLICACION;
		}
		terminado: while (!pila.empty()) {
			for (int i = 0; i < pila.peek().size(); i++) {
				if (repetido2(pila.peek().get(i).nombre, hechosIni)) {
					continue;
				}
				if (repetido2(pila.peek().get(i).nombre, hechosInf)) {
					continue;
				}
				if (repetido2(pila.peek().get(i).nombre, hechosPreg)) {
					continue;
				}
				if (buscarEnReglas(pila, pila.peek().get(i).nombre,
						reglasVisitadas)) {
					continue terminado;
				}
				if (repetido(pila.peek().get(i).nombre, hechosNoSe)) {
					continue;
				}
				respuesta = preguntaUsuario(pila.peek().get(i).nombre);
				if (respuesta == 0 || respuesta == 1) {
					if (!repetido2(pila.peek().get(i).nombre, hechosPreg)) {
						if (respuesta == 0) {
							hechosPreg.add(new Hecho(pila.peek()
									.get(i).nombre, true));
						} else {
							hechosPreg.add(new Hecho(pila.peek()
									.get(i).nombre, false));
						}

					}
					continue;
				}
				else {
					hechosNoSe.add(pila.peek().get(i).nombre);
					continue;
				}

			}
			if ((valores=evaluar(pila, reglasVisitadas))!=null) {
				pila.pop();
				if (!reglasVisitadas.empty()) {
					aux=valores.split("#");
					reglasDisp.add(reglasVisitadas.peek());
					hechosInf.add(new Hecho(objetivos.get(reglasVisitadas.peek()),Boolean.parseBoolean(aux[0]),
							Double.parseDouble(aux[1]), Double.parseDouble(aux[2])));
					reglasVisitadas.pop();
				}

			} else {
				
				// JOptionPane.showMessageDialog(null, "No se pudo completar la
				// induccion", "Error", JOptionPane.ERROR_MESSAGE);
				
				
				// Si hay algo en reglasVisitadas, significa que no se pudo
				// quitar ese piso
				// por lo tanto esa regla esta agotada, porque se preguntaron
				// todos los hechos
				// si no hay reglasVisitadas, entonces no se pudo probar el
				// objetivo pero ninguna regla se agoto
				if (!reglasVisitadas.empty()) {
					reglasAgo.add(reglasVisitadas.peek());
				}
				break;
			}
		}
		String inducidos = hacerExplicacion();
		if (inducidos.equals("")){
			inducidos="   La Inducci"+OACUTE+"n no lleg"+OACUTE+" a ning"+UACUTE+"n resultado\n";
		}
		EXPLICACION += "Inducci"+OACUTE+"n con objetivo: " + objetivo + "\n"+inducidos;
		reglasDisp.clear();
		return EXPLICACION;
	}
	

	
	private double   sumaTotal (ArrayList<Antecedente> regla){
		double suma=0;
		for (int i =0; i < regla.size(); i++){
			suma+= regla.get(i).peso;
		}
		return suma;
	}
	

	// metodo el cual busca un objetivo en las reglas para agregarlo en la pila
	// de induccion
	private boolean buscarEnReglas(Stack<ArrayList<Antecedente>> pila,
			String objetivo, Stack<Integer> reglasVisitadas) {
		int index;
		if ((index=objetivos.indexOf(objetivo))!=-1) {
			pila.push(base.get(index));
			reglasVisitadas.push(index);
			return true;
		}
		return false;
	}


	private boolean repetido2(String buscar,
			ArrayList<Hecho> estructura) {
		for (int i = 0; i < estructura.size(); i++) {
			if (buscar.equals(estructura.get(i).data)) {
				return true;
			}
		}
		return false;
	}

	// metodo para preguntar al usuario si el hecho está presente o no lo esta
	private int preguntaUsuario(String objetivo) {
		return JOptionPane.showOptionDialog(null, OQUESTIONM+"El hecho: \"" + objetivo
				+ "\" es correcto"+CQUESTIONM, "Pregunta", JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}

	// evalua si el piso actual de la pila es true
	private String evaluar(Stack<ArrayList<Antecedente>> pila,
			Stack<Integer> reglasVisitadas) {
		// si las reglas visitadas estan vacias significa que el que se quiere
		// probar es el objetivo
		// En este caso se regresa su valor, en otro caso se evalua todo el piso con
		// evaluaInferencia()
		if (reglasVisitadas.empty()) {
			return "true";
		} else {
			return disparaInduccion(pila.peek(), reglasVisitadas.peek());
		}
	}
	
	private String disparaInduccion (ArrayList<Antecedente> regla, int numRegla){
		double sumPos=0, sumNeg=0, conf, contra, sumatoriaTotal;
		for (int i =0; i < regla.size(); i++){
			if (repetido2(regla.get(i).nombre, hechosIni)){
				if (darSigno(regla.get(i).nombre, hechosIni)){
					sumPos+=regla.get(i).peso;
				}
				else {
					sumNeg+=regla.get(i).peso;
				}
			} else if (repetido2(regla.get(i).nombre, hechosInf)){
				if (darSigno(regla.get(i).nombre, hechosInf)){
					sumPos+=regla.get(i).peso;
				}
				else {
					sumNeg+=regla.get(i).peso;
				}
			} else if (repetido2(regla.get(i).nombre, hechosPreg)){
				if (darSigno(regla.get(i).nombre, hechosPreg)){
					sumPos+=regla.get(i).peso;
				}
				else {
					sumNeg+=regla.get(i).peso;
				}
			}
		}
		if (sumPos>=umbrales.get(numRegla).umbralDisp){
			sumatoriaTotal= sumaTotal(regla);
			conf=sumPos/sumatoriaTotal;
			contra=sumNeg/sumatoriaTotal;
			return "true#"+String.valueOf(conf)+"#"+String.valueOf(contra);
		} else if (sumNeg>=umbrales.get(numRegla).umbralDisp){
			sumatoriaTotal= sumaTotal(regla);
			conf=sumNeg/sumatoriaTotal;
			contra=sumPos/sumatoriaTotal;
			return "false#"+String.valueOf(conf)+"#"+String.valueOf(contra);
		}
		return null;
	}

	public void cambiaTipoEvaluacion(String tipo) {
		if (tipo.equals("Total") || tipo.equals("Mayoria")) {
			tipoEvaluacion = tipo;
		}
	}

	/**
	 * Regresa el tipo de evaluacion algebraica que se esta utilizando.
	 * 
	 * @return tipo de evaluacion algebraica
	 */
	public String getTipoEvaluacion() {
		return tipoEvaluacion;
	}

	public void intuicion() {
		reglasIntu = new ArrayList<Integer>();
		for (int i = 0; i < base.size(); i++) {
			if (reglasDisp.indexOf(i)!=-1) {
				continue;
			} else if (reglasAgo.indexOf(i)!=-1) {
				continue;
			} else if (repetido2(objetivos.get(i), hechosIni)) {
				continue;
			} else if (repetido2(objetivos.get(i), hechosInf)) {
				continue;
			} else if (repetido2(objetivos.get(i), hechosPreg)) {
				continue;
			} else {
				if (disparaIntuicion(base.get(i), i)){
					reglasIntu.add(i);
				}
			}
		}
		String intuidos = "Se intuyeron los siguientes objetivos:\n";
		for (int i=0; i<reglasIntu.size();i++){
			intuidos+="  "+objetivos.get(reglasIntu.get(i)) + "\n";
		}
		if (reglasIntu.size()==0){
			intuidos= "Se intuyeron los siguientes objetivos:\n   No se obtuvieron " +
					"resultados de la intuici"+OACUTE+"n";
		}
		EXPLICACION += intuidos+ "\n\n";
	}
	private boolean disparaIntuicion (ArrayList<Antecedente> regla, int numRegla){
		double sumPos=0, sumNeg=0;
		for (int i =0; i < regla.size(); i++){
			if (repetido2(regla.get(i).nombre, hechosIni)){
				if (darSigno(regla.get(i).nombre, hechosIni)){
					sumPos+=regla.get(i).peso;
				}
				else {
					sumNeg+=regla.get(i).peso;
				}
			} else if (repetido2(regla.get(i).nombre, hechosInf)){
				if (darSigno(regla.get(i).nombre, hechosInf)){
					sumPos+=regla.get(i).peso;
				}
				else {
					sumNeg+=regla.get(i).peso;
				}
			} else if (repetido2(regla.get(i).nombre, hechosPreg)){
				if (darSigno(regla.get(i).nombre, hechosPreg)){
					sumPos+=regla.get(i).peso;
				}
				else {
					sumNeg+=regla.get(i).peso;
				}
			}
		}
		if (sumPos>=umbrales.get(numRegla).umbralIntu){
			return true;
		} else if (sumNeg>=umbrales.get(numRegla).umbralIntu){
			return true;
		}
		return false;
	}

	public String abduccion() {
		int comienzo; // variable que guarda lee numero de hechos inferidos
						// antes de la deduccion para poder saber si de la
						// deduccion se saco un resultado positivo.
		termino: while (true) {
			comienzo = hechosInf.size();
			deduccion();
			if (comienzo < hechosInf.size()) {
				if (revisaInferencia(comienzo)) {
					break termino;
				}
			}
			intuicion();
			if (reglasIntu.isEmpty()) {
				break;
			}
			for (int i = 0; i < reglasIntu.size(); i++) {
				induccion(objetivos.get(reglasIntu.get(i)));
				if (darSigno(objetivos.get(reglasIntu.get(i)), hechosInf)) { // si el
																// elemento que
																// se quería
																// inducir tiene
																// signo
																// positivo,
																// entonces:
					break termino;
				}
			}
		}
		return EXPLICACION;
	}

	// metodo que revisa si hubo inferencias positivas para la revision en la
	// abduccion
	private boolean revisaInferencia(int comienzo) {
		for (int i = comienzo; i < hechosInf.size(); i++) {
			if (hechosInf.get(i).signo) {
				return true;
			}
		}
		return false;
	}

	public void setPorcentaje(double nuevo) {
		porcentaje_intuicion= porcentaje_intuicion*1;
		porcentaje_intuicion = nuevo;
	}
	
	public String sumaProb() {
		String valores="";
		DecimalFormat percentageFormat = new DecimalFormat("#0.00 %");
		porcentajes= new ArrayList<Antecedente>();
		for (int i = 0; i < base.size(); i++) {
			porcentajes.add(new Antecedente(dispararSumaProb(base.get(i), i), objetivos.get(i)));
		}
		porcentajes = bubbleSort(porcentajes);
		if (porcentajes.size()<5){
			for (int i=0;i<porcentajes.size();i++){
				String por = percentageFormat.format(porcentajes.get(i).peso);
				valores+=i+1+") " + por + " " + porcentajes.get(i).nombre +"\n";
			}
		}
		else {
			for (int i=0;i<5;i++){
				String por = percentageFormat.format(porcentajes.get(i).peso);
				valores+=i+1+") "+ por + " " + porcentajes.get(i).nombre +"\n";
			}
		}
		return valores;
	}
	
	private double dispararSumaProb (ArrayList<Antecedente> regla, int numRegla){
		double sumatoriaActual=0, sumatoriaTotal;
		for (int i =0; i < regla.size(); i++){
			if (repetido2(regla.get(i).nombre, hechosIni)){
				if (darSigno(regla.get(i).nombre, hechosIni)){
					sumatoriaActual+=regla.get(i).peso;
				}
				else {
					sumatoriaActual-=regla.get(i).peso;
				}
			} else if (repetido2(regla.get(i).nombre, hechosInf)){
				if (darSigno(regla.get(i).nombre, hechosInf)){
					sumatoriaActual+=regla.get(i).peso;
				}
				else {
					sumatoriaActual-=regla.get(i).peso;
				}
			} else if (repetido2(regla.get(i).nombre, hechosPreg)){
				if (darSigno(regla.get(i).nombre, hechosPreg)){
					sumatoriaActual+=regla.get(i).peso;
				}
				else {
					sumatoriaActual-=regla.get(i).peso;
				}
			}
		}
		sumatoriaTotal= sumaTotal(regla);
		return sumatoriaActual/sumatoriaTotal;
	}
	private ArrayList<Antecedente> bubbleSort (ArrayList<Antecedente> lista){
		Antecedente tmp;
		for (int i=0;i<lista.size();i++){
			for (int j=i;j<lista.size();j++){
				if(lista.get(i).peso<lista.get(j).peso){
					tmp=lista.get(i);
					lista.set(i, lista.get(j));
					lista.set(j, tmp);
				}
			}
		}
		return lista;
	}
	
	public String continuar (){
		if (porcentajes == null){
			return "";
		}
		if (porcentajes.size()==0){
			return "";
		}
		int respuesta;
		ArrayList<Antecedente> regla = base.get(objetivos.indexOf(porcentajes.get(0).nombre));
		for (int i=0;i<regla.size();i++){
			if (repetido2(regla.get(i).nombre, hechosIni)) {
				continue;
			}
			if (repetido2(regla.get(i).nombre, hechosInf)) {
				continue;
			}
			if (repetido2(regla.get(i).nombre, hechosPreg)) {
				continue;
			}
			if (repetido(regla.get(i).nombre, hechosNoSe)) {
				continue;
			}
			respuesta = preguntaUsuario(regla.get(i).nombre);
			if (respuesta == 0 || respuesta == 1) {
				if (respuesta == 0) {
					hechosPreg.add(new Hecho(regla.get(i).nombre, true));
				} else {
					hechosPreg.add(new Hecho(regla.get(i).nombre, false));
				}
				return sumaProb();
			}
			else {
				hechosNoSe.add(regla.get(i).nombre);
				return sumaProb();
			}
		}
		return "";
	}
}

// clase para armar el arbol de expresion
class Arbol {
	String data;
	Arbol right;
	Arbol left;

	public Arbol(String data) {
		this.data = data;
		this.right = null;
		this.left = null;
	}
}


class Umbrales_X_Regla {
	double umbralDisp;
	double umbralIntu;
	String tipo;
	
	public Umbrales_X_Regla (double intu, double disp, String tipo){
		this.umbralDisp=disp;
		this.umbralIntu=intu;
		this.tipo=tipo;
	}
}

class Antecedente {
	double peso;
	String nombre;
	
	public Antecedente (double peso, String nombre){
		this.peso=peso;
		this.nombre=nombre;
	}
}

class Hecho {
	String data;
	boolean signo;
	double confianza;
	double contra;	

	public Hecho(String data, boolean signo) {
		this.data = data;
		this.signo = signo;
		this.contra=0;
		this.confianza=1;
	}
	public Hecho(String data, boolean signo, double confianza, double contra) {
		this.data = data;
		this.signo = signo;
		this.contra=contra;
		this.confianza=confianza;
	}
}