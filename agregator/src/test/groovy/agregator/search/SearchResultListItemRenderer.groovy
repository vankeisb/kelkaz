package agregator.search

import javax.swing.JLabel
import javax.swing.ListCellRenderer
import java.awt.Component
import javax.swing.JList
import java.awt.Color

public class SearchResultListItemRenderer extends JLabel implements ListCellRenderer {

  public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean b1) {
    setText("$o.result.title - $o.result.shortText (from $o.cartridge.name)")
    setOpaque(true) 
    if (isSelected) {
      setBackground(Color.LIGHT_GRAY)
    } else {
      setBackground(Color.WHITE) 
    }
    return this
  }

}