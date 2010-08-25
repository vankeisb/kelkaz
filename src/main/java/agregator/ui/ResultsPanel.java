package agregator.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;

import agregator.core.Result;

public abstract class ResultsPanel<R extends Result> {

    private List<ResultSelectionListener<R>> listeners =
            Collections.synchronizedList(new ArrayList<ResultSelectionListener<R>>());

    public void addListener(ResultSelectionListener<R> listener) {
        listeners.add(listener);
    }

    public abstract JComponent getComponent();

    public abstract void clear();

    public abstract void addResult(R r);

    protected void fireResultSelected(R r) {
        for (ResultSelectionListener<R> l : listeners) {
            l.resultSelected(r);
        }
    }
}
