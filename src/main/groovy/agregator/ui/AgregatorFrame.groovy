package agregator.ui

import agregator.core.*
import javax.swing.JFrame
import java.awt.HeadlessException
import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import java.awt.Color
import javax.swing.ImageIcon
import javax.swing.JButton
import java.awt.event.ActionListener

import javax.swing.JPanel
import javax.swing.JLabel
import javax.swing.BorderFactory
import javax.swing.JSeparator
import javax.swing.SwingUtilities
import agregator.immo.ImmoRightPanel
import static agregator.ui.PanelStacker.*

public class AgregatorFrame extends JFrame implements AgregatorListener, ResultSelectionListener {

  private final Agregator agregatorFactory
  private final SearchPanel searchPanel
  private final ResultsPanel resultsPanel
  private final CartridgeListPanel cartridgeListPanel
  private def swing

  private Agregator agregator = null

  private static final java.awt.Image ICON = new ImageIcon(AgregatorFrame.class.getResource("/icon.gif")).getImage()

  public AgregatorFrame(Agregator agregator, SearchPanel searchPanel, ResultsPanel resultsPanel) throws HeadlessException {
    super("Kelkaz - recherche multi-sites")
    setIconImage ICON
    this.cartridgeListPanel = new CartridgeListPanel(agregator)
    this.agregator = agregator
    this.searchPanel = searchPanel
    this.resultsPanel = resultsPanel
    // register this as the listener for selected results
    resultsPanel.addListener(this)
    agregator.
      addListener(this).
      addListener(cartridgeListPanel)    
    createUI()
  }

  private JButton btnAgregate
  private JPanel rightPanel = new JPanel(layout: new BL())
  private boolean hasSearchedOnceAlready = false

  private void createUI() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

    swing = new SwingBuilder()

    btnAgregate = new JButton(text:'Rechercher')
    btnAgregate.addActionListener({ e ->
      btnAgregate.enabled = false
      agregate() } as ActionListener)

    setContentPane(swing.panel(layout: new BL()) {
      // banner
      def banner = new HyperLink(
              "",
              new ImageIcon(getClass().getResource('/banner.jpg')),
              JLabel.LEFT,
              {
                java.awt.Desktop.getDesktop().browse( new URL( "http://kelkaz.fr" ).toURI() )
              })
      def northPanel = panel(constraints: BL.NORTH, layout: new BL(),background: Color.white)
      northPanel.add(banner, BL.CENTER)

      // content
      def centerPanel = panel(constraints: BL.CENTER, layout: new BL())
      centerPanel.add(stackPanels([stackPanels([
                      searchPanel.getComponent(),
                      addBorder(stackPanels([btnAgregate, new JLabel()], BL.EAST), BorderFactory.createEmptyBorder(4,4,10,4)),
                      new JSeparator(),
                      cartridgeListPanel
                    ], BL.NORTH), rightPanel], BL.WEST), BL.CENTER
      )


      // feed right panel with welcome text
      rightPanel.removeAll()

      // home page
      rightPanel.add(ImmoRightPanel.createImmoRightPanel(agregator), BL.CENTER)
      rightPanel.setBorder(BorderFactory.createEmptyBorder(2,0,0,0))
    })
  }

  private void agregate() {
    if (agregator.agregating) {
      agregator.kill()
    } else {
      // start agregator in new thread
      SwingUtilities.invokeLater {
        cartridgeListPanel.clear()
        if (!hasSearchedOnceAlready) {
          rightPanel.removeAll()
          rightPanel.add(resultsPanel.component, BL.CENTER)
          hasSearchedOnceAlready = true
        }
        rightPanel.revalidate()
      }
      Thread.start {
        try {
            agregator.agregate(searchPanel.criteria);
        } catch(Exception e) {
          throw e
        }
      }
    }
  }

  public void onEvent(AgregatorEvent event) {
    if (event instanceof AgregatorEvent.AgregatorCartridgeEvent &&
        event.cartridgeEvent instanceof CartridgeEvent.ResultEvent) {
      resultsPanel.addResult(event.cartridgeEvent.result)
    } else if (event instanceof AgregatorEvent.StartedEvent) {
      SwingUtilities.invokeLater {
        resultsPanel.searchStarted()
        btnAgregate.text = "Arreter"
        btnAgregate.enabled = true
      }
    } else if (event instanceof AgregatorEvent.EndedEvent) {
      SwingUtilities.invokeLater {
        btnAgregate.text = "Rechercher"
        btnAgregate.enabled = true
      }
    }
  }

  public void resultSelected(Result result) {
    java.awt.Desktop.getDesktop().browse( new URL( result.url ).toURI() )
  }


}