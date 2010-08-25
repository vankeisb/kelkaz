package agregator.immo;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import agregator.core.Result;

public class ImmoResultsTableModel extends AbstractTableModel {
	
	ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle");
	def results = []
	
	def columnsName = []
	
	public ImmoResultsTableModel() {
		columnsName << messages.getString("column.title")
		columnsName << messages.getString("column.url")
		columnsName << messages.getString("column.site")
	}
	
	public void addResult(Result result){
		results << result
		fireTableDataChanged()
	}
	
	public void clear(){
		results.removeAll(results);
		fireTableDataChanged();
	}
	
	public Class<?> getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass();
	}
	
	public int getColumnCount() {
		return columnsName.size;
	}
	
	public String getColumnName(int columnIndex) {
		return columnsName[columnIndex];
	}
	
	public int getRowCount() {
		return results.size;
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
				return results.get(rowIndex).getTitle();
			case 1:
				return results.get(rowIndex).getUrl();
			case 2:
				return results.get(rowIndex).getCartridge().getName();
			default:
				return "Error"
		}
	}

  public ImmoResult getResult(int index)  {
    return results[index]
  }
}
