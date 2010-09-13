package agregator.immo

import javax.swing.JPanel
import java.awt.BorderLayout
import static agregator.ui.PanelStacker.*
import javax.swing.JEditorPane
import agregator.ui.HyperLink
import javax.swing.JLabel
import agregator.core.Cartridge
import agregator.core.Agregator
import java.awt.Color
import javax.swing.BorderFactory
import agregator.ui.Util
import javax.swing.JTextArea

class ImmoRightPanel extends JPanel {

  static def createImmoRightPanel(Agregator agregator) {
    def ep1 = new JEditorPane("text/html", Util.getMessage("welcome.message"))
    
    ep1.editable = false

    def cartridgeLinks = []
    for (Cartridge c : agregator.cartridges) {
      def cmp = new HyperLink(c.name, c.icon, JLabel.LEFT, {
        def s = "http://$c.name"
        java.awt.Desktop.getDesktop().browse( new URL( s ).toURI() )
      })
      cmp.border = BorderFactory.createEmptyBorder(4,4,4,4)
      cartridgeLinks <<  cmp
    }
    cartridgeLinks << new JLabel('') // used by panel stacking

    def title = createSeparatorLabel(Util.getMessage("welcome.title"))

    def label2 = createSeparatorLabel(Util.getMessage("supported.sites"))
    label2.background = Color.white
    label2.opaque = true

    def cartridgesPanel = addBorder(stackPanels(cartridgeLinks, BorderLayout.NORTH, Color.white), BorderFactory.createEmptyBorder(4,4,4,4))

    def p = addBorder(stackPanels([
            title,
            ep1,
            label2,
            cartridgesPanel
    ], BorderLayout.NORTH, Color.white), BorderFactory.createEmptyBorder(0, 10, 10, 10))

    p.background = Color.white

    return p

  }
}
