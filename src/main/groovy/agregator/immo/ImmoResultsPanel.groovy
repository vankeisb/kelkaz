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
import agregator.ui.HyperLink
import javax.swing.JTextField
import javax.swing.JButton
import java.awt.event.ActionListener
import java.util.concurrent.ConcurrentHashMap


class ImmoResultsPanel extends ResultsPanel<ImmoResult> {

  private JComponent component
  private JPanel panel
  private JLabel statusLabel
  private JPanel headerPanel
  private JTextField searchField
  private int nbResults = 0
  private ConcurrentHashMap resultsAndPanels = new ConcurrentHashMap() // result/component map used to remove from list

  private ResourceBundle messages = ResourceBundle.getBundle('MessagesBundle');  

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat('dd/MM/yyyy')

  private static final ImageIcon NO_PHOTO = new ImageIcon(ImmoResultsPanel.class.getResource("/no-photo.gif"))

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

    searchField = new JTextField(text:'')
    searchField.addActionListener({ filter() } as ActionListener)
    JButton btnFilter = new JButton(text:'Filtrer')
    btnFilter.addActionListener({ filter() } as ActionListener)

    // header panel
    headerPanel = new SwingBuilder().panel(
            visible: false,
            border: BorderFactory.createEmptyBorder(5,5,5,5)) {
      boxLayout(axis: BoxLayout.X_AXIS)
      label('Trier par ')
      widget(new HyperLink("prix", {
        sortPrice()
      }))
      label(' | ')
      widget(new HyperLink("date", {
        sortDate()
      }))
      label(' | ')
      widget(new HyperLink("fournisseur", {
        sortCartridge()
      }))
      label('         ')
      widget(Box.createHorizontalGlue())
      widget(searchField)
      label(' ')
      widget(btnFilter)       
    }
    component.add(headerPanel, BL.NORTH)
  }

  private boolean matches(ImmoResult r, String crit) {
    if (!crit) {
      return true
    }
    if (r.title && r.title.toLowerCase().indexOf(crit.toLowerCase())>0) {
      return true
    }
    if (r.description && r.description.toLowerCase().indexOf(crit.toLowerCase())>0) {
      return true
    }
    return false
  }

  private def filter() {
    String filterText = searchField.text
    resultsAndPanels.each { ImmoResult k, JComponent v ->
       v.visible = matches(k, filterText)
    }
  }

  private def doSort(Closure comparator) {
    SwingUtilities.invokeLater {
      panel.removeAll()
      def results = resultsAndPanels.keySet().sort({})
      results.sort(comparator).each { r ->
        def cmp = resultsAndPanels.get(r)
        if (cmp) {
          panel.add(cmp)
          panel.revalidate()
        }
      }
    }
  }

  private def sortPrice() {
    doSort  { a,b ->
      def pA = a.price == null ? 0 : a.price
      def pB = b.price == null ? 0 : b.price
      return pA.compareTo(pB)
    }
  }

  private def sortDate() {
    doSort  { a,b ->
      def dA = a.date == null ? new Date(0L) : a.date
      def dB = b.date == null ? new Date(0L) : b.date
      return dA.compareTo(dB)
    }
  }

  private def sortCartridge() {
    doSort  { a,b ->
      def cA = a.cartridge.name
      def cB = b.cartridge.name
      return cA.compareTo(cB)
    }
  }

  private def createResultComponent(ImmoResult r) {
    def bgColor = Color.WHITE
    def cmp = new SwingBuilder().panel(
            layout: new BL(),
            border: BorderFactory.createEmptyBorder(2,2,2,2),
            maximumSize: new Dimension(2000, 120),
            minimumSize: new Dimension(100, 120)) {
      panel(constraints: BorderLayout.CENTER, layout: new BL(), background: bgColor) {
        def photoLabel = label(constraints: BorderLayout.WEST, border: BorderFactory.createEmptyBorder(10,10,10,10))
        ImageIcon icon = null
        try {
          if (r.photoUrl!=null) {
            icon = new ImageIcon(new URL(r.photoUrl))
            Image img = icon.getImage()
            Image newImg = img.getScaledInstance(120, 80,  Image.SCALE_SMOOTH)
            icon = new ImageIcon(newImg)
          } else {
            icon = NO_PHOTO         
          }
          photoLabel.setIcon(icon)
        } catch(Exception e) {
          photoLabel.setIcon(NO_PHOTO)
        }
        panel(constraints: BorderLayout.CENTER, layout: new BL(), background: bgColor) {
          def titleLabel = label(text: r.title, constraints: BorderLayout.NORTH)
          def font = titleLabel.font
          titleLabel.font = new Font(font.name, font.style | Font.BOLD, font.size)
          editorPane(
                    background: bgColor,
                    constraints: BorderLayout.CENTER,
                    text: r.description,
                    editable: false,
                    border: BorderFactory.createEmptyBorder(),
                    minimumSize: new Dimension(100, 30),
                    maximumSize: new Dimension(200, 30),
          )
          def bottomPane = new JPanel(background: bgColor)
          bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.LINE_AXIS))
          if (r.date!=null) {
            def dateLabel = new JLabel(r.date==null ? "" : DATE_FORMAT.format(r.date))
            bottomPane.add(dateLabel)
            bottomPane.add(Box.createRigidArea(new Dimension(5,0)));
            bottomPane.add(new JLabel("-"))
          }
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
      panel(constraints: BL.SOUTH, background: bgColor, border: BorderFactory.createEmptyBorder(2,5,2,2)) {
        boxLayout(axis:BoxLayout.X_AXIS)
        widget(new HyperLink("ouvrir", r.cartridge.icon, JLabel.LEFT, {
          fireResultSelected r
        }))
        label(' | ')
        widget(new HyperLink("exclure", {
          // for now just remove from the view, later on
          // we might need persistent storage for this
            def cmp = resultsAndPanels[r]
            if (cmp) {
              resultsAndPanels.remove(r)
              SwingUtilities.invokeLater {
                panel.remove cmp
                panel.revalidate()
              }
            }
        }))
      }
    }
    resultsAndPanels[r] = cmp
    return cmp
  }

  void addResult(ImmoResult r) {
    if (!headerPanel.visible) {
      headerPanel.visible = true
    }
    nbResults++
    def newPanel = createResultComponent(r)
    def s = messages.getString('status.results.count')
    SwingUtilities.invokeLater {
      panel.add(newPanel)
      statusLabel.text = "$nbResults $s"
      panel.revalidate()
    }
  }

  void clear() {
    nbResults = 0
    resultsAndPanels.clear()
    SwingUtilities.invokeLater {
      panel.removeAll()
      statusLabel.text = null
      headerPanel.visible = false
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
