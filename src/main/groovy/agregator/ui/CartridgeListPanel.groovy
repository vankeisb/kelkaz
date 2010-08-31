package agregator.ui

import javax.swing.JPanel
import agregator.core.AgregatorListener
import agregator.core.AgregatorEvent
import agregator.core.CartridgeEvent
import java.util.concurrent.ConcurrentHashMap
import javax.swing.SwingUtilities
import javax.swing.BoxLayout
import java.awt.Component
import agregator.core.Agregator
import agregator.core.Cartridge
import javax.swing.BorderFactory

public class CartridgeListPanel extends JPanel implements AgregatorListener {

  private def items = new ConcurrentHashMap()
  private final Agregator agregator

  static ResourceBundle messages = ResourceBundle.getBundle('MessagesBundle');  

  public CartridgeListPanel(Agregator agregator) {
    this.agregator = agregator
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
    this.add(PanelStacker.createSeparatorLabel(messages.getString("supported.sites")))
    for (Cartridge c : agregator.cartridges) {
      def cmp = new CartridgeListItem(c)
      items.put(c, cmp)
      this.add(cmp)
    }
    setBorder(BorderFactory.createEmptyBorder(4,4,4,4))
  }

  public void onEvent(AgregatorEvent event) {
    if (event instanceof AgregatorEvent.AgregatorCartridgeEvent) {
      def ce = event.cartridgeEvent
      def cartridge = ce.source
      if (ce instanceof CartridgeEvent.StartedEvent) {
        // cartridge started, add cartridge to list
        def cmp = items.get(cartridge)
        if (cmp) {
          cmp.loading()
        }
      } else if (ce instanceof CartridgeEvent.ResultEvent) {
        // cartridge result, increment count for the cartridge
        def cmp = items.get(cartridge)
        if (cmp) {
          cmp.incrementResultCount()
        }
      } else if (ce instanceof CartridgeEvent.EndedEvent) {
        // cartridge ended, update working state
        def cmp = items.get(cartridge)
        if (cmp) {
          cmp.stopped()
        }
      } else if (ce instanceof CartridgeEvent.ErrorEvent) {
        // cartridge error
        def cmp = items.get(cartridge)
        if (cmp) {
          cmp.error()
        }
      }
    }
  }

  public void clear() {
    items = new ConcurrentHashMap()
    SwingUtilities.invokeLater {
      removeAll()
      revalidate()
    }
  }

}