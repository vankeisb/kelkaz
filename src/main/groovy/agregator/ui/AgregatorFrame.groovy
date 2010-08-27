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

  private AgregatorFactory agregatorFactory
  private SearchPanel searchPanel
  private ResultsPanel resultsPanel
  private CartridgeListPanel cartridgeListPanel = new CartridgeListPanel()
  private def swing

  private static final java.awt.Image ICON = new ImageIcon(AgregatorFrame.class.getResource("/icon.png")).getImage()

  public AgregatorFrame(AgregatorFactory agregatorFactory, SearchPanel searchPanel, ResultsPanel resultsPanel) throws HeadlessException {
    super("TrouvToo immobilier - recherche multi-sites")
    setIconImage ICON
    this.agregatorFactory = agregatorFactory
    this.searchPanel = searchPanel
    this.resultsPanel = resultsPanel
    // register this as the listener for selected results
    resultsPanel.addListener(this)
    createUI()
  }

  private JButton btnAgregate
  private JPanel rightPanel = new JPanel(layout: new BL())

  private void createUI() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

    swing = new SwingBuilder()

    btnAgregate = new JButton(text:'Rechercher')
    btnAgregate.addActionListener({ e ->
      agregate() } as ActionListener)

    setContentPane(swing.panel(layout: new BL()) {
      // banner
      panel(constraints: BL.NORTH, layout: new BL(),background: Color.white) {
        label(constraints: BL.CENTER,
                icon: new ImageIcon(getClass().getResource('/banner.jpg')),
                background: Color.white)
      }

      // content
      splitPane(
              constraints: BL.CENTER,
              leftComponent: stackPanels([
                      searchPanel.getComponent(),
                      addBorder(stackPanels([btnAgregate, new JLabel()], BL.EAST), BorderFactory.createEmptyBorder(4,4,10,4)),
                      new JSeparator(),
                      cartridgeListPanel
                    ], BL.NORTH),
              rightComponent: rightPanel
      )

      // feed right panel with welcome text
      rightPanel.removeAll()

      // home page
      // need to create an agregator in order to
      // have access to crtridges... pretty ugly
      rightPanel.add(ImmoRightPanel.createImmoRightPanel(agregatorFactory.create()), BL.CENTER)
    })
  }

  private void agregate() {
    // start agregator in new thread
    cartridgeListPanel.clear()
    SwingUtilities.invokeLater {
      btnAgregate.enabled = false
      rightPanel.removeAll()
      rightPanel.add(resultsPanel.component, BL.CENTER)
    }
    Thread.start {
      try {
        agregatorFactory.create().
          addListener(this).
          addListener(cartridgeListPanel).
          agregate(searchPanel.criteria);
//        removeAllListeners()        
      } catch(Exception e) {
        btnAgregate.enabled = true
        throw e
      }
    }

  }

  public void onEvent(AgregatorEvent event) {
    if (event instanceof AgregatorEvent.AgregatorCartridgeEvent &&
        event.cartridgeEvent instanceof CartridgeEvent.ResultEvent) {
      resultsPanel.addResult(event.cartridgeEvent.getResult())
    } else if (event instanceof AgregatorEvent.StartedEvent) {
      resultsPanel.clear()
    } else if (event instanceof AgregatorEvent.EndedEvent) {
      btnAgregate.enabled = true      
    }
  }

  public void resultSelected(Result result) {
    java.awt.Desktop.getDesktop().browse( new URL( result.url ).toURI() )
  }


}