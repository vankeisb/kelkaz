package agregator.immo

import agregator.ui.ResultsPanel
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.SwingUtilities
import groovy.text.GStringTemplateEngine

class ImmoResultsPanel3 extends ResultsPanel<ImmoResult> {

  private JEditorPane editorPane = new JEditorPane("text/html","")
  private def results = []
  private def template

  def ImmoResultsPanel3() {
    clear()
    def engine = new GStringTemplateEngine()
    URL u = getClass().getResource("/agregator/immo/cartridges/immoResultTemplate.template")
    template = engine.createTemplate(u)
  }

  void addResult(ImmoResult r) {
    results << r
    def resultsCopy = []
    resultsCopy.addAll(results)
    def binding = ["results":resultsCopy]
    SwingUtilities.invokeLater {
      editorPane.setText(template.make(binding).toString())
    }
  }

  void clear() {
    results = []
    SwingUtilities.invokeLater {
      editorPane.setText("")      
    }
  }

  JComponent getComponent() {
    return editorPane
  }


}
