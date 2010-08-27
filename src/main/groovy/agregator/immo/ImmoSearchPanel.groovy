package agregator.immo

import agregator.core.Criteria

import agregator.immo.ImmoCriteria.Type
import agregator.immo.ImmoCriteria.Demand

import agregator.ui.SearchPanel

import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JFrame

import javax.swing.border.TitledBorder

import javax.swing.JLabel

import javax.swing.BorderFactory
import javax.swing.border.Border
import java.awt.Font

import javax.swing.JRadioButton
import javax.swing.JTextField
import static agregator.ui.PanelStacker.*

public class ImmoSearchPanel implements SearchPanel {

  // TODO : Add some validation rules - Ex : priceMin < priceMax ...
  private ResourceBundle messages = ResourceBundle.getBundle('MessagesBundle');

  private def makeTitledBorder(String title, int fontSizeIncrement) {
    Border outerBorder = BorderFactory.createEmptyBorder(0, 0, 8, 0)
    def b = new TitledBorder(outerBorder, title, TitledBorder.LEFT, TitledBorder.ABOVE_TOP)
    Font f = b.getTitleFont()
    b.titleFont = new Font(f.name, f.style | Font.BOLD, f.size + fontSizeIncrement)
    return b
  }

  JRadioButton radioLoc = new JRadioButton(text: 'Location', selected: true)
  JRadioButton radioVente = new JRadioButton(text: 'Vente')
  JRadioButton radioAppt = new JRadioButton(text: 'Appartement', selected: true)
  JRadioButton radioMaison = new JRadioButton(text: 'Maison')

  JTextField tfNbPiecesMin = createTextField()
  JTextField tfNbPiecesMax = createTextField()
  JTextField tfSurfaceMin = createTextField()
  JTextField tfSurfaceMax = createTextField()
  JTextField tfPriceMin = createTextField()
  JTextField tfPriceMax = createTextField()
  JTextField tfCodePostal = createTextField(120)

  def buildPanel(){

    groupButtons([radioLoc, radioVente])
    groupButtons([radioAppt, radioMaison])

    def p = stackPanels([
            createSeparatorLabel('Type'),
            addBorder(stackPanels([radioLoc, radioVente], BorderLayout.WEST), BorderFactory.createEmptyBorder(0, 10, 0, 0)),
            addBorder(stackPanels([radioAppt, radioMaison], BorderLayout.WEST), BorderFactory.createEmptyBorder(0, 10, 0, 0)),

            createSeparator(),

            createSeparatorLabel('Nombre de pieces'),
            addBorder(stackPanels([
                    createFormLabel('Min'),
                    tfNbPiecesMin,
                    createSeparator(),
                    createFormLabel('Max'),
                    tfNbPiecesMax,
                    new JLabel(' ') // stretches
            ], BorderLayout.WEST), BorderFactory.createEmptyBorder(0, 10, 0, 0)),

            createSeparator(),

            createSeparatorLabel('Surface'),
            addBorder(stackPanels([
                    createFormLabel('Min'),
                    tfSurfaceMin,
                    createSeparator(),
                    createFormLabel('Max'),
                    tfSurfaceMax,
                    new JLabel(' ') // stretches
            ], BorderLayout.WEST), BorderFactory.createEmptyBorder(0, 10, 0, 0)),

            createSeparator(),

            createSeparatorLabel('Prix'),
            addBorder(stackPanels([
                    createFormLabel('Min'),
                    tfPriceMin,
                    createSeparator(),                    
                    createFormLabel('Max'),
                    tfPriceMax,
                    new JLabel(' ') // stretches
            ], BorderLayout.WEST), BorderFactory.createEmptyBorder(0, 10, 0, 0)),

            createSeparator(),

            createSeparatorLabel('Localisation'),
            addBorder(stackPanels([
                    createFormLabel('Code Postal'),
                    tfCodePostal,
                    new JLabel(' ') // stretches
            ], BorderLayout.WEST), BorderFactory.createEmptyBorder(0, 10, 0, 0)),

            createSeparator()
    ], BorderLayout.NORTH)

    p.border = BorderFactory.createEmptyBorder(0, 6, 6, 6)

    return p    
  }

  public JComponent getComponent() {
    return buildPanel()
  }

  public Criteria getCriteria() {

    def intFromTextField = { JTextField tf ->
      if (tf.text==null) {
        return null
      }
      try {
        return Integer.parseInt(tf.text)
      } catch(NumberFormatException e) {
        return null
      }
    }

    ImmoCriteria c = new ImmoCriteria()
    c.demand = radioLoc.selected ? Demand.RENT : Demand.SELL
    c.type = radioAppt.selected ? Type.APPT : Type.MAISON
    c.nbRoomsMin = intFromTextField(tfNbPiecesMin)
    c.nbRoomsMax = intFromTextField(tfNbPiecesMax)
    c.surfaceMin = intFromTextField(tfSurfaceMin)
    c.surfaceMax = intFromTextField(tfSurfaceMax)
    c.priceMin = intFromTextField(tfPriceMin)
    c.priceMax = intFromTextField(tfPriceMax)
    c.postCode = tfCodePostal.text
    return c
  }

  static void main(String[] args) {
    def p = new ImmoSearchPanel()
    JFrame f = new JFrame()
    f.layout = new BorderLayout()
    f.contentPane.add(p.getComponent())
    f.pack()
    f.visible = true
  }
}
