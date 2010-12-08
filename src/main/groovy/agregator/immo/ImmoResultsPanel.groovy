package agregator.immo

import java.awt.BorderLayout as BL

import agregator.core.Exclusions
import agregator.core.Result
import agregator.ui.HyperLink
import agregator.ui.ResultsPanel
import agregator.ui.Util
import groovy.swing.SwingBuilder
import java.awt.event.ActionListener
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.ConcurrentHashMap
import javax.swing.*
import java.awt.BorderLayout

class ImmoResultsPanel extends ResultsPanel {

  private JComponent component
  private JPanel panel
  private JLabel statusLabel
  private JPanel headerPanel
  private JTextField searchField
  private JButton btnClear
  private JButton btnFilter
  private JCheckBox cbIncludeExclusions
  private ConcurrentHashMap resultsAndPanels = new ConcurrentHashMap() // result/component map used to remove from list

  boolean showExclusions = false

  public ImmoResultsPanel(Exclusions er) {
    super(er)
    panel = new JPanel()
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
    component = new JPanel(layout: new BL())
    def scrollPane = new JScrollPane(panel)
    scrollPane.verticalScrollBar.unitIncrement = 30
    scrollPane.verticalScrollBar.blockIncrement = 50
    scrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
    scrollPane.border = BorderFactory.createEmptyBorder()

    component.add(scrollPane, BL.CENTER)
    statusLabel = new JLabel()
    statusLabel.horizontalAlignment = JLabel.RIGHT
    component.add(statusLabel, BL.SOUTH)

    searchField = new JTextField(text:'')
    searchField.addActionListener({ filter() } as ActionListener)
    btnFilter = new JButton(text:'Filtrer')
    btnFilter.addActionListener({ filter() } as ActionListener)
    btnFilter.enabled = false
    
    btnClear = new JButton(text:'Effacer les resultats')
    btnClear.addActionListener({ clear() } as ActionListener)
    btnClear.enabled = false

    cbIncludeExclusions = new JCheckBox('Afficher les resultats exclus')
    cbIncludeExclusions.addActionListener( { toggleExclusions() } as ActionListener )

    // header panel
    headerPanel = new SwingBuilder().panel(
            visible: false,
            border: BorderFactory.createEmptyBorder(5,5,5,5)) {
      boxLayout(axis: BoxLayout.Y_AXIS)
      panel() {
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
        widget(Box.createHorizontalGlue())
        widget(cbIncludeExclusions)
      }
      panel() {
        boxLayout(axis: BoxLayout.X_AXIS)
        widget(searchField)
        label(' ')
        widget(btnFilter)
        label(' ')
        widget(btnClear)
      }

    }
    component.add(headerPanel, BL.NORTH)
  }

  private void toggleExclusions() {
    showExclusions = cbIncludeExclusions.isSelected()
    SwingUtilities.invokeLater {
      searchField.text = ''
      resultsAndPanels.each { r,p ->
        p.updateExclusionStatus()
      }
      updateStatus()
    }
  }

  void searchStarted() {
    if (!headerPanel.visible) {
      SwingUtilities.invokeLater {
        headerPanel.visible = true
        updateStatus()        
      }
    }
  }

  private boolean stringMatch(String text, List<String> keywords) {
    List<String> tokenizedText = tokenize(text)
    int nbFound = 0
    for (String k : keywords) {
      if (tokenizedText.contains(k)) {
        nbFound++
      }
    }
    return nbFound == keywords.size()
  }

  private List<String> tokenize(String s) {
    if (s) {
      // cleanup the string a bit
      s = s.toLowerCase().
              replaceAll(/,/, " ").
              replaceAll(/\./, " ").
              replaceAll(/\//, " ").
              replaceAll(/'/, " ").
              replaceAll(/:/, " ").
              replaceAll(/-/, " ").
              replaceAll(/\(/, " ").
              replaceAll(/\)/, " ")
      // tokenize
      return Arrays.asList(s.split(" "))
    }
    return Collections.emptyList()
  }

  private def filter() {
    // tokenize the search text to keywords
    List<String> keywords = tokenize(searchField.text)
    resultsAndPanels.each { ImmoResult k, ImmoResultPanel p ->
      String fullText = k.title + " " + k.description
      boolean matches = stringMatch(fullText, keywords)
      if (matches) {
        SwingUtilities.invokeLater {
          p.component.visible = cbIncludeExclusions.selected || !exclusions.isExcluded(k)
        }
      } else {
        SwingUtilities.invokeLater {
          p.component.visible = false
        }
      }
    }
  }

  private def doSort(Closure comparator) {
    SwingUtilities.invokeLater {
      panel.removeAll()
      def results = resultsAndPanels.keySet().sort({})
      results.sort(comparator).each { r ->
        def p = resultsAndPanels.get(r)
        if (p) {
          panel.add(p.component)
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
    ImmoResultPanel irp = new ImmoResultPanel(this, r)
    resultsAndPanels[r] = irp
    return irp
  }

  void addResult(Result  r) {
    ImmoResultPanel newPanel = createResultComponent(r)
    def cmp = createResultComponent(r).getComponent()
    SwingUtilities.invokeLater {
      newPanel.updateExclusionStatus()
      btnClear.enabled = true
      btnFilter.enabled = true
      updateStatus()
      panel.add(cmp)
      panel.revalidate()
    }
  }

  private void updateStatus() {
    int nbRes = 0
    int nbExc = 0
    resultsAndPanels.each { r,p ->
      nbRes++
      if (exclusions.isExcluded(r)) {
        nbExc++
      }
    }
    def s = Util.getMessage('status.results.count')
    if (cbIncludeExclusions.isSelected()) {
      statusLabel.text = "$nbRes $s"
    } else {
      nbRes = Math.max(0, nbRes - nbExc)
      statusLabel.text = "$nbRes $s, ${nbExc} exclus"
    }
  }

  void clear() {
    resultsAndPanels.clear()
    SwingUtilities.invokeLater {
      searchField.text = ''
      panel.removeAll()
      statusLabel.text = null
      headerPanel.visible = false
      updateStatus()      
    }
  }

  JComponent getComponent() {
    return component
  }

  public static void main(String[] args) {
    JFrame f = new JFrame()
    f.setLayout new BL()
    ImmoResultsPanel i = new ImmoResultsPanel(new Exclusions())
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
