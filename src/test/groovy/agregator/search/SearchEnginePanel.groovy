package agregator.search

import agregator.ui.SearchPanel
import javax.swing.JTextField
import javax.swing.JComponent
import javax.swing.JPanel
import java.awt.BorderLayout
import javax.swing.JLabel
import java.awt.Dimension
import groovy.swing.SwingBuilder
import agregator.core.Criteria
import agregator.search.SearchEngineCriteria

/**
 * Created by IntelliJ IDEA.
 * User: vankeisb
 * Date: 3 oct. 2009
 * Time: 14:50:26
 * To change this template use File | Settings | File Templates.
 */

public class SearchEnginePanel implements SearchPanel {

    def tf

    public JComponent getComponent() {
      return new SwingBuilder().panel(layout:new BorderLayout()) {
        label(constraints:BorderLayout.WEST, text:"Your query")
        tf = textField(constraints:BorderLayout.CENTER, columns:20)
      }
    }

    public Criteria getCriteria() {
        return new SearchEngineCriteria(query:tf.text)
    }
}
