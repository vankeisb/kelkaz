package agregator.ui

import javax.swing.JLabel
import java.awt.event.MouseListener
import java.awt.event.MouseEvent
import java.awt.Color
import java.awt.Cursor
import java.awt.font.TextAttribute

class HyperLink extends JLabel implements MouseListener {

  private final Closure c

  private static final Color LINK_COLOR = Color.BLUE

  def HyperLink(s, icon, i, c) {
    super(s, icon, i)
    this.c = c
    init()
  }

  def HyperLink(s, i, c) {
    super(s, i)
    this.c = c
    init()
  }

  def HyperLink(s, c) {
    super(s)
    this.c = c
    init()
  }

  def HyperLink(c) {
    this.c = c
    init()
  }

  private def init() {
    setForeground LINK_COLOR
    def fontAttrs = font.attributes
    fontAttrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON)
    def newFont = font.deriveFont(fontAttrs)
    font = newFont
    addMouseListener this
  }

  void mouseClicked(MouseEvent mouseEvent) {
    if (c) {
      c.call(mouseEvent)
    }
  }

  void mousePressed(MouseEvent mouseEvent) {
  }

  void mouseReleased(MouseEvent mouseEvent) {
  }

  void mouseEntered(MouseEvent mouseEvent) {
    setCursor Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
  }

  void mouseExited(MouseEvent mouseEvent) {
    setCursor Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
  }

}
