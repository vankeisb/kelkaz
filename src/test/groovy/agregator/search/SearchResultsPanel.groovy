package agregator.search

import agregator.ui.ResultsPanel
import javax.swing.JTextArea
import javax.swing.JComponent
import groovy.swing.SwingBuilder
import javax.swing.ListSelectionModel
import agregator.core.Result
import agregator.core.Exclusions

public class SearchResultsPanel extends ResultsPanel {

  JTextArea textArea = new JTextArea()
  SearchResultListModel listModel = new SearchResultListModel()

  SearchResultsPanel(Exclusions excludedResults) {
    super(excludedResults);
  }

  public void doAddResult(Result result) {
    listModel.addElement(new SearchResultListItem(result:result, cartridge:result.cartridge)) 
    textArea.text = textArea.text + "$result.title\n$result.shortText\nurl : $result.url\nCatridge : $result.cartridge.name\n\n"
  }

  public void clear() {
    listModel.clear()
  }

  public JComponent getComponent() {
    return new SwingBuilder().scrollPane() {
      list(cellRenderer:new SearchResultListItemRenderer(),
              model:listModel,
              selectionMode:ListSelectionModel.SINGLE_SELECTION,
              mouseClicked: { e ->
                // ask for opening result if dbl click
                if (e.getClickCount() == 2 && !e.isConsumed()) {
                  e.consume();
                  def selectedItem = e.source.selectedValue
                  if (selectedItem) {
                    fireResultSelected(selectedItem.result)
                  }
                }
              }
      )
    }
  }


}