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
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.ScrollPaneConstants


class ImmoResultsPanel extends ResultsPanel<ImmoResult> {

  private JComponent component
  private JPanel resultsPanel

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat('dd/MM/yyyy')

  def ImmoResultsPanel() {
    resultsPanel = new JPanel()
    resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS))
    component = new JScrollPane(resultsPanel)
    component.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

  }

  private def createResultComponent(ImmoResult r) {
    def bgColor = Color.WHITE
    return new SwingBuilder().panel(
            layout: new BL(),
            border: BorderFactory.createEmptyBorder(2,2,2,2),
            maximumSize: new Dimension(2000, 88),
            minimumSize: new Dimension(100, 88)) {
      panel(constraints: BorderLayout.CENTER, layout: new BL(), background: bgColor) {
        def photoLabel = label(constraints: BorderLayout.WEST, border: BorderFactory.createEmptyBorder(2,2,2,2))
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
        panel(constraints: BorderLayout.CENTER, layout: new BL(), background: bgColor) {
          label(text: r.title, constraints: BorderLayout.NORTH)
          editorPane(
                    constraints: BorderLayout.CENTER,
                    text: r.description,
                    editable: false,
                    border: BorderFactory.createEmptyBorder(),
                    minimumSize: new Dimension(100, 30),
                    maximumSize: new Dimension(200, 30),
          )
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
          widget(widget: bottomPane, constraints: BorderLayout.SOUTH)
        }
      }
    }
  }

  void addResult(ImmoResult r) {
    SwingUtilities.invokeLater {
      def newPanel = createResultComponent(r)
      resultsPanel.add(newPanel)
    }
  }

  void clear() {
    SwingUtilities.invokeLater {
      resultsPanel.removeAll()
    }
  }

  JComponent getComponent() {
    return component
  }

  public static void main(String[] args) {
    JFrame f = new JFrame()
    f.setLayout new BL()
    ImmoResultsPanel i = new ImmoResultsPanel()
    i.addResult(new ImmoResult(
                    null,
                    "Location blah blah blah blah blah",
                    "http://abc.com/SQDQDQSDDQSDSQDSQSDSQDSQ",
                    "PROCHE PROMENADE/ NEGRESCO loue pr 2 étudiants bel appart 3P 75m2 tt conf insonorisé clim 2 balcons vue mer colline 1.080€CC Réf exigées T. 06.59.23.73.72",
                    1080,
                    new Date(),
                    'http://www.coderanch.com/templates/default/images/moosefly.gif'))
    f.contentPane.add(i.getComponent())
    f.pack()
    f.setVisible true
  }


}
