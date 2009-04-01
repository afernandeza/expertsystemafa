package expertsystem.userinterface;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import expertsystem.engine.Engine;
import expertsystem.engine.EngineCertainty;
import expertsystem.engine.EngineNotCertainty;

/**
 * Interfaz Grafica del sistema experto.
 * @author Alfredo Fernandez
 * @author Juan Carlos Ortiz
 * @author Daniel Tapia
 *
 */
public class GUI extends JFrame implements ActionListener {
	
	public final static char AACUTE = (char) 225;
	public final static char EACUTE = (char) 233;
	public final static char IACUTE = (char) 237;
	public final static char OACUTE = (char) 243;
	public final static char UACUTE = (char) 250;
	public final static char OQUESTIONM = (char) 191;
	public final static char CQUESTIONM = (char) 63;

	public final static boolean DEBUG = true;

	private final static int HEIGHT = 5;
	private final static int WIDTH = 5;

	private final static int RESULTS_WIDTH = 820;
	private final static int RESULTS_HEIGHT = 250;
	
	public final static int FACTS_WIDTH = 800;
	public final static int FACTS_HEIGHT = 250;
	
	private final static String OPEN = "Abrir...";
	private final static String DELETE = "Borrar";
	private final static String RUN = "Razonar...";
	private final static String DEDUCE = "Deducci"+OACUTE+"n";
	private final static String INDUCE = "Inducci"+OACUTE+"n";
	private final static String ABDUCE = "Abducci"+OACUTE+"n...";
	private final static String SUMA_PROB = "Suma Probabil"+IACUTE+"stica...";
	private final static String CONTINUAR = "Continuar";
	private final static String ACERCADE = "Acerca de JExpert";
	private final static String DEFINICIONES = "Definiciones";
	private final static String MANUAL = "Manual de usuario";
	private final static String EXIT = "Salir";
	
	private final static String TOTAL_CRITERIA = "Total";
	private final static String MAY_CRITERIA = "Mayor"+IACUTE+"a";
	
	private final static String INITIAL_FACT_STATUS = " ";
	private final static Color color = new Color(236, 233, 216);

	private final static String OBJECTIVES_OK = "Objetivos ok";

	private final static int RUN_DEDUCTION = 1;
	private final static int RUN_INDUCTION = 2;
	private final static int RUN_ABDUCTION = 3;
	private final static int RUN_PROBABILISTIC = 4;
	private final static String[] factsColumns = { "Signo", "Hechos" };

	//private JTextArea rules;
	private JFileChooser fc;
	private TextFileFilter textFilter;
	private int algorithm;
	private String criteria = "Total";
	private double percentage = 0.5;
	private ComboBoxTable initialFactsTable;
	private Engine engine;
	private GridBagConstraints c;
	private JFrame objectiveSelector;
	private CheckBoxTable objectivesTable;
	private JMenu criteriaS;
	private boolean INCERTIDUMBRE = false;
	private JButton continueButton;
	private JRadioButtonMenuItem suma_prob;
	private JTextArea resultsText;
	

	static final long serialVersionUID = -123456789;

