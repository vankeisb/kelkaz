package agregator.immo

import javax.swing.ListCellRenderer
import java.awt.Component
import javax.swing.JList
import javax.swing.JPanel
import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import javax.swing.JLabel
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.swing.JFrame
import javax.swing.BoxLayout
import javax.swing.Box
import java.awt.Dimension
import javax.swing.JEditorPane
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import java.awt.Image

class ImmoResultListCellRenderer implements ListCellRenderer {

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat('dd/MM/yyyy')

  def createComponent(ImmoResult r) {
    def bgColor = Color.WHITE
    return new SwingBuilder().panel(layout: new BL(), border: BorderFactory.createEmptyBorder(2,2,2,2)) {
      panel(constraints: BL.CENTER, layout: new BL(), background: bgColor) {
        def photoLabel = label(constraints: BL.WEST, border: BorderFactory.createEmptyBorder(2,2,2,2))
        ImageIcon icon = null
        try {
          if (r.photoUrl!=null) {
            icon = new ImageIcon(new URL(r.photoUrl))
            Image img = icon.getImage()
            Image newImg = img.getScaledInstance(120, 80,  Image.SCALE_SMOOTH)
            icon = new ImageIcon(newImg)
          }
          photoLabel.setIcon(icon)
        } catch(Exception e) {
          photoLabel.setIcon(null) // TODO use image with appropriate size
        }
        panel(constraints: BL.CENTER, layout: new BL(), background: bgColor) {
          label(text: r.title, constraints: BL.NORTH)
          scrollPane(constraints: BL.CENTER,border: BorderFactory.createEmptyBorder()) {
            editorPane(text: r.description, editable: false)
          }
          def bottomPane = new JPanel(background: bgColor)
          bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.LINE_AXIS))
          bottomPane.add(new JLabel("Prix"))
          bottomPane.add(Box.createRigidArea(new Dimension(5,0)));
          def priceLabel = new JLabel(r.price==null ? "" : Integer.toString(r.price))
          bottomPane.add(priceLabel)
          bottomPane.add(Box.createRigidArea(new Dimension(20,0)));
          bottomPane.add(new JLabel("Date"))
          bottomPane.add(Box.createRigidArea(new Dimension(5,0)));
          def dateLabel = new JLabel(r.date==null ? "" : DATE_FORMAT.format(r.date))
          bottomPane.add(dateLabel)
          widget(widget: bottomPane, constraints: BL.SOUTH)
        }
      }
    }
  }

  Component getListCellRendererComponent(JList jList, Object value, int index, boolean selected, boolean hasFocus) {
    return createComponent((ImmoResult)value)
  }

  static void main(String[] args) {
    ImmoResultListCellRenderer r  = new ImmoResultListCellRenderer()
    def f = new JFrame()
    f.setLayout new BL()
    f.setContentPane r.getListCellRendererComponent(
            null,
            new ImmoResult(
                    null,
                    "Location blah blah blah blah blah",
                    "http://abc.com/SQDQDQSDDQSDSQDSQSDSQDSQ",
                    "PROCHE PROMENADE/ NEGRESCO loue pr 2 étudiants bel appart 3P 75m2 tt conf insonorisé clim 2 balcons vue mer colline 1.080€CC Réf exigées T. 06.59.23.73.72",
                    1080,
                    new Date(),
                    'http://www.coderanch.com/templates/default/images/moosefly.gif'), 1, false, false)
    f.pack()
    f.setVisible true
  }


}
