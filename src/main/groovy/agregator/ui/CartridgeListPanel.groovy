package agregator.ui

import javax.swing.JPanel
import agregator.core.AgregatorListener
import agregator.core.AgregatorEvent
import agregator.core.CartridgeEvent
import java.util.concurrent.ConcurrentHashMap
import javax.swing.SwingUtilities
import javax.swing.BoxLayout
import java.awt.Component

public class CartridgeListPanel extends JPanel implements AgregatorListener {

  private def items = new ConcurrentHashMap()

  public CartridgeListPanel() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  }

  public void onEvent(AgregatorEvent event) {
    if (event instanceof AgregatorEvent.AgregatorCartridgeEvent) {
      def ce = event.cartridgeEvent
      def cartridge = ce.source
      if (ce instanceof CartridgeEvent.StartedEvent) {
        // cartridge started, add cartridge to list
        SwingUtilities.invokeLater {
          def cmp = new CartridgeListItem(cartridge)
          items.put(cartridge, cmp)
          this.add(cmp)
          cmp.loading()
          validate()
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
      validate()
    }
  }

}