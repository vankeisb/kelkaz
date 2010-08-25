package agregator.immo;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import agregator.core.Result;

public class ImmoResultsTableModel extends AbstractTableModel {
	
	ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle")
	def results = []
	
	def columnsName = []
	
	public ImmoResultsTableModel() {
        columnsName << messages.getString("column.site")
        columnsName << messages.getString("column.title")
        columnsName << messages.getString("column.description")
        columnsName << messages.getString("column.price")
        columnsName << messages.getString("column.date")
		columnsName << messages.getString("column.url")
	}
	
	public void addResult(Result result){
		results << result
		fireTableDataChanged()
	}
	
	public void clear(){
		results.removeAll(results)
		fireTableDataChanged()
	}
	
	public Class<?> getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass()
	}
	
	public int getColumnCount() {
		return columnsName.size()
	}
	
	public String getColumnName(int columnIndex) {
		return columnsName[columnIndex]
	}
	
	public int getRowCount() {
		return results.size
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
                return results.get(rowIndex).cartridge.name
			case 1:
                return results.get(rowIndex).title
			case 2:
				return results.get(rowIndex).description
            case 3:
                return results.get(rowIndex).price
            case 4:
                  return results.get(rowIndex).date
            case 5:
                  return results.get(rowIndex).url
			default:
				return "Error"
		}
	}

  public ImmoResult getResult(int index)  {
    return results[index]
  }
}
