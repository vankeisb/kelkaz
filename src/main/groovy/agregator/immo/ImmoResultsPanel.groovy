package agregator.immo

import agregator.ui.ResultsPanel
import agregator.core.Result
import javax.swing.JComponent
import groovy.swing.SwingBuilder
import javax.swing.JTable
import java.awt.event.MouseListener
import java.awt.event.MouseEvent

public class ImmoResultsPanel extends ResultsPanel<ImmoResult> implements MouseListener {
	
	JTable table
	ImmoResultsTableModel tableModel = new ImmoResultsTableModel()
	
	/**
	 * Use a JTable
	 */

	public void addResult(ImmoResult r) {
		tableModel.addResult(r)
	}

	public void clear() {
		tableModel.clear()
	}

	public JComponent getComponent() {
		return new SwingBuilder().scrollPane() {
			table = table(model: tableModel)
			table.setDefaultRenderer(Object.class, new ImmoResultsTableRenderer())
            table.addMouseListener(this)
		}
	}

  public void mouseClicked(MouseEvent mouseEvent) {
    if (mouseEvent.clickCount == 2) {
      def result = tableModel.getResult(table.getSelectedRow())
      this.fireResultSelected(result) 
    }
  }

  public void mouseEntered(MouseEvent mouseEvent) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void mouseExited(MouseEvent mouseEvent) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void mousePressed(MouseEvent mouseEvent) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void mouseReleased(MouseEvent mouseEvent) {
    //To change body of implemented methods use File | Settings | File Templates.
  }


}