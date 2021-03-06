package agregator.ui

import javax.swing.JLabel
import javax.swing.ImageIcon
import agregator.core.Cartridge
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.BoxLayout
import javax.swing.BorderFactory

public class CartridgeListItem extends JPanel {

  private Cartridge cartridge
  int resultCount = 0
  private JLabel iconLabel = new JLabel(' ')
  private HyperLink label
  private JLabel countLabel = new JLabel('')

  def CartridgeListItem(Cartridge c) {
    this.cartridge = c
    this.border = BorderFactory.createEmptyBorder(5,5,5,5)
    this.alignmentX = LEFT_ALIGNMENT
    label = new HyperLink('', {
      def s = "http://$c.name"
      java.awt.Desktop.getDesktop().browse( new URL( s ).toURI() )      
    })
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS))
    add(iconLabel)
    add(label)
    add(countLabel)
    label.text = "$c.name"
    label.icon = c.icon
    stopped()
  }

  void setIconFromStr(String s) {
    iconLabel.icon = new ImageIcon(getClass().getResource(s))
  }

  void loading() {
    setIconFromStr '/ajax-loader.gif'
  }

  void stopped() {
    setIconFromStr '/Ok-16x16.png'
    countLabel.text = " ($resultCount)" 
  }

  void error() {
    setIconFromStr '/Error-16x16.png' 
  }

  void reset() {
    countLabel.text = ''
    resultCount = 0
  }

  void incrementResultCount() {
    resultCount++
    countLabel.text = " ($resultCount)" 
  }

}