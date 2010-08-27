package agregator.ui

import javax.swing.JPanel
import java.awt.BorderLayout

class PanelStacker {

  static def stackPanels(panels, orientation) {
    JPanel res = new JPanel(layout: new BorderLayout())
    if (panels.size() == 2) {
      res.add(panels[0], orientation)
      res.add(panels[1], BorderLayout.CENTER)
    } else if (panels.size()>2) {
      def newPanels = []
      newPanels.addAll(panels)
      def p = panels[0]
      newPanels.remove(0)
      res.add(p, orientation)
      res.add(stackPanels(newPanels, orientation), BorderLayout.CENTER)
      return res;
    } else {
      throw new IllegalStateException("should not happen")
    }
    return res
  }

  static def stackPanels(panels, orientation, border) {
    def res = stackPanels(panels, orientation)
    res.border = border
    return res
  }

}
