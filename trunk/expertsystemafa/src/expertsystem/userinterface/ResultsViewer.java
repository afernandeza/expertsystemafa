package expertsystem.userinterface;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class ResultsViewer extends JFrame{
	
	private JTextArea resultsText;

	static final long serialVersionUID = -7823452;
	
	public ResultsViewer(){
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		
		JLabel results = new JLabel("Resultados:");
		this.add(results, c);

		resultsText = new JTextArea();
		resultsText.setEditable(false);
		resultsText.setLineWrap(true);
		resultsText.setWrapStyleWord(true);

		JScrollPane resultsPane = new JScrollPane(resultsText);
		resultsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		resultsPane.setPreferredSize(new Dimension(300, 336));

		c.gridy = 1;
		this.add(resultsPane, c);
		this.pack();
		this.setSize(this.getWidth() + 10, this.getHeight() + 10);
		this.setLocationRelativeTo(null);
	}
	
	public void showResults(String results){
		this.resultsText.setText(results);
		this.setVisible(true);
	}
	
	public void clearResults(){
		this.resultsText.setText("");
	}
	
	public static void main(String args[]){
		ResultsViewer r = new ResultsViewer();
		r.setVisible(true);
	}
	
}
