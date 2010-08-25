package agregator.ui

import javax.swing.JLabel
import javax.swing.ImageIcon
import agregator.core.Cartridge

public class CartridgeListItem extends JLabel {

  private Cartridge cartridge
  int resultCount = 0

  public CartridgeListItem(Cartridge c) {
    this.cartridge = c
    setText("$c.name (0)")
  }

  private void setIconFromStr(String s) {
    setIcon(new ImageIcon(getClass().getResource(s)))
  }

  public void loading() {
    setIconFromStr '/bigrotation2.gif'
  }

  public void stopped() {
    setIconFromStr '/Ok-32x32.png'
  }

  public void error() {
    setIconFromStr '/Error-32x32.png' 
  }

  public void incrementResultCount() {
    resultCount++
    setText("$cartridge.name ($resultCount)") 
  }

}