package expertsystem.engine;

import static expertsystem.userinterface.GUI.CQUESTIONM;
import static expertsystem.userinterface.GUI.OACUTE;
import static expertsystem.userinterface.GUI.OQUESTIONM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

public class EngineCertainty implements Engine{

	private ArrayList<String> base, objetivos, hechos, reglasDisp, reglasIntu,
			reglasAgo, hechosNoSe;
	private ArrayList<ObjetoInferencia> hechosInf, hechosIni, hechosPreg;
	private String tipoEvaluacion; // tipo de evaluación para los signos,
									// puede ser: "Total", "Mayoria". por
									// default es Total
	private double porcentaje_intuicion; // el default es 0.5
	private Object[] options = { "Cierto", "Falso", "No se" };
	private String EXPLICACION;

	
	/**
	 * Construye un motor de inferencia.
	 */
	public EngineCertainty() {
		base = new ArrayList<String>();
		objetivos = new ArrayList<String>();
		hechos = new ArrayList<String>();
		hechosIni = new ArrayList<ObjetoInferencia>();
		hechosInf = new ArrayList<ObjetoInferencia>();
		hechosPreg = new ArrayList<ObjetoInferencia>();
		reglasDisp = new ArrayList<String>();
		reglasIntu = new ArrayList<String>();
		reglasAgo = new ArrayList<String>();
		hechosNoSe = new ArrayList<String>();
		EXPLICACION="";
		tipoEvaluacion = "Total"; // default Total
		porcentaje_intuicion = 0.5;// default 0.5
	}

	/**
	 * Lee una base de conocimientos en forma de String.
	 * 
	 * @param reglas
	 *            la base de conocimientos
	 */
	public void leeBase(String reglas) {
		base = new ArrayList<String>();
		objetivos = new ArrayList<String>();
		hechos = new ArrayList<String>();
		hechosIni = new ArrayList<ObjetoInferencia>();
		hechosInf = new ArrayList<ObjetoInferencia>();
		hechosPreg = new ArrayList<ObjetoInferencia>();
		reglasDisp = new ArrayList<String>();
		reglasIntu = new ArrayList<String>();
		reglasAgo = new ArrayList<String>();
		hechosNoSe = new ArrayList<String>();
		EXPLICACION="";

		String[] reglasArray = reglas.split("\n");
		for (String leido : reglasArray) {
			base.add(leido);
		}
		leerHechosObjetivos();
	}

	/**
	 * Lee una base de conocimientos desde un archivo en el sistema.
	 * 
	 * @param archivo
	 *            archivo .txt con reglas
	 */
	public void leeBase(File archivo) {
		base = new ArrayList<String>();
		objetivos = new ArrayList<String>();
		hechos = new ArrayList<String>();
		hechosIni = new ArrayList<ObjetoInferencia>();
		hechosInf = new ArrayList<ObjetoInferencia>();
		hechosPreg = new ArrayList<ObjetoInferencia>();
		reglasDisp = new ArrayList<String>();
		reglasIntu = new ArrayList<String>();
		reglasAgo = new ArrayList<String>();
		hechosNoSe = new ArrayList<String>();
		EXPLICACION="";

		String leido = "";
		base = new ArrayList<String>();
		try {
			FileReader reader = new FileReader(archivo);
			BufferedReader in = new BufferedReader(reader);
			while ((leido = in.readLine()) != null) {
				base.add(leido);
			}
			leerHechosObjetivos();
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
			hechosIni.add(new ObjetoInferencia(nuevo, signo));
		}
	}

	/**
	 * Metodo viejo para agregar hechos iniciales. Esta depreciado porque ya
	 * todos los hechos iniciales deben tener signo forzosamente.
	 * 
	 * @param nuevo
	 *            hecho inicial
	 */
	@Deprecated
	public void agregaHecho(String nuevo) {
		if (!repetido2(nuevo, hechosIni)) {
			hechosIni.add(new ObjetoInferencia(nuevo));
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
			impresion += base.get(i) + "\n";
		}
		return impresion;
	}

	public ArrayList<String> getHechos() {
		return hechos;
	}

	public ArrayList<String> getObjetivos() {
		return objetivos;
	}

	public ArrayList<ObjetoInferencia> getInferencia() {
		return hechosInf;
	}

	public ArrayList<ObjetoInferencia> getHechosIniciales() {
		return this.hechosIni;
	}

