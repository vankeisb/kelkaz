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
import javax.swing.JCheckBox

public class ImmoSearchPanel implements SearchPanel {

  // TODO : Add some validation rules - Ex : priceMin < priceMax ...

  private def makeTitledBorder(String title, int fontSizeIncrement) {
    Border outerBorder = BorderFactory.createEmptyBorder(0, 0, 8, 0)
    def b = new TitledBorder(outerBorder, title, TitledBorder.LEFT, TitledBorder.ABOVE_TOP)
    Font f = b.getTitleFont()
    b.titleFont = new Font(f.name, f.style | Font.BOLD, f.size + fontSizeIncrement)
    return b
  }

  JRadioButton radioLoc = new JRadioButton(text: 'Location', selected: true)
  JRadioButton radioVente = new JRadioButton(text: 'Vente')

  JCheckBox cbAppt = new JCheckBox(text: 'Appartement', selected: true)
  JCheckBox cbMaison = new JCheckBox(text: 'Maison', selected: true)

  JTextField tfNbPiecesMin = createTextField()
  JTextField tfNbPiecesMax = createTextField()
  JTextField tfSurfaceMin = createTextField()
  JTextField tfSurfaceMax = createTextField()
  JTextField tfPriceMin = createTextField()
  JTextField tfPriceMax = createTextField()
  JTextField tfCodePostal = createTextField(120)

  def buildPanel(){

    groupButtons([radioLoc, radioVente])

    def p = stackPanels([
            createSeparatorLabel('Type'),
            addBorder(stackPanels([radioLoc, radioVente], BorderLayout.WEST), BorderFactory.createEmptyBorder(0, 10, 0, 0)),
            addBorder(stackPanels([cbAppt, cbMaison], BorderLayout.WEST), BorderFactory.createEmptyBorder(0, 10, 0, 0)),

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

  private def stringsFromTextField(JTextField tf) {
    if (tf.text==null) {
      return null
    }
    String txt = tf.text.replaceAll(/,/," ").replaceAll(/;/,' ')
    return txt.split(' ')
  }

  private Integer intFromTextField(JTextField tf) {
    if (tf.text==null) {
      return null
    }
    try {
      return Integer.parseInt(tf.text)
    } catch(NumberFormatException e) {
      return null
    }
  }

  public List<Criteria> getCriterias() {
    Demand demand = radioLoc.selected ? Demand.RENT : Demand.SELL
    def types = []
    if (cbAppt.selected) {
      types << Type.APPT
    }
    if (cbMaison.selected) {
      types << Type.MAISON
    }
    def nbRoomsMin = intFromTextField(tfNbPiecesMin)
    def nbRoomsMax = intFromTextField(tfNbPiecesMax)
    def surfaceMin = intFromTextField(tfSurfaceMin)
    def surfaceMax = intFromTextField(tfSurfaceMax)
    def priceMin = intFromTextField(tfPriceMin)
    def priceMax = intFromTextField(tfPriceMax)

    def postCodes = stringsFromTextField(tfCodePostal)

    def criterias = []
    types.each { type ->
      postCodes.each { postCode ->
        criterias << new ImmoCriteria(
                demand: demand,
                type: type,
                nbRoomsMin: nbRoomsMin,
                nbRoomsMax: nbRoomsMax,
                surfaceMin: surfaceMin,
                surfaceMax: surfaceMax,
                priceMin: priceMin,
                priceMax: priceMax,
                postCode: postCode)
      }
    }
    return criterias
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
