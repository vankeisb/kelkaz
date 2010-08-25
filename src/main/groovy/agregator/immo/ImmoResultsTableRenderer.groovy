package agregator.immo

import javax.swing.JPanelimport java.awt.BorderLayoutimport javax.swing.JLabelimport java.awt.Colorimport javax.swing.table.TableCellRendererimport java.awt.Componentimport javax.swing.JTable

public class ImmoResultsTableRenderer implements TableCellRenderer{
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
		JPanel panel = new JPanel()
		panel.setLayout(new BorderLayout())
		JLabel label = new JLabel(value.toString())
		if (row % 2 == 0){
			panel.setBackground(new Color(255, 239, 213));
		}else{
			panel.setBackground(new Color(240, 255, 255));
		}
		panel.add(label, BorderLayout.WEST);
		return panel;
	}
}
