package agregator.immo

import agregator.ui.ResultsPanel
import javax.swing.JComponent
import java.awt.Color
import groovy.swing.SwingBuilder
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import java.awt.Image
import java.awt.BorderLayout as BL
import javax.swing.JPanel
import javax.swing.BoxLayout
import javax.swing.Box
import javax.swing.JLabel
import java.awt.Dimension
import java.text.SimpleDateFormat
import java.text.DateFormat
import javax.swing.JScrollPane
import javax.swing.SwingUtilities


class ImmoResultsPanel4 extends ResultsPanel<ImmoResult> {

  private JComponent component
  private JPanel panel

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat('dd/MM/yyyy')

  def ImmoResultsPanel4() {
    panel = new JPanel()
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
    component = new JScrollPane(panel)
  }

  private def createResultComponent(ImmoResult r) {
    def bgColor = Color.WHITE
    return new SwingBuilder().panel(
            layout: new BL(),
            border: BorderFactory.createEmptyBorder(2,2,2,2),
            maximumSize: new Dimension(1500, 88)) {
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

  void addResult(ImmoResult r) {
    def newPanel = createResultComponent(r)
    SwingUtilities.invokeLater {
      panel.add(newPanel)
    }
  }

  void clear() {
    SwingUtilities.invokeLater {
      panel.removeAll()
    }
  }

  JComponent getComponent() {
    return component
  }


}