	public void borrar() {
		hechosIni = new ArrayList<ObjetoInferencia>();
		hechosInf = new ArrayList<ObjetoInferencia>();
		hechosPreg = new ArrayList<ObjetoInferencia>();
		reglasDisp = new ArrayList<String>();
		hechosNoSe = new ArrayList<String>();
		EXPLICACION="";
	}

	/**
	 * Extrae de la base de conocimientos leida los posibles objetivos y los
	 * posibles hechos de inicio.
	 */
	private void leerHechosObjetivos() {
		String aux[];
		for (int i = 0; i < base.size(); i++) {
			aux = base.get(i).split("->");
			if (!repetido(aux[1], objetivos)) {
				objetivos.add(aux[1]);
			}
			Arbol root = construyeArbol(aux[0]
					.substring(3, aux[0].length() - 1));
			extraeHechos(root);
		}
	}

	// metodo para saber si un elemento ya esta repetido dentro del arraylist
	private boolean repetido(String buscar, ArrayList<String> estructura) {
		for (int i = 0; i < estructura.size(); i++) {
			if (buscar.equalsIgnoreCase(estructura.get(i))) {
				return true;
			}
		}
		return false;
	}

	// crea un arbol de expresion para evaluar los predicados, recibe una
	// expresion y
	// con ella genera el arbol.
	private Arbol construyeArbol(String expresion) {
		Arbol hoja = null;
		int cont, index;
		char op = expresion.charAt(0), y;
		if (op == '(') {
			cont = 1;
			for (index = 1; index < expresion.length(); index++) {
				if (expresion.charAt(index) == '(') {
					cont++;
				}
				if (expresion.charAt(index) == ')') {
					cont--;
					if (cont == 0) {
						break;
					}
				}
			}
			if (index == (expresion.length() - 1)) {
				hoja = construyeArbol(expresion.substring(1, index));
			} else {
				y = expresion.charAt(index + 1);
				if (y == '^' || y == 'v') {
					hoja = new Arbol(String.valueOf(y));
					hoja.left = construyeArbol(expresion
							.substring(0, index + 1));
					hoja.right = construyeArbol(expresion.substring(index + 2,
							expresion.length()));
				}
			}
		} else {
			index = 0;
			for (index = 0; index < expresion.length(); index++) {
				if (expresion.charAt(index) == '^'
						|| expresion.charAt(index) == 'v') {
					break;
				}
			}
			if (index == expresion.length()) {
				hoja = new Arbol(expresion);
			} else {
				y = expresion.charAt(index);
				hoja = new Arbol(String.valueOf(y));
				hoja.left = new Arbol(expresion.substring(0, index));
				hoja.right = construyeArbol(expresion.substring(index + 1,
						expresion.length()));
			}
		}
		return hoja;
	}

	// identifica los predicados a partir de un árbol de expresion.
	private void extraeHechos(Arbol root) {
		if (root.left != null) {
			extraeHechos(root.left);
		}
		if (!root.data.equals("^") && !root.data.equals("v")) {
			if (!repetido(root.data, hechos)) {
				hechos.add(root.data);
			}
		}
		if (root.right != null) {
			extraeHechos(root.right);
		}
	}

	private void extraeHechos(Arbol root, ArrayList<String> estructura) {
		if (root.left != null) {
			extraeHechos(root.left, estructura);
		}
		if (!root.data.equals("^") && !root.data.equals("v")) {
			if (!repetido(root.data, estructura)) {
				estructura.add(root.data);
			}
		}
		if (root.right != null) {
			extraeHechos(root.right, estructura);
		}
	}

