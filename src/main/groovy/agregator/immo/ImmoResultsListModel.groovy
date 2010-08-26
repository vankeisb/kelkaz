package agregator.immo

import javax.swing.AbstractListModel

class ImmoResultsListModel extends AbstractListModel {

  private def results = []

  Object getElementAt(int i) {
    return results[i]
  }

  int getSize() {
    return results.size()
  }

  def clear() {
    int s = results.size()
    results = []
    fireIntervalRemoved(this, 0, s)
  }

  def addResult(ImmoResult r) {
    results << r
    int s = results.size()
    fireIntervalAdded(this, s-1, s)
  }
}
