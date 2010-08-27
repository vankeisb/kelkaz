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
import java.awt.FlowLayout
import javax.swing.JPanel
import javax.swing.JLabel
import javax.swing.BorderFactory
import javax.swing.JSeparator

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

  private void createUI() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

    swing = new SwingBuilder()

    btnAgregate = new JButton(text:'Rechercher')
    btnAgregate.addActionListener({ e ->
      agregate() } as ActionListener)

    JPanel btnWrapperPanel = new JPanel(layout: new FlowLayout())

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
              leftComponent: PanelStacker.stackPanels([
                      searchPanel.getComponent(),
                      PanelStacker.stackPanels([btnAgregate, new JLabel()], BL.EAST, BorderFactory.createEmptyBorder(4,4,10,4)),
                      new JSeparator(),
                      cartridgeListPanel
                    ], BL.NORTH),
              rightComponent: resultsPanel.getComponent()
      )
    })
  }

  private void agregate() {
    // start agregator in new thread
    btnAgregate.enabled = false
    cartridgeListPanel.clear()
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