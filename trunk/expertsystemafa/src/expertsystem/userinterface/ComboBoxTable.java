package expertsystem.userinterface;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class ComboBoxTable extends JPanel {
    
    static final long serialVersionUID = -1234567890;
    
    private static final int COMBOBOX_WIDTH = 60;
    
    private static final String[] COMBOBOX_OPTIONS = {"+", "-", " "};
    
    private boolean DEBUG = false;
    private JTable table;
    private MyTableModel model;

    public ComboBoxTable(String[] columnNames, Object[][] data) {
        super(new GridLayout(1, 0));

        model = new MyTableModel(columnNames, data);
        table = new JTable(model);
        
        TableColumn column = null;
        for(int i = 0; i < 2; i++){
            column = table.getColumnModel().getColumn(i);
            if(i == 0){
            	JComboBox sign = new JComboBox();
            	for(String s: COMBOBOX_OPTIONS){
            		sign.addItem(s);
            	}
                column.setPreferredWidth(COMBOBOX_WIDTH);   
                column.setCellEditor(new DefaultCellEditor(sign));
                column.setCellRenderer(new MyComboBoxRenderer(COMBOBOX_OPTIONS));
            } else{
                column.setPreferredWidth(GUI.FACTS_WIDTH - COMBOBOX_WIDTH);
            }
        }

        table.setPreferredScrollableViewportSize(new Dimension(GUI.FACTS_WIDTH, 
                GUI.FACTS_HEIGHT));
        // Esta llamada la quite para agregar funcionalidad con java 5
        //table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        JScrollPane scrollPane = new JScrollPane(table);
        //scrollPane.setPreferredSize(new Dimension(GUI.FACTS_WIDTH, GUI.FACTS_HEIGHT));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        add(scrollPane);
    }
    
    public JTable getJTable(){
        return this.table;
    }
    
    public int getRowCount() {
        return this.model.getRowCount();
    }
    
    public Object getValueAt(int row, int col) {
        return this.model.getValueAt(row, col);
    }
    
    public void setValueAt(Object value, int row, int col){
        this.model.setValueAt(value, row, col);
    }
    
    public void initializeArray(int rows, int cols){
       this.model.initializeArray( rows, cols);
    }
    
    class MyTableModel extends AbstractTableModel {
        
        static final long serialVersionUID = -1234567891;
        
        private String[] columnNames;
        private Object[][] data;

        public MyTableModel(String[] columnNames, Object[][] data) {
            this.columnNames = columnNames;
            this.data = data;
            
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public Class<?> getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            if (col == 0) {
                return true;
            } else {
                return false;
            }
        }

        public void initializeArray(int rows, int cols){
            this.data = new Object[rows][cols];
        }
        
        public void setValueAt(Object value, int row, int col) {
            if (DEBUG) {
                System.out.println("Setting value at " + row + "," + col
                        + " to " + value + " (an instance of "
                        + value.getClass() + ")");
            }

            data[row][col] = value;
            //fireTableCellUpdated(row, col);
            this.fireTableDataChanged();

            if (DEBUG) {
                System.out.println("New value of data:");
                printDebugData();
            }
        }

        private void printDebugData() {
            int numRows = getRowCount();
            int numCols = getColumnCount();

            for (int i = 0; i < numRows; i++) {
                System.out.print("    row " + i + ":");
                for (int j = 0; j < numCols; j++) {
                    System.out.print("  " + data[i][j]);
                }
                System.out.println();
            }
            System.out.println("--------------------------");
        }
    }
    
    class MyComboBoxRenderer extends JComboBox implements TableCellRenderer {
    	
    	static final long serialVersionUID = -12213443;
    	
        public MyComboBoxRenderer(String[] items) {
            super(items);
        }
    
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
    
            // Select the current value
            setSelectedItem(value);
            return this;
        }
    }
}