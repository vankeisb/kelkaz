package agregator.immo

import agregator.ui.SearchPanel
import agregator.util.ServiceLocalisation
import agregator.core.Criteria
import agregator.immo.ImmoCriteria.Type
import agregator.immo.ImmoCriteria.Country
import agregator.immo.ImmoCriteria.Demand
import javax.swing.JPanel
import com.jgoodies.forms.factories.Borders
import javax.swing.JFrame
import javax.swing.WindowConstants
import groovy.swing.SwingBuilder
import com.jgoodies.forms.factories.DefaultComponentFactory
import javax.swing.JCheckBox
import javax.swing.JTextArea
import javax.swing.JLabel

import com.jgoodies.forms.layout.FormLayout
import com.jgoodies.forms.builder.PanelBuilder
import com.jgoodies.forms.layout.CellConstraints
import javax.swing.JComboBox
import javax.swing.JTextField
import javax.swing.JComponent
import java.awt.BorderLayout


public class ImmoSearchPanel implements SearchPanel{

  // TODO : Add some validation rules - Ex : priceMin < priceMax ...
  private ResourceBundle messages = ResourceBundle.getBundle('MessagesBundle');

  // UI Attributes
  def demandCombo
  def typeCombo

  def minNbRoomsCombo
  def maxNbRoomsCombo
  def minSurfaceField
  def maxSurfaceField
  def minPriceField
  def maxPriceField

  def countryCombo
  def localisationField
  def radianField

  def newOnlyCheck;
  def withPhotosOnlyCheck;

  // JGoodies
  def dcf = DefaultComponentFactory.getInstance()
  def cc = new CellConstraints()

  def buildPanel(){
    def layout = new FormLayout('fill:pref:grow', 'p, p, p, p, p, p, p, p, p')
    def cc = new CellConstraints()
    def swing = new SwingBuilder()
    swing.panel (layout: layout, border: Borders.DIALOG_BORDER){
      widget( widget: dcf.createSeparator(messages.getString('separator.type')), 			constraints: cc.xy(1, 1))
      widget( widget: buildTypePanel(), 													constraints: cc.xy(1, 2))

      widget( widget: dcf.createSeparator(messages.getString('separator.description')), 	constraints: cc.xy(1, 3))
      widget( widget: buildDescriptionPanel(),											constraints: cc.xy(1, 4))

      widget( widget: dcf.createSeparator(messages.getString('separator.geographic')), 	constraints: cc.xy(1, 5))
      widget( widget: buildGeographicPanel(),												constraints: cc.xy(1, 6))

      widget( widget: dcf.createSeparator(messages.getString('separator.options')), 		constraints: cc.xy(1, 7))
      widget( widget: buildOptionsPanel(),												constraints: cc.xy(1, 8))
    }
  }

  def buildTypePanel(){
    def layout = new FormLayout('p, 5dlu, fill:pref:grow', 'p, 2dlu, p')
    def swing = new SwingBuilder()
    swing.panel (layout: layout, border: Borders.DIALOG_BORDER){
      label(messages.getString('type.demand'), 		constraints: cc.xy(1, 1))
      demandCombo = comboBox(items: Demand.values(), 	constraints: cc.xy(3, 1))
      label(messages.getString('type.type'), 			constraints: cc.xy(1, 3))
      typeCombo = comboBox(items: Type.values(), 		constraints: cc.xy(3, 3))
    }
  }