	private String hacerExplicacion() {
		String explicacion = "";
		String[] aux;
		ArrayList<String> facts;
		int j;
		for (int i = 0; i < reglasDisp.size(); i++) {
			facts = new ArrayList<String>();
			aux = reglasDisp.get(i).split("->");
			explicacion += "\nInferencia: "
					+ buscaSignoEnListas(aux[1], hechosInf) + aux[1]
					+ "\nRegla: " + reglasDisp.get(i) + "\nHechos de Inicio: ";
			extraeHechos(construyeArbol(aux[0]
					.substring(3, aux[0].length() - 1)), facts);
			for (j = 0; j < facts.size(); j++) {
				if (repetido2(facts.get(j), hechosIni)) {
					explicacion += buscaSignoEnListas(facts.get(j), hechosIni)
							+ facts.get(j) + ", ";
				}
			}
			explicacion += "\nHechos Inferidos: ";
			for (j = 0; j < facts.size(); j++) {
				if (repetido2(facts.get(j), hechosInf)) {
					explicacion += buscaSignoEnListas(facts.get(j), hechosInf)
							+ facts.get(j) + ", ";
				}
			}
			explicacion += "\nHechos Preguntados: ";
			for (j = 0; j < facts.size(); j++) {
				if (repetido2(facts.get(j), hechosPreg)) {
					explicacion += buscaSignoEnListas(facts.get(j), hechosPreg)
							+ facts.get(j) + ", ";
				}
			}
			explicacion += "\n\n";
		}
		return explicacion;
	}

	/**
	 * Hace deduccion con la base de conocimientos cargada y los hechos
	 * actualmente conocidos. Asume que la lista de reglas esta ordenada
	 * jerarquicamente.
	 */
	public String deduccion() {
		// ArrayList<String> reglasAL = (ArrayList)reglasAL.clone(); //verificar
		// cuando la base no sea jerarquica.
		String aux[];
		for (int i = 0; i < base.size(); i++) {
			aux = base.get(i).split("->");
			if (postorden(construyeArbol(aux[0].substring(3,
					aux[0].length() - 1)))) {
				if (!repetido2(aux[1], hechosInf)) {
					hechosInf.add(new ObjetoInferencia(aux[1],
							regresaSigno(base.get(i))));
					reglasDisp.add(base.get(i));
				}
			}
		}
		EXPLICACION += "Deducci"+OACUTE+"n: \n"+hacerExplicacion();
		reglasDisp.clear();
		return EXPLICACION;
	}

	// metodo el cual evalua un arbol de expresion para saber si una regla se
	// cumple y hacer deduccion
	private boolean postorden(Arbol a) {
		boolean izq = false, der = false;
		if (a.left != null) {
			izq = postorden(a.left);
		}
		if (a.right != null) {
			der = postorden(a.right);
		}

		if (!a.data.equals("^") && !a.data.equals("v")) {
			if (repetido2(a.data, hechosIni) || repetido2(a.data, hechosInf)
					|| repetido2(a.data, hechosPreg)) {
				return true;
			}
		}

		if (a.data.equals("^")) {
			return izq && der;
		}
		if (a.data.equals("v")) {
			return izq || der;
		}

		return false;
	}

