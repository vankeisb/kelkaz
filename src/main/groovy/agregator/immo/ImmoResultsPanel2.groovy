package agregator.immo

import agregator.ui.ResultsPanel
import javax.swing.JComponent
import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import javax.swing.SwingConstants
import javax.swing.SwingUtilities

class ImmoResultsPanel2 extends ResultsPanel<ImmoResult> {

  def model = new ImmoResultsListModel()

  void addResult(ImmoResult r) {
    SwingUtilities.invokeLater {
      model.addResult(r)      
    }
  }

  void clear() {
    model.clear()
  }

  JComponent getComponent() {
    return new SwingBuilder().panel(layout: new BL()) {
      scrollPane(constraints: BL.CENTER) {
        list(model: model, cellRenderer: new ImmoResultListCellRenderer())
      }
      label(
              constraints: BL.SOUTH,
              text: "nb results here",
              horizontalAlignment: SwingConstants.RIGHT)
    }
  }


}
