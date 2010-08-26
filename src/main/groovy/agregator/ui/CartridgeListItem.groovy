package agregator.ui

import javax.swing.JLabel
import javax.swing.ImageIcon
import agregator.core.Cartridge
import java.awt.BorderLayout
import javax.swing.JPanel

public class CartridgeListItem extends JPanel {

  private Cartridge cartridge
  int resultCount = 0
  private JLabel iconLabel = new JLabel(' ')
  private JLabel label = new JLabel()

  public CartridgeListItem(Cartridge c) {
    this.cartridge = c
    setLayout(new BorderLayout())
    add(iconLabel, BorderLayout.WEST)
    add(label, BorderLayout.CENTER)
    label.text = "$c.name (0)"
    label.icon = c.icon
  }

  private void setIconFromStr(String s) {
    iconLabel.icon = new ImageIcon(getClass().getResource(s))
  }

  public void loading() {
    setIconFromStr '/ajax-loader.gif'
  }

  public void stopped() {
    setIconFromStr '/Ok-16x16.png'
  }

  public void error() {
    setIconFromStr '/Error-16x16.png' 
  }

  public void incrementResultCount() {
    resultCount++
    label.text = "$cartridge.name ($resultCount)" 
  }

}