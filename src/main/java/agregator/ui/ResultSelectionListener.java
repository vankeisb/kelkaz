package agregator.ui;

import agregator.core.Result;

public interface ResultSelectionListener<R extends Result> {

    void resultSelected(R result);
}
