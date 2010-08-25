package agregator.ui

import agregator.core.*
import javax.swing.JFrame
import java.awt.HeadlessException
import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import javax.swing.SwingUtilities
import javax.swing.BoxLayout
import javax.swing.Box.Filler
import java.awt.Dimension
import java.awt.Component

public class AgregatorFrame extends JFrame implements AgregatorListener, ResultSelectionListener {

  private AgregatorFactory agregatorFactory
  private SearchPanel searchPanel
  private ResultsPanel resultsPanel
  private CartridgeListPanel cartridgeListPanel = new CartridgeListPanel()
  private def swing

  public AgregatorFrame(AgregatorFactory agregatorFactory, SearchPanel searchPanel, ResultsPanel resultsPanel) throws HeadlessException {
    super("Agregator")
    this.agregatorFactory = agregatorFactory
    this.searchPanel = searchPanel
    this.resultsPanel = resultsPanel
    // register this as the listener for selected results
    resultsPanel.addListener(this)
    createUI()
  }

  private void createUI() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

    swing = new SwingBuilder()

    def spacerDim = new Dimension(10, 8)
    def spacer = {
      return swing.widget(new Filler(spacerDim, spacerDim, spacerDim))
    }

    setContentPane(swing.panel(layout: new BL()) {

      // left panel
      panel(constraints: BL.WEST) {
        panel(layout:new BL()) {
          widget(widget:searchPanel.getComponent(), constraints:BL.NORTH)
          panel(constraints:BL.CENTER) {
            boxLayout(axis:BoxLayout.Y_AXIS)
            spacer()
            button(text: 'Agregate',
                    actionPerformed : { event ->  agregate() },
                    alignmentX : Component.RIGHT_ALIGNMENT
            )
            spacer()
            separator()
            spacer()
          }
          panel(constraints:BL.SOUTH, layout:new BL()) {
            widget(
                    widget: cartridgeListPanel,
                    constraints : BL.CENTER
            )
          }
        }
      }
      
      // right panel
      widget(widget:resultsPanel.getComponent(), constraints: BL.CENTER)
    })
  }

  private void agregate() {
    // start agregator in new thread
    cartridgeListPanel.clear()
    Thread.start {
      agregatorFactory.create().
        addListener(this).
        addListener(cartridgeListPanel).
        agregate(searchPanel.criteria);
//        removeAllListeners()
    }
  }

  public void onEvent(AgregatorEvent event) {
    if (event instanceof AgregatorEvent.AgregatorCartridgeEvent &&
        event.cartridgeEvent instanceof CartridgeEvent.ResultEvent) {
      SwingUtilities.invokeLater {
        resultsPanel.addResult(event.cartridgeEvent.getResult())
      }
    } else if (event instanceof AgregatorEvent.StartedEvent) {
      SwingUtilities.invokeLater {
        resultsPanel.clear()
      }
    }
  }

  public void resultSelected(Result result) {
    java.awt.Desktop.getDesktop().browse( new URL( result.url ).toURI() )
  }


}