	/** 
	 * Construye la interfaz grafica
	 */
	public GUI() {
		super("JExpert");
		this.algorithm = GUI.RUN_DEDUCTION;

		this.setIcon(this);
		this.setBackground(color);
		this.getContentPane().setBackground(color);

		this.setSize(HEIGHT, WIDTH);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		fc = new JFileChooser();
		textFilter = new TextFileFilter();

		JMenuBar mb = new JMenuBar();
		mb.setBackground(color);

		JMenu file = new JMenu("Archivo");
		mb.add(file);
		file.setBackground(color);

		JMenuItem open = new JMenuItem(OPEN);
		open.addActionListener(this);
		file.add(open);

		JMenuItem delete = new JMenuItem(DELETE);
		delete.addActionListener(this);
		file.add(delete);

		JMenuItem runMenuItem = new JMenuItem(RUN);
		runMenuItem.addActionListener(this);
		file.add(runMenuItem);

		file.addSeparator();

		JMenuItem defaultDatabase = new JMenuItem("<html>"
				+ "basedeconocimiento.txt" + "</html>");
		defaultDatabase.setActionCommand("basedeconocimiento");
		defaultDatabase.addActionListener(this);
		file.add(defaultDatabase);

		File mydir = new File("reglas");
		File[] files;
		if (mydir.exists() && mydir.isDirectory()) {
			files = mydir.listFiles();
			if (files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].toString().contains(".txt")) {
						JMenuItem recentFile = new JMenuItem("<html>"
								+ files[i].toString() + "</html>");
						recentFile.setActionCommand(files[i].toString());
						recentFile.addActionListener(this);
						file.add(recentFile);
						if (DEBUG) {
							System.out.println("Archivo en menu: " + files[i]);
						}
						if (i >= 7)
							break;
					}
				}
			}
		}
		file.addSeparator();

		JMenuItem exit = new JMenuItem(EXIT);
		exit.addActionListener(this);
		file.add(exit);

		JMenu run = new JMenu("Razonamiento");
		mb.add(run);
		run.setBackground(color);

		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem deduce = new JRadioButtonMenuItem(DEDUCE);
		deduce.setSelected(true);
		deduce.addActionListener(this);
		group.add(deduce);
		run.add(deduce);

		JRadioButtonMenuItem induce = new JRadioButtonMenuItem(INDUCE);
		induce.addActionListener(this);
		group.add(induce);
		run.add(induce);
		
		JRadioButtonMenuItem abduce = new JRadioButtonMenuItem(ABDUCE);
		abduce.addActionListener(this);
		group.add(abduce);
		run.add(abduce);
		
		suma_prob = new JRadioButtonMenuItem(SUMA_PROB);
		suma_prob.addActionListener(this);
		group.add(suma_prob);
		run.add(suma_prob);

		criteriaS = new JMenu("Signos");
		mb.add(criteriaS);
		criteriaS.setBackground(color);
		
		ButtonGroup criterion = new ButtonGroup();
		JRadioButtonMenuItem total = new JRadioButtonMenuItem(TOTAL_CRITERIA);
		total.setSelected(true);
		total.addActionListener(this);
		criterion.add(total);
		criteriaS.add(total);
		
		JRadioButtonMenuItem mayoria = new JRadioButtonMenuItem(MAY_CRITERIA);
		mayoria.addActionListener(this);
		criterion.add(mayoria);
		criteriaS.add(mayoria);
		
		JMenu help = new JMenu("Ayuda");
		mb.add(help);
		help.setBackground(color);
		
		JMenuItem definiciones = new JMenuItem(DEFINICIONES);
		definiciones.setActionCommand(DEFINICIONES);
		help.add(definiciones);
		definiciones.addActionListener(this);
		
		JMenuItem manual = new JMenuItem(MANUAL);
		help.add(manual);
		manual.setActionCommand(MANUAL);
		manual.addActionListener(this);
		
		help.addSeparator();
		JMenuItem acercade = new JMenuItem(ACERCADE);
		help.add(acercade);
		acercade.setActionCommand(ACERCADE);
		acercade.addActionListener(this);
		
		this.setJMenuBar(mb);
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);
		tb.setRollover(true);

		tb.setLayout(new BoxLayout(tb, BoxLayout.LINE_AXIS));

		JButton openButton = this.makeToolButton("open", OPEN,
				"abrir una base de datos", "abrir");
		tb.add(openButton);
		openButton.setFocusable(false);

		JButton openButton2 = this.makeToolButton("delete", DELETE,
				"Borrar los c"+ AACUTE +"lculos" + " hechos hasta el momento.", "borrar");
		tb.add(openButton2);
		openButton2.setFocusable(false);

		JButton openButton3 = this.makeToolButton("brainicon", RUN, "razonar",
				"razonar");
		tb.add(openButton3);
		openButton3.setFocusable(false);

		continueButton = this.makeToolButton("run", CONTINUAR, 
				"Continuar con la suma probabil" + IACUTE  + "stica",	"continuar");
		tb.add(continueButton);
		continueButton.setFocusable(false);
		continueButton.setVisible(false);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		this.add(tb);
		c.gridy++;

		this.setLayout(new GridBagLayout());

		/* Inicio selector de hechos */
		
		JLabel initialFactsLabel = new JLabel("Seleccione los hechos iniciales:");
		this.add(initialFactsLabel, c);
		c.gridy++;

		Object[][] data = new Object[0][0];
		initialFactsTable = new ComboBoxTable(factsColumns, data);
		this.add(initialFactsTable, c);
		c.gridy++;

		/* Fin selector de hechos */
		
		/* Inicio resultados */
		
		JLabel resultsLabel = new JLabel("Resultados:");
		this.add(resultsLabel, c);
		c.gridy++;

		resultsText = new JTextArea();
		resultsText.setEditable(false);
		resultsText.setLineWrap(true);
		resultsText.setWrapStyleWord(true);

		JScrollPane resultsPane = new JScrollPane(resultsText);
		resultsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		resultsPane.setPreferredSize(new Dimension(RESULTS_WIDTH, RESULTS_HEIGHT));
		this.add(resultsPane, c);
		
		c.gridy++;
		
		/* Fin resultados */
		
		this.pack();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
	}

	public void pack() {
		super.pack();
		this.setSize(this.getWidth() + WIDTH, this.getHeight() + HEIGHT);
		this.setLocationRelativeTo(null);
	}

	/** 
	 * Aplica el icono de la aplicacion a cualquier ventana dada.
	 * @param jFrame ventana de la aplicacion
	 */
	private void setIcon(JFrame jFrame) {
		String path = "images/brain.gif";
		try {
			jFrame.setIconImage(this.createImage(path).getImage());
		} catch (Exception e) {
			if (DEBUG) {
				System.out
						.println("No se pudo colocar icono de la aplicaci+" +OACUTE+"+n: "
								+ e);
				e.printStackTrace();
			}
		}
	}

	private JButton makeToolButton(String imageName, String actionCommand,
			String toolTipText, String altText) {

		String imgLocation = "images/" + imageName + ".gif";

		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);

		// ImageIcon icon = new ImageIcon(imgLocation, altText);
		ImageIcon i = this.createImage(imgLocation);
		i.setDescription(altText);
		button.setIcon(i);
		button.setText(altText);

		return button;
	}

	private void openDatabase() {
		fc.setFileFilter(textFilter);
		fc.showOpenDialog(this);
		File database = fc.getSelectedFile();
		if (database != null && database.exists()) {
			this.loadDatabase(database);
		} else {
			if (DEBUG) {
				System.out.println("No se cargo ningun archivo.");
			}
		}
	}

	private void loadDatabase(String reglas) {
		INCERTIDUMBRE=false;
		criteriaS.setVisible(true);
		suma_prob.setVisible(false);
		engine = new EngineCertainty();
		engine.leeBase(reglas);
		//rules.setText(engine.imprimeBase());
		
		Collections.sort(engine.getHechos());
		ArrayList<String> hechos = engine.getHechos();
		
		initialFactsTable.initializeArray(hechos.size(), 2);
		int i = 0;
		for (String s : hechos) {
			initialFactsTable.setValueAt(INITIAL_FACT_STATUS, i, 0);
			initialFactsTable.setValueAt(s, i, 1);
			i++;
		}
		if (DEBUG) {
			System.out.println("Se carg" +OACUTE+" la base de datos: \n" + reglas);
		}
	}

	private void loadDatabase(File database) {
		String leido = "";
		try {
			FileReader reader = new FileReader(database);
			BufferedReader in = new BufferedReader(reader);
			leido = in.readLine();
			if (leido.trim().equals("XXINCERTIDUMBREXX")){
				engine = new EngineNotCertainty();
				INCERTIDUMBRE=true;
				criteriaS.setVisible(false);
				suma_prob.setVisible(true);
			}
			else {
				engine = new EngineCertainty();
				INCERTIDUMBRE=false;
				criteriaS.setVisible(true);
				suma_prob.setVisible(false);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"Error al leer el archivo de entrada", "alert",
					JOptionPane.ERROR_MESSAGE);
			System.out.println(e.getMessage());
		}
		
		engine.leeBase(database);
		//rules.setText(engine.imprimeBase());
		
		Collections.sort(engine.getHechos());
		ArrayList<String> hechos = engine.getHechos();
		
		initialFactsTable.initializeArray(hechos.size(), 2);
		int i = 0;
		for (String s : hechos) {
			initialFactsTable.setValueAt(INITIAL_FACT_STATUS, i, 0);
			initialFactsTable.setValueAt(s, i, 1);
			i++;
		}
		if (DEBUG) {
			System.out.println("Se carg"+OACUTE+" la base de datos: \n"
					+ database.getAbsolutePath());
		}
	}

	private String objectivesOk() {
		String res = "";
		for (int i = 0; i < objectivesTable.getRowCount(); i++) {
			Boolean b = (Boolean) objectivesTable.getValueAt(i, 0);
			if (b) {
				String s = (String) objectivesTable.getValueAt(i, 1);
				if (DEBUG) {
					System.out.println("Haciendo inducci"+OACUTE+"n");
					System.out.println("Hechos iniciales: ");
					System.out.println("Objetivo seleccionado: ");
					System.out.println(s);
				}
				res = this.engine.induccion(s);
				if (res.trim().equals("")) {
					res = "Nada que inducir.";
				}
				this.resultsText.setText(res);
				if (DEBUG) {
					System.out.println("Hecho seleccionado: " + s);
				}
				break;
			}
		}
		objectiveSelector.setVisible(false);
		objectiveSelector.dispose();
		return res;
	}

	private void showObjectiveSelector() {
		if (this.engine == null) {
			this.resultsText.setText("Nada que inducir.");
			return;
		}
		objectiveSelector = new JFrame("Seleccione el objetivo");
		this.setIcon(objectiveSelector);

		objectiveSelector.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		String[] columns = { "", "Objetivos posibles" };

		ArrayList<String> objectives = new ArrayList<String>();
		for(String s: engine.getObjetivos()){
			objectives.add(s);	
		}
		
		Collections.sort(objectives);
		
		Object[][] data = new Object[objectives.size()][2];

		int i = 0;
		for (String s : objectives) {
			data[i][0] = false;
			data[i][1] = s;
			i++;
		}
		
		c.gridx = 0;
		c.gridy = 0;
		objectivesTable = new CheckBoxTable(columns, data);
		objectiveSelector.add(objectivesTable, c);

		JButton okButton = new JButton("Ok");
		okButton.setActionCommand(OBJECTIVES_OK);
		okButton.addActionListener(this);
		c.gridy++;
		objectiveSelector.add(okButton, c);
		objectiveSelector.pack();
		objectiveSelector.setSize(objectiveSelector.getWidth() + 5,
				objectiveSelector.getHeight() + 5);
		objectiveSelector.setLocationRelativeTo(null);
		objectiveSelector.setVisible(true);
	}

	private String deduce() {
		if (this.engine == null)
			return "Nada que deducir.";
		
		for(int i = 0; i < initialFactsTable.getRowCount(); i++){
			String signo = (String) initialFactsTable.getValueAt(i, 0);
			if(!signo.trim().equals("")){
				String hecho = (String)initialFactsTable.getValueAt(i, 1);
				this.engine.agregaHecho(hecho, signo);
			}
		}
		String res = engine.deduccion();
		if (res.trim().equals(""))
			return "Nada que deducir.";
		return res;
	}

	private String induce() {
		if (this.engine == null)
			return "Nada que deducir.";
	
		for(int i = 0; i < initialFactsTable.getRowCount(); i++){
			String signo = (String) initialFactsTable.getValueAt(i, 0);
			if(!signo.trim().equals("")){
				String hecho = (String)initialFactsTable.getValueAt(i, 1);
				this.engine.agregaHecho(hecho, signo);
			}
		}
		
		this.showObjectiveSelector();
		return "resultados inducci�n";
	}
	
	private String abduce(){
		if(this.engine == null)
			return "Nada que deducir";
		
		for(int i = 0; i < initialFactsTable.getRowCount(); i++){
			String signo = (String) initialFactsTable.getValueAt(i, 0);
			if(!signo.trim().equals("")){
				String hecho = (String) initialFactsTable.getValueAt(i, 1);
				this.engine.agregaHecho(hecho, signo);
			}
		}
		
		String res = engine.abduccion();
		if (res.trim().equals(""))
			return "Nada que abducir.";
		return res;
	}

	private void run() {
		switch (this.algorithm) {
		case GUI.RUN_DEDUCTION:
			if(DEBUG){
				System.out.println("Corriendo deduccion");
			}
			this.engine.cambiaTipoEvaluacion(this.criteria);
			String res = this.deduce();
			this.resultsText.setText(res);
			break;
		case GUI.RUN_INDUCTION:
			if(DEBUG){
				System.out.println("Corriendo induccion");
			}
			this.engine.cambiaTipoEvaluacion(this.criteria);
			res = this.induce();
			break;
		case GUI.RUN_ABDUCTION:
			if(DEBUG){
				System.out.println("Corriendo abduccion");
			}
			this.engine.cambiaTipoEvaluacion(this.criteria);
			this.engine.setPorcentaje(this.percentage);
			res = this.abduce();
			this.resultsText.setText(res);
			break;
		case GUI.RUN_PROBABILISTIC:
			if(DEBUG){
				System.out.println("Corriendo suma probabilistica");
			}
			res = this.sumaProb();
			continueButton.setVisible(true);
			this.resultsText.setText(res);
			break;
		}
	}
	private String sumaProb (){
		if (this.engine == null)
			return "Nada que deducir.";
		
		for(int i = 0; i < initialFactsTable.getRowCount(); i++){
			String signo = (String) initialFactsTable.getValueAt(i, 0);
			if(!signo.trim().equals("")){
				String hecho = (String)initialFactsTable.getValueAt(i, 1);
				this.engine.agregaHecho(hecho, signo);
			}
		}
		String res = engine.sumaProb();
		if (res.trim().equals(""))
			return "Nada que mostrar.";
		return res;
	}

	private void delete() {
		this.resultsText.setText("");
		for (int i = 0; i < initialFactsTable.getRowCount(); i++) {
			initialFactsTable.setValueAt(INITIAL_FACT_STATUS, i, 0);
		}
		if (this.engine != null) {
			this.engine.borrar();
			JOptionPane.showMessageDialog(null, "Se han borrado"
					+ " todas las listas de hechos y los c"+AACUTE+"lculos hechos "
					+ "hasta el momento.", "Borrar",
					JOptionPane.INFORMATION_MESSAGE);
			if (DEBUG) {
				System.out.println("Se borraron los c"+AACUTE+"lculos hasta el momento");
			}
		}
	}
	
	private void continuar (){
		if(DEBUG){
			System.out.println("Corriendo continuar");
		}
		String res = this.engine.continuar();
		if (res.equals("")){
			this.continueButton.setEnabled(false);
		}
		else {
			this.resultsText.setText(res);
		}
	}

	public void actionPerformed(ActionEvent ae) {
		String action = ae.getActionCommand();
		if (action.equals(OPEN)) {
			this.openDatabase();
			this.continueButton.setVisible(false);
		} else if (action.equals(DELETE)) {
			this.delete();
			this.continueButton.setVisible(false);
		} else if (action.equals(RUN)) {
			this.continueButton.setVisible(false);
			this.run();
		} else if (action.equals(CONTINUAR)) {
			this.continuar();
		} else if (action.equals(DEDUCE)) {
			if(DEBUG){
				System.out.println("Elegir deduccion");
			}
			this.algorithm = GUI.RUN_DEDUCTION;
		} else if (action.equals(INDUCE)) {
			if(DEBUG){
				System.out.println("Elegir induccion");
			}
			this.algorithm = GUI.RUN_INDUCTION;
		} else if (action.equals(SUMA_PROB)) {
			if(DEBUG){
				System.out.println("Elegir suma probabilistica");
			}
			this.algorithm = GUI.RUN_PROBABILISTIC;
		} else if (action.equals(ABDUCE)) {
			if(DEBUG){
				System.out.println("Elegir abduccion");
			}
			this.algorithm = GUI.RUN_ABDUCTION;
			if (!INCERTIDUMBRE){
				String percentage = JOptionPane.showInputDialog(
						"Porcentaje requerido de hechos conocidos para hacer intuici"+OACUTE+"n (Ej: 0.8):",
						"" + this.percentage);
				if(percentage == null){
					return;
				}
				try{
					this.percentage = Double.parseDouble(percentage);
				} catch(NumberFormatException nfe){
				}
			}
		} else if (action.equals(OBJECTIVES_OK)) {
			this.objectivesOk();
		} else if (action.equals(EXIT)) {
			this.dispose();
			this.setVisible(false);
			System.exit(0);
		} else if (action.equals("basedeconocimiento")) {
			String s = GUI.readTextFromJar("rules/basedeconocimiento.txt");
			this.loadDatabase(s);
			this.continueButton.setVisible(false);
		} else if (action.equals(TOTAL_CRITERIA)) {
			this.criteria = "Total";
			if(DEBUG){
				System.out.println("Elegir criterio total: " + this.criteria);
			}
		} else if (action.equals(MAY_CRITERIA)) {
			this.criteria = "Mayoria";
			if(DEBUG){
				System.out.println("Elegir criterio mayoria: " + this.criteria);
			}
		} else if (action.equals(DEFINICIONES)) {
			System.out.println("definiciones");
            HelpSystem hs = new HelpSystem("definitions/index.html", 450, 450);
            hs.setVisible(true);
		} else if (action.equals(MANUAL)) {
			System.out.println("manual");
			//HelpSystem hs = new HelpSystem("help/index.html", 800,600);
			//hs.setVisible(true);
			File manual = new File("ManualUsuario.doc");
			try{
				if(Desktop.isDesktopSupported()){
					Desktop d = Desktop.getDesktop();
					d.open(manual);
				}
			}catch(Exception e){
				JOptionPane.showMessageDialog(null, "Se necesita Microsoft Office para ver el manual.");
				e.printStackTrace();
			}
		} else if (action.equals(ACERCADE)) {
			System.out.println("acerca de");
	         HelpSystem hs = new HelpSystem("about/index.html", 330, 300);
	         hs.setVisible(true);
		} else {
			File f = new File(ae.getActionCommand());
			this.loadDatabase(f);
			this.continueButton.setVisible(false);
		}
	}

	private class TextFileFilter extends FileFilter {
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			String ext = getExtension(f);
			if (ext != null) {
				if (ext.equals("txt")) {
					return true;
				}
			}
			return false;
		}

		public String getDescription() {
			return "Archivos de texto (*.txt)";
		}
	}

	private static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	private ImageIcon createImage(String path) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + imgURL);
			return null;
		}
	}

	public static String readTextFromJar(String s) {
		InputStream is = null;
		BufferedReader br = null;
		String line;
		StringBuffer buffer = new StringBuffer();

		try {
			is = GUI.class.getResourceAsStream(s);
			br = new BufferedReader(new InputStreamReader(is));
			while (null != (line = br.readLine())) {
				buffer.append(line);
				buffer.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return buffer.toString();
	}

	public static void main(String args[]) {
		GUI g = new GUI();
		g.setVisible(true);
	}
}