  def buildDescriptionPanel(){
    def layout = new FormLayout('fill:pref:grow, 5dlu, p, 2dlu, p, 2dlu, p, 2dlu, p', 'p, 2dlu, p, 2dlu, p')
    def swing = new SwingBuilder()
    swing.panel (layout: layout, border: Borders.DIALOG_BORDER) {
      // Nb rooms
      label(messages.getString('description.nbrooms'), 	constraints: cc.xy(1, 1))
      label(messages.getString('description.mini'),	 	constraints: cc.xy(3, 1))
      minNbRoomsCombo = comboBox(items: [1,2,3,4,5,6,7,8],constraints: cc.xy(5, 1))
      label(messages.getString('description.maxi'),		constraints: cc.xy(7, 1))
      maxNbRoomsCombo = comboBox(items: [2,3,4,5,6,7,8,9],constraints: cc.xy(9, 1))

      // Surface
      label(messages.getString('description.surface'), 	constraints: cc.xy(1, 3))
      label(messages.getString('description.mini'),	 	constraints: cc.xy(3, 3))
      minSurfaceField = textField(columns: 5,		 		constraints: cc.xy(5, 3))
      label(messages.getString('description.maxi'),	 	constraints: cc.xy(7, 3))
      maxSurfaceField = textField(columns: 5,		 		constraints: cc.xy(9, 3))

      // Price
      label(messages.getString('description.price'), 		constraints: cc.xy(1, 5))
      label(messages.getString('description.mini'),	 	constraints: cc.xy(3, 5))
      minPriceField = textField(columns: 8,		 		constraints: cc.xy(5, 5))
      label(messages.getString('description.maxi'),	 	constraints: cc.xy(7, 5))
      maxPriceField = textField(columns: 8,		 		constraints: cc.xy(9, 5))
    }
  }

  def buildGeographicPanel(){
    def layout = new FormLayout('p, 5dlu, fill:pref:grow', 'p, 2dlu, p, 2dlu, p')
    def swing = new SwingBuilder()
    swing.panel (layout: layout, border: Borders.DIALOG_BORDER){
      // Country
      label(messages.getString('geographic.country'), 	constraints: cc.xy(1, 1))
      countryCombo = comboBox(items: Country.values(),	constraints: cc.xy(3, 1))

      // Localisation
      label(messages.getString('geographic.localisation'),constraints: cc.xy(1, 3))
      localisationField = textField(columns: 10,			constraints: cc.xy(3, 3))

      // Radian
      label(messages.getString('geographic.radian'), 		constraints: cc.xy(1, 5))
      radianField = textField(columns: 3,					constraints: cc.xy(3, 5))
    }
  }

  def buildOptionsPanel(){
    def layout = new FormLayout('p, 5dlu, fill:pref:grow', 'p, 2dlu, p')
    def swing = new SwingBuilder()
    swing.panel (layout: layout, border: Borders.DIALOG_BORDER){
      // new
      label(messages.getString('options.newonly'), 	constraints: cc.xy(1, 1))
      newOnlyCheck = checkBox(selected: false,		constraints: cc.xy(3, 1))

      // with photos
      label(messages.getString('options.withphotos'), constraints: cc.xy(1, 3))
      withPhotosOnlyCheck = checkBox(selected: false,	constraints: cc.xy(3, 3))
    }
  }

  public JComponent getComponent() {
    return buildPanel()
  }

  public Criteria getCriteria() {
    ImmoCriteria criteria = new ImmoCriteria();

    if (localisationField.text){
      String[] results = ServiceLocalisation.INSTANCE.getLocalisation(localisationField.text);
      criteria.setCity(results[0])
      criteria.setPostCode(results[1])
    }


    criteria.setCountry((Country)countryCombo.selectedItem)

    criteria.setDemand((Demand)demandCombo.selectedItem)

    criteria.setNbRoomsMax((Integer)maxNbRoomsCombo.selectedItem)

    criteria.setNbRoomsMin((Integer)minNbRoomsCombo.selectedItem)

    criteria.setNewsOnly(newOnlyCheck.selected)

    if (maxPriceField.text)
      criteria.setPriceMax(Integer.valueOf(maxPriceField.text))

    if (minPriceField.text)
      criteria.setPriceMin(Integer.valueOf(minPriceField.text))

    if (radianField.text != null && !radianField.text.equals(''))
      criteria.setSearchRadius(Integer.valueOf(radianField.text))


    if (maxSurfaceField.text != null && !maxSurfaceField.text.equals(''))
      criteria.setSurfaceMax(Integer.valueOf(maxSurfaceField.text))

    if (minSurfaceField.text != null && !minSurfaceField.text.equals(''))
      criteria.setSurfaceMin(Integer.valueOf(minSurfaceField.text))

    criteria.setType((Type)typeCombo.selectedItem)

    criteria.setWithPhotosOnly(withPhotosOnlyCheck.selected)

    return criteria;
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