	/**
	 * Hace induccion con la base de datos cargada. Recibe el objetivo a
	 * inducir.
	 */
	public String induccion(String objetivo) {
		int respuesta;
		Stack<ArrayList<ObjetoInferencia>> pila = new Stack<ArrayList<ObjetoInferencia>>();
		pila.push(new ArrayList<ObjetoInferencia>());
		pila.peek().add(new ObjetoInferencia(objetivo));
		Stack<String> reglasVisitadas = new Stack<String>();
		String[] aux;
		if (repetido2(objetivo, hechosIni) || repetido2(objetivo, hechosInf)
				|| repetido2(objetivo, hechosPreg)) {
			EXPLICACION += "Inducci"+OACUTE+"n con objetivo: " + objetivo + "\nEl objetivo ya era un hecho.\n\n";
			return EXPLICACION;
		}
		terminado: while (!pila.empty()) {
			for (int i = 0; i < pila.peek().size(); i++) {
				if (repetido2(pila.peek().get(i).data, hechosIni)) {
					pila.peek().get(i).valor = true;
					continue;
				}
				if (repetido2(pila.peek().get(i).data, hechosInf)) {
					pila.peek().get(i).valor = true;
					continue;
				}
				if (repetido2(pila.peek().get(i).data, hechosPreg)) {
					pila.peek().get(i).valor = true;
					continue;
				}
				if (buscarEnReglas(pila, pila.peek().get(i).data,
						reglasVisitadas)) {
					continue terminado;
				}
				if (repetido(pila.peek().get(i).data, hechosNoSe)) {
					pila.peek().get(i).valor = false;
					continue;
				}
				respuesta = preguntaUsuario(pila.peek().get(i).data);
				if (respuesta == 0 || respuesta == 1) {
					if (!repetido2(pila.peek().get(i).data, hechosPreg)) {
						if (respuesta == 0) {
							hechosPreg.add(new ObjetoInferencia(pila.peek()
									.get(i).data, true));
						} else {
							hechosPreg.add(new ObjetoInferencia(pila.peek()
									.get(i).data, false));
						}

					}
					pila.peek().get(i).valor = true;
					continue;
				}
				else {
					hechosNoSe.add(pila.peek().get(i).data);
					pila.peek().get(i).valor = false;
					continue;
				}

			}
			if (evaluar(pila, reglasVisitadas)) {
				pila.pop();
				if (!reglasVisitadas.empty()) {
					reglasDisp.add(reglasVisitadas.peek());
					aux = reglasVisitadas.peek().split("->");
					hechosInf.add(new ObjetoInferencia(aux[1],
							regresaSigno(reglasVisitadas.peek())));
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

		EXPLICACION += "Inducci"+OACUTE+"n con objetivo: " + objetivo + "\n"+hacerExplicacion();
		reglasDisp.clear();
		return EXPLICACION;
	}

	// metodo el cual busca un objetivo en las reglas para agregarlo en la pila
	// de induccion
	private boolean buscarEnReglas(Stack<ArrayList<ObjetoInferencia>> pila,
			String objetivo, Stack<String> reglasVisitadas) {
		String aux[];
		for (int i = 0; i < base.size(); i++) {
			aux = base.get(i).split("->");
			if (objetivo.equals(aux[1])) {
				pila.push(new ArrayList<ObjetoInferencia>());
				extraeHechos2(construyeArbol(aux[0].substring(3, aux[0]
						.length() - 1)), pila.peek());
				reglasVisitadas.push(base.get(i));
				return true;
			}
		}
		return false;
	}

	private void extraeHechos2(Arbol root, ArrayList<ObjetoInferencia> hechos) {
		if (root.left != null) {
			extraeHechos2(root.left, hechos);
		}
		if (!root.data.equals("^") && !root.data.equals("v")) {
			if (!repetido2(root.data, hechos)) {
				hechos.add(new ObjetoInferencia(root.data));
			}
		}
		if (root.right != null) {
			extraeHechos2(root.right, hechos);
		}
	}

	private boolean repetido2(String buscar,
			ArrayList<ObjetoInferencia> estructura) {
		for (int i = 0; i < estructura.size(); i++) {
			if (buscar.equalsIgnoreCase(estructura.get(i).data)) {
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
	private boolean evaluar(Stack<ArrayList<ObjetoInferencia>> pila,
			Stack<String> reglasVisitadas) {
		String[] aux;
		// si las reglas visitadas estan vacias significa que el que se quiere
		// probar es el objetivo
		// En este caso se regresa su valor, sino se evalua todo el piso con
		// evaluaInferencia()
		if (reglasVisitadas.empty()) {
			return pila.peek().get(0).valor;
		} else {
			aux = reglasVisitadas.peek().split("->");
			return evaluaInferencia(construyeArbol(aux[0].substring(3, aux[0]
					.length() - 1)), pila.peek());
		}
	}

	private boolean evaluaInferencia(Arbol a,
			ArrayList<ObjetoInferencia> valores) {
		boolean izq = false, der = false;
		if (a.left != null) {
			izq = evaluaInferencia(a.left, valores);
		}
		if (a.right != null) {
			der = evaluaInferencia(a.right, valores);
		}

		if (!a.data.equals("^") && !a.data.equals("v")) {
			return buscaValor(a.data, valores);

		}

		if (a.data.equals("^")) {
			return izq && der;
		}
		if (a.data.equals("v")) {
			return izq || der;
		}

		return false;
	}

	private boolean buscaValor(String hecho, ArrayList<ObjetoInferencia> valores) {
		for (int i = 0; i < valores.size(); i++) {
			if (hecho.equalsIgnoreCase(valores.get(i).data)) {
				return valores.get(i).valor;
			}
		}
		return false;
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

	// metodo principal que hace la evaluacion de todos los signos de los
	// antecedentes y regresa
	// el signo dependiendo del algebra de signos elegida
	private boolean regresaSigno(String regla) {
		String[] aux;
		ArrayList<String> facts = new ArrayList<String>();
		aux = regla.split("->");
		extraeHechos(construyeArbol(aux[0].substring(3, aux[0].length() - 1)),
				facts);
		if (tipoEvaluacion.equals("Total")) {
			return evaluacionTotal(facts);
		} else if (tipoEvaluacion.equals("Mayoria")) {
			return evaluacionMayoria(facts);
		}
		return false;
	}

	private boolean evaluacionTotal(ArrayList<String> hechos) {
		for (int i = 0; i < hechos.size(); i++) {
			if (!buscaSignoEnListas(hechos.get(i))) {
				return false;
			}
		}
		return true;
	}

	private boolean evaluacionMayoria(ArrayList<String> hechos) {
		int verdadero = 0, falso = 0;
		for (int i = 0; i < hechos.size(); i++) {
			if (buscaSignoEnListas(hechos.get(i))) {
				verdadero++;
			} else {
				falso++;
			}
		}
		if (verdadero > falso) {
			return true;
		}
		return false;
	}

	// busca el signo de un hecho en las listas y regresa el signo que tiene.
	private boolean buscaSignoEnListas(String buscar) {
		int i = 0;
		for (i = 0; i < hechosIni.size(); i++) {
			if (buscar.equals(hechosIni.get(i).data)) {
				return hechosIni.get(i).signo;
			}
		}
		for (i = 0; i < hechosInf.size(); i++) {
			if (buscar.equals(hechosInf.get(i).data)) {
				return hechosInf.get(i).signo;
			}
		}
		for (i = 0; i < hechosPreg.size(); i++) {
			if (buscar.equals(hechosPreg.get(i).data)) {
				return hechosPreg.get(i).signo;
			}
		}
		return false;
	}

	// metodo que recibe un hecho y lo busca en las lista de parametro y regresa
	// + si es true y - si es false.
	// Este método solo se usa para la impresion de hechos (Explicacion).
	private String buscaSignoEnListas(String buscar,
			ArrayList<ObjetoInferencia> buscando) {
		int i = 0;
		for (i = 0; i < buscando.size(); i++) {
			if (buscar.equals(buscando.get(i).data)) {
				if (buscando.get(i).signo) {
					return "+";
				} else {
					return "-";
				}
			}
		}
		return "-";
	}

	public void intuicion() {
		reglasIntu = new ArrayList<String>();
		ArrayList<String> antecedentes = new ArrayList<String>();
		String[] aux;
		int encontrados = 0;
		double porcentaje;
		for (int i = 0; i < base.size(); i++) {
			aux = base.get(i).split("->");
			if (repetido(base.get(i), reglasDisp)) {
				continue;
			} else if (repetido(base.get(i), reglasAgo)) {
				continue;
			} else if (repetido2(aux[1], hechosIni)) {
				continue;
			} else if (repetido2(aux[1], hechosInf)) {
				continue;
			} else if (repetido2(aux[1], hechosPreg)) {
				continue;
			} else {
				antecedentes = new ArrayList<String>();
				encontrados = 0;
				extraeHechos(construyeArbol(aux[0].substring(3,
						aux[0].length() - 1)), antecedentes);
				for (int j = 0; j < antecedentes.size(); j++) {
					if (repetido2(antecedentes.get(j), hechosIni)) {
						encontrados++;
					} else if (repetido2(antecedentes.get(j), hechosInf)) {
						encontrados++;
					} else if (repetido2(antecedentes.get(j), hechosPreg)) {
						encontrados++;
					}
				}
				porcentaje = (double) encontrados
						/ (double) antecedentes.size();
				if (porcentaje >= porcentaje_intuicion) {
					if (!repetido(aux[1], reglasIntu)) {
						reglasIntu.add(aux[1]);
					}

				}
			}
		}
		EXPLICACION += "Se intuyeron los siguientes objetivos:\n" + reglasIntu.toString() + "\n";
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
				induccion(reglasIntu.get(i));
				if (buscaSignoEnListas(reglasIntu.get(i))) { // si el
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
		porcentaje_intuicion = nuevo;
	}
	public String sumaProb (){
		throw new UnsupportedOperationException();
	}
	public String continuar(){
		throw new UnsupportedOperationException();
	}
}
class ObjetoInferencia {
	String data;
	boolean valor; // valor para la evaluacion en la induccion.
	boolean signo;

	public ObjetoInferencia(String data) {
		this.data = data;
		this.valor = false;
	}

	public ObjetoInferencia(String data, boolean signo) {
		this.data = data;
		this.valor = false;
		this.signo = signo;
	}
}