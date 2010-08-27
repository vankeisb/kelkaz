package agregator.ui

import javax.swing.JPanel
import java.awt.BorderLayout
import javax.swing.JTextField
import java.awt.Dimension
import javax.swing.JLabel
import javax.swing.BorderFactory
import java.awt.Font
import javax.swing.ButtonGroup
import java.awt.Color

class PanelStacker {

  static int tfHeight = new JTextField().height  

  static def addBorder(cmp, border) {
    cmp.border = border
    return cmp
  }

  static def stackPanels(panels, orientation, bgColor) {
    JPanel res = new JPanel(layout: new BorderLayout())
    if (bgColor) {
      res.background = bgColor
    }
    if (panels.size() == 2) {
      res.add(panels[0], orientation)
      res.add(panels[1], BorderLayout.CENTER)
    } else if (panels.size()>2) {
      def newPanels = []
      newPanels.addAll(panels)
      def p = panels[0]
      newPanels.remove(0)
      res.add(p, orientation)
      res.add(stackPanels(newPanels, orientation, bgColor), BorderLayout.CENTER)
      return res;
    } else {
      throw new IllegalStateException("should not happen")
    }
    return res
  }

  static JPanel stackPanels(panels, orientation) {
    return stackPanels(panels, orientation, null)
  }

  static JTextField createTextField(int prefWidth) {
    def min = new Dimension(50, tfHeight)
    def max = new Dimension(150, tfHeight)
    def pref = new Dimension(prefWidth, tfHeight)
    new JTextField(minimumSize:min, maximumSize:max, preferredSize: pref)
  }

  static JTextField createTextField() {
    return createTextField(100)
  }

  static def createFormLabel(String text) {
    new JLabel(text:text, border: BorderFactory.createEmptyBorder(2,2,2,4))
  }

  static def createSeparatorLabel(String text) {
    def l = new JLabel(text:text, border: BorderFactory.createEmptyBorder(8,0,4,0))
    l.font = new Font(l.font.name, l.font.style | Font.BOLD, l.font.size + 4)
    return l
  }

  static def createSeparator() {
    Dimension d = new Dimension(8,8)
    new JLabel(text:'', preferredSize: d, maximumSize: d, minimumSize: d)
  }

  static def groupButtons(buttons) {
    ButtonGroup g = new ButtonGroup()
    buttons.each { g.add it }
    return g
  }


}
