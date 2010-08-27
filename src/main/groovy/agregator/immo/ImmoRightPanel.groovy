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

class ImmoRightPanel extends JPanel {

  static def createImmoRightPanel(Agregator agregator) {

    def ep1 = new JEditorPane("text/plain", """TrouvToo Immobilier est un agrégateur : il cherche pour vous dans plusieurs sites d'annonces immobilières. Au lieu de répéter vos recherches dans chaque site, saisissez vos critères et laisser TrouvToo chercher pour vous !

Remplissez le formulaire de recherche ci-contre, et cliquez sur le bouton "Rechercher" afin d'accéder aux résultats.""")

    ep1.editable = false

    def cartridgeLinks = []
    for (Cartridge c : agregator.cartridges) {
      cartridgeLinks <<  new HyperLink(c.name, c.icon, JLabel.LEFT, {
        def s = "http://$c.name"
        java.awt.Desktop.getDesktop().browse( new URL( s ).toURI() )
      })
    }
    cartridgeLinks << new JLabel('') // used by panel stacking

    def title = createSeparatorLabel('Bienvenue Dans TrouvToo Immobilier')

    def label2 = createSeparatorLabel("Sites supportés :")
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
