package agregator.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;

import agregator.core.ExcludedResults;
import agregator.core.Result;

public abstract class ResultsPanel<R extends Result> {

    private final List<ResultSelectionListener<R>> listeners =
            Collections.synchronizedList(new ArrayList<ResultSelectionListener<R>>());

    private final ExcludedResults excludedResults;

    protected ResultsPanel(ExcludedResults excludedResults) {
      if (excludedResults==null) {
        throw new IllegalArgumentException("excludedResults cannot be null");
      }
      this.excludedResults = excludedResults;
    }

    public ExcludedResults getExcludedResults() {
      return excludedResults;
    }

    public void addListener(ResultSelectionListener<R> listener) {
        listeners.add(listener);
    }

    public abstract JComponent getComponent();

    public abstract void clear();

    public final void addResult(R r) {
      if (!excludedResults.isExcluded(r)) {
        doAddResult(r);
      }
    }

    protected abstract void doAddResult(R r);

    protected void fireResultSelected(R r) {
        for (ResultSelectionListener<R> l : listeners) {
            l.resultSelected(r);
        }
    }

    protected void fireResultExcluded(R r) {
      excludedResults.addExclusion(r);
    }
}
