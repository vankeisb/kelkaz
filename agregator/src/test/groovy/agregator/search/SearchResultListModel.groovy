package agregator.search

import javax.swing.DefaultListModel

public class SearchResultListModel extends DefaultListModel {

  public void clear() {
    removeAllElements()
    fireContentsChanged(this, 0, 0) 
  }


}