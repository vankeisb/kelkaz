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
import java.awt.Font


class ImmoResultsPanel extends ResultsPanel<ImmoResult> {

  private JComponent component
  private JPanel panel
  private JLabel statusLabel
  private int nbResults = 0

  private ResourceBundle messages = ResourceBundle.getBundle('MessagesBundle');  

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat('dd/MM/yyyy')

  def ImmoResultsPanel() {
    panel = new JPanel()
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
    component = new JPanel(layout: new BL())
    def scrollPane = new JScrollPane(panel)
    scrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
    scrollPane.border = BorderFactory.createEmptyBorder()
    component.add(scrollPane, BL.CENTER)
    statusLabel = new JLabel()
    statusLabel.horizontalAlignment = JLabel.RIGHT
    component.add(statusLabel, BL.SOUTH)    
  }

  private def createResultComponent(ImmoResult r) {
    def bgColor = Color.WHITE
    return new SwingBuilder().panel(
            layout: new BL(),
            border: BorderFactory.createEmptyBorder(2,2,2,2),
            maximumSize: new Dimension(2000, 100),
            minimumSize: new Dimension(100, 100)) {
      panel(constraints: BorderLayout.CENTER, layout: new BL(), background: bgColor) {
        def photoLabel = label(constraints: BorderLayout.WEST, border: BorderFactory.createEmptyBorder(10,10,10,10))
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
          def titleLabel = label(text: r.title, constraints: BorderLayout.NORTH)
          def font = titleLabel.font
          titleLabel.font = new Font(font.name, font.style | Font.BOLD, font.size)
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
          def dateLabel = new JLabel(r.date==null ? "" : DATE_FORMAT.format(r.date))
          bottomPane.add(dateLabel)
          bottomPane.add(Box.createRigidArea(new Dimension(5,0)));
          bottomPane.add(new JLabel("-"))
          bottomPane.add(Box.createRigidArea(new Dimension(5,0)));
          def priceLabel = new JLabel(r.price==null ? "" : Integer.toString(r.price))
          bottomPane.add(priceLabel)
          bottomPane.add(Box.createRigidArea(new Dimension(3, 0)));
          def euroSign = new JLabel(
                  text: messages.getString("currency.euro"),
                  horizontalAlignment: JLabel.LEFT)          
          bottomPane.add(euroSign)
          widget(widget: bottomPane, constraints: BorderLayout.SOUTH)
        }
      }
    }
  }

  void addResult(ImmoResult r) {
    nbResults++
    SwingUtilities.invokeLater {
      def newPanel = createResultComponent(r)
      panel.add(newPanel)
      def s = messages.getString('status.results.count')
      statusLabel.text = "$nbResults $s"
    }
  }

  void clear() {
    nbResults = 0
    SwingUtilities.invokeLater {
      panel.removeAll()
      statusLabel.text = null
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
